package com.breezefw.framework.netserver;

import com.breeze.framwork.netserver.process.ServerProcessManager;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.*;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.breeze.framwork.servicerg.*;
import com.breeze.support.cfg.*;

/**
 * 通用的Request的Breeze的执行函数，这个函数有固定的uri结构，结构如下： /package/service/ext.xxx
 * 头两个是包名，后面是扩展部部分给程序扩展使用，放入到_file上下文中
 * 
 * @author Logan
 */
public class RequestBreezePoint extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ATTRIBUTENAME_SERVICENAME = "ServiceName";
	private Logger log = Logger
			.getLogger("com.breeze.framwork.netserver.RequestBreezePoint");

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 转到doPost上
		this.doPost(request, response);
	}

	/**
	 * 结果处理： 后台逻辑处理完后，页面将转向到上下文地址为template路径所指向的jsp处理对应的内容
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String threadSignal = null;
		try {
			// 设置字符集
			String charset = Cfg.getCfg().getString("DefalutFileChartset");
			request.setCharacterEncoding(charset);
			response.setCharacterEncoding(charset);

			// 通过forward转向到这个服务中去
			// 获取标示
			threadSignal = request.getParameter("threadSignal");
			if (threadSignal != null) {
				log.setTreadSignal(threadSignal);
			}

			String urlCtx = this.getServletContext().getContextPath();
			// 从request中获取URI
			String uri = request.getRequestURI();
			uri = uri.substring(uri.indexOf(urlCtx)+urlCtx.length()+1);
			// 根据/分成3截
			String[] uriArr = uri.split("/");
			if (uriArr.length != 3) {
				String excStr = "uri format not corret! uri is:" + uri;
				throw new RuntimeException(excStr);
			}
			// 从全局的AllServiceCfigRegister中获取要处理的服务ServiceOperParameter,server名称
			String packageName = uriArr[0];
			String serviceName = packageName + '.' + uriArr[1];
			ServiceTemplate t = AllServiceTemplate.INSTANCE
					.getTemple(serviceName);
			if (t == null) {
				String msg = "can not get the temple:" + serviceName;
				log.severe(msg);
				throw new RuntimeException(msg);
			}
			String flowName = t.getServerName();
			String fileName = uriArr[2];

			log.fine("begin servce[" + serviceName + "]");
			// 初始化上下文
			ContextMgr.initRootContext();
			BreezeContext root = ContextMgr.getRootContext();
			root.setContext("_Req", new BreezeContext(request));
			root.setContext("_Rsp", new BreezeContext(response));
			root.setContext("_ServiceName", new BreezeContext(serviceName));
			root.setContext("_FlowName", new BreezeContext(flowName));
			root.setContext("_G", ContextMgr.global);
			root.setContext("_R", new RequestContext(request));
			root.setContext("_S", new SessionContext(request));
			root.setContext("_F", new BreezeContext(fileName));
			ServerProcessManager.INSTANCE.getServer(flowName).process(root, t);
			// 处理结果
			BreezeContext templatePathCtx = root.getContext("template");
			if (templatePathCtx == null || templatePathCtx.isNull()) {
				String resultStr = ContextTools.getJsonString(root, new String[]{"code","data"});
				log.fine("result is:\n" + resultStr);
				response.getWriter().write(resultStr);
				return;
			}
			String template = templatePathCtx.getData().toString();
			// forward 的时候将所有data的值转到request的attribure中
			if (log.isLoggable(Level.FINE)) {
				log.fine("forwarding to template:" + template
						+ " the root ctx is:");
				for (String key : root.getMapSet()) {
					if (key.startsWith("_")) {
						continue;
					}
					log.fine("root[" + key + "]" + root.getContext(key));
				}
			}
			// 转换attribute
			BreezeContext dataCtx = root.getContext("data");
			if (dataCtx != null && !dataCtx.isNull() && dataCtx.getType() == BreezeContext.TYPE_MAP) {
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
			log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
			throw new RuntimeException(e);
		} finally {
			if (threadSignal != null) {
				log.removeThreadSignal();
			}
		}
	}

	/**
	 * @roseuid 4B94F72F0399
	 * @J2EE_METHOD -- ServiceProcessEntrance
	 */
	public RequestBreezePoint() {
	}
	
	public static void main(String[] args){
		String a= "aa/aa/aa.bf";
		String[] arr= a.split("/");
		for (int i=0;i<arr.length;i++){
			System.out.println(arr[i]);
		}
	}
}