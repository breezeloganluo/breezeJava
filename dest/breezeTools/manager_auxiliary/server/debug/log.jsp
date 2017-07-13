<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="java.io.*"%>
<%@ include  file="../module/result.jsp"%>
<%
//这是一个多功能日志服务操作jsp，参数oper决定了做什么
//log为上报日志
//get为获取日志
//del为清除日志
%>
<%!
    BreezeContext logs = new BreezeContext();
%>
<%
   //!这段代码完成参数获取
    BreezeContext root = ContextMgr.getRootContext();
    //获取import内容
    BreezeContext typeCtx = root.getContextByPath("_R.oper");
    if (typeCtx == null || typeCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，type为空")));
       return;
    }
    String type = typeCtx.toString();
%>

<%
   //!这段代码处理log类型
   if ("log".equals(type)){
      //获取import内容
      BreezeContext logCtx = root.getContextByPath("_R.log");
      if (logCtx == null || logCtx.isNull()){
         response.getWriter().println( genResult(12,new BreezeContext("参数错误，log为空")));
         return;
      }
      this.logs.pushContext(logCtx);
      response.getWriter().println( genResult(0,new BreezeContext("成功")));
    }
%>
<%
   //!这段代码处理log类型
   if ("get".equals(type)){
      response.getWriter().println( genResult(0,this.logs));
   }
%>

<%
   //!这段代码处理log类型
   if ("del".equals(type)){
      this.logs = new BreezeContext();
      response.getWriter().println( genResult(0,new BreezeContext("成功")));
   }
%>