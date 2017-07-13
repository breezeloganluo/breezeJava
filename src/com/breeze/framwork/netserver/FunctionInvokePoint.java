package com.breeze.framwork.netserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.databus.RequestContext;
import com.breeze.framwork.databus.SessionContext;
import com.breeze.framwork.netserver.process.ServerProcessManager;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.framwork.servicerg.AllServiceTemplate;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.thread.ThreadProcess;
/**
 * 一个函数的service调用方式
 * @author Loganluo
 * @version 0.01 2015-03-20罗光瑜增加了通过cfg配置确定函数调用时是否打开日志
 *
 */
public class FunctionInvokePoint {
	private static FunctionInvokePoint instance;
	private static final Object lock = new Object();
	public static final String SERVICETRACEPATH = "servicetrace";
	
	

	private FunctionInvokePoint(){};
	public static FunctionInvokePoint getInc() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new FunctionInvokePoint();
				}
			}
		}
		return instance;
	}

	private Logger log = Logger
			.getLogger("com.weiguang.framework.netserver.FunctionInvokePoint");

	/**
	 * 2013-10-11 罗光瑜修改，增加了一个外部传入的context而直接调用的方法
	 * 根据传入的BreezeContext来调用对应service函数
	 * root.setContext("_FlowName", new BreezeContext(flowName));这个没有设置，真正有必要的时候在这个函数中
	 * 设置一下
	 * @param serviceName
	 * @param param
	 * @return
	 */
	@SuppressWarnings("finally")
	public BreezeContext breezeInvokeUsedAllCtx(String serviceName, BreezeContext root) {
		
		try {
			ThreadProcess.Info.set(serviceName);
			ServiceTemplate t = AllServiceTemplate.INSTANCE
					.getTemple(serviceName);
			if (t == null) {
				String msg = "can not get the temple:" + serviceName;
				throw new RuntimeException(msg);
			}
			String flowName = t.getServerName();
			log.fine("service ["+serviceName+"] will be called in FunctionInvokePoint!");
			ServerProcessManager.INSTANCE.getServer(flowName).process(root, t);
		} catch (Exception e) {
			log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
			throw new RuntimeException(e);
		} finally {
			return root;
		}
	}
	
	
	/**
	 * 2013-10-11 罗光瑜修改，增加了一个外部传入的context而直接调用的方法
	 * 根据传入的BreezeContext来调用对应service函数
	 * root.setContext("_FlowName", new BreezeContext(flowName));这个没有设置，真正有必要的时候在这个函数中
	 * 设置一下
	 * @param serviceName
	 * @param param
	 * @return
	 */
	@SuppressWarnings("finally")
	public String breezeInvokeUsedAllCtxJsonResult(String serviceName, BreezeContext root) {
			BreezeContext resultCtx = this.breezeInvokeUsedAllCtx(serviceName,root);
			String resultStr = ContextTools.getJsonString(root, new String[] { "code", "data" });
			return resultStr;
	}

	/**
	 * 罗光瑜2013-11-2日添加
	 * @param serviceName
	 * @param _R 仅仅是 _R里面的参数信息
	 * @return
	 */
	public BreezeContext breezeInvokeUsedCtxAsParam(String serviceName, BreezeContext _R) {
		return this.breezeInvokeUsedCtxAsParam(serviceName, _R,null, null);
	}
	
	@SuppressWarnings("finally")
	public BreezeContext breezeInvokeUsedCtxAsParam(String serviceName, BreezeContext _R,
			HttpServletRequest request, HttpServletResponse response) {
		BreezeContext root = null;
		try {
			String charset = "UTF-8";
			if (Cfg.getCfg() != null) {
				charset = Cfg.getCfg().getString("DefalutFileChartset");
			}
			if (request != null) {
				request.setCharacterEncoding(charset);
			}
			
			ContextMgr.initRootContext();
			root = ContextMgr.getRootContext();

			root.setContext("_Req", new BreezeContext(request));
			root.setContext("_Rsp", new BreezeContext(response));
			root.setContext("_ServiceName", new BreezeContext(serviceName));			
			root.setContext("_G", ContextMgr.global);
			root.setContext("_R", _R);
			if (request != null) {
				root.setContext("_S", new SessionContext(request));
			}
			this.breezeInvokeUsedAllCtx(serviceName, root);
		} catch (Exception e) {
			log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
			throw new RuntimeException(e);
		} finally {
			return root;
		}
	}

	public BreezeContext breezeInvokeUsedCtxAsParam(String _packageName,String _serviceName, BreezeContext _R) {
		return this.breezeInvokeUsedCtxAsParam(_packageName,_serviceName, _R,null, null);
	}
	public BreezeContext breezeInvokeUsedCtxAsParam(String _packageName, String _serviceName,
			BreezeContext _R, HttpServletRequest request,
			HttpServletResponse response) {
		String serviceName = _packageName + '.' + _serviceName;
		if (_packageName == null) {
			serviceName = _serviceName;
		}
		return this.breezeInvokeUsedCtxAsParam(serviceName, _R, request, response);
	}

	public BreezeContext breezeInvokeUseRequestAsParam(String _packageName, String _serviceName,
			HttpServletRequest request, HttpServletResponse response) {
		return this.breezeInvokeUsedCtxAsParam(_packageName, _serviceName,
				new RequestContext(request), request, response);
	}

	public BreezeContext breezeInvokeUseRequestAsParam(String serviceName,
			HttpServletRequest request, HttpServletResponse response) {
		return this.breezeInvokeUsedCtxAsParam(serviceName, new RequestContext(
				request), request, response);
	}

}
