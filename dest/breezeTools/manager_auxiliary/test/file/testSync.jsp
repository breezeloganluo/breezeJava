<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<jsp:include page="/page/allhead.jsp"/>
<!DOCTYPE html>
<html>

	<head>

	</head>

	<body>
从服务器获取文件
		<form method="post" action="http://fwdev.joinlinking.com/fwdev///manager_auxiliary/server/file/syncoper.jsp">
			<textarea name="data" style="width:100%;height:200px">
			{
			  "name": "syncoper",
			  "package": "file",
			  "param": {
			    "operType": "appFile",
			    "param": {
			    	"fileName":"bbb"
			    }
			  }
			}
		 </textarea><br/>
		 <input type="submit" value="ok"/>
		</form>
		 <br/>
		 
上传全新文件
		<form method="post" action="http://fwdev.joinlinking.com/fwdev///manager_auxiliary/server/file/syncoper.jsp">
			<textarea name="data" style="width:100%;height:300px">
		   {
	 		  "name": "syncoper",
			  "package": "file",
			  "param": {
			    "operType": "setAllFile",
			    "param": {
			    	"fileName":"bbb",
			    	"content":[
			    	  {
			    	  	"lockKey":"aa",
			    	  	"content":"fdddaa"
			    	  },
			    	   {
			    	  	"lockKey":"bb",
			    	  	"content":"fdddaa"
			    	  }
			    	]
			    }
			  }
			}
		 </textarea><br/>
		 <input type="submit" value="ok"/>
				</form>
		 <br/>
 
创建全局锁、对象锁
		<form method="post" action="http://fwdev.joinlinking.com/fwdev///manager_auxiliary/server/file/syncoper.jsp">
			<textarea name="data" style="width:100%;height:300px">
		 {
	 		  "name": "syncoper",
			  "package": "file",
			  "param": {
			    "operType": "appLock",
			    "param": {
			    	"fileName":"bbb",
			    	"lockKey":"aa",
			    	"version":"1"
			    }
			  }
		 }
		 </textarea><br/>
		 <input type="submit" value="ok"/>
				</form>
		 <br/>
		 
向服务器提交文件
		<form method="post" action="http://fwdev.joinlinking.com/fwdev///manager_auxiliary/server/file/syncoper.jsp">
			<textarea name="data" style="width:100%;height:300px">
		 {
	 		  "name": "syncoper",
			  "package": "file",
			  "param": {
			    "operType": "checkin",
			    "param": {
			    	"fileName":"bbb",
			    	"lockKey":"aa",
			    	"version":"1",
			    	"wholdVersion":"1",
			    	"content":[
			    	  {
			    	  	"lockKey":"aa",
			    	  	"content":"fdddaa"
			    	  },
			    	   {
			    	  	"lockKey":"bb",
			    	  	"content":"fdddaa"
			    	  }
			    	]
			    }
			  }
		 }
		 </textarea><br/>
		 <input type="submit" value="ok"/>
				</form>
		 <br/>
	</body>

</html>