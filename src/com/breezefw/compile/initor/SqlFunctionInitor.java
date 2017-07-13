package com.breezefw.compile.initor;

import com.breezefw.ability.btl.BTLParser;
import com.breezefw.compile.CompileObjInitor;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

public class SqlFunctionInitor extends CompileObjInitor {
	/**
	 * 这个负责各种实例化后的初始化工作，由子类去处理,子类处理并重载的类
	 * 
	 * @param obj
	 */
	public void init(Object obj) {
		try {
			SqlFunctionAbs wfu = (SqlFunctionAbs) obj;
			BTLParser.INSTANCE("sql").addFunction(wfu);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
