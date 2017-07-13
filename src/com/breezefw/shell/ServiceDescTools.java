/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.breezefw.shell;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author 罗光瑜 这个函数根据java的元数据模式，还原每个字段的基本信息
 */
public class ServiceDescTools {
	public static BreezeContext parserItemDesc(Class ItemBase) {
		System.out.println("begin to parser class----------->" + ItemBase.getName());
		
		if (!TemplateItemBase.class.isAssignableFrom(ItemBase)){
			return null;
		}
		// 初始化一个BreezeContext[]作为结果数组
		BreezeContext result = new BreezeContext();
		// 便利所有私有成员{
		for (Field f : ItemBase.getDeclaredFields()) {
			f.setAccessible(true);
			//如果是静态成员，直接排除
			if (Modifier.isStatic(f.getModifiers())){
				continue;
			}
			Class fc = f.getType();
			FieldDesc fieldDesc = f.getAnnotation(FieldDesc.class);
			// if (有注释){
			if (fieldDesc != null) {
				// if (不是数组){
				if (!fc.isArray()) {
					// 生成对应的BreezeContext
					BreezeContext one = null;
					one = createOneField(fieldDesc.title(), fieldDesc.type(),
							fieldDesc.valueRange(), fieldDesc.desc());
					// 还有其他类型
					// 加入到结果中
					result.setContext(ItemBase.getName() + "." + f.getName(),
							one);
				} else {
					// }else{
					//先创建最基本的数据 将title去掉了  林浩旋
					BreezeContext one = createOneFieldByList( fieldDesc.type(),
							fieldDesc.valueRange(), fieldDesc.desc());
					//创建父亲的array对象
					BreezeContext valueRange = one;
					BreezeContext breezeArrayFather = createOneField(f.getName(), "List",
							valueRange, "");
					result.setContext(ItemBase.getName() + "." + f.getName(),
							breezeArrayFather);
					// }
				}
			} else {
				// }else{
				Class ffc = fc;
				// 判断是否数组，根据是否数组对象选择递归对象类型
				boolean isArray = false;
				if (fc.isArray()) {
					ffc = fc.getComponentType();
					isArray = true;
				}
				// if (字符串类型 || int类型 || long类型 || flow类型 || double类型 ||char类型
				// ||short 类型){
				if (ffc.equals(String.class) || ffc.equals(int.class)
						|| ffc.equals(long.class) || ffc.equals(float.class)
						|| ffc.equals(double.class) || ffc.equals(char.class)
						|| ffc.equals(short.class)) {
					// continue;
					continue;
				} else {
					// }else {
					// 递归调用 获取tmpResult = parserItemDesc(这个类型)
					BreezeContext tmpResult = parserItemDesc(ffc);
					// if (tmpResult == null){
					if (tmpResult == null || tmpResult.isEmpty()) {
						// 说明这个对象内没有注释continue
						continue;
					}else{
						BreezeContext one = null;
						// }else{
						// 将根据成员生成，和是否数组对象生成对应BreezeContext
						if (isArray){
							BreezeContext valueRange = new BreezeContext();
							valueRange.pushContext(tmpResult);							
							one = createOneField(f.getName(),"List",valueRange,"");
						}else{
							BreezeContext valueRange = tmpResult;
							one = createOneField(f.getName(),"Object",valueRange,"");
						}
						// }
						//加入结果
						result.setContext(ItemBase.getName() + "." + f.getName(),
								one);
					}
				}
				// }
			}
		}
		// }
		// 返回结果
		return result;
	}

	/**
	 * 将字符串解析成双数组形式，其格式形如 {{a}}{{b},{c}}
	 * 
	 * @return
	 */
	private static String[][] parserValueRange2arr(String valueRange) {
		ArrayList<String[]> tmpResult = new ArrayList<String[]>();
		StringBuffer sb = new StringBuffer();
		// 第一轮迭代将里层{}全部解析，同时将里层{}替换掉
		Pattern p = Pattern.compile("\\{([^\\{\\}]+)\\}");
		Matcher m = p.matcher(valueRange);
		int i = 0;
		while (m.find()) {
			tmpResult.add(m.group(1).split(","));
		}

		// 最后还原成结果类型
		String[][] result = new String[tmpResult.size()][];
		for (i = 0; i < result.length; i++) {
			result[i] = tmpResult.get(i);
		}
		return result;
	}

	/**
	 * 这个函数用于创建一个普通输入框,其中valueRange是一个二维数组，表示要校验的内容
	 * 
	 * @param field
	 *            字段名称
	 * @param title
	 *            字段标题
	 * @param valueRange
	 *            实际的valueRage的描述信息
	 * @param desc
	 *            描述信息
	 * @return 被创建的描述对象
	 */
	public static BreezeContext createOneField(String title, String type,
			String valueRangeStr, String desc) {
		System.out.println(valueRangeStr);
		System.out.println(desc);
		return createOneField(title, type,
				ContextTools.getBreezeContext4Json(valueRangeStr), desc);
	}
	
	
	
	/**
	 * 这个函数用于创建一个普通输入框,其中valueRange是一个二维数组，表示要校验的内容
	 * 
	 * 这里比较特殊  是为了生成特殊List单独写的。林浩旋
	 * 
	 * @param field
	 *            字段名称
	 * @param valueRange
	 *            实际的valueRage的描述信息
	 * @param desc
	 *            描述信息
	 * @return 被创建的描述对象
	 */
	public static BreezeContext createOneFieldByList( String type,
			String valueRangeStr, String desc) {
		
		System.out.println(valueRangeStr);
		System.out.println(desc);
		return createOneFieldByList( type,
				ContextTools.getBreezeContext4Json(valueRangeStr), desc);
	}
	
	/**
	 * 这个函数用于创建一个普通输入框,其中valueRange是一个二维数组，表示要校验的内容
	 * 
	 * @param field
	 *            字段名称
	 * @param title
	 *            字段标题
	 * @param valueRange
	 *            校验数组，可以多组，每组第一个是提示信息，后面是正则表达式
	 * @param desc
	 *            描述信息
	 * @return 被创建的描述对象
	 */
	public static BreezeContext createOneField(String title, String type,
			BreezeContext valueRange, String desc) {
		// 先转换参数
		BreezeContext result = new BreezeContext();
		result.setContext("title", new BreezeContext(title));
		result.setContext("type", new BreezeContext(type));
		result.setContext("desc", new BreezeContext(desc));
		result.setContext("valueRange",valueRange);
		return result;
	}
	
	
	/**
	 * 这个函数用于创建一个普通输入框,其中valueRange是一个二维数组，表示要校验的内容
	 * 
	 * 这里比较特殊  是为了生成特殊List单独写的。林浩旋
	 * 
	 * @param field
	 *            字段名称
	 * @param valueRange
	 *            校验数组，可以多组，每组第一个是提示信息，后面是正则表达式
	 * @param desc
	 *            描述信息
	 * @return 被创建的描述对象
	 */
	public static BreezeContext createOneFieldByList( String type,
			BreezeContext valueRange, String desc) {
		// 先转换参数
		BreezeContext result = new BreezeContext();
		result.setContext("type", new BreezeContext(type));
		result.setContext("desc", new BreezeContext(desc));
		result.setContext("valueRange",valueRange);
		return result;
	}

	public static class TTT extends TemplateItemBase {
		@FieldDesc(desc = "", title = "", valueRange = "[{failTips:'aaaa',checker:[/a/,/b/]}]")
		String a;
	}

	public static void main(String args[]) throws SecurityException,
			NoSuchFieldException {
		String aaa = "{{a,b}{c,d}}";
		String[][] ar = parserValueRange2arr(aaa);

		TTT[] arr = new TTT[1];
		Class c = arr.getClass();
		System.out.println(TTT.class.getName());
		System.out
				.println(c.getComponentType().getDeclaredField("a").getType());

	}
}
