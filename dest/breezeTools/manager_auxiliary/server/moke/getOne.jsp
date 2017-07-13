<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.support.tools.FileTools"%>
<%@ page import="java.io.*"%>

<%@ include  file="../module/result.jsp"%>
<%
   /*!
   * 本类根据传入的标识，获取指定的模拟数据，先到内存取，取不到到磁盘上取
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
    //!这一段获取内存数据
    BreezeContext jspCtx = mokeCtx.getContext(url);
    if (jspCtx!=null && !jspCtx.isNull() && !jspCtx.isEmpty()){
        result = jspCtx.getContext(sig);
    }
%>

<%
    //!这一段获取文件内容
    if (result == null || result.isNull()){
        String fileDir = baseDir+"/manager_auxiliary/data/moke/"+url;
        File f = new File(fileDir);
        for (File ff : f.listFiles()){
           String one = ff.getName().replaceAll("\\.mok","");
           if (ff.isDirectory() ){
               continue;
           }
           if (sig.equals(one)){
               String content = FileTools.readFile(ff,"UTF-8");
               result = ContextTools.getBreezeContext4Json(content);
               //设置回内存
               if (jspCtx == null || jspCtx.isNull()){
                   jspCtx = new BreezeContext();
                   mokeCtx.setContext(url,jspCtx);
               }
               jspCtx.setContext(sig,result);
               result.setContextByPath("evn.lasttime",new BreezeContext(System.currentTimeMillis()));
           }
        }
    }
%>
<%
    //!这一段返回结果
    response.getWriter().println( genResult(0,result));
    return;
%>