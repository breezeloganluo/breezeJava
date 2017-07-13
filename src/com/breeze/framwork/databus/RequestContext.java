/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.databus;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Administrator
 */
public class RequestContext extends BreezeContext{
    private HttpServletRequest request = null;
    public RequestContext(HttpServletRequest prequest){
        this.request = prequest;
    }
    /**
     * 获请求对象对应的值，该方法重载了父类的函数
     * @param pname
     * @return 
     */
    @Override
    public BreezeContext getContext(String pname) {
        String[] values = this.request.getParameterValues(pname);
        if (values!=null && values.length == 1){
            return new BreezeContext(values[0]);
        }else if (values != null){
        	BreezeContext valueArrCtx = new BreezeContext();
        	for (int i=0;i<values.length;i++){
        		valueArrCtx.pushContext(new BreezeContext(values[i]));
        	}
            return new BreezeContext(valueArrCtx);
        }
        //下面还是有可能是取数组去不到
        String valueStr = this.request.getParameter(pname);
        if (valueStr != null){
        	return new BreezeContext(valueStr);
        }
        //剩下的是为空的情况，到attribute中获取参数
        Object value = this.request.getAttribute(pname);
        if (value != null){
        	//2015-02-05罗光瑜修改，判断一下返回的对象本身是否是BreezeContext如果是就不要再包装
        	if (value instanceof BreezeContext){
        		return (BreezeContext)value;
        	}
        	return new BreezeContext(value);
        }
        
        return null;
    }
    
    /**
     * 本子类应该不再支持该方法
     *
     * @param pname
     * @param value
     */
    @Override
    public void setContext(String pname, BreezeContext value) {
    	if (value == null){
    		return;
    	}
        this.request.setAttribute(pname, value.getData());
    }
    @Override
    public BreezeContext getSelf(){
    	Enumeration<String> names = request.getParameterNames();
    	BreezeContext result = new BreezeContext();
    	while(names.hasMoreElements()){
    		String name = names.nextElement();
    		result.setContext(name, new BreezeContext(request.getParameter(name)));
    	}
    	return result;
    }
}
