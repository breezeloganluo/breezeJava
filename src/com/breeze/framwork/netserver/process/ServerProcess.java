/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.netserver.process;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;

/**
 *
 * @author l00162771
 */
public abstract class ServerProcess {

	public static class WorkFlowUnitDesc{
    	public WorkFlowUnit action;
    	public String alias;
    	
    	public WorkFlowUnitDesc(WorkFlowUnit ac,String al){
    		this.action = ac;
    		this.alias = al;
    	}
    }
	
    /**
     * 这个方法是用于子类实现的，父类不实现
     *
     * @param request
     * @param response @roseuid 4B979B4F00EA
     */
    public abstract void process(BreezeContext context,ServiceTemplate st);

    /**
     * 这个类用于子类实现，本身父类没有实现
     *
     * @return 本工作流所涉及到的所有WorkFlowUnit类
     */
    public abstract WorkFlowUnitDesc[] getAllWorkFlowUnit();

    public abstract String getProcessName();
}
