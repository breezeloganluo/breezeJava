/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.netserver.workflow;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class WorkFlowUnitMgr {
    public static WorkFlowUnitMgr INSTANCE = new WorkFlowUnitMgr();
    private HashMap<String, WorkFlowUnit> unitMap;
    
    private WorkFlowUnitMgr(){
        this.unitMap = new HashMap<String, WorkFlowUnit>();
    }
    
    public final void init(WorkFlowUnit[] wu){
        for (int i=0;i<wu.length;i++){
            this.unitMap.put(wu[i].getName(), wu[i]);
            wu[i].loadingInit();
        }
    }
    
    /**
     * 添加新的处理单元
     * @param wu
     */
    public void addUnit(WorkFlowUnit wu){
    	this.unitMap.put(wu.getName(), wu);
    	wu.loadingInit();
    }
    
    /**
     * @param unitName
     * @return com.qbhui.framwork.netserver.workflow.WorkFlowUnit @roseuid
     * 4B979FB90128
     */
    public WorkFlowUnit getUnit(String unitName) {
        return this.unitMap.get(unitName);
    }
    
    public Set<String> getAllUnitKey() {
        return this.unitMap.keySet();
    }
}
