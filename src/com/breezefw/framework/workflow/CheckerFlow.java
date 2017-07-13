package com.breezefw.framework.workflow;

import java.util.ArrayList;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breeze.init.LoadClasses;
import com.breezefw.framework.template.CheckerItem;
import com.breezefw.framework.template.CheckerItem.CheckInfo;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;
import com.breezefw.framework.workflow.checker.SingleContextCheckerMgr;

public class CheckerFlow extends WorkFlowUnit {

	private final static String SINGLEITEMNAME = "checkerFlow";
	private static final Logger log = Logger
			.getLogger("com.breezefw.framework.workflow.CheckerFlow");

	@Override
	public String getName() {
		return SINGLEITEMNAME;
	}

	@Override
	protected void loadingInit() {
		ArrayList<SingleContextCheckerAbs> sc = LoadClasses
				.createObject(
						"com.breezefw.framework.workflow.checker.single",
						com.breezefw.framework.workflow.checker.SingleContextCheckerAbs.class);
		SingleContextCheckerMgr.INSTANCE.init(sc);
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		// TODO Auto-generated method stub
		TemplateItemParserAbs resultAbs = new CommTemplateItemParser(
				SINGLEITEMNAME,
				com.breezefw.framework.template.CheckerItem.class);
		TemplateItemParserAbs tp = resultAbs;
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { tp };
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

		CheckerItem singleItem = (CheckerItem) this.getItem(root, st,
				SINGLEITEMNAME);
		// 获取被校验的信息
		CheckInfo[] checkInfo = singleItem.getCheckInfo();

		if (checkInfo == null) {
			log.fine("checkinfo is null,no check select");
			return 0;
		}
		// 获取对应的 单值校验类
		for (CheckInfo item : checkInfo) {
			if (log.isLoggable(Level.FINE)) {
				log.fine("go check for " + item.checkerObj);
			}
			// 获取被校验的额
			BreezeContext breezeContext = BreezeContext.getObjectByPath(
					item.checkerObj, root);
			Object [] checkerParam = new Object[]{ item.checkerParam };
			SingleContextCheckerAbs sccm = SingleContextCheckerMgr.INSTANCE.getSingleCheck(item.checkerName);
			boolean result = sccm.check(root,breezeContext, checkerParam);
			
			
			if (!result) {
				log.fine("check fail:" + item.checkerObj + "value:"
						+ breezeContext);
				return item.failCode;
			}
		}
		return 0;
	}
}
