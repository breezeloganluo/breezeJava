<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="java.io.*" %>
<%@ include  file="../module/result.jsp"%>
<%@ include  file="./fileOper.jsp"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@page import="com.breezefw.service.cms.*"%>

<% 
    //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    String baseDir  = Cfg.getCfg().getRootDir();

%>

<%!
   
   public void getFileInfo(File baseDir,BreezeContext result)throws IOException{
   
       File[] files = baseDir.listFiles();
       for (int i=0;files != null && i<files.length;i++){
           File one = files[i];
           if (one.isFile()){

               BreezeContext oneCtx = getOneResult(one);
               
               if (oneCtx == null){
                  continue;
               }

               String fileName = oneCtx.getStringValue("url",null);

               if (fileName == null){
                   continue;
               }
              
                result.setContext(fileName,oneCtx);
                
           }else{
			   if (!filterDir(one)){
				   continue;
			   }
               getFileInfo(one,result);
           }
       }
   }
   /**
   * 输出的格式为:
   * url:xx
   * lastModify:xxx
   * length:xxx
   * modifyCount:xxxx
   * lastHistory:最后一个文件
   */
   public BreezeContext getOneResult(File one) throws IOException{
       BreezeContext result = new BreezeContext();
       String name = one.getName();
       long fileLen = one.length();
       long lastModify = one.lastModified();
       
              
       //获取文件路径
       String url = getStanderUrl(one);
       
       result.setContext("url",new BreezeContext(url));
       //设置最后修改时间
       result.setContext("lastModify",new BreezeContext(String.valueOf(lastModify)));
       //设置大小
       result.setContext("length",new BreezeContext(String.valueOf(fileLen)));
       return result;
   }
   
   /**
   *对比这个是否是可比较的路径
   */
   public boolean filterDir(File dir){
	   try{
		   String url = "/"+getStanderUrl(dir);
		   String [] whiteNameList = new String[]{
			   "/page",
			   "/servicegadget",
			   "/WEB-INF/classes/djava"
		   };
		   for (int i=0 ; i< whiteNameList.length;i++){
			   if (whiteNameList[i].indexOf(url) == 0 || url.indexOf(whiteNameList[i])==0){
				   return true;
			   }
		   }
		   return false;
	   }catch(Exception e){
		   return false;
	   }
   }
%>

<%
    File f = new File(baseDir);
    BreezeContext result = new BreezeContext();
    getFileInfo(f,result);
    response.getWriter().println( genResult(0,result));
%>