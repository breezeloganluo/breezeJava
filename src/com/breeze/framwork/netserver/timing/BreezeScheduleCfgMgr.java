package com.breeze.framwork.netserver.timing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.breeze.base.log.Logger;
import com.breeze.framwork.netserver.filter.BreezeFilterMgr;
import com.breeze.support.tools.DirManager;

public class BreezeScheduleCfgMgr {
	private HashMap<Integer, ArrayList<BreezeScheduleCfg>> timingMap = new HashMap<Integer, ArrayList<BreezeScheduleCfg>>();
	private String baseDir;
	private static Logger log = Logger.getLogger("com.breeze.framwork.netserver.filter.BreezeTimingMgr");
	private DirManager dMgr;
	
	public static BreezeScheduleCfgMgr INSTANCE = new BreezeScheduleCfgMgr();
	
	public void init(String rootDir,String processDir) {                
        //基础路径
        this.baseDir = rootDir + processDir;
        log.severe("begin load flow setting in dir:" + baseDir);
        this.reloadMap();
        
    }
	
	public void reloadMap(){
		HashMap<Integer, ArrayList<BreezeScheduleCfg>> newMap = new HashMap<Integer, ArrayList<BreezeScheduleCfg>>(); 
        dMgr = new DirManager(this.baseDir);
        //读入磁盘路径
        ArrayList<File> dirL = dMgr.getAllFile(null);
        if (dirL != null) {
            for (File f : dirL) {
                try {
                    log.severe("begin load timming :" + f);
                    BreezeScheduleCfg p = BreezeScheduleCfg.createTiming(f);
                    if (p != null) {
                    	ArrayList<BreezeScheduleCfg>sList = newMap.get(p.getTime());
                    	if (sList == null){
                    		sList = new ArrayList<BreezeScheduleCfg>();
                    		newMap.put(p.getTime(), sList);
                    	}
                    	sList.add(p);
                    }
                    log.severe("finished load timming :" + f);
                } catch (Exception e) {
                    log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
                }
            }
        }
        
        this.timingMap = newMap; 
	}
	
	public ArrayList<BreezeScheduleCfg> getTiming(int time){

		return this.timingMap.get(time);
	}
}
