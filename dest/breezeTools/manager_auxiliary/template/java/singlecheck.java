package com.breezefw.framework.workflow.checker.single;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;

public class MyChecker extends SingleContextCheckerAbs {
	private static final Logger log = Logger.getLogger("com.breezefw.framework.checker.MyChecker");

	@Override
	public boolean check(BreezeContext root,BreezeContext arg0, Object[] arg1) {
		
		
		if(arg0.getData().equals("")||arg0.getData()==null)
		{
			return false;
		}
		return true;
	}


	@Override
	public String getName() {
		return "MyChecker";
	}

}
