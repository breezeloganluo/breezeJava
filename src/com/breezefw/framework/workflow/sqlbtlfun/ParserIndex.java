package com.breezefw.framework.workflow.sqlbtlfun;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLFunctionAbs;

public class ParserIndex extends SqlFunctionAbs {

	private static Object LOCK = new Object();
	private static long idx;
	private static String lastDate = null;
	
	
	@Override
	protected String getName() {
		return "idx";
	}

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		synchronized (ParserIndex.LOCK) {
			SimpleDateFormat st = new SimpleDateFormat("MMdd");
			String sdate = st.format(new Date());
			if (!sdate.equals(ParserIndex.lastDate)){
				ParserIndex.lastDate = sdate;
				ParserIndex.idx = 0;
			}
			output.add(sdate+(System.currentTimeMillis() %100) +(new DecimalFormat("0000")).format(ParserIndex.idx));
			ParserIndex.idx++;	
			return "?";
		}
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}
}
