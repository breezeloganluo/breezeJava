package com.breezefw.compile.initor;

import com.breezefw.compile.CompileObjInitor;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;
import com.breezefw.framework.workflow.checker.SingleContextCheckerMgr;

public class CheckerInitor extends CompileObjInitor {
	/**
	 * 这个负责各种实例化后的初始化工作，由子类去处理,子类处理并重载的类
	 * @param obj
	 */
	public void init(Object obj){
		try{
			SingleContextCheckerAbs checker = (SingleContextCheckerAbs)obj;
			SingleContextCheckerMgr.INSTANCE.addSingle(checker);
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
}
