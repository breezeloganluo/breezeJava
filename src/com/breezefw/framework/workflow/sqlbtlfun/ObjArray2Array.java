package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

/**
 * 这个类是将一个对象数组转换成一个实际的数组，便于后续插入操作，例如上一个sql语句进行查询，返回的是一个数组，数组里面是多个结构相同对象，这时
 * 需要向从这个sql的结果集中的某个字段做另外一个插入语句的某个字段数组，自然需要用到这个方法,
 * 这个方法传入的是一个二目参数，path,cloumn名
 * @author Logan
 *
 */
public class ObjArray2Array  extends SqlFunctionAbs {
	private Logger log = Logger.getLogger("com.breezefw.framework.workflow.sqlbtlfun.ObjArray2Array");
	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		BreezeContext root = (BreezeContext) evenenvironment[0];
		if (funParam == null){
			log.fine("funParam is null");
			return "";
		}
		String[] paramArr = funParam.split(",");
	    if (paramArr == null || paramArr.length != 2){
	    	log.fine("param error:"+funParam);
	    	return "";
	    }
	    
	    BreezeContext data = root.getContextByPath(paramArr[0]);
	    if (data == null || data.isNull()){
	    	log.fine("path not right in path :"+paramArr[0]);
	    	return "";
	    }
	    
	    if (data.getType() != BreezeContext.TYPE_ARRAY){
	    	log.fine("data not Array in path :"+paramArr[0]);
	    	return "";
	    }
	    
	    Object[] arr = new Object[data.getArraySize()];
	    for (int i=0;i<data.getArraySize();i++){
	    	BreezeContext oneValueCtx = data.getContext(i).getContext(paramArr[1]);
	    	if (oneValueCtx!= null && oneValueCtx.getType() == BreezeContext.TYPE_DATA){
	    		arr[i] = oneValueCtx.getData();
	    	}else{
	    	    arr[i] = "";
	    	}
	    }
		output.add(arr);
		return "?";
	}

	@Override
	protected String getName() {
		return "ObjArray";
	}
	
	@Override
	protected String getPackage() {
		return "sql";
	}

}
