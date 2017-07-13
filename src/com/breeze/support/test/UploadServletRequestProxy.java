/*
 * UploadServletRequestProxy.java
 *
 * Created on 2008年1月24日, 上午9:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.test;
import java.util.*;

import java.lang.reflect.*;
import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

/**
 *
 * @author happy
 */
/**
 *
 * @author happy
 */
public class UploadServletRequestProxy extends ServletRequestProxy{
    private String boundary = "---------------------------7d73d8192026e";
    
    
    private String contentType = "multipart/form-data; boundary="+boundary;
    private String method = "post";
    
    private HashMap<String,byte[]>fileMap = new HashMap<String,byte[]>();
    
    private byte[]content = new byte[50000000];
    private int contentLen;
    private String defaultContentType = "text/plain";
    private HashMap<String,String>contenttypeMap = new HashMap<String,String>();
    
    private boolean finished = false;
    
    /** Creates a new instance of UploadServletProxy */
    public UploadServletRequestProxy() {
    }
    
    /**
     *设置参数
     */
    public void setParameter(String pName,String pValue){
        if(finished){
            throw new RuntimeException("finished setting");
        }
        super.parameterMap.put(pName,pValue);
    }
    /**
     *设置文件,要求文件名和上面的参数名相同,而上面的值为文件名
     */
    public void setFiles(String name,byte[]file){
        if(finished){
            throw new RuntimeException("finished setting");
        }
        this.fileMap.put(name,file);
    }
    
    /**
     *设置文件的内容类型
     */
    public void setFilesContentType(String name,String type){
        if(finished){
            throw new RuntimeException("finished setting");
        }
        this.contenttypeMap.put(name,type);
    }
    
    /**
     *完成所有设置
     */
    public void finished()throws Exception{
        //处理普通参数
        Set<String> nameSet = super.parameterMap.keySet();
        String cd = "Content-Disposition: form-data; name=\"";
        String finishedStr = "--"+boundary+"--\r\n";
        
        this.contentLen = 0;
        for(String name:nameSet){
            byte[] file = this.fileMap.get(name);
            String value = super.parameterMap.get(name);
            
            StringBuilder sb = new StringBuilder();
            sb.append("--").append(this.boundary).append("\r\n");
            sb.append(cd).append(name).append("\"");
            if (file == null){
                //不是文件
                sb.append("\r\n\r\n");
                sb.append(value).append("\r\n");
            } else{
                //是文件
                sb.append("; filename=\""+value+"\"").append("\r\n");                
                String type = this.contenttypeMap.get(name);
                if (type == null){
                    type = this.defaultContentType;
                }
                sb.append("Content-Type: ").append(type).append("\r\n\r\n");
            }
            
            //将内容拷贝到数组中
            byte[] tmpContent = sb.toString().getBytes();
            System.arraycopy(tmpContent,0,this.content,this.contentLen,tmpContent.length);
            this.contentLen+=tmpContent.length;
            
            //如果是文件,将文件拷入到内容数组中
            if (file != null){
                tmpContent = file;
                System.arraycopy(tmpContent,0,this.content,this.contentLen,tmpContent.length);
                this.contentLen+=tmpContent.length;
                this.content[this.contentLen++] = 13;
                this.content[this.contentLen++] = 10;
            }
        }
        
        byte[] tmpContent = finishedStr.getBytes();
        System.arraycopy(tmpContent,0,this.content,this.contentLen,tmpContent.length);
        this.contentLen+=tmpContent.length;
        
        this.finished = true;
    }
    
    public String getMethod(){
        return this.method;
    }
    
    public String getContentType(){
        return this.contentType;
    }
    
    public int getContentLength(){
        return this.contentLen;
    }
    
    public ServletInputStream getInputStream() throws java.io.IOException{
        ByteArrayInputStream in = new ByteArrayInputStream(this.content,0,this.contentLen);
        return new ServletRequestProxy.ServletInputStreamCreater(in);
    }
}