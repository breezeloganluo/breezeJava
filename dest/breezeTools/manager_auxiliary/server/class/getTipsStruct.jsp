<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.regex.*"%>
<%@page import="java.lang.*"%>
<%@page import="java.lang.reflect.Field"%>
<%@page import="java.lang.reflect.Method"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.framwork.databus.BreezeContext" %>
<%@ page import="com.breezefw.compile.BreezeCompile" %>
<%@ page import="java.util.regex.*" %>
<%@ include  file="../module/result.jsp"%>
<%
   //！本jsp功能是根据客户端上传的类路径串，返回该串下详细的成员信息，例如，客户端传入的是：String.class
   //--那么本类就要通过类反射，返回String类下class成员，下的所有属性和方法
   //--这个参数通过参数名path上传，另外，为了查找类，客户端还将import的内容，以及包名一起传入，本类要根据这些信息查找类
%>






<%
    //！这段代码主要是获取客户端参数用
    BreezeContext root = ContextMgr.getRootContext();
    //获取import内容
    BreezeContext include = root.getContextByPath("_R.include");
    //获取package内容
	BreezeContext pkgCtx = root.getContextByPath("_R.package");
    if (pkgCtx == null || pkgCtx.isNull()){
       response.getWriter().println( genResult(11,new BreezeContext("参数错误，package为空")));
       return;
    }
    String pkg = pkgCtx.toString();
     //获取路径信息
     BreezeContext pathCtx = root.getContextByPath("_R.path");
    if (pathCtx == null || pathCtx.isNull()){
       response.getWriter().println(genResult(11,new BreezeContext("参数错误，path为空")));
       return;
    }
    
    //如果pathArr只有一层结构，那么就把pathArr的所有属性和方法返回去
    //--返回的格式是 {属性名:"true/false","函数名":{函数体声明:true}}
    //--其中，属性名对应变量如果是java的基本类型，例如int，byte，就返回true即可，否则返回false
    //--其中函数体声明的格式如：(String p1,int p2)
    //声明相关基础变量
     
    
    String path = pathCtx.toString();
    String[] pathArr = path.split("\\.");

    Class myClass = null;
    
    if (pathArr.length == 1){
       myClass = getMyDefindClass(include,pkg,pathArr[0]);
       
    }else{
      //如果pathArr大于1那么，后面的path的内容就是对应对象的实际成员，一直查找如上，把这个成员最后的一个节点内容的下一层所有属性和函数返回
      //--返回的格式参考上一段即pathArr只有一层情况的代码
   	   
       myClass = getMyDefindClass(include,pkg,pathArr[pathArr.length-1]);
       
    }
    //如果还没找到类，就返回错误
     if (myClass == null){
          response.getWriter().println(genResult(12,new BreezeContext("参数错误，无法找类")));
          return;
      }
      BreezeContext result =  getMyResult(myClass);
      if(result==null){
      	 response.getWriter().println(genResult(12,new BreezeContext("处理异常")));
         return;
      }
      response.getWriter().println(genResult(0,result));
   
   
%>

<%!
	 public BreezeContext  getMyResult(Class myClass){
     
        BreezeContext result = new BreezeContext();

     	try {
 			Field[] gg = myClass.getFields();
			if(gg!=null&&gg.length>0){
				for(int i = 0 ; i <gg.length ; i++){
					Field ff = gg[i];
                    String name = ff.getType().toString();

                    if(name.equals("byte")||name.equals("shot")||name.equals("int")||name.equals("long")||
							name.equals("float")||name.equals("double")||name.equals("char")||name.equals("boolean")){
                         result.setContext(ff.getName(),new BreezeContext(true));
					}else {
                    	result.setContext(ff.getName(),new BreezeContext(false));
					}
 
 				}
				 
			}
            
            
            Method[] methods = myClass.getMethods();
	           if(methods!=null&&methods.length>0){
	        	   for (Method method : methods) {
		               String methodName = method.getName();
 		               Class<?>[] parameterTypes = method.getParameterTypes();
                       
                       StringBuilder sb = new StringBuilder(); 
                       BreezeContext tmp = new BreezeContext();
		               if(parameterTypes==null||parameterTypes.length==0){
                           sb.append("()");
                           tmp.setContext(sb.toString(),new BreezeContext(true));
                           result.setContext(methodName,tmp);
		            	   continue;
		               }
                      
                       

		               sb.append("(");
		               for (Class<?> clas : parameterTypes) {
		                   String parameterName = clas.getName(); 
		                   sb.append(parameterName).append(",");
 		               }
                       String tempStr = sb.toString().substring(0,sb.toString().length()-1);
		               tempStr += ")";
                       tmp.setContext(tempStr,new BreezeContext(true));
                       result.setContext(methodName,tmp);
 		           }
	           }
            
            
		
		} catch (Exception e) {
 			 
           return null;
		}
        
        return result;

      
     }


%>

<%!
 
    //!这段代码在找到类后，就开始进行类成员查找
    //--如果没有找到，则返回错误结果码为13的信息给客户端
    
    public  Class getMyDefindClass(BreezeContext include,String pkg,String index){
  	    //!这段代码查找类对象
   
      Class myClass = null;
      //从import中找对象,import是一个数组
      //--要判断imort中是p.c.Class还是p.c.*模式，如果是后者，就要凭借pathArr第一个元素拼接，判断类是否存在
      //--否则，去最后一段匹配那个Class和pathArr第一个元素是否匹配
      //--如果找到了，那么就跳过类查找，进入下一步
      if (include != null && !include.isNull()){
         for (int i=0;i<include.getArraySize();i++){

            String temp = "";
            String one = include.getContext(i).toString();

             if(one.contains("*")){

                temp =  one.substring(0,one.lastIndexOf(".")+1)+ index;

                try {
                   myClass = Class.forName(temp,false,BreezeCompile.getCurLoader());
                } catch (Exception e) {

                   continue;
                }


             }else{
               temp = one.substring(one.lastIndexOf(".")+1, one.length());
               if(temp.equals(index)) {
               try {
                  myClass = Class.forName(one,false,BreezeCompile.getCurLoader());
               } catch (Exception e) {

                   continue;
               }
               break;
               }
            }

         }
      }
       //如果上面找不到类，就到同包下查找类，即把pathArr第一个元素，和包名拼接在一起查找类
      if (myClass == null){
          String temp = pkg +"."+index;
           try {
               myClass = Class.forName(temp,false,BreezeCompile.getCurLoader());
           } catch (Exception e) {

               myClass = null;
           }

      }
      //如果上面还找不到类，就到java.lang包中拼接pathArr第一个元素，进行类查找
      if (myClass == null){

          String temp = "java.lang." + index;
           try {
               myClass = Class.forName(temp,false,BreezeCompile.getCurLoader());
           } catch (Exception e) {

               return null;
           }
      }
      return myClass;
   
  }
 

%>
 
    
    
 
 