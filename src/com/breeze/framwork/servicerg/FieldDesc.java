/**
 * 
 */
package com.breeze.framwork.servicerg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 一个注释类信息，这个类用于描述模板类的基本数据类型。
 * @author 罗光瑜
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldDesc {
	String title();
	String desc();
	String type()default "Text";
	String valueRange();
}
