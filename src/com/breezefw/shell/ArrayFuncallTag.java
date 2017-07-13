package com.breezefw.shell;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;

/**
 * 这个类用用户解析所有的多维数组情况下，使之能用此类，一层一层的嵌套调用下去
 * 
 * @author Loganluo
 *
 */
public class ArrayFuncallTag extends BreezeFunctioinCallTag {
	private static Object lock = new Object();
	private static Logger log = null;

	private BreezeContext resultData;
	private BreezeContext parentResult;
	private int idx = 0;
	private String threadSignal = null;
	private int resultIdx = 0;

	public BreezeContext getResultData() {
		return resultData;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public void setJspBody(JspFragment jspBody) {
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

		
		BreezeFunctioinCallTag p = (BreezeFunctioinCallTag) this.getParent();
		this.parentResult = p.getResultData();
		super.setChildCall();
		super.setJspBody(jspBody);
	}

	public void doTag() throws JspException, IOException {
		JspFragment jf = this.getJspBody();
		StringBuilder resultStr = new StringBuilder();
		// if (属于当前索引){
		if (   ((Integer)this.parentResult.getContext("__i").getData()).intValue() == this.idx  ) {
			BreezeContext dataCtx = this.parentResult.getContext("__data");
			if (log.isLoggable(Level.FINE)) {
				log.fine("result data:" + dataCtx);
			}
			if (dataCtx == null){
				this.resultData = this.parentResult;
				StringWriter sw = new StringWriter();
				jf.invoke(sw);
				resultStr.append(sw.toString());
			}
			else if (dataCtx.getType() == BreezeContext.TYPE_ARRAY) {
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
						this.resultData.setContext("__i", new BreezeContext(this.resultIdx));
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
