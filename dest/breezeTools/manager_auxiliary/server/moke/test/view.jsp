<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.framwork.databus.ContextTools" %>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%
    BreezeContext mokeCtx = null;
    synchronized(ContextMgr.global){
       mokeCtx = ContextMgr.global.getContext("moke");
       if (mokeCtx == null || mokeCtx.isNull() || "true".equals(request.getParameter("clear"))){
           mokeCtx = new BreezeContext();
           ContextMgr.global.setContext("moke",mokeCtx);
       }
    }
    String result = "没有数据";
    if (mokeCtx != null && !mokeCtx.isNull()){
       result = ContextTools.getJsonString(mokeCtx,null);
    }
%>
<a href="./view.jsp?clear=true">清除</a><a href="./view.jsp">刷新</a><br/>
<%=result%>