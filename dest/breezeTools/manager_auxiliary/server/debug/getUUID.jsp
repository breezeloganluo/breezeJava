<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.support.tools.FileTools"%>
<%@page import="java.util.UUID"%>

<%@ include  file="../module/result.jsp"%>
<%
response.getWriter().println( genResult(0,new BreezeContext(UUID.randomUUID().toString())));
%>