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
<%@ page import="com.breeze.support.tools.Md5"%>

<%

   String host = request.getRemoteHost();
   if (request.getHeader("Host") != null){
       host = request.getHeader("Host");
   }
   String app = this.getServletContext().getContextPath();
   String projectUrl = "http://"+host+app+"/";
        
        
   //这段代码获取参数
   String sig = request.getParameter("sig");
   String timeStr = request.getParameter("time");
   String uid  = request.getParameter("uid");
   if (sig == null || timeStr == null || uid == null){
        
        projectUrl = java.net.URLEncoder.encode(projectUrl,"utf-8");   
        response.sendRedirect("http://weima.joinlinking.com/page/stylepc/customer/ssoLogin.jsp?backhost="+projectUrl);
        return;
   }
%>

<%
   String errMsg = null;
   long time = Long.parseLong(timeStr);
   if (System.currentTimeMillis() - 10*60*1000 >= time){
       errMsg = "登录超时，请重新登录";
   }
   
   if (errMsg == null){
       String path1 = CmsIniter.CMSPARAMPRIFIX;
	   BreezeContext tmpObjCtx = ContextMgr.global.getContextByPath(path1);
       BreezeContext ssoKeyCtx = tmpObjCtx.getContext("ssoKey4tools");
       String key = "";
       if (ssoKeyCtx != null && !ssoKeyCtx.isNull()){
           key = ssoKeyCtx.toString();
       }
       
       String value = Md5.getMd5Str(key + '.' +  projectUrl + '.' + uid + "." + time);
       
       if (value.equals(sig)){
            session.setAttribute("toolsUser", uid);
            Object backurl = session.getAttribute("backurl");
            if (backurl != null){
                response.sendRedirect(backurl.toString());
                session.removeAttribute("backurl");
                return;
            }
            response.sendRedirect("./page/framework/main.jsp");
            return;
       }else{
           errMsg = "登录验证失败，请重新登录";
       }
   }
%>
<%=errMsg%>