<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.util.*"%>
<%@ page import="com.breeze.support.cfg.Cfg" %>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@page import="java.io.*"%>
<%@ page import="com.breeze.support.tools.FileTools"%>
<%@ page import="com.breeze.framwork.databus.ContextTools"%>
<%@ include  file="../module/result.jsp"%>
<%/*!--本函数主要是进行程序打包处理，
     思路是输入一个目录，进行递归，这是文件遍历递归。
     每处理一个文件，优先去处理这个文件的依赖，每处理完一个依赖文件，就就对这个文件进行打包，这是第二个递归
     */
%>
<%//!声明要转换内容
   String[] iDir = new String[]{"page","package"};
   String[][][] filterDir = new String[][][]{       //第一层对应上面第几个iDir
       {  //第二层对应里面第钟过滤，第一个是filter，dierge是包含
         {"page/manager/mdefault"},
         {"gadget"}
       }
       ,
       {  //第二层对应里面第钟过滤，第一个是filter，dierge是包含
         {"package/temp"},
         {}
       }
   };
   
   String destFile = "package/Gadget.js";
%>

<%!
   HashSet<String> hashProcess;
   HashSet<String> depandStack;
   HashMap<String,ArrayList<String>> depandMap;
   Object lock = new Object();
   StringBuilder display;
   
   BreezeContext okList;
   BreezeContext noList;
   BreezeContext contentNullList;
   int idx = 0;
%>
<%!
   //根据输入的对象路径，处理路径下所有js脚本
   //返回的是包括自己和所有他依赖的对象
   public void processAll(String dir,String base,String[][] filterDir)throws IOException{
       String baseDir = base;
       if (baseDir == null){
          baseDir = Cfg.getCfg().getRootDir() + "/";
       }

       File fDir = new File(baseDir + dir);

      outer: for (File f : fDir.listFiles()){
       
          if (f.isDirectory()){
              //先进行目录判断是否要过滤
              String s = getStanderUrl(f.getCanonicalPath());
              for (int i=0;i<filterDir[0].length;i++){
                  if (s.startsWith(filterDir[0][i])){
                      continue outer;
              	  }
              }
              
              
              processAll(f.getName(),baseDir+"/"+dir+"/",filterDir);
              continue;
          }
          //初始化依赖堆栈，防止循环引用
          depandStack = new HashSet<String>();
          String s = f.getCanonicalPath();
          for (int i=0;i<filterDir[1].length;i++){
             if (s.indexOf(filterDir[1][i])<0){
            	continue outer;
         	 }
          }
          processOneFile(f);
       }
   }
   

%>

<%!
   public  ArrayList<String> processOneFile(File f) throws IOException{
      String[] any1 = f.getName().split("\\.");
      String ext = any1[any1.length-1];
      if (!"js".equalsIgnoreCase(ext) && !"tpl".equalsIgnoreCase(ext)){
         return null;
      }
      String baseSig = getStanderUrl(f.getCanonicalPath());

      boolean canWrite = true;
      
      
      //判断是否已经有引用堆栈，即循环引用情况
      if (depandStack.contains(baseSig)){
		 canWrite = false;
      }
      else{
         depandStack.add(baseSig);
      }
      
      
      //判断是否已经写入过
      if (hashProcess.contains(baseSig)){
         canWrite = false;
      }
      
      String content = FileTools.readFile(f,"UTF-8");
      ArrayList<String> myDepand = depandMap.get(baseSig);
      if (myDepand == null){
          myDepand = getDepand(baseSig,content);
          if (myDepand == null){
              noList.pushContext(new BreezeContext(baseSig));
              return null;
          }
          depandMap.put(baseSig,myDepand);
      }
      
      
      //处理写入
      if (canWrite ){
          okList.pushContext(new BreezeContext(baseSig));
          hashProcess.add(baseSig);
          if (baseSig.endsWith(".tpl")){
              writeOneTpl(baseSig,content,myDepand);
          }else{
              writeOneJs(baseSig,content,myDepand);
          }
          
      }
      //处理返回结果，需要合并新的结果对象，然后返回

       return myDepand;
   }
%>

<%!
    public String getStanderUrl(String url) throws IOException{
       String baseS = (Cfg.getCfg().getRootDir()+"/").replaceAll("[\\\\\\/]+","/");
       File f = new File(url);
       String fName = f.getCanonicalPath().replaceAll("[\\\\\\/]+","/");

       if (!fName.startsWith(baseS)){
           return null;
       }
       
       String rs = fName.substring(baseS.length());
       if (rs.endsWith(".js")){
           rs = rs.substring(0,rs.length()-3);
       }
       return rs;
    } 
%>

<%!
    public ArrayList<String> getDepand(String baseSig,String content) throws IOException{
       //首先要判断，这个是不是要压缩的文件，如果不是就忽略掉
       if (content == null){
           contentNullList.pushContext(new BreezeContext(baseSig));
           return null;
       }

       if (baseSig.endsWith(".tpl")){
           return new ArrayList<String>();
       }
       String myContent = content;
       
       while(true){
           Pattern p = Pattern.compile("^[^\\w-+=\\\\\\/@!#\\$]*/\\*[\\s\\S]+?\\*/\\s*([\\s\\S]+)");
           Matcher m = p.matcher(myContent.trim());
           if (m!=null && m.find()){

              myContent = m.group(1);

           }else{
              break;
           }
           
       }
       
       Pattern p = Pattern.compile("^define\\(");
       Matcher m = p.matcher(myContent);
       if (!m.find()){
           return null;
       }

       //查找所有的require
       p = Pattern.compile("[\\r\\n][^/\\*\\r\\n]+require\\s*\\(([\"'])([\\.\\w\\/]+)\\1\\)");
       m = p.matcher(myContent);
       ArrayList<String> result =  new ArrayList<String>();
       
       while(m.find()){
           String newDepand = m.group(2).trim();
           //获取当前相对路径
           int lastidx = baseSig.lastIndexOf("/");
           String rDir = baseSig.substring(0,lastidx+1);
          
          if (!newDepand.endsWith(".js") && !newDepand.endsWith(".tpl")){
               newDepand =  newDepand + ".js";
           }
           
           if (newDepand.startsWith(".")){
              newDepand = rDir + newDepand;
           }
           
           String newDepandStand = getStanderUrl(Cfg.getCfg().getRootDir() + "/" + newDepand);
           result.add(newDepandStand);
           File f = new File(Cfg.getCfg().getRootDir() + "/" + newDepand);
           //递归获取这里面的依赖文件
           ArrayList<String> newDepandArr = processOneFile(f);
           if (newDepandArr != null){
               result.addAll(newDepandArr);
           }else{
           display.append(baseSig).append("->").append(f.toString()).append("\n");
               return null;
           }
       }
       
       return result;
       
    } 
%>

<%!
    public void writeOneJs(String baseSig,String content,ArrayList<String> depand){
        if (depand == null){
            return;
        }
        this.idx ++ ;
        StringBuilder replaceStr = new StringBuilder();
        replaceStr.append("define(\"").append(baseSig).append("\",[");
        for (int i=0;i<depand.size();i++){
            if (i>0){
                replaceStr.append(',');
            }
            replaceStr.append("\"").append(depand.get(i)).append("\"");
        }
        replaceStr.append("],function");
        String result = content.replace("define(function",replaceStr.toString())+";\n";
        
        
        File rf = new File(Cfg.getCfg().getRootDir() + "/package/temp/" + this.idx + ".js");
        FileTools.writeFile(rf,result,"utf-8");
    }
%>

<%!
    public void writeOneTpl(String baseSig,String content,ArrayList<String> depand){
        if (depand == null){
            return;
        }
        this.idx ++ ;
        
        String s = content.replaceAll("\\\\","\\\\\\\\").replaceAll("[\\n\\r]+","\\\\n").replace("\"","\\\"");
        String result = "define(\""+baseSig+"\",[],\""+s+"\");";
        
        File rf = new File(Cfg.getCfg().getRootDir() + "/package/temp/" + this.idx + ".tpl");
        FileTools.writeFile(rf,result,"utf-8");
    }
%>

<%!
   //根据输入的对象路径，处理路径下所有js脚本
   //返回的是包括自己和所有他依赖的对象
   public void combineAll(String destFile)throws IOException{
       OutputStream destStream = new BufferedOutputStream(new FileOutputStream(Cfg.getCfg().getRootDir() + "/" + destFile));
   
       File srcs = new File(Cfg.getCfg().getRootDir() + "/package/temp/");
       for (File f : srcs.listFiles()){
           BufferedInputStream inf = new BufferedInputStream(new FileInputStream(f));
           byte[] buff = new byte[1000000];
           while(true){
              int len = inf.read(buff,0,buff.length);
              if (len == -1){
                 inf.close();
                 break;
              }
              destStream.write(buff,0,len);
           }
       }
       
       destStream.close();
   }
   

%>

<%
     synchronized(this.lock){
       //初始化全局变量
       hashProcess = new HashSet<String>();
       
       depandMap = new HashMap<String,ArrayList<String>>();
       this.idx = 0;
       display = new StringBuilder();
       
       
       okList = new BreezeContext();
       noList = new BreezeContext();
       contentNullList = new BreezeContext();
   
       //初始化原来的文件
       File s = new File(Cfg.getCfg().getRootDir() + "/package/Gadget.temp");
       File d = new File(Cfg.getCfg().getRootDir() + "/package/Gadget.js");
       if (s.exists()){
          FileTools.copyFile(d,s);
       }
       
       File old = new File(Cfg.getCfg().getRootDir() + "/package/temp/");
	   if (old.exists()){
		   for (File f : old.listFiles()){
			   old.delete();
		   }
	   }
       
       //开始转换
       for (int i=0;i<iDir.length;i++){
           processAll(iDir[i],null,filterDir[i]);
       }
       combineAll(destFile);
     }

     
     
     BreezeContext result = new BreezeContext();
     result.setContext("okList",okList);
     result.setContext("noList",noList);
     result.setContext("contentNullList",contentNullList);
     response.getWriter().println( genResult(0,result));
%>