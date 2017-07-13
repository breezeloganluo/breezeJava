<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.*"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.breeze.support.cfg.*"%>
<%@ page import="com.breeze.framwork.servicerg.*"%>
<%@ page import="com.breeze.support.tools.*"%>
<%@ page import="com.google.gson.*" %>
<%@ page import="java.text.*" %>
<%@page import="java.sql.*"%>
<%@page import="com.breeze.base.db.*"%>
<%@ include  file="../module/result.jsp"%>

<% 
    //!第一段获取参数
    request.setCharacterEncoding("UTF-8");
    BreezeContext root = ContextMgr.getRootContext();
    if (root == null){
        response.getWriter().println(genResult( 999,new BreezeContext("root 为空")));
        return;
    }
    
	BreezeContext queryTypeC = root.getContextByPath("_R.queryType");
    if (queryTypeC == null){
        response.getWriter().println(genResult( 11,new BreezeContext("查询类型为空")));
        return;
    }
    
    //为null则表示查询全部
    BreezeContext condictionC = root.getContextByPath("_R.condiction");
    if (condictionC != null && condictionC.getType() != BreezeContext.TYPE_ARRAY){
    	condictionC = null;
    }
    if (condictionC != null && condictionC.getArraySize() == 0){
        condictionC = null;
    }
    
    String sql = "select * from cmsmetadata ";
    BreezeContext data = new BreezeContext();
%>

<%
    //!这一段处理查询
    String queryCol = "tableName";
    if ("alias".equals(queryTypeC.getData())){
        queryCol = "alias";
    }
        if (condictionC != null){
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            sb.append("where ").append(queryCol).append (" in(");
            for (int i=0;i<condictionC.getArraySize();i++){
                
                BreezeContext one = condictionC.getContext(i);
                if (one==null || one.isNull()){
                      continue;
                }
                if (first){
                   first = false;
                }else{
                   sb.append(',');
                }
                sb.append('"').append(one.toString()).append('"');
                
            }
            
            sb.append(')');
            
            sql = sql  + sb.toString();
            
        }
        
            
            
        ResultSet result = COMMDB.executeSql(sql);
        try{
          while (result.next()) {
              BreezeContext oneRecord = new BreezeContext();
              for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                  // 得到结果集中的列名
                  String culomnName = result.getMetaData().getColumnName(i + 1);
                  // 得到每个列名的值，并把值放入BreezeContext
                  if ("dataDesc".equals(culomnName)){
                      BreezeContext dataDesc = ContextTools.getBreezeContext4Json(result.getString(culomnName));
                      oneRecord.setContext(culomnName,dataDesc);
                      continue;
                  }
                  oneRecord.setContext(culomnName, new BreezeContext(
                  result.getString(culomnName)));
              }
              // 把得到的每条记录都放入resultSetContext
              data.pushContext(oneRecord);
          }
        }
        catch(Exception e){
           BreezeContext exCtx = new BreezeContext();
           exCtx.setContext("exception",new BreezeContext(e.toString()));
           exCtx.setContext("sql",new BreezeContext(sql));
           response.getWriter().println(genResult( 0,exCtx));
        }
        finally{
           result.close();
        }
   
%>

<%
   //!这一段返回结果
   response.getWriter().println(genResult( 0,data));
%>