package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

public class Parse2StringArray extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "str[]";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		
		//2016-10-26对原始的输入值，如果是[]情况，就忽略，如果是['']情况就通过
		if(data == null || data.isNull()) {
			String [] result = new String[0];
			output.add(result);
			return "?";
		}
		
		if (data.getType() != BreezeContext.TYPE_ARRAY){
			//不是数组抛出异常
			throw new RuntimeException("type error:input path is not array");
		}
		
		int size = data.getArraySize();
		String[] result = new String[size];
		for (int i=0;i<size;i++){
			result[i] = data.getContext(i).getData().toString();
		}
		output.add(result);
		return "?";
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}
}
