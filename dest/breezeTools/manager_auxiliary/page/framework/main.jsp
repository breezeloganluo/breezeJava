<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="com.breeze.support.cfg.Cfg"%>
<%@ include  file="../../server/module/auth.jsp"%>
<html>
	<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>breeze在线工具</title>
	
	<!-- basic styles -->

	<link href="../../pageplugin/bootstrap-3.3.5/css/bootstrap.min.css" rel="stylesheet" />
    <link href="../../pageplugin/treeview/bootstrap-treeview.min.css" rel="stylesheet"></link>
	
	<link rel="stylesheet" href="../../pageplugin/codemirror/lib/codemirror.css">
	<link rel="stylesheet" href="../../pageplugin/codemirror/addon/hint/show-hint.css">
	<link rel="stylesheet" href="../../pageplugin/datetimepicker/css/datetimepicker.css" />
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
        

		
		
		
		
		<!--头部的管理条-->
			<script>
						  fileGlobleSetting = [
							{
								name:"调试控制",
								icon:"./img/icon/debugctr.png",
								toolsUrl:"page/debug/debugMgr.jsp"
							},
                            {
							    name:"文件比较",
								initDir: "/",
								icon:"./img/icon/filecompare.png",
								type:"self",
								gadget:"fileCompare"
							},
							{
							    name:"打包工具",
								initDir: "/",
								icon:"./img/icon/package.png",
								type:"self",
								gadget:"package"
							},
							{
							    name:"组件管理",
								initDir: "/",
								icon:"./img/icon/conponent.png",
								type:"self",
								gadget:"componentMgr"
							},
                            {
								  name:"Java编辑器",
								  exp:"|java|",
								  initDir: "/",
								  icon:"./img/icon/java.png",
								  type:"selfedit",
								  gadget:"editJava"
							  
							},
                            {
								  name:"功能需求",
								  exp:"|frs|",
								  initDir: "/",
								  icon:"./img/icon/frs.png",
								  type:"selfedit",
								  gadget:"funsrs"
							  
							},
							{
								  name:"gadget编辑器",
								  exp:"|js|",
								  icon:"./img/icon/editgadget.png",
								  type:"file",
								  initDir: "/",
								  clickSetting: {
									  "link": "点击自身的事件",
									  'newone':"./gadgetCreator.jsp?fileUrl=[fileUrl]",
									  "编辑": "./gadgetCreator.jsp?fileUrl=[fileUrl]"
								  }
							  
							},                
							{
								name:"页面编辑器",
								exp:"|jsp|js|css|",
								icon:"./img/icon/editpage.png",
								type:"file",
								initDir: "/",
								clickSetting: {
									"link": "点击自身的事件",
									'newone':"./htmlCreator.jsp?fileUrl=[fileUrl]",
									"编辑": "./htmlCreator.jsp?fileUrl=[fileUrl]"
								}
							},
							{
								name:"需求管理",
								icon:"./img/icon/srsview.png",
								exp:"|jsp|",
								initDir: "/design/srs/",
								type:"file",
								clickSetting: {
									"link": "点击自身的事件",
									'newone':"./SRSCreator.jsp?fileUrl=[fileUrl]",
									"编辑": "./SRSCreator.jsp?fileUrl=[fileUrl]"
								}
							},
							{
								name:"顺序图编辑",
								icon:"./img/icon/sequence.png",
								exp:"|seq|js|",
								initDir: "design/hld/sequence/",
								type:"file",
								clickSetting: {
									"link": "点击自身的事件",
									'newone':"./sequenceCreator.jsp?fileUrl=[fileUrl]",
									"编辑": "./sequenceCreator.jsp?fileUrl=[fileUrl]"
								}
							},
							{
								name:"顺序图管理",
								icon:"./img/icon/sequence.png",
								exp:"|js|jsp|",
								initDir: "/",
								type:"selfedit",
								gadget:"seqmgr",
                                auth:{
                                    edit:true
                                }
							},
							{
								name:"模拟数据管理",
								icon:"./img/icon/testgadget.png",
								exp:"|jsp|",
								initDir: "/",
								type:"selfedit",
								gadget:"mokemgr",
                                auth:{
                                    edit:true
                                }
							},
							{
								name:"数据库管理",
								icon:"./img/icon/createdb.png",
								exp:"|js|jsp|",
								initDir: "/",
								type:"selfedit",
								gadget:"dbcontrol",
                                auth:{
                                    edit:false,
                                    add:false
                                }
							},
							{
								name:"单文件比较",
								icon:"./img/icon/createdb.png",
								exp:"|js|jsp|",
								initDir: "/",
								type:"selfedit",
								gadget:"oneFileCompare",
                                auth:{
                                    edit:false,
                                    add:false
                                }
							},
							{
								name:"flow编辑器",
								icon:"./img/icon/createflow.png",
								toolsUrl:"./createflow.jsp"
							},
							{
								name:"service调试器",
								icon:"./img/icon/debugservice.png",
								toolsUrl:"./debugService.jsp"
							},
                            {
							    name:"service调试器",
								initDir: "/",
								icon:"./img/icon/conponent.png",
								type:"self",
								gadget:"debugservice"
							},
							{
								name:"service调编辑试器",
								icon:"./img/icon/editservice.png",
								toolsUrl:"./editservice.jsp"
							},
							{
								  name:"Service测试",
								  exp:"|js|",
								  initDir: "/",
								  icon:"./img/icon/servicetest.png",
								  type:"selfedit",
								  gadget:"editServiceTest"
							  
							},
							{
								name:"日志查看",
								icon:"./img/icon/logsview.png",
								toolsUrl:"logsview.jsp"
							},
							{
								name:"查阅上下文",
								icon:"./img/icon/viewcontext.png",
								toolsUrl:"viewContext.jsp"
							},
							{
								name:"创建数据库",
								icon:"./img/icon/createdb.png",
								toolsUrl:"./createdb.jsp"
							},
							{ 
								name:"导出数据",
								icon:"./img/icon/exportmodule.png",
								toolsUrl:"exportModule.jsp"
							},
							{
								name:"导入数据",
								icon:"./img/icon/importmodule.png",
								toolsUrl:"importModule.jsp"
							}
						  ]
		</script>

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
		<!--@main@{}-->

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
        <script src="../../pageplugin/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
	    
		<script >
           seajs.config({
           base: '${B}'
           });
           seajs.use(['./main','./fileCompare',
		   '../componentMgr/componentMgr',
		   '../javaedit/editJava',
		   '../funsrs/funsrs',
           "../seqmgr/seqmgr",
		   "../db/dbcontrol",
		   "../moke/mokemgr",
		   "../service/debugservice",
		   "../package/package",
		   "./oneFileCompare.js"
		   ], function(a) {
           window.FW = a;
           a.go('${S}');
           });
        </script>
	</body>
</html>