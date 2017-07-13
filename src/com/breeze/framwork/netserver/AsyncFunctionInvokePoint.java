package com.breeze.framwork.netserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.RequestContext;
import com.breeze.framwork.databus.SessionContext;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.support.cfg.Cfg;

public class AsyncFunctionInvokePoint implements Runnable {
	private String asyncServiceName;
	private BreezeContext asyncRoot;
	private AsyncCallResult asyncResult;
	
	private static AsyncFunctionInvokePoint instance;
	private static final Object lock = new Object();
	
	

	private AsyncFunctionInvokePoint(){};
	public static AsyncFunctionInvokePoint getInc() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new AsyncFunctionInvokePoint();
				}
			}
		}
		return instance;
	}
	
	
	public void breezeInvokeUsedAllCtx(String serviceName, BreezeContext root,AsyncCallResult result) {
		AsyncFunctionInvokePoint inc = new AsyncFunctionInvokePoint();
		inc.asyncServiceName = serviceName;
		inc.asyncRoot = root;
		inc.asyncResult = result;
		
		Thread t = new Thread(inc);
		t.start();
	}
	
	/**
	 * 罗光瑜2013-11-2日添加
	 * @param serviceName
	 * @param _R 仅仅是 _R里面的参数信息
	 * @return
	 */
	public void breezeInvokeUsedCtxAsParam(String serviceName, BreezeContext _R,AsyncCallResult result) {
		this.breezeInvokeUsedCtxAsParam(serviceName, _R,null, null,result);
	}
	

	public void breezeInvokeUsedCtxAsParam(String serviceName, BreezeContext _R,
			HttpServletRequest request, HttpServletResponse response,AsyncCallResult result) {
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
			this.breezeInvokeUsedAllCtx(serviceName, root,result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	public void breezeInvokeUsedCtxAsParam(String _packageName,String _serviceName, BreezeContext _R,AsyncCallResult result) {
		this.breezeInvokeUsedCtxAsParam(_packageName,_serviceName, _R,null, null,result);
	}
	public void breezeInvokeUsedCtxAsParam(String _packageName, String _serviceName,
			BreezeContext _R, HttpServletRequest request,
			HttpServletResponse response,AsyncCallResult result) {
		String serviceName = _packageName + '.' + _serviceName;
		if (_packageName == null) {
			serviceName = _serviceName;
		}
		this.breezeInvokeUsedCtxAsParam(serviceName, _R, request, response,result);
	}

	public void breezeInvokeUseRequestAsParam(String _packageName, String _serviceName,
			HttpServletRequest request, HttpServletResponse response,AsyncCallResult result) {
		this.breezeInvokeUsedCtxAsParam(_packageName, _serviceName,
				new RequestContext(request), request, response,result);
	}

	public void breezeInvokeUseRequestAsParam(String serviceName,
			HttpServletRequest request, HttpServletResponse response,AsyncCallResult result) {
		this.breezeInvokeUsedCtxAsParam(serviceName, new RequestContext(
				request), request, response,result);
	}
	
	
	@Override
	public void run() {
		BreezeContext result = FunctionInvokePoint.getInc().breezeInvokeUsedAllCtx(this.asyncServiceName,this.asyncRoot);
		if (this.asyncResult!=null){
			this.asyncResult.result(result);
		}
	}
}
