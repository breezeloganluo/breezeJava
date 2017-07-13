<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.lang.*"%>
<%@page import="java.lang.reflect.Field"%>
<%@page import="java.lang.reflect.Method"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="java.util.regex.*" %>
<%@ include  file="../module/result.jsp"%>
<%@ page  import="java.io.* "%>
<%@ page import="java.net.URLConnection"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.io.File" %>
<%@ page import="java.io.InputStream" %>

<%
   //！本jsp输入的是参数url，根据url到远程下载，下载完后将文件名按照客户端的参数要求保存起来
   //文件名fileName也是客户端上传上来的
%>
<% 
   //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    //获取import内容
    BreezeContext urlCtx = root.getContextByPath("_R.url");
    if (urlCtx == null || urlCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，url为空")));
       return;
    }
    String url = urlCtx.toString();
    
    //获取fileName内容
    BreezeContext fileNameCtx = root.getContextByPath("_R.fileName");
    
    if (fileNameCtx == null || fileNameCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，fileName为空")));
       return;
    }
    String fileName = fileNameCtx.toString();
    String baseDir  = Cfg.getCfg().getRootDir() + "/";
%>

<%  

    //!这段代码根据url利用java的http客户端发请求到远端下载，并保存到fileName文件中
    
   //定义字节输入与输出流
     InputStream fileInput=null;
     FileOutputStream fileOutput =null;
     StringBuilder message = new StringBuilder();
     int code = 0;
    try{
        URL conurl = new URL(url);  
	    URLConnection conn = conurl.openConnection();  
	    fileInput = conn.getInputStream(); 
        fileName = baseDir+fileName;
    	File file = new File(fileName);
    	File outputFile = new File(fileName);

    	if(!outputFile.isDirectory()){
    		String parentPath = outputFile.getParent();
    		File pathFile = outputFile.getParentFile();
    		if(pathFile == null){
    			pathFile = new File(parentPath);
    		}
    		pathFile.mkdirs();
    	}
        
        fileOutput = new FileOutputStream(fileName);
        
        byte[] buffer = new byte[4028];
        int len = 0;
        while(true){
           len = fileInput.read(buffer);
           if (len < 0){
             break;
           }
           fileOutput.write(buffer,0,len);
        }
        
        message.append("下载成功");
    }catch(Exception e){

      code = 22;
      //定义一个流
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
     //把错误堆栈储存到流中
      e.printStackTrace(new PrintStream(bos));
      message.append("下载失败,原因： ");
      message.append(bos.toString());
     
    }finally{
       if(fileInput != null){
         fileInput.close();
       }
       if(fileOutput != null){
         fileOutput.close();
       }
    }
   
%>


<%
   //!这段代码返回结果
   response.getWriter().println(genResult( code,new BreezeContext(message.toString())));
%>
