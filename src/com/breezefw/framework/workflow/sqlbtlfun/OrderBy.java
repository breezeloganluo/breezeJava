package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;



import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

public class OrderBy extends SqlFunctionAbs {
	Logger log = Logger.getLogger("com.breezefw.framework.workflow.sqlbtlfun.OrderBy");

	/**
	 * 格式："orderby": [{
                        "name": "returntime",
                        "type": "asc"
                    }]
	 */
	@Override
	protected String fun (String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		try{
			BreezeContext root = (BreezeContext) evenenvironment[0];
			BreezeContext orderbyCtx = root.getContextByPath(funParam);
			StringBuilder result = new StringBuilder();
			result.append("order by ");
			for (int i=0;i<orderbyCtx.getArraySize();i++){
				if (i!=0){
					result.append(',');
				}
				BreezeContext oneCtx = orderbyCtx.getContext(i);
				result.append(oneCtx.getContext("name"));
				BreezeContext oneTypeCtx = oneCtx.getContext("type");
				if (oneTypeCtx != null){
					result.append(' ').append(oneTypeCtx.toString());
				}
			}
			return result.toString();
		}catch(Exception e){
			log.severe("", e);
			return "";
		}
	}

	@Override
	protected String getName() {
		return "orderby";
	}

}
