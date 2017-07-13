<%@page import="com.breeze.framwork.databus.*"%>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@page import="com.breeze.base.db.*"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.*"%>
<%!
   public int setDBData(String dbName,BreezeContext dataCtx){
       for (int i = 0;i<dataCtx.getArraySize();i++){
           BreezeContext one = dataCtx.getContext(i);
           StringBuilder sql = new StringBuilder();
           StringBuilder values = new StringBuilder();
           ArrayList val = new ArrayList();
           
           sql.append("insert into ").append(dbName).append('(');
           values.append("values(");
           boolean isFirst = true;
           for(String cName :one.getMapSet()){
               BreezeContext valCtx = one.getContext(cName);
               if (isFirst){
                   isFirst = false;
               }
               else{
                   sql.append(',');
                   values.append(',');
               }
               sql.append(cName);
               values.append('?');
               if (valCtx == null || valCtx.isNull()){
                   val.add("");
               }
               else{
                   val.add(valCtx.getData());
               }
           }
           sql.append(')');
           values.append(')');
           String execSql = sql.append(values.toString()).toString();
           try{
               COMMDB.executeUpdate("delete from  "+ dbName);
               COMMDB.executeUpdate(execSql,val);
               
           }catch(Exception e){
               e.printStackTrace();
               return 998;
           }
       }
       return 0;
   }
%>