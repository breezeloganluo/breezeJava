<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%

    synchronized(ContextMgr.global){
    
       BreezeContext delMokeCtx = ContextMgr.global.getContext("moke");
       if (delMokeCtx != null && !delMokeCtx.isNull()){

           for (String jspStr : delMokeCtx.getMapSet()){
              BreezeContext allJsp  = delMokeCtx.getContext(jspStr);

              if (allJsp != null && !allJsp.isNull()){
                   for (String seneStr : allJsp.getMapSet()){
                       BreezeContext seneCtx = allJsp.getContext(seneStr);
                       BreezeContext lasttimeCtx = seneCtx.getContextByPath("evn.lasttime");

                       if (lasttimeCtx == null || lasttimeCtx.isNull()){

                           allJsp.setContext(seneStr,null);
                       }

                       long lasttime = Long.parseLong(lasttimeCtx.toString());
                       if (lasttime + 4 * 60 * 60 * 1000 < System.currentTimeMillis()){
                           allJsp.setContext(seneStr,null);
                       }
                   }
              }
           }


       }
    
    }
    
%>