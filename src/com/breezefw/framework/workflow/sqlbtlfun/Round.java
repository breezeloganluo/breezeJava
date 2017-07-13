package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;



import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

/**
 * 产生一个随机数的函数一共三个参数： 长度，类型，存放的内存地址
 */
public class Round extends SqlFunctionAbs {
	
	private Logger log = Logger.getLogger("com.breezefw.framework.workflow.sqlbtlfun.Round");
	

	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		// 获取内存上下文
		BreezeContext root = (BreezeContext) evenenvironment[0];
		// 解析参数
		String[] ps = funParam.split(",");
		if (ps.length != 3){
			log.severe("param error,len not 3");
			return "[round error]";
		}
		int len = Integer.parseInt(ps[0].trim());
		int type = Integer.parseInt(ps[1].trim());
		String path = ps[2];
		// 调用gentRound生成随机数
		String runStr = this.gentRound(len, type);
		// 处理结果，将结果写入上下文
		root.setContextByPath(path, new BreezeContext(runStr));
		return "";
	}

	/**
	 * @param len
	 *            长度
	 * @param type
	 *            0数字，1字母，2数字或字母
	 * @return 对应随机数
	 */
	private String gentRound(int len, int type) {
		char[] result = null;
		// if (类型是数字){按数字处理结果
		if (type == 0) {
			result = new char[len];
			for (int i = 0; i < len; i++) {
				result[i] = (char) (Math.random() * 10 + '0');
			}
		}
		// }
		// else if (类型是字母){处理字母情况的随机数
		else if (type == 1) {
			result = new char[len];
			for (int i = 0; i < len; i++) {
				result[i] = (char) (Math.random() * 26 + 'a');
			}
		}
		// }
		// else{数字加字母情况
		else{
			result = new char[len];
			for (int i = 0; i < len; i++) {
				result[i] = (char) (Math.random() * 36 + '0');
				double seed = Math.random()*36;
				if (seed <10){
					result[i] = (char) (seed + '0');
				}
				else{
					result[i] = (char) ((seed-10) + 'a');
				}
			}
		}
		// }
		return String.valueOf(result);
	}

	@Override
	protected String getName() {
		return "round";
	}

	public static void main(String[] args) {
		Round r = new Round();
		System.out.println(r.gentRound(5, 2));
	}

}
