/*
 * ObjTools.java
 *
 * Created on 2010年7月3日, 下午6:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 * 解析对象专用类，解析对象的字符串格式：
 * {[objName][className(p1,p2.....)]}{....}....
 */

package com.breeze.support.tools;
import java.util.*;
import java.util.regex.*;
import java.lang.reflect.*;
import java.net.URLDecoder;
/**
 *
 * @author happy
 * 对象处理类，能根据字符串反射，生成对应的对象
 */
public class ObjTools {
    /**
     *主解析类，解析的字符串格式为：
     *{[objName][className(p1,p2.....)]}{....}....
     *或者
     *{[objName][objMapKeyName)]}
     *@param objStr 一个主map解析参数
     *@param pObjMap 一个对象池
     */
    public static HashMap<String,Object> getObjMap(String objStr,HashMap<String,Object> pObjMap)throws Exception{
        HashMap<String,Object> objMap = pObjMap!=null?pObjMap:new HashMap<String,Object>();
        if (objStr != null){
            String[][] tmpParser = StrParserTools.parserNormalStr(objStr);
            for (int i=0;tmpParser != null && i<tmpParser.length;i++){
                //获取名称
                String objName = tmpParser[i][0];
                String objPStr = tmpParser[i][1];
                //获取对象
                Object obj = getObj(objPStr,objMap);
                if (obj != null){
                    objMap.put(objName,obj);
                }
            }
        }
        return objMap;
    }
    /**
     *getObjMap 一个不带参数的过载函数
     */
    public static HashMap<String,Object> getObjMap(String objStr)throws Exception{
        return getObjMap(objStr,null);
    }
    
    /**
     *从一个字符串中获取一个对象,对象的字符串为：
     *className(p1,p2.....)
     *数组
     *className(a1;a2;a3:ArrayList<String>,p2);
     *hashMap
     *className(n1|a1;n2|a2:HashMap<String;String>,p2);
     *
     *如果字符串没有()
     *那么直接从map中获取对象
     *
     *如果为_()那么返回(中的第一个参数为对象)
     */
    public static Object getObj(String objStr,HashMap<String,Object> objMap)throws Exception{
        //首先用正则表达式获取类名
        Pattern p = Pattern.compile("(\\S+)\\((.*?)\\)");
        Matcher m = p.matcher(objStr);
        String className = null;
        String paramStr = null;
        if(m.find()){
            className = m.group(1);
            paramStr = m.group(2);
        }else{
            //否则，整个对象都来自map对象
            if(objMap != null){
                return objMap.get(objStr);
            }
            return null;
        }
        //用正则表达式获取所有参数
        Object[] paramArr = getParamObj(paramStr,objMap);
        //判断是否是_()的情况
        if ("_".equals(className)){
            if (paramArr.length >=1){
                return paramArr[0];
            }else{
                return null;
            }
        }
        //用发射获取类实例
        Class c = Class.forName(className);
        Constructor[] ccs = c.getDeclaredConstructors();
        for (Constructor cc:ccs){
            int paramCount = cc.getParameterTypes().length;
            if (paramCount == paramArr.length){
                int klen =0;boolean flag = true;
                for (Class pClass:cc.getParameterTypes()){
                    if (pClass.equals(int.class)){
                        pClass = Integer.class;
                    }
                    if (!pClass.equals(paramArr[klen++].getClass())){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    return cc.newInstance(paramArr);
                }
            }
        }
        return null;
    }
    /**
     *给parserServiceTester_getObjMap解析参数用的解析函数
     *这个类输入的是格式为xxx:int,xxx:String,xxxx:Object的格式
     *数组：a1;a2;a3:ArrayList<String>,p2
     *map：n1|a1;n2|a2:HashMap<String;String>,p2
     *输出的是格式类型转换好的List数组
     */
    static Object[] getParamObj(String objStr,HashMap<String,Object> objMap)throws Exception{
        String[]arr1 = objStr.split(",");
        if (arr1 == null || arr1.length == 0){
            return new Object[0];
        }
        Object[] result = new Object[arr1.length];
        for (int i=0;i<arr1.length;i++){
            String session = arr1[i];
            String[] arr2 = session.split(":");
            String valueStr = arr2[0];
            String type = arr2[1];
            if("int".equals(type)){
                result[i] = Integer.parseInt(valueStr);
            }else if("String".equals(type)){
                result[i] = URLDecoder.decode(valueStr,"utf-8");
            }else if("Object".equals(type)){
                if(objMap!=null){
                    result[i] = objMap.get(valueStr);
                }
            }else if(type.indexOf("ArrayList")>=0){
                result[i] = createArrayList(valueStr,type,objMap);
            }else if(type.indexOf("HashMap")>=0){
                result[i] = createHashMap(valueStr,type,objMap);
            }else{
                return null;
            }
        }
        return result;
    }
    /**
     *处理相应的array类型
     *输入的格式为：a1|v1;a2|v2;a3|v3    HashMap<String;String>
     */
    static HashMap createHashMap(String valueStr,String typeStr,HashMap<String,Object> objMap)throws Exception{
        if(valueStr == null ||typeStr == null){
            return null;
        }
        String[]types = typeStr.split(";|:|<|>");
        //最后一个参数是类型
        int last = types.length-1;
        String type1 = types[last-1];
        String type2 = types[last];
        
        
        Pattern p = Pattern.compile("([^\\|;]+)\\|([^\\|;]+);?");
        Matcher m = p.matcher(valueStr);
        HashMap result=new HashMap();
        while(m.find()){
            Object key = null;
            Object value =null;
            String tmpSrc = URLDecoder.decode(m.group(2),"utf-8");
            if ("int".equals(type1)){
                key = Integer.valueOf(m.group(1));
            }else if("String".equals(type1)){
                key = m.group(1);
            }else if("Object".equals(type1)){
                if(objMap != null){
                    key=objMap.get(m.group(1));
                }else{
                    continue;
                }
            }else{
                return null;
            }
            
            if ("int".equals(type2)){
                value = Integer.valueOf(tmpSrc);
            }else if("String".equals(type2)){
                value = tmpSrc;
            }else if("Object".equals(type2)){
                if(objMap != null){
                    value=objMap.get(tmpSrc);
                }else{
                    value=null;
                }
            }else{
                return null;
            }
            
            result.put(key,value);
        }
        return result;
    }
    
    /**
     *处理相应的array类型
     *输入的格式为：a1;a2;a3    ArrayList<String>
     */
    static ArrayList createArrayList(String valueStr,String typeStr,HashMap<String,Object> objMap)throws Exception{
        if(valueStr == null ||typeStr == null){
            return null;
        }
        String[]types = typeStr.split(";|:|<|>");
        //最后一个参数是类型
        int last = types.length-1;
        String type = types[last];
        
        String[]sa = valueStr.split(";|:|<|>");
        
        
        ArrayList result=new ArrayList();
        for(int i=0;i<sa.length;i++){
            if ("int".equals(type)){
                result.add(Integer.parseInt(sa[i]));
            }else if("String".equals(type)){
                result.add(URLDecoder.decode(sa[i],"utf-8"));
            }else if("Object".equals(type)){
                if(objMap != null){
                    result.add(objMap.get(sa[i]));
                }else{
                    result.add(null);
                }
            }else{
                return null;
            }
        }
        return result;
    }
}
