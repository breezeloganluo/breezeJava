package com.breeze.framwork.netserver.dbtransprocess;

import java.io.File;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.support.tools.FileTools;

/**
 * 用json解释过来的bean
 * @author Administrator
 * 注意，本来说，这是一个bean类，就是这个类的结构应该是一个json的结构
 * 但由于这个配置很简单，仅仅是一个字符串数组，所以改变了一下设计
 *
 */
public class BreezeDBTransProcessBean {
	private BreezeContext data;
	private BreezeDBTransProcessBean(BreezeContext bc){
		this.data = bc;
	}
	
	public static BreezeDBTransProcessBean createFilter(File in){
		String content = FileTools.readFile(in, "UTF-8");
		BreezeContext data = ContextTools.getBreezeContext4Json(content);
		return new BreezeDBTransProcessBean(data);
	}
	
	public int getSize(){
		return data.getArraySize();
	}
	
	public String getServiceName(int idx){
		BreezeContext d = this.data.getContext(idx);
		if (d == null){
			return null;
		}
		return d.toString();
	}
	
	
}
