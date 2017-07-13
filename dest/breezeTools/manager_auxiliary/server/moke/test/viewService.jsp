<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="com.breezefw.shell.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.framwork.databus.ContextTools" %>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%


       if ( "true".equals(request.getParameter("clear"))){
		   LogService.clear();
       }

    String result = "没有数据";
    if (LogService.memLog != null && !LogService.memLog.isNull()){
       result = ContextTools.getJsonString(LogService.memLog,null);
    }
%>
<a href="./viewService.jsp?clear=true">清除</a><a href="./viewService.jsp">刷新</a><br/>
<textarea style="width:100%;height:800px">
<%=result%>
</textarea>