<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.lang.*"%>
<%@page import="java.lang.reflect.Field"%>
<%@page import="java.lang.reflect.Method"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="net.lingala.zip4j.core.ZipFile" %>
<%@ page import="net.lingala.zip4j.exception.ZipException" %>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<%@ include  file="../module/result.jsp"%>

<%
   //！本jsp输入的是参数zip，这是zip所在文件目录位置，程序根据这个zip将zip解压到对应的dir目录上
   //dir也是客户端传入上来的一个参数
%>
<% 
   //!本段代码获取所有的参数
    BreezeContext root = ContextMgr.getRootContext();
    //获取import内容
    BreezeContext zipCtx = root.getContextByPath("_R.zip");
    if (zipCtx == null || zipCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，zip为空")));
       return;
    }
    String zip = zipCtx.toString();
    
    //获取fileName内容
    BreezeContext dirCtx = root.getContextByPath("_R.dir");
    
    if (dirCtx == null || dirCtx.isNull()){
       response.getWriter().println( genResult(12,new BreezeContext("参数错误，dir为空")));
       return;
    }
    String dir = dirCtx.toString();
    String baseDir  = Cfg.getCfg().getRootDir() + "/";
    
    
    //获取fileName内容
    BreezeContext chartSetCtx = root.getContextByPath("_R.chartset");
    
    String charset = "UTF-8";
    if (chartSetCtx != null && !chartSetCtx.isNull()){
		charset = chartSetCtx.toString();
    }

    
%>

<%
    //!这段代码根据参数zip进行解压
    //下面代码只是解压的样例示例程序，解压用zip4j包已经集成，记得如果目录不存在则要重新创建目录
    
    // Initiate ZipFile object with the path/name of the zip file.
	//ZipFile zipFile = new ZipFile("c:\\ZipTest\\ExtractAllFiles.zip");
			
	// Extracts all files to the path specified
	//zipFile.extractAll("c:\\ZipTest");
    
%>

<%
// String zip = "test/a.zip";
//  String dir = "test/zip/a";
    //!这段代码要遍历解压后的目录，将目录的的文件结果返回给客户端
    //返回的格式为：
    //{
    //  "文件1":true
    //  "目录1":{
    //            文件2:true
    //               ...
    //          }
    //}
    //下面代码只是解压的样例示例程序，解压用zip4j包已经集成，记得如果目录不存在则要重新创建目录
    
         ZipFile zFile = new ZipFile(baseDir+zip);

		 zFile.setFileNameCharset(charset);
		 if(!zFile.isValidZipFile()){
		    response.getWriter().println( genResult(12,new BreezeContext("不是有效的zip文件")));
		  }
		  //判断指定目录是否存在
		  File destDir = new File(baseDir+dir);
		  if(destDir.isDirectory() && !destDir.exists()){
		     destDir.mkdir();
		  }
		  zFile.extractAll(baseDir+dir);
        
		  BreezeContext base = new BreezeContext();
		   listFile(destDir,base);
           
    
%>
<%!
   private  BreezeContext listFile(File filePath,BreezeContext basebcsy){
		File files[];
		files = filePath.listFiles();
		Arrays.sort(files);
		BreezeContext basebc = null;
		
		for(int i = 0; i < files.length; i++){
			File file = files[i];
			
			if(file.isDirectory()){
				basebc = new BreezeContext();
				basebc = listFile(file,basebc);
				basebcsy.setContext(file.getName(), basebc);
			}else{
				basebcsy.setContext(file.getName(), new BreezeContext(true));
			}
			
		}
		return basebcsy;
      }
%>


<%
   //!这段代码返回结果
  // BreezeContext result = new BreezeContext();
   //加密设置返回的内容
 //  result.pushContext(base);
   response.getWriter().println(genResult(0,base));
%>
