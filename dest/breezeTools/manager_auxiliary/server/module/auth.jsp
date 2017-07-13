<%@page import="com.breeze.framwork.databus.*"%>
<%@page import="com.breezefw.service.cms.*"%>
<%@page import="com.breeze.support.cfg.Cfg"%>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
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
    
    
    request.setAttribute("_","$");
    request.setAttribute("S",request.getAttribute("B")+"breeze/framework/jsp/BreezeFW.jsp");

	if (session.getAttribute("toolsUser") == null){
		String url = request.getRequestURI();
		String qStr = request.getQueryString();
		if (qStr != null){
			url = url + "?" + qStr;
		}
		
		
        //下面根据不同的配置导向到不同的登录页面
        String path1 = CmsIniter.CMSPARAMPRIFIX;
	    BreezeContext tmpObjCtx = ContextMgr.global.getContextByPath(path1);
        BreezeContext loginWayCtx = tmpObjCtx.getContext("loginWay4tools");
        if (loginWayCtx == null || loginWayCtx.isNull()){
             response.sendRedirect(baseUrl + "/manager_auxiliary/loginLocal.jsp");
             return;
        }
        String loginWay = loginWayCtx.toString();
        if ("weima".equals(loginWay)){
             response.sendRedirect(baseUrl + "/manager_auxiliary/loginWeima.jsp");
             return;
        }
        response.sendRedirect(baseUrl + "/manager_auxiliary/loginLocal.jsp");
        return;
   }
%>