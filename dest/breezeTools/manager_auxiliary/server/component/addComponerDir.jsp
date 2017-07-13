<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.io.File"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>

<%@ include  file="../module/result.jsp"%>
<% 
   //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    //获取cid内容
    BreezeContext dirCtx = root.getContextByPath("_R.dir");
    if (dirCtx == null || dirCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，dir为空")));
       return;
    }
    String dir = dirCtx.toString();
%>

<%
    //!创建目录
    File f = new File( Cfg.getCfg().getRootDir() + dir);
    System.out.println("dir:" + f);
    f.mkdirs();
%>
<%
    response.getWriter().println( genResult(0,new BreezeContext("操作成功")));
%>