<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<jsp:include page="/page/allhead.jsp"/>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8" />
		<title></title>
		<meta name="description" content="Draggabble Widget Boxes &amp; Containers" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<!--@config@{"./":"/page/style/resource/"}-->

	</head>

	<body>
		<div id="a" class="FWApp">
        <!--@testAll@{param1:'paramvalue'}-->
			
			
		</div>
		
		
		
		
		<script src="${B}breeze/lib/js/jquery.js"></script>
        <script src="${B}breeze/lib/js/sea.js"></script>
        <script src="${B}breeze/lib/js/seajs-text.js"></script>
        
        <script src="${B}breeze/framework/js/BreezeFW.js"></script>
    
		<script >
           seajs.config({
           base: '${B}'
           });
           seajs.use(['./testAll'], function(a) {
           a.go('${S}');
           window.FW = a;
           });
        </script>
	</body>

</html>