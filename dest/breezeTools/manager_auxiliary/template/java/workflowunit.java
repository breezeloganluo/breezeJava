package com.breezefw.service.xxx.workflow;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breezefw.framework.template.DBOperateItem;

public class TestFlowUnit extends WorkFlowUnit {
	private static final Logger log = Logger.getLogger("com.breezefw.framework.workflow.QueryFlow");
	private final static String FLOWNAME = "queryFlow";
	
	@Override
	public String getName() {
		return FLOWNAME;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias, int lastResult) {
		XXXTemplate fileItem = (XXXTemplate) this.getItem(context, st, FLOWNAME);
		return 0;
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { new CommTemplateItemParser(
				FLOWNAME, TestFlowUnitItem.class) };
		return result;
	}

}