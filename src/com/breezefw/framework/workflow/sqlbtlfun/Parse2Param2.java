package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2Param2 extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "parInt";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null  || data.isNull()){
			return "0";
		}
		
		return data.getData().toString();
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}
}
