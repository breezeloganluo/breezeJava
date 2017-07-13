package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

public class Parse2String extends SqlFunctionAbs {
	
	private Logger log = Logger.getLogger("com.weiguang.framework.workflow.sqlbtlfun.Parse2String");

	@Override
	protected String getName() {
		return "str";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null){
			output.add("");
			return "?";
		}
		if (data.getType() == BreezeContext.TYPE_MAP){
			//不是数组抛出异常
			throw new RuntimeException("type error:input path is not data,path is:" + funParam + "\n" + root);
		}
		if (data.getType() == BreezeContext.TYPE_DATA){		
			output.add(data.getData().toString());
		}
		if (data.getType() == BreezeContext.TYPE_ARRAY){
			String[] arrResult = new String[data.getArraySize()];				
			for (int i=0;i<data.getArraySize();i++){
				String addData = "";
				if (data.getContext(i) != null && !data.isNull()){
					addData = data.getContext(i).getData().toString();
				}
				arrResult[i] = (addData);
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
