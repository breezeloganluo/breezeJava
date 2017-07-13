package com.breezefw.framework.workflow;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breeze.init.LoadClasses;
import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLFunctionAbs;
import com.breezefw.ability.btl.BTLParser;
import com.breezefw.ability.http.HTTP;
import com.breezefw.framework.template.HttpItem;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;


public class HttpSendFlow extends WorkFlowUnit {
	private static final Logger log = Logger
			.getLogger("com.breezefw.framework.workflow.QueryFlow");
	private final static String FLOWNAME = "httpFlow";

	@Override
	public String getName() {
		return FLOWNAME;
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { new CommTemplateItemParser(
				FLOWNAME, HttpItem.class) };
		return result;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias,
			int lastResult) {
		// 获取根上下文信息
		BreezeContext root = context;

		if (log.isLoggable(Level.FINE)) {
			log.fine("go Process [" + this.getName() + "]lastResult["
					+ lastResult + "]");
		}
		ResultSet result = null;
		try {
			HttpItem items = (HttpItem) this.getItem(context, st, FLOWNAME);
			// 获取URL
			BTLExecutor execs = items.getUrlExec();
			String url = execs.execute(new Object[] { root }, null);
			log.fine("sending url :"+url);
			// 处理post参数,也要确定是post还是get
			boolean isGet = false;
			String postData = "";
			
			if (null == items.getPostData(root)
					|| "".equals(items.getPostData(root).trim())) {
				isGet = true;
			}
			
			else if (!"--".equals(items.getPostData(root))) {
				BreezeContext d = root.getContextByPath(items.getPostData(root));
				if (d == null || d.isNull()) {
					postData = items.getPostData(root);
				} else if (BreezeContext.TYPE_DATA == d.getType()) {
					postData = d.getData().toString();
				} else {
					postData = this.getPostDataFromContext(d);
				} 
			}
			// 发送请求
			String resultHttp = null;
			log.fine("postData is:" + postData);
			if ("https".equals(items.getType())) {
				log.fine("use https to send!");
				resultHttp = HTTP.getInc().sendHttpsPost("https://"+url, postData);
			} else {
				if (isGet) {
					resultHttp = HTTP.getInc().sendHttpGet("http://"+url, null);
				} else {
					resultHttp = HTTP.getInc().sendHttpPost("http://"+url, postData);
				}
			}
			log.fine("result from remote is:\n"+resultHttp);

			// 设置结果
			if ("json".equals(items.getResultType())){
				BreezeContext resultCtx = ContextTools.getBreezeContext4Json(resultHttp);
				root.setContextByPath(items.getResultPath(), resultCtx);
			}else if ("xml".equals(items.getResultType())){
				//这个代码还不支持，但是会支持的
				BreezeContext resultCtx = ContextTools.getBreezeContext4xml(resultHttp, null, null);
				if (resultCtx == null || resultCtx.isEmpty()){
					log.severe("parser xml from http result fail,return is empty");
				}
				root.setContextByPath(items.getResultPath(), resultCtx);
			}
			else{
				root.setContextByPath(items.getResultPath(), new BreezeContext(resultHttp));
			}

		} catch (Exception e) {
			log.severe("", e);
			return 999;
		}
		return 0;
	}

	String getPostDataFromContext(BreezeContext data) {
		String result = ContextTools.getJsonString(data, null);
		return result;
	}
	
	public static void main(String arg[]){
		BTLParser.init("sql");
		// 获取所有的初始化资源
		ArrayList<SqlFunctionAbs> initList = LoadClasses.createObject(
				"com.breezefw.framework.workflow.sqlbtlfun",
				SqlFunctionAbs.class);
		for (BTLFunctionAbs btl : initList) {
			BTLParser.INSTANCE("sql").addFunction(btl);
		}

		// 获取所有的初始化资源
		initList = LoadClasses.createObject("com.breezefw.service",
				SqlFunctionAbs.class);
		for (BTLFunctionAbs btl : initList) {
			BTLParser.INSTANCE("sql").addFunction(btl);
		}
		
		
		
		final HttpItem item = new HttpItem();
		item.setUrl("qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=P_tmDcgV-d4bGi2LiEUT9kD2SGnB0zUWn24RyOXXslMZ2zwlwGzAiF6DHzybYMQq&code=8c3418b1907b1a5d21ce21c4f21c32fb&agentid=2");
		item.setType("https");
		item.setResultPath("data");
		item.loadingInit();
		
		HttpSendFlow hsf = new HttpSendFlow(){
			public TemplateItemBase getItem(BreezeContext context,ServiceTemplate st, String FLOWNAME){
				return item;
			}
		};
		
		BreezeContext root = new BreezeContext();
		hsf.process(root, null, null, 0);
		System.out.println("root:"+root);
		
	}

}
