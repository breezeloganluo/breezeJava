package com.breezefw.compile.initor;

import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.netserver.workflow.WorkFlowUnitMgr;
import com.breezefw.compile.CompileObjInitor;

public class FlowInitor extends CompileObjInitor {
	/**
	 * 这个负责各种实例化后的初始化工作，由子类去处理,子类处理并重载的类
	 * 
	 * @param obj
	 */
	public void init(Object obj) {
		try {
			WorkFlowUnit wfu = (WorkFlowUnit) obj;
			WorkFlowUnitMgr.INSTANCE.addUnit(wfu);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
