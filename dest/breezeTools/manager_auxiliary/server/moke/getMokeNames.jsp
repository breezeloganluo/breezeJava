<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>

<%@ include  file="../module/result.jsp"%>
<%
   /*!
   * 本类完成获取当前所有模拟数据名称，首先遍历内存，然后遍历文件，再将两者结果混合合并返回客户端
   */
%>
<%
   //!这段代码完成参数获取
    BreezeContext root = ContextMgr.getRootContext();
    //获取import内容
    BreezeContext urlCtx = root.getContextByPath("_R.url");
    if (urlCtx == null || urlCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，url为空")));
       return;
    }
    String url = urlCtx.toString();
    
    
    //获取全局moke对象
    BreezeContext mokeCtx = null;
    synchronized(ContextMgr.global){
       mokeCtx = ContextMgr.global.getContext("moke");
       if (mokeCtx == null){
           mokeCtx = new BreezeContext();
           ContextMgr.global.setContext("moke",mokeCtx);
       }
    }
    
    
    String baseDir  = Cfg.getCfg().getRootDir() + "/";
    
    BreezeContext result = new BreezeContext();
    HashSet<String> s = new HashSet<String>();
%>
<%
    //!这一段获取内存数据
    BreezeContext jspCtx = mokeCtx.getContext(url);
    if (jspCtx!=null && !jspCtx.isNull() && !jspCtx.isEmpty()){
        for (String n : jspCtx.getMapSet()){
             result.pushContext(new BreezeContext(n));
             s.add(n);
        }
    }
%>
<%
    //!这一段获取文件内容
    String fileDir = baseDir+"/manager_auxiliary/data/moke/"+url;
    File f = new File(fileDir);
    File[] farry = f.listFiles();
    for (int i=0;farry != null && i<farry.length;i++){
       File ff = farry[i];
       String one = ff.getName().replaceAll("\\.mok","");
       if (s.contains(one) || ff.isDirectory() ){
           continue;
       }
       result.pushContext(new BreezeContext(one));
    }
%>
<%
    //!这一段返回结果;
    response.getWriter().println( genResult(0,result));
    return;
%>