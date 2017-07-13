package com.breezefw.framework.netserver;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
/**
 * 这个类是扩展上传组件类，和普通的BreezeClient类一样，他接受上传请求，然后把上传作为一个Breeze的入口进行驱动
 * 这里约定的参数就是serviceName,package,还有param三个，其他视作和上传相关了
 */
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.databus.SessionContext;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.upload.UploadHttpRequest;
import com.breezefw.framework.init.service.cfg.CfgContext;

/**
 * 
 * @author Logan
 * @version 0.02罗光瑜修改，这次修改还原原来的实现，即对外的输出由本函数实现，不放在contentResult那边
 */
public class UploadBreezePoint extends HttpServlet {
	private Logger log = Logger.getLogger("com.breeze.framwork.netserver.UploadBreezePoint");

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 转到doPost上
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String threadSignal = null;
		try {
			request.setCharacterEncoding(Cfg.getCfg().getString("DefalutFileChartset"));
			response.setCharacterEncoding("utf-8");
			// 设置字符集
			UploadHttpRequest.Upload re = UploadHttpRequest.createRequest(request, 100000000L);

			threadSignal = re.getParameter("threadSignal");
			if (threadSignal != null) {
				log.setTreadSignal(threadSignal);
			}

			String serviceName = re.getParameter("serviceName");
			log.fine("serviceName:" + serviceName);
			String packageName = re.getParameter("packageName");
			log.fine("packageName:" + packageName);
			String param = re.getParameter("param");

			// 初始化上下文
			ContextMgr.initRootContext();
			BreezeContext root = ContextMgr.getRootContext();
			root.setContext("_Req", new BreezeContext(request));
			root.setContext("_Rsp", new BreezeContext(response));
			root.setContext("_UploadReq", new BreezeContext(re));
			root.setContext("_G", ContextMgr.global);

			root.setContext("_S", new SessionContext(request));
			root.setContext("_ServiceName", new BreezeContext(serviceName));
			root.setContext("_CFG", CfgContext.CFGCONTEXT);
			root.setContext("_ContextResult", new BreezeContext("output"));
			if (param != null) {
				root.setContext("_R", ContextTools.getBreezeContext4Json(param));
			}

			// 调用服务内容
			FunctionInvokePoint.getInc().breezeInvokeUsedAllCtx(packageName + "." + serviceName, root);

			// 处理结果
			BreezeContext templatePathCtx = root.getContext("template");
			if (templatePathCtx == null || templatePathCtx.isNull() || templatePathCtx.getData() == null || "".equals(templatePathCtx.toString().trim())) {
				String resultStr = ContextTools.getJsonString(root, new String[]{"code","data"});
				log.fine("result is:\n" + resultStr);
				response.getWriter().write(resultStr);
				return;
			}
			String template = templatePathCtx.getData().toString();
			// forward 的时候将所有data的值转到request的attribure中
			if (log.isLoggable(Level.FINE)) {
				log.fine("forwarding to template:" + template + " the root ctx is:");
				for (String key : root.getMapSet()) {
					if (key.startsWith("_")) {
						continue;
					}
					log.fine("root[" + key + "]" + root.getContext(key));
				}
			}
			// 转换attribute
			BreezeContext dataCtx = root.getContext("data");
			if (dataCtx != null && !dataCtx.isNull()) {
				for (String key : dataCtx.getMapSet()) {
					BreezeContext valueCtx = dataCtx.getContext(key);
					if (valueCtx.getType() == BreezeContext.TYPE_DATA) {
						request.setAttribute(key, valueCtx.getData());
					} else {
						request.setAttribute(key, valueCtx);
					}
				}
			}
			RequestDispatcher disp = request.getRequestDispatcher(template);
			disp.forward(request, response);
		} catch (Exception e) {
			log.severe("Exception in upload", e);
			throw new RuntimeException(e);
		} finally {
			if (threadSignal != null) {
				log.removeThreadSignal();
			}
		}
	}
}
