package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;




import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2Int extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "int";
	}
	private Logger log = Logger.getLogger("com.breezefw.ability.btl.function.sql.Parse2Int");

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		try {
			BreezeContext root = (BreezeContext) evenenvironment[0];
			BreezeContext data = root.getContextByPath(funParam);
			if (data == null) {
				output.add(0);
				return "?";
			}
			if (data.getType() == BreezeContext.TYPE_MAP) {
				// 不是数组抛出异常
				log.severe("type error:input path is not data,so use the default Data -1");
				output.add(-1);
				return "?";
			}
			if (data.getType() == BreezeContext.TYPE_DATA){
				output.add(Integer.parseInt(data.getData().toString()));
			}
			if (data.getType() == BreezeContext.TYPE_ARRAY){
				Integer[] arrResult = new Integer[data.getArraySize()];				
				for (int i=0;i<data.getArraySize();i++){
					arrResult[i] = Integer.parseInt(data.getContext(i).getData().toString());
				}
				output.add(arrResult);
			}
			return "?";
		} catch (Exception e) {
			log.severe("Exception use the default Data -1:",e);
			output.add(-1);
			return "?";
		}
	}

	@Override
	protected String getPackage() {
		return "sql";
	}
	
	public static void main(String[] arg){
		System.out.println(Integer.parseInt(""));
	}
}
