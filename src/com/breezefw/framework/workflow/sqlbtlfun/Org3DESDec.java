package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.tools.ThreeDes;
/**
 * 一个3DES的处理，接收三个参数，第一个是被加密的值的内存地址
 * 第二个是加密的秘钥地址
 * 第三个是可选参数，如果不存在则返回全部，否则以,为间隔符，返回第n个数据
 * @author 罗光瑜
 *
 */
public class Org3DESDec extends SqlFunctionAbs {
	Logger log = Logger.getLogger("com.breezefw.framework.workflow.sqlbtlfun.Org3DES");
	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		String[] args = funParam.split(",");
		if (args==null || args.length < 2){
			log.severe("param error!");
			return null;
		}
		BreezeContext root = (BreezeContext)evenenvironment[0];		
		BreezeContext valueCtx = root.getContextByPath(args[0]);
		if (valueCtx == null){
			log.severe("value not found in path "+args[0]);
			return "";
		}
		String value = valueCtx.toString();
		
		BreezeContext keyCtx = root.getContextByPath(args[1]);
		if (keyCtx == null){
			log.severe("key not found in path"+args[1]);
			return "";
		}
		String key = keyCtx.toString();
		try{
			String result = ThreeDes.decrypt(value, key);
			if (args.length == 3){
				int idx = Integer.parseInt(args[2]);
				String[] values = result.split(",");
				return values[idx];
			}else{
				return result;
			}
		}catch(Exception e){
			log.severe("3des error", e);
			return "";
		}
		 
	}

	@Override
	protected String getName() {
		return "org3DESDec";
	}
}
