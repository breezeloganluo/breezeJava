package com.breezefw.framework.workflow.checker;

import java.util.HashMap;


/**
 *
 * @author Administrator
 */
public class FullContextCheckerMgr {

    public static FullContextCheckerMgr INSTANCE = new FullContextCheckerMgr();
    private HashMap<String, FullContextCheckerAbs> fcheckMap;

    private FullContextCheckerMgr() {
        this.fcheckMap = new HashMap<String, FullContextCheckerAbs>();
    }

    public void init(FullContextCheckerAbs[] f) {
        for (int i = 0;f!=null && i < f.length; i++) {
            this.fcheckMap.put(f[i].getName(), f[i]);
        }
    }

    public FullContextCheckerAbs getFuckCheck(String name) {
        return this.fcheckMap.get(name);
    }
}
