package com.breezefw.framework.template;



import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breezefw.shell.ServiceDescTools;


public class JsonTestItem extends TemplateItemBase {
	
	public static class JsonTest extends TemplateItemBase
	{
		@FieldDesc(desc = "名称desc", title = "名称title", valueRange = "")
		public String titleString;
		@FieldDesc(desc = "内容请填写完成", title = "内容", valueRange = "")
		public String descString;
		public String msgString;
	}
	
	private JsonTest[] jsonTest;
	public JsonTest[] getJsonTest()
	{
		return jsonTest;
	}
	
	public static void main(String args[]){
		BreezeContext bc = new BreezeContext();
		BreezeContext b = ServiceDescTools.parserItemDesc(JsonTestItem.class);
		bc.combindContext(b);
		System.out.println("---------->>>flowContent:\n"+bc+"\n\n\n");
		String flowContent = ContextTools.getJsonString(bc, null);
		System.out.println("---------->>>flowContent:"+flowContent);
	}
}
