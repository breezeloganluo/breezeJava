package com.breezefw.framework.init.service.cfg;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.support.cfg.Cfg;

public class CfgContext extends BreezeContext {
	public static CfgContext CFGCONTEXT = new CfgContext();
	@Override
    public BreezeContext getContext(String pname) {
		return new BreezeContext(Cfg.getCfg().getString(pname));
	}
	@Override
    public void setContext(String pname, BreezeContext value) {
		
	}
}
