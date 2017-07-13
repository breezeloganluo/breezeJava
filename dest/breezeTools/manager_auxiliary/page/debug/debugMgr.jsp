<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="com.breeze.support.cfg.Cfg"%>
<%@page import="java.util.UUID"%>
<html>
	<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title></title>
	
	<!-- basic styles -->

	<link href="../../pageplugin/bootstrap-3.3.5/css/bootstrap.min.css" rel="stylesheet" />
    <link href="../../pageplugin/treeview/bootstrap-treeview.min.css" rel="stylesheet"></link>
	
	<link rel="stylesheet" href="../../pageplugin/codemirror/lib/codemirror.css">
	<link rel="stylesheet" href="../../pageplugin/codemirror/addon/hint/show-hint.css">
	<style>
	body {
	  padding-top: 50px;
	}
	.starter-template {
	  padding: 40px 15px;
	  text-align: center;
	}
	</style>
	</head>
	<body>
		<%
		String baseUrl = this.getServletContext().getContextPath();
		String configUrlPrefix = Cfg.getCfg().getString("siteprefix");
		if (configUrlPrefix !=null && !configUrlPrefix.equals("--")){
			baseUrl = configUrlPrefix;
		}
		if ("/".equals(baseUrl)){
			request.setAttribute("B","/");
			baseUrl = "";
		}else{
			request.setAttribute("B",baseUrl+'/');
		}
		request.setAttribute("S",request.getAttribute("B")+"breeze/framework/jsp/BreezeFW.jsp");
		%>
        

		
		

		<nav class="navbar navbar-inverse navbar-fixed-top">
		  <div class="container">
			<div class="navbar-header">
			  <a class="navbar-brand" href="#">Breeze在线工具系列</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
			  <ul class="nav navbar-nav">
				<li class="active"><a href="#">首页</a></li>
				<li><a href="#about">插件管理</a></li>
			  </ul>
			  <div class="pull-right" id="topIcon_main">

			  </div>
			</div><!--/.nav-collapse -->
		  </div>
		</nav>
		
		
		
		<div class="container-fluid FWApp" style="height:100%;" id="main">
		<!--@debugMgr@{
        }-->

		</div>
        
        <script src="../../../breeze/lib/js/jquery.js"></script>
        <script src="../../../breeze/lib/js/sea.js"></script>
        <script src="../../../breeze/lib/js/seajs-text.js"></script>
		<script src="../../../config/config.jsp"></script>
        
        <script src="../../pageplugin/treeview/bootstrap-treeview.min.js"></script>
		
		<script src="../../pageplugin/codemirror/lib/codemirror.js"></script>
		<script src="../../pageplugin/codemirror/addon/hint/show-hint.js"></script>
		<script src="../../pageplugin/codemirror/mode/javascript/javascript.js"></script>
		<script src="../../pageplugin/codemirror/mode/markdown/markdown.js"></script>
    
		<script >
           seajs.config({
           base: '${B}'
           });
           seajs.use(['./debugMgr'], function(a) {
           window.FW = a;
           a.go('${S}');
           });
        </script>
	</body>
</html>