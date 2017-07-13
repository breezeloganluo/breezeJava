package com.breezefw.framework.init.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.netserver.workflow.WorkFlowUnitMgr;
import com.breeze.init.Initable;
import com.breeze.init.LoadClasses;

public class BreezeObjInit implements Initable {

	@Override
	public int getInitOrder() {
		return 3;
	}

	@Override
	public void doInit(HashMap<String, String> paramMap) {
		ArrayList<WorkFlowUnit> wl = LoadClasses.createObject(
				"com.breezefw.framework.workflow", WorkFlowUnit.class);
		wl.addAll(LoadClasses.createObject("com.breezefw.service",
				WorkFlowUnit.class));

		WorkFlowUnit[] workFlowList = new WorkFlowUnit[wl.size()];
		for (int i = 0; i < wl.size(); i++) {
			workFlowList[i] = wl.get(i);
		}
		
		WorkFlowUnitMgr.INSTANCE.init(workFlowList);
	}

	@Override
	public String getInitName() {
		return "BreezeObject";
	}

}