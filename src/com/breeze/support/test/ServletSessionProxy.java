/*
 * ServletSessionProxy.java
 *
 * Created on 2007年7月17日, 上午8:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.test;
import java.lang.reflect.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author happy
 */
public class ServletSessionProxy implements InvocationHandler {
    
    /** Creates a new instance of ServletSessionProxy */
    public ServletSessionProxy() {
    }

    public static HttpSession createRequesst(){
        return createRequest(new ServletSessionProxy());
    }
    public static HttpSession createRequest(ServletSessionProxy p_proxy){
        return (HttpSession)Proxy.newProxyInstance(HttpSession.class.getClassLoader(),new Class[]{HttpSession.class},p_proxy);
    }
    
    private Object innerInvoker(Object proxy,Method method,Object[] args,Object moke)throws Throwable{
        System.out.println("method："+method);
        System.out.println("method name:"+method.getName());
        Method[] thisMethod = moke.getClass().getMethods();
        for (Method m:thisMethod){
            if(!m.getName().equals(method.getName())){
                continue;
            }
            Class[] thisPc = m.getParameterTypes();
            Class[] inputPc = method.getParameterTypes();
            if(thisPc.length == inputPc.length){
                for(int i=0;i<thisPc.length;i++){
                    if (!thisPc[i].equals(inputPc[i])){
                        return null;
                    }
                }
                
                return m.invoke(moke,args);
            }
        }
        return null;
    }
    public Object invoke(Object proxy,Method method,Object[] args)throws Throwable{
        return this.innerInvoker(proxy,method,args,this);
    }
    
    //下面名模拟方法
    private HashMap<String,Object> attr = new HashMap<String,Object>();
    public void setAttribute(String pName,Object pObject){
        this.attr.put(pName,pObject);
    }
    public Object getAttribute(String pName){
        return this.attr.get(pName);
    }
    private String sessionId = null;
    public String getId(){
        if (this.sessionId == null){
            this.sessionId = "fackId"+System.currentTimeMillis();
        }
        return this.sessionId;
    }
}
