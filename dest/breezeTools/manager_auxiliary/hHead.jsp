
<%
	//2016-11-17 15:01 FrankCheng 修复无法进入创建数据库页面问题
	if (session.getAttribute("login") == null && session.getAttribute("toolsUser") == null){
		String[] urlArr = request.getRequestURI().split("/");
		String url = urlArr[urlArr.length -1];
		String qStr = request.getQueryString();
		if (qStr != null){
			url = "./" + url + "?" + qStr;
		}
		session.setAttribute("backurl",url);
		
	   response.sendRedirect("index.jsp");
	   return;
   }
%>