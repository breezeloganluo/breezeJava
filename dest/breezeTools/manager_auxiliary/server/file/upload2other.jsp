<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.lang.*"%>

<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="java.util.regex.*" %>
<%@ page  import="java.io.* "%>
<%@ page import="java.net.URLConnection"%>
<%@ page import="java.net.URL"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.support.tools.FileTools"%>
<%@ page import="com.breezefw.ability.http.HTTP"%>

<%@ include  file="../module/result.jsp"%>
<%
   //！本jsp输入的是参数url，根据url到远程下载，下载完后将文件名按照客户端的参数要求保存起来
   //文件名fileName也是客户端上传上来的
%>

<%  
   //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    //获取baseDir信息
    BreezeContext baseDirCtx = root.getContextByPath("_R.baseDir");
    if (baseDirCtx == null || baseDirCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，baseDirCtxCtx为空")));
       return;
    }
    String baseDir = baseDirCtx.toString();
    
    
    //获取fileName信息
    BreezeContext fileNameCtx = root.getContextByPath("_R.fileName");
    if (fileNameCtx == null || fileNameCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，fileNameCtx为空")));
       return;
    }
    String fileName = fileNameCtx.toString();
    
    //获取host内容
    BreezeContext hostCtx = root.getContextByPath("_R.host");
    if (hostCtx == null || hostCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，hostCtx为空")));
       return;
    }
    String host = hostCtx.toString();
    
    int code = 0;
    StringBuilder message = new StringBuilder();
    String holdbaseDir  = Cfg.getCfg().getRootDir() + "/" + baseDir+"/";
%>

<%
    //这段代码要读取文件内容
    File f = new File (holdbaseDir + fileName);
    String content = FileTools.readFile(f,"UTF-8");
%>

<%
    //这段代码组织上传到服务器上
    BreezeContext req = new BreezeContext();
    req.setContext("name",new BreezeContext("fileOper"));
    req.setContext("package",new BreezeContext(""));
    BreezeContext paramCtx = new BreezeContext();
    paramCtx.setContext("baseDir",new BreezeContext(baseDir));
    paramCtx.setContext("fileName",new BreezeContext(fileName));
    paramCtx.setContext("content",new BreezeContext(content));
    req.setContext("param",paramCtx);
    String postData ="data=" +java.net.URLEncoder.encode(  "[" + ContextTools.getJsonString(req,null) + "]","UTF-8");
    //发请求

    String url = host+"/breeze/framework/jsp/BreezeFW.jsp";

    String resultHttp = HTTP.getInc().sendHttpPost(url, postData);
    resultHttp = resultHttp.replaceAll("(^\\s*\\[|\\]\\s*$)","");


%>
<%=resultHttp%>