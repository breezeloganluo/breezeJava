package com.breeze.framwork.netserver.filter;

import java.io.File;
import com.breeze.support.tools.FileTools;
import com.google.gson.Gson;

public class BreezeFilterBean {
	private String serviceName;
	private String before;
	private String after;
	
	public static BreezeFilterBean createFilter(File in){
		String content = FileTools.readFile(in, "UTF-8");
		Gson gson = new Gson();
		BreezeFilterBean fc = gson.fromJson(content, BreezeFilterBean.class);
		return fc;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}	
}
