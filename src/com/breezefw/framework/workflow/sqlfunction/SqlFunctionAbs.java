package com.breezefw.framework.workflow.sqlfunction;

import com.breezefw.ability.btl.BTLFunctionAbs;

public abstract class SqlFunctionAbs extends BTLFunctionAbs {
	@Override
	protected String getPackage() {
		return "sql";
	}
}
