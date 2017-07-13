package com.breeze.framwork.netserver.dbtransprocess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import com.breeze.base.log.Logger;
import com.breeze.support.tools.DirManager;

public class BreezeDBTransProcessMgr {
	private HashSet<String> transSet = new HashSet<String>();
	private String baseDir;
	private static Logger log = Logger.getLogger("com.breeze.framwork.netserver.filter.BreezeDBTransProcessMgr");
	private DirManager dMgr;
	
	public static BreezeDBTransProcessMgr INSTANCE = new BreezeDBTransProcessMgr();
	
	public void init(String rootDir,String processDir) {                
        //基础路径
        this.baseDir = rootDir + processDir;
        log.severe("begin load trans setting in dir:" + baseDir);
        this.reloadMap();
        
    }
	
	public void reloadMap(){
		HashSet<String> newSet = new HashSet<String>();
        dMgr = new DirManager(this.baseDir);
        //读入磁盘路径
        ArrayList<File> dirL = dMgr.getAllFile(null);
        if (dirL != null) {
            for (File f : dirL) {
                try {
                    log.severe("begin load transCfg :" + f);
                    BreezeDBTransProcessBean dbbean = BreezeDBTransProcessBean.createFilter(f);
                    for (int i=0;i<dbbean.getSize();i++){
                    	newSet.add(dbbean.getServiceName(i));
                    }
                    log.severe("finished load transCfg :" + f);
                } catch (Exception e) {
                    log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
                }
            }
        }
        
        this.transSet = newSet; 
	}
	
	public boolean hasTrans(String name){
		return this.transSet.contains(name);
	}
}
