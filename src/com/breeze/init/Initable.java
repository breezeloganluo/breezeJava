package com.breeze.init;

import java.util.HashMap;

public interface Initable {
	public int getInitOrder();
	public void doInit(HashMap<String,String> paramMap);
	public String getInitName();
}
