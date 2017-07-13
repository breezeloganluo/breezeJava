<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include  file="/page/allhead.jsp"%>
<html>

	<head>
		<meta charset="utf-8" />
		<title>测试版本获取</title>
		
	</head>

	<body>
		<div>
            <form action="${B}/breeze/framework/jsp/BreezeFW.jsp" method="post" onsubmit="return gosubmit();">
                开始时间:<input type="text" id="s"/>yyyy-mm-dd&nbsp;hh:mm<br/>
                结束时间:<input type="text" id="e"/>yyyy-mm-dd&nbsp;hh:mm</br>
                用户id:<input type="text" id="u"/><br/>
                <input type="hidden" name="data" id="data"/>
                <input type="submit" value="ok" />
            </form>
		</div>
		

		<script >
              var gosubmit = function(){
                  var s = document.getElementById("s").value;
                  var e = document.getElementById("e").value;
                  var u = document.getElementById("u").value;
                  
                  var postdata ='[{"name":"getFileRecord","package":"file","param":{"start":"_1","end":"_2","uid":"_3"}}]';
                  postdata = postdata.replace(/_1/,getTime(s));
                  postdata = postdata.replace(/_2/,getTime(e));
                  postdata = postdata.replace(/_3/,u);
                  document.getElementById("data").value = postdata;
                  alert(postdata);
                  return true;
              }
              
              var getTime = function(t){
                  var execResult = /(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2})/i.exec(t);
                  if (execResult == null){
                      return null;
                  }
                  var date = new Date();
                  date.setYear(execResult[1]);
                  date.setMonth(execResult[2]-1);
                  date.setDate(execResult[3]);
                  date.setHours(execResult[4]);
                  date.setMinutes(execResult[5]);
                  return date.getTime();
              }
        </script>
        
	</body>

</html>