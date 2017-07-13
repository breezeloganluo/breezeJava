<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>


<%@ include  file="../module/result.jsp"%>
<%
   /*!全新改版的数据截获处理功能，数据保存在内存中，有需要的时候再进行序列化到前台，整体的数据结构如下：
     * {
     *    [入口jsp]{
	 *	    [场景名]:{
     *         evn:{
     *             name:"场景名",
     *             package:"场景分类",
     *             qstr:"xxxxx",
     *             lasttime:"加载到内存的时间，根据这个来把场景清出内存",
     *         }
     *         data:{
	 *	          [gadgetName] :{
     *               info:{
     *                  gadgeturl:
     *                  type:"function/service",//如果是service那么gadgetName是package，function是name
     *               },
     *               method:{
	 *	                  [functionname]:[{
     *                        session:sessionName,因为是多次异步请求上来，所以要确认是否是同一个回话里头的，因为同一个函数也可以调用多次的
     *                        
	 *                        isMoke:[true/false],这是一个开关，说明这个方法是否要进行模拟
	 *                        input:[],各个参数,
     *                        mokereq:[],后面每次moke请求的参数，只保留一个
     *                        return:{}返回对象
     *                        callback:{
	 *                             c0:[],第一个回调，里面第一个回调的第n次调用
	 *                             c1:[]第二个回调，里面第一个回调的第n次调用
	 *                        }
	 *                    }]
     *               }
	 *            }
     *         }
	 *      }
     *    }
	 *  }
   * 这个数据放到_G.moke下面
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
    
    //获取fileName内容
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
    
    //获取threadSignal
    String type = request.getParameter("threadSignal");

    if (type == null){
        type = "default";
    }
    
    //获取input内容
    BreezeContext logObjCtx = root.getContextByPath("_R.logObj");
    if (logObjCtx == null || logObjCtx.isNull()){
       response.getWriter().println( genResult(13,new BreezeContext("参数错误，logObj为空")));
       return;
    }
    
    //获取qstr
    BreezeContext qstrCtx = root.getContextByPath("_R.qstr");
    if (qstrCtx == null || qstrCtx.isNull()){
       response.getWriter().println( genResult(13,new BreezeContext("参数错误，qstr为空")));
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
%>
<%@ include  file="./bean4ClearOvertime.jsp"%>
<%
   //!这一段将内容加入
   synchronized(mokeCtx){
       
       BreezeContext jspCtx = mokeCtx.getContext(url);
       if (jspCtx == null){
           jspCtx = new BreezeContext();
           mokeCtx.setContext(url,jspCtx);
       }
       
       //处理evn数据
       BreezeContext oldOstrCtx = jspCtx.getContextByPath(type+".evn.qstr");
       if (oldOstrCtx == null){
           jspCtx.setContextByPath(type+".evn.qstr",qstrCtx);
       }
       
       BreezeContext oldTime = jspCtx.getContextByPath(type+".evn.lasttime");
       if (oldTime == null){
           jspCtx.setContextByPath(type+".evn.lasttime",new BreezeContext(System.currentTimeMillis()));
       }
       
       //处理gadgeturl数据
       BreezeContext oldGadgetUrlCtx = jspCtx.getContextByPath(type+".data."+gadgetName+".info.gadgeturl");
       if (oldGadgetUrlCtx == null || oldGadgetUrlCtx.isNull()){
           jspCtx.setContextByPath(type +".data."+gadgetName+".info.gadgeturl",gadgetUrlCtx);
           jspCtx.setContextByPath(type +".data."+gadgetName+".info.type",new BreezeContext("function"));
       }
       
       
       //处理函数数据       
       BreezeContext funCtx = jspCtx.getContextByPath(type+".data."+gadgetName+".method."+funName);
       if (funCtx == null){
           funCtx = new BreezeContext();
           jspCtx.setContextByPath(type+".data."+gadgetName+".method."+funName,funCtx);
       }

       //获取会话值
       BreezeContext sessionCtx = logObjCtx.getContext("session");
       if (sessionCtx == null || sessionCtx.isNull()){
           response.getWriter().println( genResult(13,new BreezeContext("参数错误，logObj的session为空")));
       }

       String sName = sessionCtx.toString();

       //根据session的值遍历数组，看是否要新增加
       BreezeContext oneFun = new BreezeContext();
       oneFun.setContext("session",sessionCtx);
       oneFun.setContext("isMoke",new BreezeContext("true"));
       boolean isNew = true;
       for (int i=0;i<funCtx.getArraySize();i++){
           BreezeContext tmp = funCtx.getContext(i);
           BreezeContext ts = tmp.getContext("session");
           if (ts == null){
               continue;
           }
           if (sName.equals(ts.toString())){
               oneFun = tmp;
               isNew = false;
           }
       }

       //根据不同的类型写入不同的值
       String operName = logObjCtx.getContext("name").toString();
       if ("callback".equals(operName)){
           String callName = logObjCtx.getContextByPath("logObj.name").toString();
           BreezeContext oneCallbackParamArray = oneFun.getContextByPath("callback."+callName);
           if (oneCallbackParamArray == null || oneCallbackParamArray.isNull()){
               oneCallbackParamArray = new BreezeContext();
               oneFun.setContextByPath("callback."+callName,oneCallbackParamArray);
           }
           oneCallbackParamArray.pushContext(logObjCtx.getContextByPath("logObj.param"));
       }
       else{
           oneFun.setContext(operName,logObjCtx.getContext("logObj"));
       }
       if (isNew){
           funCtx.pushContext(oneFun);
       }
       
       
       
   }
   response.getWriter().println( genResult(0,new BreezeContext("ok")));
%>
