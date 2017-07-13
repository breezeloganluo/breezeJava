package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2LongArray extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "long[]";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null){
			throw new RuntimeException("data is null");
		}
		if (data.getType() != BreezeContext.TYPE_ARRAY){
			//不是数组抛出异常
			throw new RuntimeException("type error:input path is not array");
		}
		
		int size = data.getArraySize();
		Long[] result = new Long[size];
		for (int i=0;i<size;i++){
			result[i] = Long.parseLong(data.getContext(i).getData().toString());
		}
		output.add(result);
		return "?";
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}
}
