package com.breezefw.framework.template;

import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;

public class ServiceCallItem extends TemplateItemBase {
	@FieldDesc(desc = "call servcie的名称类型", title = "serviceNameType", valueRange = "[{'serviceName':0,'servicePath':1}]", type = "Select")
	private String serviceNameType;
	@FieldDesc(desc = "call servcie的名称", title = "serviceName", valueRange = "")
	private String serviceName;
	@FieldDesc(desc = "当业务名称没找到时返回的结果码,默认不填就是0", title = "serviceCallNotFoundResult", valueRange = "")
	private String serviceNameNoFoundResult;
	@FieldDesc(desc = "callService的三种模式：sync(默认模式),sync_context同步，且独立上下文，async异步", title = "callMod", valueRange = "")
	private String callMode;

	/**
	 * @return the serviceNameNoFoundResult
	 */
	public int getServiceNameNoFoundResult() {
		if (this.serviceNameNoFoundResult == null
				|| "".equals(this.serviceNameNoFoundResult)) {
			return 0;
		} else {
			return Integer.parseInt(this.serviceNameNoFoundResult);
		}
	}

	public String getServiceNameType() {
		return serviceNameType;
	}

	public void setServiceNameType(String serviceNameType) {
		this.serviceNameType = serviceNameType;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getCallMode(){
		if (this.callMode == null){
			return "callMode";
		}
		else{
			return this.callMode;
		}
	}

}
