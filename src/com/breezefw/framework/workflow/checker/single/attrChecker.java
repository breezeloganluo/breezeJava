package com.breezefw.framework.workflow.checker.single;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;

/**
 * 这个类是一个比较类，可以比较输入的对象和参数是否相等，这个时候，就不用写=
 * 否则一定是数字，可以用于比较大小，而这种情况下，参数写法是被比较的值，和><或!=
 * @author Logan
 *
 */
public class attrChecker extends SingleContextCheckerAbs {

	private static final Logger log=Logger.getLogger("com.breezefw.framework.checker.attrChecker");
	public String getName() {
		// TODO Auto-generated method stub
		return "attrChecker";
	}

	public boolean check(BreezeContext root,BreezeContext checkValue, Object[] param) {
		String p = param[0].toString();
		log.severe("param is "  + p);
		log.severe("checkValue is "  + checkValue);
		
		//包含","的情况 如：“_S.count,>”
		if(p.indexOf(",")>0){
			log.severe("param contain \",\" ");
			String[] params = p.split(",");
			log.severe("original param is "+params[0] + " and " + params[1]);
			//获取需要校验的对象，并转为int类型
			int isCheckedObj = Integer.parseInt(checkValue.getData().toString());
			log.severe("isCheckedObj param is "+isCheckedObj);
			//获取校验所需的参数
			int paramObj = Integer.parseInt(root.getContextByPath(params[0]).getData().toString());
			log.severe("compared param is "+paramObj);
			//判断校验的类型
			if(">".equals(params[1])){
				if(isCheckedObj>paramObj){
					return true;
				}else{
					return false;
				}
			}else if("<".equals(params[1])){
				if(isCheckedObj<paramObj){
					return true;
				}else{
					return false;
				}
			}else if(">=".equals(params[1])){
				if(isCheckedObj>=paramObj){
					return true;
				}else{
					return false;
				}
			}else if("<=".equals(params[1])){
				if(isCheckedObj<=paramObj){
					return true;
				}else{
					return false;
				}
			}else if("!=".equals(params[1])){
				if(isCheckedObj!=paramObj){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
		BreezeContext breezeContext2 = root.getContextByPath(p);
		
		
		if(checkValue.getData().toString().equals(breezeContext2.toString()))
		{
			return true;
		}
		return false;
	}

}
