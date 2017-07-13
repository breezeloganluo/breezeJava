package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2Org extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "Org";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null){
			return "";
		}
		if (data.getType() != BreezeContext.TYPE_DATA){
			//不是数组抛出异常
			throw new RuntimeException("type error:input path is not data");
		}
		return data.getData().toString();
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}

}
