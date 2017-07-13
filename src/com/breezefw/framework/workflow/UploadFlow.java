package com.breezefw.framework.workflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.upload.UploadHttpRequest;
import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLParser;
import com.breezefw.framework.template.UploadFlowItem;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.unzip.UnzipUtil;

public class UploadFlow extends WorkFlowUnit {

	private final Logger log = Logger
			.getLogger("com.breezefw.framework.workflow.UploadFlow");
	private final static String FLOWNAME = "UploadFlow";
	
	@Override
	public String getName() {
		return FLOWNAME;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias, int lastResult) {
		UploadFlowItem items = (UploadFlowItem) this.getItem(context, st,
				FLOWNAME);
		try{
		//获取上传对象
		BreezeContext uploadCtx = context.getContext("_UploadReq");
		if (uploadCtx == null || uploadCtx.isNull()){
			log.severe("no uplad obj found!");
			return 101;
		}
		
		UploadHttpRequest.Upload re = (UploadHttpRequest.Upload)uploadCtx.getData();
		
		//获取上传信息
		UploadFlowItem.UploadSetting[] upsArr = items.getUploadSettingList();
		String settingFilePath = upsArr.length>1?items.getDestFileCtxPath()+"[]":items.getDestFileCtxPath();
		String settingDirPath = upsArr.length>1?items.getDestDirCtxPath()+"[]":items.getDestDirCtxPath();
		for (int i=0;i<upsArr.length;i++){
			UploadFlowItem.UploadSetting one = upsArr[i];
			BTLExecutor exe = BTLParser.INSTANCE("sql").parser(one.getDestFileName());
			String baseFileName = exe.execute(new Object[]{context}, null);
			String fileName = Cfg.getCfg().getRootDir() + "/"+baseFileName;
			String dirName = "/";
			Pattern p = Pattern.compile("(.+?)[^\\\\\\/]+$");
			Matcher m = p.matcher(baseFileName);
			if (m.find()){
				dirName = m.group(1);
				File dir = new File(Cfg.getCfg().getRootDir()+"/"+dirName);
				dir.mkdirs();
			}
			String field = one.getUploadField();
			re.saveFile(field, new File(fileName));
			//下面解压zip
			if ("y".equals(one.getIsZip())){
				log.fine("is zip!");
				ZipInputStream is = null;
				OutputStream os = null;
				int BUFF_SIZE = 4096;
				
				ZipFile zipFile = new ZipFile(fileName);
				String destinationPath = Cfg.getCfg().getRootDir() + dirName;
				List fileHeaderList = zipFile.getFileHeaders();
				
				// Loop through all the fileHeaders
				for (int j = 0; j < fileHeaderList.size(); j++) {
					FileHeader fileHeader = (FileHeader)fileHeaderList.get(j);
					if (fileHeader != null) {
						String outFilePath = destinationPath + "/" + fileHeader.getFileName();
						//重新设定文件名和文件路径
						baseFileName = dirName + "/" + fileHeader.getFileName();
						p = Pattern.compile("(.+?)[^\\\\\\/]+$");
						m = p.matcher(baseFileName);
						if (m.find()){
							dirName = m.group(1);
							File dir = new File(Cfg.getCfg().getRootDir()+"/"+dirName);
							dir.mkdirs();
						}
						
						
						File outFile = new File(outFilePath);
						
						//Checks if the file is a directory
						if (fileHeader.isDirectory()) {
							//This functionality is up to your requirements
							//For now I create the directory
							outFile.mkdirs();
							continue;
						}
						
						//Check if the directories(including parent directories)
						//in the output file path exists
						File parentDir = outFile.getParentFile();
						if (!parentDir.exists()) {
							parentDir.mkdirs();
						}
						
						//Get the InputStream from the ZipFile
						is = zipFile.getInputStream(fileHeader);
						//Initialize the output stream
						os = new FileOutputStream(outFile);
						
						int readLen = -1;
						byte[] buff = new byte[BUFF_SIZE];
						
						//Loop until End of File and write the contents to the output stream
						while ((readLen = is.read(buff)) != -1) {
							os.write(buff, 0, readLen);
						}
						
						//Please have a look into this method for some important comments
						closeFileHandlers(is, os);
						
						//To restore File attributes (ex: last modified file time, 
						//read only flag, etc) of the extracted file, a utility class
						//can be used as shown below
						UnzipUtil.applyFileAttributes(fileHeader, outFile);

					} else {
						log.severe("fileheader is null. Shouldn't be here");
					}
				}
			}
			context.setContextByPath(settingFilePath, new BreezeContext(baseFileName));
			context.setContextByPath(settingDirPath, new BreezeContext(dirName));
		}
		return 0;
		}catch(Exception e){
			log.severe("上传异常",e);
			return 999;
		}
	}
	
	private void closeFileHandlers(ZipInputStream is, OutputStream os) throws IOException{
		//Close output stream
		if (os != null) {
			os.close();
			os = null;
		}
		
		//Closing inputstream also checks for CRC of the the just extracted file.
		//If CRC check has to be skipped (for ex: to cancel the unzip operation, etc)
		//use method is.close(boolean skipCRCCheck) and set the flag,
		//skipCRCCheck to false
		//NOTE: It is recommended to close outputStream first because Zip4j throws 
		//an exception if CRC check fails
		if (is != null) {
			is.close();
			is = null;
		}
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { new CommTemplateItemParser(
				FLOWNAME, UploadFlowItem.class) };
		return result;
	}

}
