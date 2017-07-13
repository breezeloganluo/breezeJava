/*
 * 最终被解析过的excutor
 * 输入一些固定的换进参数，进行模板的实际替换
 */
package com.breezefw.ability.btl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breeze.base.log.Logger;


/**
 *
 * @author 罗光瑜
 */
public class BTLExecutor {
	private Logger log = Logger.getLogger("com.weiguang.ability.btl.BTLExecutor");
	//常量函数
    private class _C extends BTLFunctionAbs {

		@Override
		protected String fun(String funParam, Object[] evenenvironment,
				ArrayList<Object> output) {			
			return funParam;
		}

		@Override
		protected String getName() {			
			return "_C";
		}

		@Override
		protected String getPackage() {
			// TODO Auto-generated method stub
			return "_C";
		}
    }
    
    private class funStruct{
    	String funName;
    	BTLFunctionAbs fun;
    	String param;
    	funStruct(BTLFunctionAbs _f,String _n,String fn){
    		this.fun = _f;
    		this.param = _n;
    		this.funName = fn;
    	}
    }

    BTLExecutor(HashMap<String, BTLFunctionAbs> pm) {
        this.functionList = new ArrayList<funStruct>();
        if (pm != null) {
            this.functionTypeMap = pm;
        } else {
            this.functionTypeMap = new HashMap<String, BTLFunctionAbs>();
        }
        this.defalutFun = this.functionTypeMap.get("");
        if (this.defalutFun == null){
        	this.defalutFun = new _C();
        }
    }

    
    
    private ArrayList<funStruct> functionList;//解析后的函数列表
    private HashMap<String, BTLFunctionAbs> functionTypeMap;//本域的函数map
    private BTLFunctionAbs defalutFun = null;//默认函数

    
    /**
     * 执行函数，本函数将被执行
     * @param param 输入的
     * @param result
     * @return
     */
    public String execute(Object[] evenenvironment, ArrayList<Object> output) {
        if (this.functionList.isEmpty()) {
            return null;
        }
        if (this.functionList.size() == 1) {
        	funStruct exec = this.functionList.get(0);
            return exec.fun.fun(exec.param, evenenvironment,output);
        }
        StringBuilder returnresult = new StringBuilder();
        for (funStruct funStruct : this.functionList) {
        	if (funStruct.fun == null){
        		throw new RuntimeException("btl function (" + funStruct.funName + ") not found!");
        	}
            returnresult.append(funStruct.fun.fun(funStruct.param, evenenvironment,output));
        }
        return returnresult.toString();
    }

    /**
     * 增加一个函数
     * @param btlFunStr 整个函数的字符串
     */
    void addFunc(String btlFunStr) {
        //如果里面还有{}就报错
        if (btlFunStr.indexOf('{') >= 0 || btlFunStr.indexOf('}') >= 0) {
            throw new RuntimeException("template error {|} is exist in function parameter " + btlFunStr);
        }
        Pattern p = Pattern.compile("\\s*([^\\(\\)]+)\\(([^\\(\\)]*)\\)\\s*");
        Matcher m = p.matcher(btlFunStr);
        StringBuffer buff = new StringBuffer();
        if (m.find()) {
        	String funName = m.group(1).trim();
        	BTLFunctionAbs fun = this.functionTypeMap.get(funName);
        	String param = m.group(2);
        	if (param == null){
        		param = "";
        	}else{
        		param = param.trim();
        	}
        	this.functionList.add(new funStruct(fun,param,funName));
        	return;
        }
        //其他情况用常量处理
        this.functionList.add(new funStruct(this.defalutFun,btlFunStr,"_C"));
    }

    //增加普通函数
    void addString(String c) {
        this.functionList.add(new funStruct(new _C(),c,"_C"));
    }
    
}
