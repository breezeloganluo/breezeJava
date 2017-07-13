<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.breeze.framwork.databus.*"%>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@page import="com.breeze.framwork.netserver.FunctionInvokePoint"%>
<%@page import="com.breeze.framwork.databus.ContextTools"%>
<%@page import="com.breeze.base.db.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%@ include  file="../module/FlashDBData.jsp"%>
<%@ include  file="../module/GetOneTable.jsp"%>

<%
   BreezeContext root = ContextMgr.getRootContext();
   BreezeContext allDb = root.getContextByPath("_R.case.initData");
   BreezeContext param = root.getContextByPath("_R.case.param");
   BreezeContext backupData = new BreezeContext();
   BreezeContext afterExcute = new BreezeContext();
   BreezeContext execResult = null;
   int code = 0;
   String msg=null;
   //备份所有定义的表
   for (String tbName : allDb.getMapSet()){
	   BreezeContext oneTableData = getOneTable(tbName);
	   if (oneTableData != null){
		   backupData.setContext(tbName,oneTableData);
	   }
	   else{
		   code = 11;
		   msg="备份表"+tbName+"失败";
		   break;
	   }
   }
   //设置所有新表
   if (code == 0){
	   for (String tbName : allDb.getMapSet()){
		   int result = setDBData(tbName,allDb.getContext(tbName));
		   if (result != 0){
			   code = 12;
			   msg = "设置表"+tbName+"数据失败";
			   break;
		   }
	   }
   }
   //执行service
   if (code == 0){
	   BreezeContext serviceCtx = root.getContextByPath("_R.servicename");
	   BreezeContext packageCtx = root.getContextByPath("_R.package");
	   if (serviceCtx == null || packageCtx == null || serviceCtx.isNull() ||packageCtx.isNull()){
		   code = 13;
		   msg = "service name or package is null";
	   }
	   String serviceName = serviceCtx.toString();
	   String packageName = packageCtx.toString();
	   execResult = FunctionInvokePoint.getInc().breezeInvokeUsedCtxAsParam(packageName,serviceName,param,request,response);
   }
   //获取所有表的结果
   if (code == 0){
	   for (String tbName : allDb.getMapSet()){
		   BreezeContext oneTableData = getOneTable(tbName);
		   if (oneTableData != null){
			   afterExcute.setContext(tbName,oneTableData);
		   }
		   else{
			   code = 14;
			   msg="获取表"+tbName+"失败";
			   break;
		   }
	   }
   }
   
   //恢复所有表
   if (code != 11){
	   for (String tbName : backupData.getMapSet()){
		   int result = setDBData(tbName,backupData.getContext(tbName));
		   if (result != 0){
			   code = 15;
			   msg = "恢复表"+tbName+"数据失败";
			   break;
		   }
	   }
   }
   //返回结果
   //int result = setDBData("zlwx_test",allDb);
   
   if (code !=0){
%>
{
   "code":<%=code%>,
   "data":"<%=msg%>"
}
   
<%
   }
   else{
	   execResult.setContext("dbData",afterExcute);
	   String result = ContextTools.getJsonString(execResult,new String[]{"code","data","dbData"});
	   out.println(result);
   }
%>