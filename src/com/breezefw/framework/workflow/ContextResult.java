package com.breezefw.framework.workflow;

/**
 * @version 0.02 2016-07-31 罗光瑜修改，这次修改主要是还原原来的实现方案，即contentResult只写上下文，不做跳转。跳转还是放到外围去做，否则，包括后面的servciecall等功能都会失效
 */
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breeze.support.cfg.Cfg;
import com.breezefw.framework.template.ContextResultItem;

public class ContextResult extends WorkFlowUnit {

	private static final Logger log = Logger.getLogger("com.breezefw.framework.workflow.ContextResult");

	@Override
	public String getName() {
		return "contextResult";
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs resultAbs = new CommTemplateItemParser("contextResult", ContextResultItem.class);
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { resultAbs };
		return result;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias, int lastResult) {

		// 获取根上下文信息
		String charset = Cfg.getCfg().getString("DefalutFileChartset");
		BreezeContext root = context;
		if (log.isLoggable(Level.FINE)) {
			log.fine("go Process [" + this.getName() + "]lastResult[" + lastResult + "]");
		}

		

		// 设置code
		root.setContext("code", new BreezeContext(lastResult));
		// 设置data
		if (root.getContext("data") == null) {
			if (lastResult == 0) {
				root.setContext("data", new BreezeContext("succ!"));
			} else {
				root.setContext("data", new BreezeContext("oper fail"));
			}
		}

		// 设置对应的template，这是给一个特定的service即直接用http请求的service使用的
		ContextResultItem singleItem = (ContextResultItem) this.getItem(root, st, "contextResult");
		BreezeContext templateCtx = root.getContext("template");
		if (lastResult == 0) {
			if (templateCtx == null || templateCtx.isNull()) {
				root.setContext("template", new BreezeContext(singleItem.getTemplateUrl()));
			} else {
				root.setContext("template",
						new BreezeContext(singleItem.getTemplateUrl() + templateCtx.getData().toString()));
			}
		} else {
			if (singleItem.getTemplateErrorUrl() != null) {
				root.setContext("template", new BreezeContext(singleItem.getTemplateErrorUrl()));
			} else {
				root.setContext("template", new BreezeContext("/page/error.jsp"));
			}
		}

		return 0;
	}
}
