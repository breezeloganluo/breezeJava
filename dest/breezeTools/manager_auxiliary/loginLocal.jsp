<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@page import="com.breeze.framwork.databus.BreezeContext"%>
<%@page import="com.breezefw.service.cms.CmsIniter"%>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@page import="com.breeze.support.tools.*"%>
<%@page import="com.breeze.support.cfg.*"%>
<%@page import="java.util.regex.*"%>
<%@ page import="java.util.*"%>
<%
	BreezeContext password4tools = ContextMgr.global.getContextByPath(CmsIniter.CMSPARAMPRIFIX);
	String rightkey = "1qaz@WSX";
	
	if(password4tools != null && !password4tools.isNull() && password4tools.getContext("password4tools")!=null && !password4tools.getContext("password4tools").isNull()){
		if(!password4tools.getContext("password4tools").getData().toString().equals("--")){
			rightkey = password4tools.getContext("password4tools").getData().toString();	
		}
	}

	boolean needLogin = true;
	if (session.getAttribute("toolsUser") != null) {
		needLogin = false;
	} else {
		String password = request.getParameter("password");
		if (rightkey.equals(password)) {
			session.setAttribute("toolsUser", "local");
			needLogin = false;
		}else{
			session.removeAttribute("toolsUser");
		}
	}
    if (!needLogin){
        Object backurl = session.getAttribute("backurl");
        if (backurl != null){
            response.sendRedirect(backurl.toString());
            session.removeAttribute("backurl");
            return;
        }
        response.sendRedirect("./page/framework/main.jsp");
        return;
    }
%>






<html>
	<head>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>breeze在线工具</title>
	
	<!-- basic styles -->

	<link href="./pageplugin/bootstrap-3.3.5/css/bootstrap.min.css" rel="stylesheet" />
    <link href="./pageplugin/treeview/bootstrap-treeview.min.css" rel="stylesheet"></link>
	
	<link rel="stylesheet" href="./pageplugin/codemirror/lib/codemirror.css">
	<link rel="stylesheet" href="./pageplugin/codemirror/addon/hint/show-hint.css">
    
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
    
		<nav class="navbar navbar-inverse navbar-fixed-top">
		  <div class="container">
			<div class="navbar-header">
			  <a class="navbar-brand" href="#">欢迎,Breeze在线工具，请输入密码登录</a>
			</div>
            
			
            
            
		  </div>
		</nav>
		
        
   
		
		
		<div class="container-fluid " style="height:100%;margin-top:60px;" id="main">
            
			<div class="row">
             <div class="col-md-3"></div>
             <div class="col-md-6">
                     <form role="form" method="post">
                        <div class="form-group">
                          <label for="exampleInputPassword1">请输入密码</label>
                          <input type="password" class="form-control" name="password" placeholder="Password">
                        </div>

                        <button type="submit" class="btn btn-default">确认提交</button>
                     </form>
             
             </div>
             <div class="col-md-3"></div>
          </div>
          
          
		</div>
        
	</body>
</html>


	
