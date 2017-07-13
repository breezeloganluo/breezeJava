package com.breezefw.shell;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.tools.FileTools;

/**
 * 这是一个对外提供breezecall后然后进行调用的标签类
 * 
 * @author Loganluo
 *
 */
public class BreezeFunctioinCallTag extends SimpleTagSupport {
	private static Object lock = new Object();
	private static Logger log = null;

	private String servicename = null;
	private String btl = null;
	private String param = null;
	private boolean isChildCall = false;

	private BreezeContext orgResult;
	private BreezeContext resultData;
	private int resultIdx = 0;
	private String threadSignal = null;
	private String simulateFile = "default";

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public String getBtl() {
		return btl;
	}

	public void setBtl(String btl) {
		this.btl = btl;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public BreezeContext getResultData() {
		return resultData;
	}

	public void setResultData(BreezeContext resultData) {
		this.resultData = resultData;
	}

	public int getResultIdx() {
		return resultIdx;
	}

	public void setResultIdx(int resultIdx) {
		this.resultIdx = resultIdx;
	}

	public String getSimulateFile() {
		return simulateFile;
	}

	public void setSimulateFile(String simulateFile) {
		this.simulateFile = simulateFile;
	}

	/**
	 * 设置是子类调用，就说明该类被子类实例所使用
	 */
	public void setChildCall() {
		this.isChildCall = true;
	}

	public void setJspBody(JspFragment jspBody) {
		// 如果是子类调用，直接处理
		if (this.isChildCall) {
			super.setJspBody(jspBody);
			return;
		}
		// 惰性初始化日志类
		if (log == null) {
			synchronized (lock) {
				if (log == null) {
					log = Logger
							.getLogger("com.breezefw.shell.BreezeFunctioinCallTag");
				}
			}
		}

		// 准备相关的参数
		PageContext pageContext = (PageContext) this.getJspContext();
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();

		this.threadSignal = request.getParameter("threadSignal");
		if (this.threadSignal != null) {
			log.setTreadSignal(this.threadSignal);
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("call " + this.servicename + "(" + this.param + ")");
		}

		// 结果信息清0
		this.resultIdx = 0;
		// 判断param参数，如果有的话设置到request.attribute中
		if (this.param != null) {
			// 转换成BreezeContext
			BreezeContext bx = ContextTools.getBreezeContext4Json(this.param);
			// 第一层直接设置，第二层就转换成为BreezeContext
			if (bx.getType() == BreezeContext.TYPE_MAP) {
				for (String k : bx.getMapSet()) {
					BreezeContext valueCtx = bx.getContext(k);
					if (valueCtx.getType() == BreezeContext.TYPE_DATA) {
						request.setAttribute(k, valueCtx.toString());
					} else {
						request.setAttribute(k, valueCtx);
					}
				}
			}
		}
		// 进行service调用，并把结果存放到基本对象中，特殊值判断，如果是page.joinlinking.com的域名，则从模拟数据中获取
		String url = request.getServerName();

		if (url.indexOf("page.joinlinking.com") >= 0) {

			// 说明是模拟页面，使用模拟数据
			String dir = Cfg.getCfg().getRootDir() + "simulateservice/"
					+ this.servicename + "/" + this.simulateFile + ".js";
			String content = FileTools.readFile(dir, "UTF-8");
			this.orgResult = ContextTools.getBreezeContext4Json(content);
		} else {

			this.orgResult = FunctionInvokePoint.getInc()
					.breezeInvokeUseRequestAsParam(this.servicename, request,
							response);
		}

		super.setJspBody(jspBody);
	}

	public void doTag() throws JspException, IOException {
		JspFragment jf = this.getJspBody();
		StringBuilder resultStr = new StringBuilder();
		// if (调用结果正确){
		if ("0".equals(this.orgResult.getContext("code").toString())) {
			BreezeContext dataCtx = this.orgResult.getContext("data");
			if (log.isLoggable(Level.FINE)) {
				log.fine("result data:" + dataCtx);
			}
			if (dataCtx.getType() == BreezeContext.TYPE_ARRAY) {
				// for(结果daata中的数组进行循环){调用body内容
				for (this.resultIdx = 0; dataCtx != null && !dataCtx.isNull()
						&& this.resultIdx < dataCtx.getArraySize(); this.resultIdx++) {
					// 调用body内容，并合并
					this.resultData = dataCtx.getContext(this.resultIdx);
					if (this.resultData.getType() == BreezeContext.TYPE_MAP) {
						this.resultData.setContext("__i", new BreezeContext(
								this.resultIdx));
					} else if (this.resultData.getType() == BreezeContext.TYPE_ARRAY) {
						BreezeContext tmp = this.resultData;
						this.resultData = new BreezeContext();
						this.resultData.setContext("__data", tmp);
						this.resultData.setContext("__i", new BreezeContext(
								this.resultIdx));
					}
					StringWriter sw = new StringWriter();
					jf.invoke(sw);
					resultStr.append(sw.toString());
				}
				// }
			} else if (dataCtx.getType() == BreezeContext.TYPE_DATA) {
				this.resultData = new BreezeContext();
				this.resultData.setContext("data", dataCtx);
				StringWriter sw = new StringWriter();
				jf.invoke(sw);
				resultStr.append(sw.toString());
			} else if (dataCtx.getType() == BreezeContext.TYPE_MAP) {
				this.resultData = dataCtx;
				StringWriter sw = new StringWriter();
				jf.invoke(sw);
				resultStr.append(sw.toString());
			}

			// 输出到页面
			this.getJspContext().getOut().write(resultStr.toString());
		}
		// }

		// 关闭日志
		if (this.threadSignal != null) {
			log.removeThreadSignal();
		}
	}
}
