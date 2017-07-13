package com.breezefw.framework.template;


import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breezefw.shell.ServiceDescTools;


public class CheckerItem extends TemplateItemBase {
	public static class CheckInfo extends TemplateItemBase{
		
		@FieldDesc(desc = "校验对象", title = "校验对象", valueRange = "",type="Text")
		public String checkerObj;
		@FieldDesc(desc = "校验类名，从checker包里拿出来的check类", title = "校验类名", valueRange = "[{'regChecker':'regChecker','NotNull':'NotNull','attrChecker':'attrChecker','sqlChecker':'sqlChecker'}]",type="Select")
		public String checkerName;
		@FieldDesc(desc = "校验参数判断的参数", title = "校验参数", valueRange = "",type="Text")
		public String checkerParam;
		@FieldDesc(desc = "校验失败后返回的结果码", title = "校验失败结果码", valueRange = "",type="Text")
		public int failCode;
	}
	
	private CheckInfo[] checkerInfo;
	
	public  CheckInfo[] getCheckInfo(){
		return checkerInfo;
	}	
	
	public static void main(String[]args){
		System.out.println(ServiceDescTools.parserItemDesc(CheckerItem.class));
	}
}
