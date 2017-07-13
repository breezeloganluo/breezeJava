package com.breezefw.framework.template;

import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breezefw.shell.ServiceDescTools;

public class ForwardResultItem extends TemplateItemBase {
	public static class ForwardInfo{
		public String index;
		public String jspName;
	}
	public static class ResultInfo{
		public String key;
		public String value;
	}
	private ForwardInfo[] forwardInfos;
	private ResultInfo[] resultInfos;

	public ForwardInfo[] getForwardInfos() {
		return forwardInfos;
	}
	
	public ResultInfo[] getResultInfos() {
		return resultInfos;
	}
	
	public static void main(String[]args){
		ServiceDescTools.parserItemDesc(CheckerItem.class);
	}
}
