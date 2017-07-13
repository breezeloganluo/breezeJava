package com.breezefw.client.shell;

import java.io.IOException;
import java.io.InputStream;

import com.breezefw.client.service.ProjectMgr;
import com.breezefw.client.tools.BreezeSystemTools;

public class ProjectGetting {
	public static void main(String[] args) throws IOException{
		String remoteHost = args[0];
		String projectBase = args[1];
		String loadingObj = args[2];
		String paramName = "functionName";
		String serviceName = "getFunctionDepend";
		ProjectMgr.getIn().getFileFromServer(remoteHost, projectBase, loadingObj, paramName, serviceName);
		System.out.println("client upgrade....");
		try{
			String url = remoteHost + "upload/client/build.jar";
			InputStream in = BreezeSystemTools.getResponseStream(url);
			BreezeSystemTools.copyFile(projectBase + "../build.jar", in);
		}catch(Exception e){
			System.out.println("no upgrade....");
		}
	}
}
