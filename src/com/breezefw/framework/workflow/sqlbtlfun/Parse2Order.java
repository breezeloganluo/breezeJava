package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parse2Order extends SqlFunctionAbs {

	@Override
	protected String getName() {
		return "ord";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext)evenenvironment[0];
		BreezeContext spes = root.getContextByPath(funParam);
		StringBuilder param = new StringBuilder();
		if (spes == null || spes.isNull()){
			return "";
		}
		BreezeContext order = spes.getContextByPath("orderby");
		if (order == null || order.isNull()){
			
		}else{
			int size = order.getArraySize()-1;
			String column = "";
			String desc = "";
			param.append(" order by ");
			for(int i=0;i<size;i++){
				column = spes.getContextByPath("orderby["+i+"].name").getData().toString();
				desc = spes.getContextByPath("orderby["+i+"].desc").getData().toString();
				param.append(column);
				if(desc.equals("true")){
					param.append(" desc,");
				}else{
					param.append(" asc,");
				}
			}
			column = spes.getContextByPath("orderby["+size+"].name").getData().toString();
			desc = spes.getContextByPath("orderby["+size+"].desc").getData().toString();
			param.append(column);
			if(desc.equals("true")){
				param.append(" desc");
			}else{
				param.append(" asc");
			}
		}
		
		BreezeContext limit = spes.getContextByPath("limit");
		if (limit == null || limit.isNull()){
			
		}else{
			String start = limit.getContextByPath("start").getData().toString();
			String length = limit.getContextByPath("length").getData().toString();
			param.append(" limit " + start + "," + length);
		}
		
		return param.toString();
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}

}
