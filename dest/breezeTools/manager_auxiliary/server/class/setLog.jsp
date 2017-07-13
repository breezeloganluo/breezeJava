<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="com.breeze.base.log.BreezeLogQuere"%>
<%@ include  file="../module/result.jsp"%>
<%
   //！本jsp是进行设置日志标识用的
%>
<%
    //！这段代码主要是获取客户端参数用
    BreezeContext root = ContextMgr.getRootContext();
    //获取threadSignal内容
    BreezeContext threadSignalCtx = root.getContextByPath("_R.threadSignal");
    if (threadSignalCtx == null || threadSignalCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，threadSignal为空")));
       return;
    }
    String threadSignal = threadSignalCtx.toString();
    
%>



<%
   //!这段代码获取日志信息 
    BreezeLogQuere.getInc().setLog(threadSignal);

   response.getWriter().println( genResult(0,new BreezeContext("操作成功")));
%>
