package com.breezefw.client.shell;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breeze.support.tools.FileTools;
import com.breezefw.client.tools.BreezeSystemTools;
import com.breezefw.client.tools.UploadFile;

/*这是一个给ant用的版本上传类
 * 1.读取版本信息
 * 2.上传文件a.版本文件b.补丁文件c.版本文档
 * 3.设置更新的版本记录
 */
public class UploadOneVersion {
	private String baseHost;
	private String mainDir;
	private String versionFile;

	public UploadOneVersion(String m, String v, String h) {
		this.mainDir = m;
		this.versionFile = v;
		this.baseHost = h;
	}

	private String contentDir;
	private String patchDir;
	private String docDir;
	private String type;
	private String version;
	private String versionDesc;

	/*
	 * 这个函数用于读取磁盘上的版本记录信息 包括： ContentDir:版本所在的目录 PatchDir:补丁所在的目录 DocDir:文档所在的目录
	 * Version:版本号 VersionDesc:版本说明
	 */
	void readVersion() {
		// 读入文件
		String allPath = this.mainDir + "/" + this.versionFile;
		String fileText = FileTools.readFile(allPath, "UTF-8");
		// 用正则解析ContentDir
		Pattern p = Pattern.compile("<ContentDir>([\\s\\S]+?)</ContentDir>",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(fileText);
		if (m.find()) {
			this.contentDir = m.group(1).trim();
		}
		// 用正则解析patchDir
		p = Pattern.compile("<PatchDir>([\\s\\S]+?)</PatchDir>",
				Pattern.CASE_INSENSITIVE);
		m = p.matcher(fileText);
		if (m.find()) {
			this.patchDir = m.group(1).trim();
		}
		// 用正则解析docDir
		p = Pattern.compile("<DocDir>([\\s\\S]+?)</DocDir>",
				Pattern.CASE_INSENSITIVE);
		m = p.matcher(fileText);
		if (m.find()) {
			this.docDir = m.group(1).trim();
		}

		// 用正则解析类型
		p = Pattern.compile("<Type>([\\s\\S]+?)</Type>",
				Pattern.CASE_INSENSITIVE);
		m = p.matcher(fileText);
		if (m.find()) {
			this.type = m.group(1).trim();
		}
		
		// 用正则解析版本
		p = Pattern.compile("<Version>([\\s\\S]+?)</Version>",
				Pattern.CASE_INSENSITIVE);
		m = p.matcher(fileText);
		if (m.find()) {
			this.version = m.group(1).trim();
		}
		// 用正则解析版本描述
		p = Pattern.compile("<VersionDesc>([\\s\\S]+?)</VersionDesc>",
				Pattern.CASE_INSENSITIVE);
		m = p.matcher(fileText);
		if (m.find()) {
			this.versionDesc = m.group(1).trim();
		}
	}

	private String contentServerDir;
	private String patchServerDir;
	private String docServerDir;

	/*
	 * 负责上传3个文件
	 */
	void uploadFile() throws Exception {
		// 整理基本url
		String url = this.baseHost + "/upload.up";
		// 处理上传基本文件
		UploadFile up = new UploadFile(url);
		up.setFiles("upload", this.mainDir + "/" + this.contentDir);
		up.finished();
		this.contentServerDir = up.send();
		// 处理上传补丁
		if (this.patchDir != null) {
			up = new UploadFile(url);
			up.setFiles("upload", this.mainDir +  "/" + this.patchDir);
			up.finished();
			this.patchServerDir = up.send();
		}
		// 处理上传文档
		if (this.docDir != null) {
			up = new UploadFile(url);
			up.setFiles("upload", this.mainDir +  "/" + this.docDir);
			up.finished();
			this.docServerDir = up.send();
		}
	}

	/*
	 * 设置远程的数据
	 */
	void setVersionData() throws IOException {
		String url  = this.baseHost + "/breeze.brz";
		String service = "frameversion.uploadOne";
		HashMap<String,Object>param = new HashMap<String,Object>();
		param.put("version", this.version);
		param.put("type", this.type);
		param.put("versionDesc", this.versionDesc);
		param.put("contentUrl", this.contentServerDir);
		if (this.patchDir!=null){
			param.put("patchUrl", this.patchServerDir);
		}
		param.put("docUrl", this.docServerDir);
		
		BreezeSystemTools.callRemoteService(url,service,param);
	}

	public static void main(String[] args) throws Exception {
		String mainDir = args[0];
		String versionDir = args[1];
		String baseHost = args[2];
		UploadOneVersion uov = new UploadOneVersion(mainDir, versionDir,
				baseHost);
		uov.readVersion();
		uov.uploadFile();
		uov.setVersionData();
	}

	// 下面是get函数，只是为了测试用的
	public String getMainDir() {
		return mainDir;
	}

	public String getVersionFile() {
		return versionFile;
	}

	public String getContentDir() {
		return contentDir;
	}

	public String getPatchDir() {
		return patchDir;
	}

	public String getDocDir() {
		return docDir;
	}

	public String getVersion() {
		return version;
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public String getContentServerDir() {
		return contentServerDir;
	}

	public String getPatchServerDir() {
		return patchServerDir;
	}

	public String getDocServerDir() {
		return docServerDir;
	}

	public String getType() {
		return type;
	}
	
	

}
