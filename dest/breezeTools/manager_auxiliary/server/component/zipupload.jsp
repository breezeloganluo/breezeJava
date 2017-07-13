<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="net.lingala.zip4j.core.ZipFile" %>
<%@ page import="net.lingala.zip4j.exception.ZipException" %>
<%@ page import="net.lingala.zip4j.util.Zip4jConstants" %>
<%@ page import="net.lingala.zip4j.model.ZipParameters" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ include  file="../module/result.jsp"%>
<%
   //！本jsp有两个作用，首先要把文件进行zip压缩，然后发送http的post请求，完成打包工作
%>
<%
    //!本段代码优先判断是否有进程正在上传中
    String pre = (String)session.getAttribute("uploadProgress");
    if (pre!= null && pre.indexOf("成功")<0 && pre.indexOf("完成")<0 && pre.indexOf("失败")<0){
        response.getWriter().println( genResult(40,new BreezeContext("之前上传事务还未结束，请耐心等待")));
        return;
    }
%>
<% 
   //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    //获取cid内容
    BreezeContext cidCtx = root.getContextByPath("_R.cid");
    if (cidCtx == null || cidCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，cid为空")));
       return;
    }
    String cid = cidCtx.toString();
    
	//获取title内容
    BreezeContext titleCtx = root.getContextByPath("_R.title");
    if (titleCtx == null || titleCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，title为空")));
       return;
    }
    String title = titleCtx.toString();
    
    //获取version内容
    BreezeContext versionCtx = root.getContextByPath("_R.version");
    if (versionCtx == null || versionCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，version为空")));
       return;
    }
    String version = versionCtx.toString();
    session.setAttribute("uploadProgress","1/5正在压缩");
%>

<%
     //!这一段负责进行压缩
     String srcdir = Cfg.getCfg().getRootDir() + "/manager_auxiliary/data/componentMgr/c_" + cid + "_" + title;
     //获取源压缩路径
     String destdir = Cfg.getCfg().getRootDir() + "/manager_auxiliary/data/componentMgr/c_" + cid + "_" + title+"_up.zip";
     //获取目标压缩路径
     System.out.println("srcdir:"+srcdir);
     System.out.println("destdir:"+destdir);
     //进行zip压缩
     ZipFile zipFile = new ZipFile(destdir);
     File srcF = new File(srcdir);

     ZipParameters parameters = new ZipParameters();
     parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
     parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

     for (File f : srcF.listFiles()){
         if (f.isDirectory()){
             zipFile.addFolder(f, parameters);
         }
         else{
             zipFile.addFile(f, parameters);
         }
     }
     session.setAttribute("uploadProgress","2/5压缩完毕");
%>

<%
     InputStream fileIn = null;
     OutputStream sentOut = null;
     BufferedReader reader = null;
     
     File sendZip = new File(destdir);
     System.setProperty("sun.net.client.defaultConnectTimeout", "30000");  
     System.setProperty("sun.net.client.defaultReadTimeout", "30000");
     long totalLen = sendZip.length();
     
     try{
       //!这一段负责将文件发送到服务端
       URL localURL = new URL("http://www.joinlinking.com/component/upload.jsp");
       URLConnection connection = localURL.openConnection();
       HttpURLConnection httpURLConnection = (HttpURLConnection)connection;

       httpURLConnection.setDoOutput(true);
       httpURLConnection.setRequestMethod("POST");
       httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
       httpURLConnection.setRequestProperty("Content-Type", "application/zip");
       httpURLConnection.setRequestProperty("Content-Length", String.valueOf(totalLen));
       httpURLConnection.setRequestProperty("cid",cid);
       httpURLConnection.setRequestProperty("version",version);

       fileIn = new BufferedInputStream(new FileInputStream(sendZip));
       sentOut = httpURLConnection.getOutputStream();
       byte[] buff = new byte[2048];
       int sentTotal = 0;
       while(true){
           int len = fileIn.read(buff);
           if (len < 0){
              break;
           }
           sentOut.write(buff,0,len);
           sentOut.flush();
           sentTotal += len;
           String viewProgress = String.valueOf(sentTotal)+"/"+totalLen;
           
       }
       session.setAttribute("uploadProgress","3/5上传中,总共"+totalLen+"B");

       if (httpURLConnection.getResponseCode() >= 300) {
           response.getWriter().println(genResult(21,new BreezeContext("sending exception!")));
       }

       InputStream receiveIn = httpURLConnection.getInputStream();
       InputStreamReader receiveReader = new InputStreamReader(receiveIn);
       reader = new BufferedReader(receiveReader);
       StringBuilder resultBuffer = new StringBuilder();

       String tempLine = "";
       while ((tempLine = reader.readLine()) != null) {
           resultBuffer.append(tempLine);
       }
       
       session.setAttribute("uploadProgress","4/5上传中");

       String sentResult = resultBuffer.toString().trim();
       if (!"succ".equals(sentResult)){
           session.setAttribute("uploadProgress","4/5上传失败了对端结果:"+sentResult);
           response.getWriter().println(genResult(22,new BreezeContext(sentResult)));
           return;
       }
     }
     catch(Exception e){
       e.printStackTrace();
       session.setAttribute("uploadProgress","4/5上传失败了抛出异常:"+e);
       response.getWriter().println(genResult(31,new BreezeContext(e.toString())));
     }
     finally {
        if (fileIn!=null){
            fileIn.close();
        }
        
        if (sentOut != null){
            sentOut.close();
        }
        
        if (reader != null){
            reader.close();
        }
     }
     session.setAttribute("uploadProgress","5/5完成");
%>


<%
   //!这段代码返回结果
   BreezeContext result = new BreezeContext("ok");
   
   //加密设置返回的内容
   response.getWriter().println(genResult(0,result));
%>