package com.breeze.framwork.netserver.filter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.breeze.framwork.netserver.process.AutoMachineProcess;
import com.breeze.framwork.netserver.process.ServerProcess;
import com.breeze.framwork.netserver.process.ServerProcessManager;
import com.breeze.support.tools.DirManager;
import com.breeze.base.log.*;

/**
 * filter原理类似与Servlet里面的filter机制
 * 在该机制下，支持在调用正式的service时，调用其他的service
 * filter的配置信息都是
 * {
 * 	serviceName:xxx.xxx
 *  before:xxx.xxx
 *  after:xxx.xxx
 * }
 * 全局维护一个map 以servicename为key
 * @author wgfly
 *
 */
public class BreezeFilterMgr { 
	private HashMap<String,BreezeFilterBean> filterMap = new HashMap<String,BreezeFilterBean>();
	private String baseDir;
	private static Logger log = Logger.getLogger("com.breeze.framwork.netserver.filter.BreezeFilterMgr");
	private DirManager dMgr;
	
	public static BreezeFilterMgr INSTANCE = new BreezeFilterMgr();
	
	public void init(String rootDir,String processDir) {                
        //基础路径
        this.baseDir = rootDir + processDir;
        log.severe("begin load flow setting in dir:" + baseDir);
        this.reloadMap();
        
    }
	
	public void reloadMap(){
		HashMap<String, BreezeFilterBean> newMap = new HashMap<String, BreezeFilterBean>(); 
        dMgr = new DirManager(this.baseDir);
        //读入磁盘路径
        ArrayList<File> dirL = dMgr.getAllFile(null);
        if (dirL != null) {
            for (File f : dirL) {
                try {
                    log.severe("begin load filter :" + f);
                    BreezeFilterBean p = BreezeFilterBean.createFilter(f);
                    String serverName = p.getServiceName();
                    if (p != null && serverName != null) {
                    	newMap.put(serverName, p);                        
                    }
                    log.severe("finished load filter :" + f);
                } catch (Exception e) {
                    log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
                }
            }
        }
        
        this.filterMap = newMap; 
	}
	
	public BreezeFilterBean getFilter(String name){

		return this.filterMap.get(name);
	}
}
