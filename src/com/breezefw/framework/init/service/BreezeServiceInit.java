package com.breezefw.framework.init.service;

import java.util.HashMap;

import com.breeze.framwork.netserver.dbtransprocess.BreezeDBTransProcessMgr;
import com.breeze.framwork.netserver.filter.BreezeFilterMgr;
import com.breeze.framwork.netserver.process.ServerProcessManager;
import com.breeze.framwork.netserver.timing.BreezeScheduleCfgMgr;
import com.breeze.framwork.servicerg.AllServiceTemplate;
import com.breeze.init.Initable;

public class BreezeServiceInit implements Initable {

	@Override
	public int getInitOrder() {
		return 5;
	}

	@Override
	public void doInit(HashMap<String, String> paramMap) {
		try {
			String webBaseDir = paramMap.get("BaseDir");
			String processDir = "WEB-INF/classes/flow/";
			String sericeTemplateDir = "WEB-INF/classes/service/";
			String filterDir = "WEB-INF/classes/filter/";
			String schedulerDir = "WEB-INF/classes/scheduler/";

			ServerProcessManager.INSTANCE.init(webBaseDir, processDir);

			AllServiceTemplate.INSTANCE.init(webBaseDir, sericeTemplateDir);

			BreezeFilterMgr.INSTANCE.init(webBaseDir, filterDir);

			BreezeScheduleCfgMgr.INSTANCE.init(webBaseDir, schedulerDir);

			BreezeDBTransProcessMgr.INSTANCE.init(webBaseDir, "WEB-INF/classes/trans/");
		} catch (Exception e) {
			System.out.println("init " + this.getInitName() + "fail");
			e.printStackTrace();
		}

	}

	@Override
	public String getInitName() {
		return "BreezeService";
	}

}
