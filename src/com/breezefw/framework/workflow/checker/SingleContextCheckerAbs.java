package com.breezefw.framework.workflow.checker;

import com.breeze.framwork.databus.BreezeContext;

/**
 * 单值的校验类，这是一个虚类，具体的使用其子类进行详细的校验动作。
 */
public abstract class SingleContextCheckerAbs{

    public abstract String getName();


    /**
     * 校验函数，输入的第一个参数是被校验的值
     * 第二个参数是本次校验需要的辅助参数，校验通过返回成功，否则返回失败。
     * @param checkValue
     * @param param
     * @return boolean
     * @roseuid 4B9345710242
     */
    public abstract boolean check(BreezeContext root,BreezeContext checkValue, Object[] param) ;
}
