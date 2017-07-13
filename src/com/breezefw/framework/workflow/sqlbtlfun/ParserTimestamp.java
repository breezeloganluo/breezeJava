package com.breezefw.framework.workflow.sqlbtlfun;
/**
 * @version 0.01  2016-0507 罗光瑜修改 时间函数支持特殊含义，比如daybegin获取当天开始时间，dayend获取当天最晚时间
 * 
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breeze.base.log.Logger;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
/**
 * 默认输出的是一个时间戳
 * 输入参数如果是daybegin则是一天的开始
 * 输入参数如果是dayend就是返回一天的结束
 * 输入参数中，如果有returnvalue则在返回值中返回时间戳，而不是?且不会向数组返回实际值
 * @author Logan
 *
 */
public class ParserTimestamp extends SqlFunctionAbs {

	
	@Override
	protected String getName() {
		return "time";
	}
	private Logger log = Logger.getLogger("com.breezefgw.ability.btl.function.sql.ParserTimestamp");

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		try {
			//判断输出转向
			boolean returnValue = false;
			Pattern p = Pattern.compile("\\s*returnvalue\\s*",Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(funParam);
			if (m.find()){
				returnValue = true;
			}
			
			//判断是否开始时间
			p = Pattern.compile("\\s*daybegin\\s*",Pattern.CASE_INSENSITIVE);
			m = p.matcher(funParam);
			if (m.find()){
				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND,0);
				if (!returnValue){
					output.add(c.getTimeInMillis());
					return "?";
				}
				else{
					return String.valueOf(c.getTimeInMillis()) ;
				}
			}
			//判断和处理dayend
			p = Pattern.compile("\\s*dayend\\s*",Pattern.CASE_INSENSITIVE);
			m = p.matcher(funParam);
			if (m.find()){
				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, 23);
				c.set(Calendar.MINUTE, 59);
				c.set(Calendar.SECOND,59);
				if (!returnValue){
					output.add(c.getTimeInMillis());
					return "?";
				}
				else{
					return String.valueOf(c.getTimeInMillis());
				}
			}
			
			
			//最后就返回当前时间
			if (!returnValue){
				output.add(System.currentTimeMillis());
				return "?";
			}
			else{
				return String.valueOf(System.currentTimeMillis());
			}
		} catch (Exception e) {
			log.severe("Exception use the default Data -1:",e);
			output.add(0);
			return "?";
		}
	}

	@Override
	protected String getPackage() {
		return "sql";
	}
	
	public static void main(String[] args){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);

            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            
            System.out.println( c.getTimeInMillis());
	}
}
