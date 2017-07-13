
<%@page import="com.breeze.framwork.databus.*"%>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%!
   //结果对象直接返回
   public String genResult(int code,BreezeContext data){
       BreezeContext result  = new BreezeContext();
       result.setContext("code",new BreezeContext(code));
       if (data!=null){
            result.setContext("data",data);
       }
       String resultStr = ContextTools.getJsonString(result, new String[] { "code", "data" });
	   return resultStr;
   }
%>