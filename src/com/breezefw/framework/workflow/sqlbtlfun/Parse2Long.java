package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2Long extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "long";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null){
			output.add(0L);
			return "?";
		}
		if (data.getType() == BreezeContext.TYPE_MAP){
			//不是数组抛出异常
			throw new RuntimeException("type error:input path is not data");
		}
		
		if (data.getType() == BreezeContext.TYPE_DATA){
			output.add(Long.parseLong((data.getData().toString())));
		}
		
		if (data.getType() == BreezeContext.TYPE_ARRAY){
			Long[] arrResult = new Long[data.getArraySize()];				
			for (int i=0;i<data.getArraySize();i++){
				arrResult[i] = Long.parseLong(data.getContext(i).getData().toString());
			}
			output.add(arrResult);
		}
		return "?";
	}

	@Override
	protected String getPackage() {
		return "sql";
	}
}
