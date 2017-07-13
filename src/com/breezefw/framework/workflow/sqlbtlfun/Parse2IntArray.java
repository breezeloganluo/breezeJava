package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2IntArray extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "int[]";
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
		Integer[] result = new Integer[size];
		for (int i=0;i<size;i++){
			result[i] = Integer.parseInt(data.getContext(i).getData().toString());
		}
		output.add(result);
		return "?";
	}

	@Override
	protected String getPackage() {
		return "sql";
	}
}
