package com.breezefw.framework.init.service;

import java.util.HashMap;

import com.breeze.init.Initable;
import com.breezefw.compile.BreezeCompile;

public class DClassesInitor implements Initable {

	@Override
	public int getInitOrder() {
		return 4;
	}

	@Override
	public void doInit(HashMap<String, String> paramMap) {
		BreezeCompile.INC.searchAllJavaClass(null);
		BreezeCompile.INC.loadAndInitAll();
	}

	@Override
	public String getInitName() {
		return "DClass";
	}

}
