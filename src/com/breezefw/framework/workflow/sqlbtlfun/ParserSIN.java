package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

/**
 * 这个类是将一个内存数组组装成类似sql语句的in函数输出，即括号扩起后，用逗号隔开
 * 比如将[a,b]转成成(a,b)
 * @author Logan
 *
 */
public class ParserSIN extends SqlFunctionAbs {
	@Override
	protected String getName() {
		return "SIN";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext) evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null) {
			output.add("");
			return "(?)";
		}
		if (data.getType() != BreezeContext.TYPE_ARRAY) {
			// 不是数组抛出异常
			throw new RuntimeException(
					"type error:input path is not data,path is:" + funParam
							+ "\n" + root);
		}
		StringBuilder returnStr = new StringBuilder();		
		for (int i=0;i<data.getArraySize();i++){
			if (i!=0){
				returnStr.append(',');
			}
			returnStr.append('?');
			output.add(data.getContext(i).toString());
		}
		return "("+returnStr.toString()+")";

	}

}
