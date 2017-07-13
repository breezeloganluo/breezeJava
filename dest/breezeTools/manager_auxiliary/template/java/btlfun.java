package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class MyBtlFun extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "MyBtlFun";
	}
	private Logger log = Logger.getLogger("com.breezefw.ability.btl.function.sql.MyBtlFun");

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		try {
			BreezeContext root = (BreezeContext) evenenvironment[0];
			BreezeContext data = root.getContextByPath(funParam);
			
			return "?";
		} catch (Exception e) {
			log.severe("Exception use the default Data -1:",e);
			return "?";
		}
	}

	@Override
	protected String getPackage() {
		return "sql";
	}
}
