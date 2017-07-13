package com.breezefw.framework.workflow.checker.single;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;

public class RegChecker extends SingleContextCheckerAbs {
	private static final Logger log=Logger.getLogger("com.breezefw.framework.checker.RegChecker");

	@Override
	public boolean check(BreezeContext root,BreezeContext breezeContext, Object[] reg) {
		try{
			//用Pattern类的matcher()方法生成一个Matcher对象
			Pattern p = Pattern.compile(reg[0].toString());
			Matcher m = p.matcher(breezeContext.getData().toString());
	        if(m.find()){
	        	return true;
	        }
		}catch(Exception ex){
			log.severe("regStr is null or not match! - INFO:"+ex.toString());
		}
		return false;
	}

	@Override
	public String getName() {	
		return "regChecker";
	}

}
