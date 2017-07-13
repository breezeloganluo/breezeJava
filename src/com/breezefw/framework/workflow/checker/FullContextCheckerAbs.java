package com.breezefw.framework.workflow.checker;

import com.breeze.framwork.databus.BreezeContext;



/**
 * 全参数校验，输入的是一个全部参数。
 * 该函数也是一个虚拟函数。
 */
public abstract class FullContextCheckerAbs 
{
   public abstract String getName();   
   
   
   /**
    * @param checkValues
    * @return boolean
    * @roseuid 4B93464703B9
    */
   public abstract boolean checker(BreezeContext[] checkValues) ;
   
}
