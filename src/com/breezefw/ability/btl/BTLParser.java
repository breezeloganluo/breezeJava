package com.breezefw.ability.btl;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个解析类函数， 原来支持多内部嵌套，格式是${#abc(xxx)}这种格式虽然功能强大，但是不好用，容易弄错
 * 现在一次改版，降低难度，重点是提升其可用性，其格式为： ${fun()}简单的方式就是${abc}=${(abc)}即用默认值
 * 
 * 这个函数通过初始化，可以初始化一个域，每个域有这个域下面独立的解析函数，不同域下面，解析函数可以重复
 * 
 * 该BTL的解析模式：先用parser解析模板得到一个excutor，然后把环境参数输入进去，执行，就可以得到被解析后的实际字符串
 * 
 * 2013-09-18罗光瑜做了一次重构，主要是把初始化的方式修改掉，让初始化，下放到业务包中，而不是集中的初始化工作
 * 
 * @author 罗光瑜
 */
public class BTLParser {

	private BTLParser() {
	}

	private static HashMap<String, BTLParser> parserMap;
	private static Object lock = new Object();

	/**
	 * 单例模式，输入一个域，这个域下面有一系列自己的函数集合
	 * 
	 * @param parserName
	 *            返回一个域对应的btl实例
	 * @return �BTLParser
	 */
	public static BTLParser INSTANCE(String parserName) {
		return parserMap.get(parserName);
	}

	/**
	 * 初始化函数，将按照域的方式返回每个域下的转换函数集合
	 * 
	 * @param parserGroupInfo
	 *            一个域的函数集合，map的key就是域名，里面的函数名
	 */
	public static void init(String parserName) {
		synchronized (lock) {
			if (parserMap == null) {
				parserMap = new HashMap<String, BTLParser>();
			}
			if (parserMap.get(parserName) == null) {
				BTLParser inc = new BTLParser();
				inc.packageName = parserName;
				parserMap.put(parserName, inc);
				
			}
		}
	}

	private HashMap<String, BTLFunctionAbs> functionMap = new HashMap<String, BTLFunctionAbs>();
	private String packageName;

	public void addFunction(BTLFunctionAbs f) {
		synchronized (lock) {
			
				this.functionMap.put(f.getName(), f);
	
		}
	}

	/**
	 * 整体解析函数，将产生一个excutor
	 * 
	 * @param template
	 *            被解析的模板
	 * @return 解析过的一个excutor
	 */
	public BTLExecutor parser(String template) {
		BTLExecutor result = new BTLExecutor(this.functionMap);
		// 将所有函数解析出来
		Pattern p = Pattern.compile("([^\\$]*)\\$\\{(.*?)\\}([^\\$]*)");
		Matcher m = p.matcher(template);
		boolean hasFound = false;
		while (m.find()) {
			hasFound = true;
			if (!"".equals(m.group(1))) {
				result.addString(m.group(1));
			}
			result.addFunc(m.group(2));
			if (!"".equals(m.group(3))) {
				result.addString(m.group(3));
			}
		}
		// 如果没有找到函数，直接返回
		if (!hasFound) {
			result.addString(template);
		}
		return result;
	}

	BTLFunctionAbs getBTLFunctionAbs(String name) {
		return functionMap.get(name);
	}
}
