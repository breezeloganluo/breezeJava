<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="java.io.*" %>
<%!
    public String getStanderUrl(String url) throws IOException{
       File f = new File(url);
	   return getStanderUrl(f);
    } 
%>
<%!
    public String getStanderUrl(File f) throws IOException{
       String baseS = (Cfg.getCfg().getRootDir()+"/").replaceAll("[\\\\\\/]+","/");
       String fName = f.getCanonicalPath().replaceAll("[\\\\\\/]+","/");

       if (!fName.startsWith(baseS)){
           return null;
       }
       
       String rs = fName.substring(baseS.length());
       if (rs.startsWith("backup/")){
           rs = rs.substring(7,rs.length());
       }
       return rs;
    } 
%>