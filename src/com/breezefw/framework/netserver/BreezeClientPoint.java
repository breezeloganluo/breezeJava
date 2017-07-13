package com.breezefw.framework.netserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.db.COMMDB;
import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.databus.SessionContext;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.framwork.netserver.process.ServerProcess;
import com.breeze.framwork.netserver.process.ServerProcessManager;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.framwork.servicerg.AllServiceTemplate;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.thread.ThreadProcess;
import com.breeze.support.tools.FileTools;
import com.breezefw.framework.init.service.cfg.CfgContext;
import com.breezefw.shell.LogService;
/**
 * 
 * @author Logan
 * @version 0.02罗光瑜修改，这次修改还原原来的实现，即对外的输出由本函数实现，不放在contentResult那边
 */
public class BreezeClientPoint extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ATTRIBUTENAME_SERVICENAME = "ServiceName";
	private Logger log = Logger.getLogger("com.breeze.framwork.netserver.ServiceProcessEntrance");

	/**
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 * @roseuid 4B9209D90204
	 * @J2EE_METHOD -- doGet Called by the server (via the service method) to
	 *              allow a servlet to handle a GET request. The servlet
	 *              container must write the headers before committing the
	 *              response, because in HTTP the headers must be sent before
	 *              the response body. The GET method should be safe and
	 *              idempotent. If the request is incorrectly formatted, doGet
	 *              returns an HTTP 'Bad Request' message.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 转到doPost上
		this.doPost(request, response);
	}

	/**
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 * @roseuid 4B9209D90252
	 * @J2EE_METHOD -- doPost Called by the server (via the service method) to
	 *              allow a servlet to handle a POST request. The HTTP POST
	 *              method allows the client to send data of unlimited length to
	 *              the Web server a single time and is useful when posting
	 *              information such as credit card numbers. If the HTTP POST
	 *              request is incorrectly formatted, doPost returns an HTTP
	 *              'Bad Request' message.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String threadSignal = null;
		try {
			// 设置字符集
			String charset = Cfg.getCfg().getString("DefalutFileChartset");
			request.setCharacterEncoding(charset);

			String paramData = FileTools.readFile(request.getInputStream(), charset);

			//提取模拟数据，如果有模拟参数则直接跳转forward
			String mokeSignal = request.getParameter("mokeSignal");
			// 获取标示
			threadSignal = request.getParameter("threadSignal");

			if (threadSignal != null) {
				mokeSignal = null;
				log.setTreadSignal(threadSignal);
			}
			Pattern p = Pattern.compile("^data\\s*=\\s*([^&]+)");
			Matcher m = p.matcher(paramData);
			if (m.find()) {
				paramData = m.group(1);
				paramData = URLDecoder.decode(paramData, "utf-8");
			}

			// 如果不存在，尝试着将所有post的数据进行
			if (paramData == null) {
				log.severe("data is not fount");
				return;
			}

			if (log.isLoggable(Level.FINE)) {
				log.fine("begin one request,get param data is:\n" + paramData);
			}

			BreezeContext paramContext = ContextTools.getBreezeContext4Json(paramData);
			if (paramContext == null) {
				log.severe("can not resovel breeze context");
				return;
			}

			if (paramContext.getType() != BreezeContext.TYPE_ARRAY) {
				log.severe("Breeze context type not right! not array");
				return;
			}

			BreezeContext result = new BreezeContext();
			// 下面开始循环，处理每一个服务。
			response.setContentType("text/html;charset=" + charset);
			// 处理跨域问题
			String crosset = Cfg.getCfg().getString("Access-Control-Allow-Origin");
			if (crosset != null) {
				response.setHeader("Access-Control-Allow-Origin", crosset);
			}
			response.getWriter().println('[');
			for (int i = 0; i < paramContext.getArraySize(); i++) {
				if (i > 0) {
					response.getWriter().println(',');
				}
				BreezeContext sObj = paramContext.getContext(i);
				String packageName = sObj.getContext("package").getData().toString();
				String serviceName = sObj.getContext("name").getData().toString();
				String sName = serviceName;
				if (!"".equals(packageName)) {
					serviceName = packageName + "." + serviceName;
				}
				log.fine("begin servce[" + serviceName + "]");
				ThreadProcess.Info.set(serviceName);
				ServiceTemplate t = AllServiceTemplate.INSTANCE.getTemple(serviceName);
				if (t == null) {
					String msg = "can not get the temple:" + serviceName;
					log.severe(msg);
					throw new RuntimeException(msg);
				}
				String flowName = t.getServerName();

				// 初始化上下文
				ContextMgr.initRootContext();
				BreezeContext root = ContextMgr.getRootContext();
				root.setContext("_Req", new BreezeContext(request));
				root.setContext("_Rsp", new BreezeContext(response));
				root.setContext("_G", ContextMgr.global);
				root.setContext("_R", sObj.getContext("param"));
				root.setContext("_S", new SessionContext(request));
				root.setContext("_ServiceName", new BreezeContext(serviceName));
				root.setContext("_FlowName", new BreezeContext(flowName));
				root.setContext("_CFG", CfgContext.CFGCONTEXT);
				
				

				// 调用服务内容
				if (mokeSignal == null){
					FunctionInvokePoint.getInc().breezeInvokeUsedAllCtx(serviceName, root);
					response.getWriter().println(ContextTools.getJsonString(root, new String[]{"code","data"}));
				}else{
					request.setAttribute("serviceName", sName);
					request.setAttribute("packageName", packageName);
					request.setAttribute("secene", mokeSignal);
					request.getRequestDispatcher("/manager_auxiliary/server/moke/getMokeService.jsp").include(request, response);
				}
			    //如果是加标识的，就录入调用数据
				if (threadSignal != null){
					LogService.log(sName, packageName, threadSignal, root);
				}
			}
			response.getWriter().println(']');
			response.getWriter().flush();
		} catch (Exception e) {
			log.severe("enterPoint Exception:", e);
			throw new RuntimeException(e);
		} finally {
			if (threadSignal != null) {
				log.removeThreadSignal();
			}
		}
	}
}
