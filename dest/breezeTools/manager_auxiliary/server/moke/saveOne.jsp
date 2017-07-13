<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.support.tools.FileTools"%>

<%@ include  file="../module/result.jsp"%>
<%
   /*!
   * 本类完成对模拟数据的保存，
   * 保存完后，会更新内存，并且更新最后的更新时间
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
    
    
    BreezeContext sigCtx = root.getContextByPath("_R.sig");
    if (sigCtx == null || sigCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，sig为空")));
       return;
    }
    String sig = sigCtx.toString();
    
    
    BreezeContext contentCtx = root.getContextByPath("_R.content");
    if (contentCtx == null || contentCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，content为空")));
       return;
    }
    
    
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
%>

<%
    //!这一段设置内存
    BreezeContext jspCtx = mokeCtx.getContext(url);
    if (jspCtx==null || jspCtx.isNull()){
        jspCtx = new BreezeContext();
        mokeCtx.setContext(url,jspCtx);
    }
    jspCtx.setContext(sig,contentCtx);
    contentCtx.setContextByPath("evn.lasttime",new BreezeContext(System.currentTimeMillis()));
    
%>

<%
    //!这一段写入文件
    String content = ContextTools.getJsonString(contentCtx,null);
    String fileDir = baseDir+"/manager_auxiliary/data/moke/"+url+"/"+sig+".mok";
    FileTools.writeFile(fileDir,content,"UTF-8");
%>
<%
    //!这一段返回结果
    response.getWriter().println( genResult(0,new BreezeContext("ok")));
    return;
%>