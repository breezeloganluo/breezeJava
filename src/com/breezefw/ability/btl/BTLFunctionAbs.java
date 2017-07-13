package com.breezefw.ability.btl;

import java.util.ArrayList;

/**
 * 这是一个函数执行的虚基类 原先的函数体系太复杂，不好懂，这次修改，功能减弱，目的在于使用简单容易理解
 * 
 * @author 罗光瑜
 */
public abstract class BTLFunctionAbs {

	/**
	 * 执行函数，被子类继承，在解析函数中被调用
	 * 
	 * @param funParam
	 *            在模板中，函数的输入参数，注意这里只输入函数中()部分字符，如果有多个参数，要自己解析， 例如，函数输入是：
	 *            fun(a,b)这里funParam就是"a,b"
	 * @param evenenvironment
	 *            是外界环境信息变量，函数根据这个反馈信息
	 * @param output
	 *            如果有第三方的输出，可以往则个输出参数中写入东西
	 * @return 解析后的字符串
	 */
	protected abstract String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output);

	/**
	 * 返回本函数的名称，用于制作函数类型map
	 * 注意：""是特殊值表示这是默认函数
	 * @return
	 */
	protected abstract String getName();
	
	protected abstract String getPackage();
}