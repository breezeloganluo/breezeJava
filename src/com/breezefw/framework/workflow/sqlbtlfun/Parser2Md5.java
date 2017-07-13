package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;



import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.support.tools.Md5;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class Parser2Md5 extends SqlFunctionAbs {

	private Logger log = Logger.getLogger("com.breezefw.ability.btl.function.sql.Parser2Md5");
	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext) evenenvironment[0];
		BreezeContext data = root.getContextByPath(funParam);
		if (data == null || data.isNull()) {
			output.add("");
		} else {
			try {
				output.add(Md5.getMd5Str(data.getData().toString()));
			} catch (Exception e) {
				log.severe("md5 excption!", e);
				throw new RuntimeException (e);
			}
		}
		return "?";
	}

	@Override
	protected String getName() {
		return "md5";
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}

}
