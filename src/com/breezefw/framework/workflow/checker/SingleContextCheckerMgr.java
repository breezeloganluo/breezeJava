/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breezefw.framework.workflow.checker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Administrator
 */
public class SingleContextCheckerMgr {

    public static SingleContextCheckerMgr INSTANCE = new SingleContextCheckerMgr();
    private HashMap<String, SingleContextCheckerAbs> scheckMap;

    private SingleContextCheckerMgr() {
        this.scheckMap = new HashMap<String, SingleContextCheckerAbs>();
    }

    public void init(ArrayList<SingleContextCheckerAbs> s) {
        for (int i = 0;s!=null && i < s.size(); i++) {
            this.scheckMap.put(s.get(i).getName(), s.get(i));
        }
    }

    public SingleContextCheckerAbs getSingleCheck(String name) {
        return this.scheckMap.get(name);
    }
    
    public void addSingle(SingleContextCheckerAbs checker){
    	this.scheckMap.put(checker.getName(), checker);
    }
}