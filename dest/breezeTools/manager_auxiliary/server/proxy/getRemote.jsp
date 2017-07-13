<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.ContextTools"%>
<%@ page import="com.breezefw.ability.http.HTTP"%>
<%
BreezeContext root = ContextMgr.getRootContext();
String postData = "["+ContextTools.getJsonString(root.getContext("_R") ,null)+"]";
System.out.println("param:"+postData);
%>
<%
        //发请求
		String url = "http://www.joinlinking.com/breeze.brz";
		String resultHttp = HTTP.getInc().sendHttpPost(url, postData);
		resultHttp = resultHttp.replaceAll("(^\\s*\\[|\\]\\s*$)","");
%>
<%=resultHttp%>