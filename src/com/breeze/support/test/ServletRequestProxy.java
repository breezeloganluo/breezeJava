/*
 * ServletProxy.java
 *
 * Created on 2007年7月7日, 上午8:46
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
public class ServletRequestProxy implements InvocationHandler{
    
    public ServletRequestProxy(){
        
    }
    
    public static HttpServletRequest createRequest(ServletRequestProxy p_proxy){
        return (HttpServletRequest)Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),new Class[]{HttpServletRequest.class},p_proxy);
    }
    
    
    
    protected HashMap<String,String> parameterMap = new HashMap<String,String>();
    private HashMap<String,Object> attributeMap = new HashMap<String,Object>();
    
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
    
    /**
     *这是一个servlet的类,不直接使用,便于后面类继承使用
     */
    public static class ServletInputStreamCreater extends ServletInputStream{
        InputStream in;
        public ServletInputStreamCreater(InputStream pin){
            this.in = pin;
        }
        public int available()throws IOException{
            return this.in.available();
        }
        
        public void close()throws IOException{
            this.in.close();
        }
        
        public void mark(int readlimit){
            this.in.mark(readlimit);
        }
        
        public void reset()throws IOException{
            this.in.reset();
        }
        
        public boolean markSupported(){
            return this.in.markSupported();
        }
        
        public int read() throws java.io.IOException{
            return this.in.read();
        }
        
        public int read(byte[] b)throws IOException{
            return this.in.read(b);
        }
        public int read(byte[] b,int off,int len)throws IOException{
            return this.in.read(b,off,len);
        }
        public long skip(long n)throws IOException{
            return this.in.skip(n);
        }
    }
    public static class MyDispatcher implements RequestDispatcher{
        private String Url;
        private MyDispatcher(String pUrl){
            this.Url = pUrl;
        }
        public void include(ServletRequest request,ServletResponse response){
            
        }
        
        public void forward(ServletRequest request,ServletResponse response){
            
        }
        
        //重载toString方法
        public String toString(){
            return this.Url;
        }
    }
    //下面是这个类的要模拟的request对象的方法
    public void setParameter(String pName,String pValue){
        this.parameterMap.put(pName,pValue);
    }
    
    public HashMap<String, String> getParameterMap() {
        return parameterMap;
    }
    
    public String getParameter(String pName){
        return this.parameterMap.get(pName);
    }
    
    public void setAttribute(String pName ,Object pValue){
        this.attributeMap.put(pName,pValue);
    }
    
    public Object getAttribute(String pName){
        
        return this.attributeMap.get(pName);
    }
    public ArrayList<RequestDispatcher> myDispatcherList = new ArrayList<RequestDispatcher> ();
    public RequestDispatcher getRequestDispatcher(String url){
        RequestDispatcher result = new MyDispatcher(url);
        myDispatcherList.add(result);
        return result;
    }
    
    HttpSession mySession = null;
    public HttpSession getSession(){
        if (mySession == null){
            mySession = ServletSessionProxy.createRequesst();
        }
        return mySession;
    }
    
    public HttpSession getSession(boolean flag){
        return getSession();
    }
    
    public String getMethod(){
        return "Post";
    }
    
    private HashMap<String,String[]> values = new HashMap<String,String[]>();
    public void setParameterValues(String key,String[]values){
        this.values.put(key,values);
    }
    public String[] getParameterValues(String key){
        return this.values.get(key);
    }
    
    public static class MyEnumeration implements Enumeration{
        Iterator it;
        MyEnumeration(Iterator p_it){
            it = p_it;
        }
        public boolean hasMoreElements(){
            return it.hasNext();
        }
        public Object nextElement(){
            return it.next();
        }
    }
    public Enumeration getParameterNames(){
        Iterator<String> it = this.parameterMap.keySet().iterator();
        return new MyEnumeration(it);
    }
    
    private String remoteAddr;
    public void setRemoteAddr(String ip){
        this.remoteAddr = ip;
    }
    public String getRemoteAddr(){
        return this.remoteAddr;
    }
    
    private HashMap<String,String> headerMap = new HashMap<String,String>();
    public void setHead(String name,String value){
        this.headerMap.put(name,value);
    }
    public String getHeader(String name){
        return this.headerMap.get(name);
    }
    private String requestUri;
    public void setRequestUri(String uri){
        this.requestUri = uri;
    }
    public String getRequestURI(){
        return this.requestUri;
    }
    public static void main(String[]args)throws Exception{
        try{
            RequestDispatcher d;
            viewAbstract(ServletInputStream.class);
            viewConstructor(ServletInputStream.class);
            ServletRequestProxy requestProxy = new ServletRequestProxy();
            HttpServletRequest request = ServletRequestProxy.createRequest(requestProxy);
            
            requestProxy.setParameter("aa","1111");
            System.out.println(" request.getParameter :"+request.getParameter("aa"));
            
            request.setAttribute("sdf","ddd");
            System.out.println("request.getAttribute():"+request.getAttribute("sdf"));
            
            RequestDispatcher dp= request.getRequestDispatcher("<<<dispatch>>>");
            System.out.print("dispatcher :" + dp);
            
            HttpSession session = request.getSession();
            session.setAttribute("iiii","session test");
            
            session = request.getSession(false);
            System.out.println("session is :"+session.getAttribute("iiii"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
    private static void viewAbstract(Class c){
        Method[] ms = c.getMethods();
        for (Method m:ms){
            if (Modifier.isAbstract(m.getModifiers())){
                System.out.println("--view Abstract"+c.getName()+" --:" + m);
            }
        }
    }
    
    private static void viewConstructor(Class c){
        Constructor[] cns = c.getConstructors();
        //System.out.println("cns length::"+ cns.length);
        for (Constructor cn:cns){
            
            System.out.println("--view Abstract"+c.getName()+" --:" + cn);
            
        }
    }
}
