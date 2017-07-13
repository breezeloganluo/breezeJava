package com.breezefw.client.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.breeze.support.tools.FileTools;
import com.breezefw.client.moudle.BreezeResult;
import com.breezefw.client.moudle.ConfigData;
import com.breezefw.client.moudle.FunctionItem;
import com.breezefw.client.service.dataProcess.DataProcessAbs;
import com.breezefw.client.service.dataProcess.NullProcessor;
import com.breezefw.client.service.dataProcess.SQLProcessor;
import com.breezefw.client.tools.BreezeSystemTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProjectMgr {
	public final static String basePrifix = "manager_auxiliary/";
	private static ProjectMgr inc = new ProjectMgr();
	private String baseDir;

	public static ProjectMgr getIn() {
		return inc;
	}
	
	public String createNewProject(String projectBase) throws IOException {		this.baseDir = projectBase;
		HashMap<Integer,DataProcessAbs> loadingDataMap = this.getLoadingMap();
		
		String configFile = projectBase + basePrifix + "manager/config.cfg";
		ConfigData cfg = BreezeSystemTools.ConfigData(configFile);
		return this.getFileFromServer(cfg.getServerHost(),cfg.getFilename(), projectBase, cfg.getProjectName(), "projectName", "getAllUnitByProject");
	}
	public String getFileFromServer(String remoteHost,String projectBase,String loadingObj,String paramName,String serviceName) throws IOException {
		return this.getFileFromServer(remoteHost, "breeze.brz", projectBase, loadingObj, paramName, serviceName);		
	}
	public String getFileFromServer(String remoteHost,String fileName,String projectBase,String loadingObj,String paramName,String serviceName) throws IOException {
		System.out.println("version 1.04");
		this.baseDir = projectBase;
		HashMap<Integer,DataProcessAbs> loadingDataMap = this.getLoadingMap();
		
		System.out.println("[[step 1]]: begin loading object:"+loadingObj);

		// 下面合成请求参数
		HashMap<String, Object> breezeParam = new HashMap<String, Object>();
		breezeParam.put(paramName, loadingObj);
		String paramJson = "["
				+ BreezeSystemTools.getPostJsonString("project",
						serviceName, breezeParam) + ']';
		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("data", paramJson);

		// 下面合成url
		String postUrl = remoteHost + fileName;
		String httpResult = BreezeSystemTools.getPostResponseText(
				postUrl, postParam);
		//下面将分析这个文件并解压分析到目录中。
		Gson gson = new Gson();
		BreezeResult[] resultObj = gson.fromJson(httpResult,
				new TypeToken<BreezeResult[]>() {
				}.getType());

		// 下面解压
		FunctionItem[] items = resultObj[0].getData(); 
		System.out.println("[[step 2]]: begin get item total is:"+items.length);
		
		for (int i = 0; i < items.length; i++) {
			System.out.println("--------------------------------");
			FunctionItem one = items[i];
			DataProcessAbs dp = loadingDataMap.get(one.getDataProcessorType());
			System.out.println("loading data type is :"+one.getDataProcessorType() + " | "+dp);
			if (dp != null){
				System.out.println("loadingData is..."+one.getLoadingData());
				dp.doData(one.getLoadingData(), one.getFileDir());
			}
			String url = remoteHost + one.getFileUrl();
			System.out.println("   >>>getting:" + url);
			String outputDir = projectBase + one.getFileDir();
			if (one.getFileDir() == null){
				outputDir = projectBase;
			}
			InputStream in = BreezeSystemTools.getResponseStream(url);
			
			System.out.println("   >>>>extart to :" + outputDir);
			if (one.getCompressionType() == 1) {
				//将输入流保存到内存
				byte[]buff = BreezeSystemTools.change2ByteArray(in);
				System.out.println("   >>>>zip FileLen:" + buff.length);
				//首先找出filter文件并将之解析
				InputStream tempIn = new ByteArrayInputStream(buff);
				String filterText = BreezeSystemTools.getFileText("/filter.list", tempIn);
				System.out.println("   >>>>filter.list text is:\n" + filterText);
				tempIn.close();
				List filter = BreezeSystemTools.getFilterList(filterText);
				tempIn = new ByteArrayInputStream(buff);
				List<String> blackList = new ArrayList<String>();
				blackList.add("filter.list");
				BreezeSystemTools.unZIP(outputDir, tempIn,filter,blackList);
				tempIn.close();
				System.out.println("   >>>>finished zip ");
			} else {
				BreezeSystemTools.copyFile(outputDir, in);
			}
			in.close();
		}
		System.out.println("[[step 3]]: process loadingData");
		this.finishedLoadingData(loadingDataMap);
		
		//将本次解压的结果放入到一个文件中
		System.out.println("[[step 4]]: saving result");
		FileTools.writeFile(this.baseDir + basePrifix +"projectItemList.prj", httpResult, "UTF-8");
		return httpResult;
	}
	
	private HashMap<Integer,DataProcessAbs> getLoadingMap(){
		HashMap<Integer,DataProcessAbs> result = new HashMap<Integer,DataProcessAbs>();
		result.put(0, new NullProcessor());
		result.put(1, new SQLProcessor(this.baseDir));
		return result;
	}
	
	private void finishedLoadingData(HashMap<Integer,DataProcessAbs> m){
		System.out.println("begin to do loading data!\n" + m);
		for (Integer i:m.keySet()){
			System.out.println("finish 2:" + i);
			m.get(i).finished();
		}
	}
}
