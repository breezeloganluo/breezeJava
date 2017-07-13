<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>

<%@ include  file="./fileOper.jsp"%>
<%@ include  file="../module/result.jsp"%>
<%
   /*!本类是一个处理文件同步的类
     1.类似SVN的操作，客户端操作文件时，会申请一个锁，如果没有其他人在使用这个锁，那么就把这个锁给这个客户端使用
     2.特殊的地方，svn是所一个大文件，而本类所的是一个特殊的有结构的文件，即可以用BreezeContext表示的数据结构
     3.因为是结构体，所以所示可以进行局部锁的，当然也有全局锁
     4.这个后台也保留完整的全局文件内容，当客户端数据是旧的时候，服务端的数据会下放数据给客户端
     5.下放数据给客户端的还是结构体，即BreezeContext的内容
     6.lasttime是版本标识，也要判断的，每次携带上来，没有的话只能是新文件情况，防止版本不够新被覆盖
	 7.免超时设定，就是每次如果时间戳是10分钟前的就视为无效
   */
%>
<%
  //!这个片段用于定义全局变量，这些全局变量用this.xxx直接访问，这段不用写代码
  
  /**
  * fileSyncOper是一个记录全局数据的对象，其结构如下：
  * {
  *    fileName:{  //fileName表示客户端传入的文件名，即要保存和同步的文件名
  *       lasttime:"最后更新时间"
  *       uid:""
  *    }
  * }
  */
System.out.println("======================");
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
	
	String type = root.getStringValue("_R.operType",null);
	if (type == null){
	   response.getWriter().println( genResult(12,new BreezeContext("参数错误，operType为空")));
       return;
	}
    
    //获取session中用户id
    String uid = (String)session.getId();
	if (uid == null){
	   response.getWriter().println( genResult(13,new BreezeContext("session已超时")));
       return;
	}

    //获取lastmodify
    long lasttime = root.getLongValue("_R.lasttime",-1);

	String baseDir  = Cfg.getCfg().getRootDir();
	
	
	//获取全局锁内存
    BreezeContext fileLock = null;
    synchronized(ContextMgr.global){
       fileLock = ContextMgr.global.getContext("fileLock");
       if (fileLock == null){
           fileLock = new BreezeContext();
           ContextMgr.global.setContext("fileLock",fileLock);
       }
    }
%>
<%synchronized(fileLock){%>
<%
  //!这一段是公共获取锁代码
  String surl = getStanderUrl(baseDir + url);
  BreezeContext oneLock = fileLock.getContext(surl);
  long currtime = System.currentTimeMillis();
%>
<%
  //!这一段代码申请锁
  if ("apply".equals(type)){

	  //判断之前是否有其他申请，如果有判断是否是自己，如果已经有占用返回失败，否则把自己加入内存
	  if (oneLock == null || oneLock.isNull()){

		  //申请一个新锁
		  oneLock = new BreezeContext();
		  fileLock.setContext(surl,oneLock);
		  oneLock.setContext("uid",new BreezeContext(uid));
          
		  oneLock.setContext("lasttime",new BreezeContext(currtime));
		  response.getWriter().println( genResult(0,new BreezeContext(currtime)));
		  return;
	  }
	  String oldAuth = oneLock.getStringValue("uid",null);
	  long oldTime = oneLock.getLongValue("lasttime",-1);
      if (oldAuth == null){

          //这也是可以申请的
		  if (lasttime !=-1 && lasttime != oldTime && oldTime!=-1 ){

			  //这是之前申请过，但是被别人占了，而自己继续打开编辑，后面其他人释放，这个时候就要拦住了
			  response.getWriter().println( genResult(2,new BreezeContext(currtime)));
		      return;
		  }
		  
		  oneLock.setContext("lasttime",new BreezeContext(currtime));
		  oneLock.setContext("uid",new BreezeContext(uid));
		  response.getWriter().println( genResult(0,new BreezeContext(currtime)));
		  return;
      }
	  if (uid.equals(oldAuth)){

		  //申请过，有权限的
		  oneLock.setContext("lasttime",new BreezeContext(currtime));
		  response.getWriter().println( genResult(0,new BreezeContext(currtime)));
		  return;
	  }
	  else{
		  //如果原来用户申请的时间已经超时，比如10分钟，那么视为可申请
		  if (lasttime == -1 && currtime - 10*60*1000>oldTime){

			  oneLock.setContext("lasttime",new BreezeContext(currtime));
			  oneLock.setContext("uid",new BreezeContext(uid));
			  response.getWriter().println( genResult(0,new BreezeContext(currtime)));
			  return;
		  }
	  }

	  response.getWriter().println( genResult(1,new BreezeContext(oneLock.getLongValue("lasttime",-1))));
  }
%>
<%
  //!这一段代码释放锁
  if ("release".equals(type)){

	  if (oneLock == null || oneLock.isNull()){
		  //没有锁，直接返回
		  response.getWriter().println(genResult(0,new BreezeContext("没有锁")));
		  return;
	  }
	  String oldAuth = oneLock.getStringValue("uid",null);
	  if (uid.equals(oldAuth)){
		  //申请过，有权限的
		  long applyTime = oneLock.getLongValue("lasttime",-1);
		  //15秒内释放时间比申请时间要小的，忽略释放
		  if (System.currentTimeMillis()-applyTime < 15000){
			  response.getWriter().println(genResult(0,new BreezeContext("无需释放")));
			  return;
		  }
		  oneLock.setContext("uid",null);
		  response.getWriter().println( genResult(0,new BreezeContext("释放成功")));
		  return;
	  }
	  response.getWriter().println( genResult(0,new BreezeContext("无需释放")));
  }
%>
<%}%>