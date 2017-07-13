package com.breezefw.framework.workflow.sqlbtlfun;

import java.util.ArrayList;



import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.support.tools.Md5;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;

public class OrgMD5 extends SqlFunctionAbs {

	Logger log = Logger.getLogger("com.breezefw.framework.workflow.sqlbtlfun.OrgMD5");
	@Override
	protected String fun(String funParam, Object[] evenenvironment,
			ArrayList<Object> output) {
		String[] args = funParam.split(",");
		if (args==null || args.length ==0){
			log.severe("param error!");
			return null;
		}
		StringBuilder orgStr = new StringBuilder();
		BreezeContext root = (BreezeContext)evenenvironment[0];
		log.fine("param is:");
		for (int i=0;i<args.length;i++){
			BreezeContext strCtx = root.getContextByPath(args[i]);
			if (strCtx!= null && !strCtx.isNull()){
				log.fine("param["+i+"]="+strCtx.toString());
				orgStr.append(strCtx.toString());
			}
			else{
				log.fine("param["+i+"]="+args[i]);
				orgStr.append(args[i]);
			}
		}
		String md5str = orgStr.toString();
		log.fine("md5str:"+md5str);
		String result = null;
		try {
			result = Md5.getStanderMd5(md5str);
		} catch (Exception e) {
			log.severe("", e);
		}
		return result.toUpperCase();
	}

	@Override
	protected String getName() {
		return "orgMD5";
	}

}
