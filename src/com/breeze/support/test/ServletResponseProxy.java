/*
 * ServletResponseProxy.java
 *
 * Created on 2010年4月26日, 上午7:02
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
public class ServletResponseProxy  implements InvocationHandler{
    
    /** Creates a new instance of ServletResponseProxy */
    public ServletResponseProxy() {
    }
    public static HttpServletResponse createResponse(ServletResponseProxy p_proxy){
        return (HttpServletResponse)Proxy.newProxyInstance(HttpServletResponse.class.getClassLoader(),new Class[]{HttpServletResponse.class},p_proxy);
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
    
    private ByteArrayOutputStream bout = new ByteArrayOutputStream();
    private PrintWriter interwriter = new PrintWriter(bout);
    public PrintWriter getWriter(){
        return this.interwriter;
    }
    public String getWriterResult(){
        return this.bout.toString();
    }
}
