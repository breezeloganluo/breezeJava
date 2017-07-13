package com.breezefw.framework.template;

import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;

public class ContextResultItem extends TemplateItemBase {
	@FieldDesc(desc = "在页面方法模式下，转向结果jsp的url可以不填，在客户端doServer模式请求情况下，该字段无效", title = "结果模板url", valueRange = "")
	private String templateUrl;
	@FieldDesc(desc = "在页面方法模式下，页面调用失败后的转向jsp页面", title = "失败结果模板url", valueRange = "")
	private String templateErrorUrl;
	
	public String getTemplateUrl(){		
		return this.templateUrl == null ?"":this.templateUrl;
	}
	
	public String getTemplateErrorUrl(){
		return this.templateErrorUrl;
	}
}
