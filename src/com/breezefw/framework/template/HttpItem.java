package com.breezefw.framework.template;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLParser;

public class HttpItem extends TemplateItemBase {
	@FieldDesc(desc = "发送http的协议类型是http还是https", type="Select",title = "协议类型", valueRange = "[{'http':'http'},{'https':'https'}]")
	private String type;
	@FieldDesc(desc = "发送http请求的url的内容，支持BTL表达式,注意使用BTL的Org函数", title = "url值", valueRange = "")
	private String url;
	@FieldDesc(desc = "http请求的post的数据，如果为空，则为get请求，否则是post请求，如果对应的地址有内容，且是直接的值，那么就post这个值，否则按照json对象进行解析，post这个json值。如果设置成--post空数据,注意，这个值支持BTL表达式,注意使用BTL的Org函数", title = "post设置", type="TextArea",valueRange = "")
	private String postData;
	@FieldDesc(desc = "http请求的结果存放路径", title = "结果存放路径", valueRange = "")
	private String resultPath;
	@FieldDesc(desc = "http请求的选择结果类型，是直接的结果还是要进行json或者xml解析", title = "结果类型",type="Select", valueRange = "[{'value':'value'},{'json':'json'},{'xml':'xml'}]")
	private String resultType;
	
	private BTLExecutor urlExec;
	private BTLExecutor postDataExec;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPostData(BreezeContext root) {
		if (this.postData == null){
			return null;
		}
		return this.postDataExec.execute(new Object[]{root}, new ArrayList());
	}
	public void setPostData(String postData) {
		this.postData = postData;
	}
	public String getResultPath() {
		return resultPath;
	}
	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	
	
	
	public BTLExecutor getUrlExec() {
		return urlExec;
	}
	public void setUrlExec(BTLExecutor urlExec) {
		this.urlExec = urlExec;
	}

	
	
	public BTLExecutor getPostDataExec() {
		return postDataExec;
	}
	public void setPostDataExec(BTLExecutor postDataExec) {
		this.postDataExec = postDataExec;
	}
	
	
	@Override
	public void loadingInit()
	{
		this.urlExec = BTLParser.INSTANCE("sql").parser(this.url);
		this.postDataExec = BTLParser.INSTANCE("sql").parser(this.postData);
	}
	
}
