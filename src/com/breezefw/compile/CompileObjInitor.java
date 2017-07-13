package com.breezefw.compile;

import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breezefw.compile.initor.CheckerInitor;
import com.breezefw.compile.initor.FlowInitor;
import com.breezefw.compile.initor.SqlFunctionInitor;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

public class CompileObjInitor {
	public static CompileObjInitor INSTANCE(){
		CompileObjInitor cint =  new CompileObjInitor();
		cint.compileObjInitorMap = new HashMap<Class,CompileObjInitor>();
		cint.compileObjInitorMap.put(WorkFlowUnit.class, new FlowInitor());
		cint.compileObjInitorMap.put(SingleContextCheckerAbs.class, new CheckerInitor());
		cint.compileObjInitorMap.put(SqlFunctionAbs.class, new SqlFunctionInitor());
		return cint;
	}
	
	public CompileObjInitor(){
		
	}
	/**
	 * 这个负责各种实例化后的初始化工作，由子类去处理,子类处理并重载的类
	 * @param obj
	 */
	public void init(Object obj){
		
	}
	
	HashMap<Class,CompileObjInitor> compileObjInitorMap ;
	
	/**
	 * 用传入的Class进行实例化，并通过初始化，加载到系统里面
	 * map中，第一个参数是被传入参数的父类，第二个参数是这个类的初始化方法的实例
	 * @param c 传入的class
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
    public void loadObj(Class c) throws InstantiationException, IllegalAccessException{
    	for (Class father:this.compileObjInitorMap.keySet()){
    		//如果不是父子关系就下一个
    		if (!father.isAssignableFrom(c) || Modifier.isAbstract(c.getModifiers())){
				continue;
			}
    		CompileObjInitor coi = this.compileObjInitorMap.get(father);
    		Object obj = c.newInstance();
    		

    		coi.init(obj);

    	}
    }
}
