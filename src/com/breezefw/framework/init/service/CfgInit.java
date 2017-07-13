package com.breezefw.framework.init.service;

import java.io.File;
import java.util.HashMap;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.init.Initable;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.tools.FileTools;
import com.breeze.support.tools.GsonTools;

public class CfgInit extends Cfg implements Initable {
	public static final String paramPath= "cms.param";
	/**
	 * 这是给放射调用的
	 */
	public CfgInit() {
		super(null);
	}

	public CfgInit(String rootdir) {
		super(rootdir);
	}

	@Override
	public String reload() {
		String dir = this.webRootDir + "WEB-INF/config.cfg";
		File file = new File(dir);
		String cfgText = FileTools.readFile(file, "UTF-8");
		this.cfgMap = GsonTools.parserJsonMapObj(cfgText);
		return null;
	}

	
	public int getInitOrder() {
		return 0;
	}

	
	public void doInit(HashMap<String, String> paramMap) {
		String path = paramMap.get("BaseDir");
		CfgInit cfg = new CfgInit(path);
		cfg.reload();
		Cfg.initCfg(cfg);
	}

	
	public String getInitName() {
		return "config";
	}
	
	@Override
	public String getString(String pItem){
		String result = super.getString(pItem);
		if (result == null){
			String path = paramPath+'.'+pItem;
			BreezeContext resutCtx = ContextMgr.global.getContextByPath(path);
			if (resutCtx == null || resutCtx.isNull()){
				return null;
			}
			result = resutCtx.toString();
		}
		return result;
	}

}
