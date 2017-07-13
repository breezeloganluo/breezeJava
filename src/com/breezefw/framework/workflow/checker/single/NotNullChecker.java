package com.breezefw.framework.workflow.checker.single;

import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;

public class NotNullChecker extends SingleContextCheckerAbs {

	@Override
	public boolean check(BreezeContext root,BreezeContext arg0, Object[] arg1) {
		if(arg0 == null || arg0.isNull()){
			return false;
		}
		return true;
	}
	
//	@Override
	public String getName() {
		return "NotNull";
	}

}
