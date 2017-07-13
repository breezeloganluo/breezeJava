package com.breeze.support.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.google.gson.Gson;

public class ClassTools {
	/**
	 * 用Map<String,String>创建对象，其中
	 * key对应类名，value对应其值
	 * @param _c 对象类型
	 * @param _m 成员map
	 * @param needFullName 成员map中的key是否要全路径名及pageName.className.fieldName true是需要，false不需要
	 * @return
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object getObjFromMap(Class _c,Map<String,String>_m,boolean needFullName) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		//创建实例
		Object o = _c.getConstructor().newInstance();
		//遍历所有的私有成员
		for (Field f:_c.getDeclaredFields()){
			Class fc = f.getType();
			String key = needFullName?_c.getName()+'.'+f.getName():f.getName();
			String vstr = _m.get(key);
			if (vstr == null){
				continue;
			}
			f.setAccessible(true);
			if (fc.equals(String.class)){
				f.set(o, vstr);
			}else
			if (fc.equals(int.class)){
				f.set(o, Integer.parseInt(vstr));
			}else
			if (fc.equals(double.class)){
				f.set(o, Double.parseDouble(vstr));
			}else
			if (fc.equals(boolean.class)){
				f.set(o,Boolean.parseBoolean(vstr));
			}else
			if (fc.equals(float.class)){
				f.set(o, Float.parseFloat(vstr));
			}else{
				//用gson解析
				Gson gson = new Gson();
				f.set(o, gson.fromJson(vstr, fc));
			}			
		}
		return o;
	}
	/**
	 * 用Map<String,GsonTools.OS>创建对象实例
	 * 其中key是成员名，GsonTols.OS是对应值的gson表示
	 * @param _c 对象类型
	 * @param _m 成员map
	 * @param needFullName 成员map中的key是否要全路径名及pageName.className.fieldName true是需要，false不需要
	 * @return 对应的对象
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object getObjFromGsonOsMap(Class _c,Map<String,GsonTools.OS>_m,boolean needFullName) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		//创建实例
		Object o = _c.getConstructor().newInstance();
		//便利所有的私有成员
		for (Field f:_c.getDeclaredFields()){
			Class fc = f.getType();
			String key = needFullName?_c.getName()+'.'+f.getName():f.getName();
			GsonTools.OS vstr = _m.get(key);
			if (vstr == null){
				continue;
			}
			f.setAccessible(true);
			if (fc.equals(String.class)){
				f.set(o, vstr.toString());
			}else
			if (fc.equals(int.class)){
				f.set(o, Integer.parseInt(vstr.toString()));
			}else
			if (fc.equals(double.class)){
				f.set(o, Double.parseDouble(vstr.toString()));
			}else
			if (fc.equals(boolean.class)){
				f.set(o,Boolean.parseBoolean(vstr.toString()));
			}else
			if (fc.equals(float.class)){
				f.set(o, Float.parseFloat(vstr.toString()));
			}else{
				//用gson解析
				Gson gson = new Gson();
				f.set(o, vstr.toObject(fc));
			}			
		}
		return o;
	}
}
