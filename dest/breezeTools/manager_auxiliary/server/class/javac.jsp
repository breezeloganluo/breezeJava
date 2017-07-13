<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.breezefw.compile.*" %>
<%@ include  file="../module/result.jsp"%>
<%
    //！这段代码主要是获取客户端参数用
    BreezeContext root = ContextMgr.getRootContext();
    //获取class内容
    BreezeContext javaCtx = root.getContextByPath("_R.java");
    if (javaCtx == null || javaCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，java为空")));
       return;
    }
    String java = javaCtx.toString();
    
    
    BreezeContext packageCtx = root.getContextByPath("_R.package");
    if (packageCtx == null || packageCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，package为空")));
       return;
    }
    String packageStr = packageCtx.toString();
    
%>
<%
    //!这一段要做成java内存流
    ByteArrayOutputStream myOut = new ByteArrayOutputStream();
    OutputStreamWriter wr = new OutputStreamWriter(myOut);
%>
<%
   //！这段主编译代码
   BreezeCompile.INC.searchAllJavaSrc(packageStr.replaceAll("\\.","/"));
   boolean result = BreezeCompile.INC.compileFile(packageStr+'.'+java,wr);
   wr.close();
   int code = result?0:30;
   
   response.getWriter().println( genResult(code,new BreezeContext(myOut.toString())));
%>