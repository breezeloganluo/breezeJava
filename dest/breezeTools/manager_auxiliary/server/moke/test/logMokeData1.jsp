<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<jsp:include page="/page/allhead.jsp"/>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8" />
		<title>测试正常的上传一个参数</title>
	</head>

	<body>
       <form action="${B}/breeze/framework/jsp/BreezeFW.jsp" method="post">
<textarea name="data" style="width:100%;height:500px;">
[
    {
        "name":"logMokeData",
        "package":"moke",
        "param":{
            "url":"abc_dd_s.jsp",
            "qstr":"a=1&b=2",
            "gadgeturl":"a/b/ss.js",
            "gadgetName":"ss",
            "funName":"mmmm",
            "logObj":{
                "name":"input",
                "session":"345555",
                "logObj":[3,555]
            }
        }
     }
]

</textarea>
<input type="submit" value="提交"/>
       </form>
		
	</body>

</html>