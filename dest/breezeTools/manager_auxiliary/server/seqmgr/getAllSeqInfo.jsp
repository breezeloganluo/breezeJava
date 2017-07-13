<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@ page import="java.io.File" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.support.tools.FileTools"%>
<%@ page import="com.breeze.support.tools.DirManager"%>
<%@ include  file="../module/result.jsp"%>
<%
   //!这一段读取所有的目录文件,并加载到内存中
   BreezeContext result = new BreezeContext();
   DirManager dm = new DirManager(Cfg.getCfg().getRootDir() + "/design");
   HashMap<String,ArrayList<File>> allMap = dm.getAllFileInPackage(null);
   if (allMap != null){
       for (String n : allMap.keySet()){
           String dir = n.replaceAll("\\.","/");
           ArrayList<File> flist = allMap.get(n);
           for (File f : flist){
               String name =  f.getName();
               //过滤文件
               if (name.endsWith(".js") || name.endsWith(".seq")){
                   String content = FileTools.readFile(f,"UTF-8");
                   result.setContext(dir+"/"+name,new BreezeContext(content));
               }
           }
       }
   }
%>
<%
   //!这一段返回结果
   response.getWriter().println(genResult( 0,result));
%>