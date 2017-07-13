<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.breeze.support.tools.FileTools"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breeze.framwork.databus.ContextTools" %>
<%@page import="java.util.*"%>
<%@page import="java.util.regex.*"%>

<%
   //这一段接受客户端过来的文件路径信息
   String fileDir = request.getParameter("file");
   if (fileDir == null){
       out.println("请输入正确的文件路径");
       return;
   }
%>
<%
   //本段代码，负责将文本读入并写入到格式化好的BreezeContext中
   String text = FileTools.readFile(Cfg.getCfg().getRootDir() + fileDir,"UTF-8");
   BreezeContext root = ContextTools.getBreezeContext4Json(text);
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=root.getContextByPath("docinfo.project")%></title>
 <style type="text/css">
 *{margin:0; padding:0;}
 .header{
	 width:1000px;
	 height:500px;
	 border:#CCC 1px solid;
	 text-align:center;
	 padding-top:50px;
	 margin:0 auto;
 }
 .comwidth{width:1000px;margin:0 auto;
}
 .content{}
 </style>

</head>

<body>
	<div class="header">
        <div class="comwidth">
            <h1 style="margin-bottom:50px;"><%=root.getContextByPath("docinfo.project")%></h1><hr/>
            <h2><%=root.getContextByPath("docinfo.corporate")%></h2></br></br></br></br></br>
            <h2><%=root.getContextByPath("docinfo.date")%></h2>
            <h2>SHENZHEN</h2>
        </div>
    </div>
    <div class="comwidth" style="margin-top:50px; font-family:微软雅黑;">
    	<table width="1000px" height="200px" border="1" cellspacing="0">
    	<p style="margin-bottom:20px;">修订记录</p>
        <tr>
        	<th>日期</th>
            <th>版本</th>
            <th>描述</th>
            <th>作者</th>
        </tr>
        <%
           BreezeContext record = root.getContextByPath("docinfo.record");
           if (record != null){
           for (int i=0;i<record.getArraySize();i++){
              BreezeContext one = record.getContext(i);
        %>
        <tr>
        	<td><%=one.getContext("date")%></td>
            <td><%=one.getContext("version")%></td>
            <td><%=one.getContext("desc")%></td>
            <td><%=one.getContext("auth")%></td>
        </tr>
        <%
           }
           }
        %>
        
        <tr>
        	<td>-</td>
            <td></td>
            <td></td>
            <td></td>
        </tr>
    </table>
    
    <div class="catelogo" style="font-family:宋体; margin-top:20px;">
    	<h3>目录</h3>
        <div id="menu">
        
        </div>
    </div>
    
    <div class="cotent" style="font-family:宋体;">
    	<h1 menu="p1" style="margin-top: 20px;">1 项目概述</h1>
        <h2 menu="p2">1.1项目背景</h2>
        <p>&nbsp;&nbsp;&nbsp;&nbsp;<%=root.getContextByPath("docinfo.backdrop")%></p>
        <h2 menu="p2">1.2项目目的</h2>
        
        <p>&nbsp;&nbsp;&nbsp;&nbsp;<%=root.getContextByPath("docinfo.purpose").toString().replace("[\r\n]","<br/>")%></p>
    	<h1 menu="p1" style="margin-top:20px;">2 需求</h1>
        <h2 menu="p2">2.1需求概要</h2>
        <h3 menu="p3">2.1.1用户角色</h3>
        
        
        
        <table align="center" border="1" cellspacing="0" width="1000px" height="300px">
            <tr>
                <th>角色</th>
                <th>场景描述</th>
            </tr>
            <%
              BreezeContext actorList = root.getContext("actor");
              if (actorList != null){
              for (int i=0;i<actorList.getArraySize();i++){
              BreezeContext one = actorList.getContext(i);
            %>
            <tr>
            	<td><%=one.getContext("name")%></td>
                <td><%=one.getContext("desc")%></td>
            </tr>
            <%
              }
              }
            %>
        	
            
			
    </table>
    
    
    
    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;表1：用户角色表</p>
    <br/><br/><br/>
    
    
    <h1 menu="p2">2.2 功能需求</h1>
    <h2 menu="p3">2.2.1 功能总览</h2>
    <%
       BreezeContext funList = root.getContext("funsrs");
       if (funList != null){
       for (int i=0;i<funList.getArraySize();i++){
           BreezeContext topSrs = funList.getContext(i);
    %>
       <h4><%=topSrs.getContext("name")%></h4>
       <table align="center" border="1" cellspacing="0" width="1000px" height="300px">
            <tr>
                <th>功能点</th>
                <th>描述</th>
            </tr>
    <%
          //这里开始要做非递归式遍历用数组堆栈替代递归功能
          ArrayList<BreezeContext> funArray = new ArrayList<BreezeContext>();
          funArray.add(topSrs);
          while(true){
              if (funArray.size() == 0){
                  break;
              }
              BreezeContext curr = funArray.remove(funArray.size()-1);
              if ("folder".equals(curr.getContext("type").getData())){
                  BreezeContext children = curr.getContext("children");
                  if (children == null){
                      continue;
                  }
                  for (int ii=0;ii<children.getArraySize();ii++){
                      funArray.add(children.getContext(ii));
                  }
                  continue;
              }else{
    %>
              <tr>
                <td><%=curr.getContext("name")%></th>
                <td><%=curr.getContext("desc")%></th>
              </tr>   
    <%
              }
          }
    %>
          </table><br/><br/><br/>
    <%
       }
       }
    %>
    
    
    
    
    
    <%
       funList = root.getContext("funsrs");
       if (funList != null){
       for (int i=0;i<funList.getArraySize();i++){
           BreezeContext topSrs = funList.getContext(i);
           int j = 0;
    %>
       <h2 menu="p3">2.2.<%=i+2%> <%=topSrs.getContext("name")%></h2>
    <%
          //这里开始要做非递归式遍历用数组堆栈替代递归功能
          ArrayList<BreezeContext> funArray = new ArrayList<BreezeContext>();
          funArray.add(topSrs);
          while(true){
              if (funArray.size() == 0){
                  break;
              }
              BreezeContext curr = funArray.remove(0);
              if ("folder".equals(curr.getContext("type").getData())){
                  BreezeContext children = curr.getContext("children");
                  if (children == null){
                      continue;
                  }
                  for (int ii=0;ii<children.getArraySize();ii++){
                      BreezeContext child = children.getContext(ii);
                      String father = curr.getContext("name").toString();
                      if (curr.getContext("father") != null){
                          father = curr.getContext("father").toString();
                      }
                      father =father +  '.' + child.getContext("name");
                      child.setContext("father",new BreezeContext(father));
                      funArray.add(child);
                  }
                  continue;
              }else{
    %>
              <h3 menu="p4">2.2.<%=i+2%>.<%=++j%> <%=curr.getContext("father")%></h3>
              1.用户场景</br>
              <%
              if (curr.getContext("scene") == null || "".equals(curr.getContext("scene").getData())){
                 curr.setContext("scene",new BreezeContext("无"));
              }
              %>
              &nbsp;&nbsp;<%=curr.getContext("scene").toString().replaceAll("[\\r\\n]","<br/>")%><br/>
              
              2.前置条件</br>
              <%
              if (curr.getContext("precondition") == null || "".equals(curr.getContext("precondition").getData())){
                 curr.setContext("precondition",new BreezeContext("无"));
              }
              %>
              &nbsp;&nbsp;<%=curr.getContext("precondition").toString().replaceAll("[\\r\\n]","<br/>")%><br/>
              
              3.要求描述</br>
              <%
              if (curr.getContext("desc") == null || "".equals(curr.getContext("desc").getData())){
                 curr.setContext("desc",new BreezeContext("无"));
              }
              %>
              &nbsp;&nbsp;<%=curr.getContext("desc").toString().replaceAll("[\\r\\n]","<br/>")%><br/>
              <%
              if (curr.getContext("srspoint") != null){
                  BreezeContext onePointArr = curr.getContext("srspoint");
                  for (int iii =0 ;iii<onePointArr.getArraySize();iii++){
                      BreezeContext onePoint = onePointArr.getContext(iii);
              %>
                      &nbsp;&nbsp;<%=iii+1%>.<%=onePoint.getContext("point")%><br/>
              <%
                  }
              }
              %>
              
              <%
              if (curr.getContext("prototype") != null){
                  BreezeContext onePrototypeArr = curr.getContext("prototype");
                  for (int iii =0 ;iii<onePrototypeArr.getArraySize();iii++){
                      BreezeContext onePrototype = onePrototypeArr.getContext(iii);
                      String fileUrl = onePrototype.toString();
                      String imgUrl = fileUrl;
                      String[] imgUrlArr = fileUrl.split("\\.");
                      if ("html".equals(imgUrlArr[imgUrlArr.length-1])){
                          imgUrl = "./htmlImg.jpg";
                      }
                      if (!"".equals(imgUrl)){
              %>
                      
                      <a href="<%=fileUrl%>" target="_blank">
                         <img src="<%=imgUrl%>" width="300" height="400"/>
                      </a>                      
              <%
                      }
                  }
              }
              %>
              
              <br/>
              4.异常说明</br>
              <%
              if (curr.getContext("exception") == null || "".equals(curr.getContext("exception").getData())){
                 curr.setContext("exception",new BreezeContext("无"));
              }
              %>
              &nbsp;&nbsp;<%=curr.getContext("exception").toString().replaceAll("[\\r\\n]","<br/>")%><br/>

           
              <br/>
    <%
              }
          }
          out.println("<br/><br/>");
       }
       }
    %>
    
    
    
    

            <h1 menu="p1" style="margin-top:20px;">3  项目开发方面</h1>
            <h2 menu="p2">3.1 开发流程</h2>
        	<p>&nbsp;&nbsp;我们将严格按照项目管理(PMP)的标准来实施该项目。从而保证项目的可控性和保证项目的交付的产品的质量。</p>
            <p>&nbsp;&nbsp;项目由于采用到集成，需要完成基础部分的对接和稳定，第一部分采用原始瀑布研发流程，后期针对能源交换站、用户部分采用迭代敏捷完成</p>
            
            <h2 menu="p3">3.2 开发方式</h2>
            <p>&nbsp;&nbsp;由于需求的变动的可能性很大,故我们建议采用增量式的开发式。 每一步增量实现了一个或多个最终用户功能。每一步增量包含所有早期的已开发的功能集加上一些新的功能，系统在逐步累积的增量中增长。</p>
            
            <h2 menu="p3">3.3 甲方支持流程</h2>
            <p>
            &nbsp;&nbsp;需求澄清</br>&nbsp;&nbsp;原型评审</br>	&nbsp;&nbsp;UI评审</br>&nbsp;&nbsp;需求变更双方确认</br>&nbsp;&nbsp;问题修改, 统一表跟踪</br>
            </p>
            
            <h1 menu="p1" style="margin-top:20px;">4  交付件</h1>
            <h2 menu="p2">4.1 软件交付件</h2>
            <p>&nbsp;&nbsp;根据需求,我们将在最后项目结束和项目过程中交付以下工作产品:</br>
                &nbsp;&nbsp;1)	软件需求规格说明书。</br>
                &nbsp;&nbsp;2)	软件设计文档。</br>
                &nbsp;&nbsp;3)	安装说明文档。</br>
                &nbsp;&nbsp;4)	软件使用手册。</br>
                &nbsp;&nbsp;5)	可运行的发布包。</br>
            </p>

    </div>
	</div>
    <br/><br/><br/><br/><br/><br/><br/><br/>
    <script src="../../../breeze/lib/js/jquery.js">
    </script>
    <script >
           var html = "";
           $("[menu]").each(function(){
             var dom = $(this);
             var level = Number(dom.attr("menu").replace("p",""));
             var name = dom.html();
             var content = "";
             for (var i=0;i<level-1;i++){
                  content+="&nbsp;&nbsp";
             }            
             content+=name;
             
             var page = parseInt(Number(dom.position().top)/800)+1;
             html += "<div style='position: relative;'>.....................................................................................................";
             html += "<span style='position: absolute;left: 0;background-color: white;'>"+content+"</span>";
             html += "<span'position: absolute;right: 0;background-color: white;'>"+page+"</span>";
             html += "</div>";
             
           });
           
           $("#menu").html(html);
    </script>
</body>
</html>
