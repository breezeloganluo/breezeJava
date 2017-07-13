<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="java.io.*" %>
<%@ include  file="../module/result.jsp"%>
<%@ include  file="./fileOper.jsp"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@page import="com.breezefw.service.cms.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.ParsePosition"%>
<%@page import="java.util.Date"%>

<% 
    //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    //获取import内容
    long start = root.getLongValue("_R.start",-1);
    long end = root.getLongValue("_R.end",-1);
    int uid = root.getIntValue("_R.uid",-1);
    
    String baseDir  = Cfg.getCfg().getRootDir() + "/backup";

%>
<%
    //!本段代码简单判断是否需要验证，uid有值，且又不是微码登录的，无法获取用户信息，直接返回
    BreezeContext tmpObjCtx = ContextMgr.global.getContextByPath(CmsIniter.CMSPARAMPRIFIX);
    String loginWay = tmpObjCtx.getStringValue("loginWay4tools",null);
    if (uid != -1 && !"weima".equals(loginWay)){
        response.getWriter().println( genResult(1,new BreezeContext("local login")));
        return;
    }
%>
<%!
   public void getFileInfo(File baseDir,long tStart,long tEnd,int uid,BreezeContext result)throws IOException{
   
       File[] files = baseDir.listFiles();
       for (int i=0;files != null && i<files.length;i++){
           File one = files[i];
           if (one.isFile()){

               BreezeContext oneCtx = getOneResult(one,tStart,tEnd,uid);
               
               if (oneCtx == null){
                  continue;
               }

               String fileName = oneCtx.getStringValue("url",null);

               if (fileName == null){
                   continue;
               }
               BreezeContext oldCtx = result.getContext(fileName);
               
               if (oldCtx == null || oldCtx.isNull()){

                    result.setContext(fileName,oneCtx);
                    continue;
               }

               int count = oldCtx.getIntValue("modifyCount",0);
               
               if (oldCtx.getLongValue("lastModify",-1) < oneCtx.getLongValue("lastModify",-1)){
                   //用新的
                   oneCtx.setContext("modifyCount",new BreezeContext(String.valueOf(++count)));
                   result.setContext(fileName,oneCtx);
               }
               else{
                   //用旧的
                   oldCtx.setContext("modifyCount",new BreezeContext(String.valueOf(++count)));
               }
               
           }else{
               getFileInfo(one,tStart,tEnd,uid,result);
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
   * orgUrl:最原始文件url便于读取文件内容进行比较
   */
   public BreezeContext getOneResult(File one,long tS,long tE,int uid) throws IOException{
       BreezeContext result = new BreezeContext();
       String name = one.getName();
       long fileLen = one.length();
       long lastModify = getModify(name);

       //处理名字，不符合就返回null
       if (uid != -1){
           int fuid = getUid(name);
           if (fuid != uid){
               return null;
           }
       }
       
       //判断开始时间
       if (tS != -1){
           if (lastModify < tS){
               return null;
           }
       }
       
       //判断结束时间
       if (tE != -1){
           if (tE < lastModify){
               return null;
           }
       }
       
       //获取文件路径
       File parent = one.getParentFile();
       String url = getStanderUrl(parent);
       String lastHistory = getStanderUrl(one);
       
       result.setContext("url",new BreezeContext(url));
       //设置最后修改时间
       result.setContext("lastModify",new BreezeContext(String.valueOf(lastModify)));
       //设置大小
       result.setContext("length",new BreezeContext(String.valueOf(fileLen)));
       
       //设置次数
       result.setContext("modifyCount",new BreezeContext("1"));
       
       //设置lastHistory
       result.setContext("lastHistory",new BreezeContext(lastHistory));
       
       //设置原始文件
       result.setContext("orgUrl",new BreezeContext("backup/"+getStanderUrl(one)));
      
       return result;
   }
   
   public int getUid(String fileName){
        Pattern p = Pattern.compile("\\d+_(\\d+)\\.");
		Matcher m = p.matcher(fileName);
		if (m.find()){
			String d = m.group(1);
			return Integer.parseInt(d);
		}
        else{
            return -1;
        }
   }
   
   public long getModify(String fileName){
        Pattern p = Pattern.compile("(\\d+)_?");
		Matcher m = p.matcher(fileName);
		if (m.find()){
			String d = m.group(1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			Date result = sdf.parse(d,new ParsePosition(0));
			return result.getTime();
		}
        else{
            return -1;
        }
   }
%>

<%
    File f = new File(baseDir);
    BreezeContext result = new BreezeContext();
    getFileInfo(f,start,end,uid,result);
    response.getWriter().println( genResult(0,result));
%>