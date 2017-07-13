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
   //！本jsp只是将上传文件的进度信息返回给客户端而已
%>
<%
   Object result = session.getAttribute("uploadProgress");
   if (result == null){
       response.getWriter().println( genResult(11,new BreezeContext("没有进度信息")));
       return;
   }
   response.getWriter().println( genResult(0,new BreezeContext(result)));
%>