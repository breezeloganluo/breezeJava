<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.framwork.netserver.process.ServerProcessManager" %>
<%@ page import="com.breeze.framwork.servicerg.AllServiceTemplate" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.breezefw.compile.*" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>


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
    PrintWriter wr = new PrintWriter(myOut);
%>
<%
   //！这段主加载代码
   int code = 0;
   try{
      BreezeCompile.INC.loadAndInit(packageStr+'.'+java);
   }
   catch(Exception e){
      e.printStackTrace(wr);
      code = 30;
   }
   if (code == 0){
     wr.print("操作成功");
   }
   wr.close();

   
   
%>
<%
   //!这一段进行flow初始化和service的初始化
   if (code == 0){
     ServerProcessManager.INSTANCE.init(Cfg.getCfg().getRootDir() , "WEB-INF/classes/flow/");
     AllServiceTemplate.INSTANCE.init(Cfg.getCfg().getRootDir(), "WEB-INF/classes/service/");
   }
%>
<%
   //!返回结果
   response.getWriter().println( genResult(code,new BreezeContext(myOut.toString())));
%>