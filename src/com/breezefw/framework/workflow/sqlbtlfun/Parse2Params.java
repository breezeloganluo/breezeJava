package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2Params extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "par";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null || data.isNull()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(String key:data.getMapSet()){
			sb.append(" and ");
			sb.append(key).append(" = ? ");
			BreezeContext d = data.getContext(key);
			output.add(d.getData());
		}
		return sb.toString();
		
	}

	@Override
	protected String getPackage() {
		return "sql";
	}
}
