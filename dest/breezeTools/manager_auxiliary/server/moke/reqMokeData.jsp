<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.support.tools.FileTools"%>
<%@ page import="java.io.*"%>

<%@ include  file="../module/result.jsp"%>
<%
   /*!本类接受客户端的模拟请求，返回模拟数据
   *数据在内存中，总体的数据结构，参见logMokeData.jsp的描述
   *文件保存的地方为：manager_auxiliary/data/moke/[jsp]/[mokeSignal].mok
   */
   System.out.println("===========begin reqMokeData============");
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
    
    //获取qstr
    BreezeContext qstrCtx = root.getContextByPath("_R.qstr");
    if (qstrCtx == null || qstrCtx.isNull()){
       response.getWriter().println( genResult(13,new BreezeContext("参数错误，qstr为空")));
       return;
    }
    
    //获取gadget内容
    BreezeContext gadgetNameCtx = root.getContextByPath("_R.gadgetName");
    
    if (gadgetNameCtx == null || gadgetNameCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，gadgetName为空")));
       return;
    }
    String gadgetName = gadgetNameCtx.toString();
    
    BreezeContext gadgetUrlCtx = root.getContextByPath("_R.gadgeturl");
    if (gadgetUrlCtx == null || gadgetUrlCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，gadgetUrlCtx为空")));
       return;
    }
    
    //获取funName内容
    BreezeContext funNameCtx = root.getContextByPath("_R.funName");
    
    if (funNameCtx == null || funNameCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，funName为空")));
       return;
    }
    String funName = funNameCtx.toString();
    
    
    //获取mokeSignal
    String type = request.getParameter("mokeSignal");

    if (type == null){
        type = "default";
    }
    
    
    //获取input内容
    BreezeContext inputCtx = root.getContextByPath("_R.input");
    
    String baseDir  = Cfg.getCfg().getRootDir() + "/";
    
    //获取全局moke对象
    BreezeContext mokeCtx = null;
    synchronized(ContextMgr.global){
       mokeCtx = ContextMgr.global.getContext("moke");
       if (mokeCtx == null){
           mokeCtx = new BreezeContext();
           ContextMgr.global.setContext("moke",mokeCtx);
       }
    }
    //声明一个单独函数上下文
    BreezeContext oneFunCtx = null;
%>
<%@ include  file="./bean4ClearOvertime.jsp"%>
<%synchronized(mokeCtx){%>
<%
    //!这一段尝试读取内存中数据

    BreezeContext jspCtx = mokeCtx.getContext(url);
    if (jspCtx != null && !jspCtx.isNull()){ 
        oneFunCtx = jspCtx.getContextByPath( type + ".data." + gadgetName + ".method." + funName);
        if ( oneFunCtx != null && !oneFunCtx.isNull() ){
            jspCtx.setContextByPath( type + "." + "evn.lasttime" , new BreezeContext(System.currentTimeMillis()));
        }
    }
%>
<%
    //!这一段如果上面没取到，则尝试从文件中读取
   if (oneFunCtx == null || oneFunCtx.isNull()){
       File f = new File(baseDir + "/manager_auxiliary/data/moke/"+url+"/"+type+".mok");
       String content = FileTools.readFile(f,"UTF-8");
       if (content != null){
           BreezeContext oneSigCtx = ContextTools.getBreezeContext4Json(content);
           if (oneSigCtx !=null && !oneSigCtx.isNull()){
               oneSigCtx.setContextByPath("evn.lasttime",new BreezeContext(System.currentTimeMillis()));
               jspCtx = mokeCtx.getContext(url);
               if (jspCtx == null || jspCtx.isNull()){
                   jspCtx = new BreezeContext();
                   mokeCtx.setContext(url,jspCtx);
               }
               jspCtx.setContext(type,oneSigCtx);
               oneFunCtx = jspCtx.getContextByPath( type + ".data." + gadgetName + ".method." + funName);
           }
       }
   }
%>

<%
   //!这一段如果还未取到，则要创建一个新的模拟对象
   if (oneFunCtx == null || oneFunCtx.isNull()){
       jspCtx = mokeCtx.getContext(url);
       if (jspCtx == null || jspCtx.isNull()){
            jspCtx = new BreezeContext();
            mokeCtx.setContext(url,jspCtx);
       }
       BreezeContext tmpFun = new BreezeContext();
       tmpFun.setContext("isMoke",new BreezeContext(true));
       tmpFun.setContext("mokereq",inputCtx);
       tmpFun.setContext("return",new BreezeContext("返回值，待补充"));
       tmpFun.setContextByPath("callback.c0[][]",new BreezeContext("回调参数待补充，没有就删除"));
       
       jspCtx.setContextByPath( type + ".data." + gadgetName + ".method." + funName + "[]",tmpFun );
       jspCtx.setContextByPath( type + ".data." + gadgetName + ".info.type",new BreezeContext("function") );
       jspCtx.setContextByPath( type + ".data." + gadgetName + ".info.gadgeturl",gadgetUrlCtx );
       jspCtx.setContextByPath( type + ".evn.qstr",qstrCtx);
       jspCtx.setContextByPath( type + ".evn.lasttime",new BreezeContext(System.currentTimeMillis()) );
       
       response.getWriter().println( genResult(1,new BreezeContext("no data")));
       return;
   }
%>

<%
   //!这一段从获取到的oneFunCtx中取数据并下发
   BreezeContext result = new BreezeContext();
   Integer idx = (Integer)session.getAttribute(url + "." +type+"."+gadgetName+"." + funName);
   if (idx == null){
       idx = 0;
   }
   
   int len = oneFunCtx.getArraySize();
   result.setContextByPath(gadgetName+"."+funName,oneFunCtx.getContext(idx%len));
   oneFunCtx.getContext(idx%len).setContext("mokereq",inputCtx);
   idx = (idx +1) % len;
   session.setAttribute(url + "." +type+"."+gadgetName+"." + funName,idx);
   response.getWriter().println( genResult(0,result));
%>

<%}%>