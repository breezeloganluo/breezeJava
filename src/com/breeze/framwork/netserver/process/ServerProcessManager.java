/**
 * 这是一个服务的处理的管理对象，负责载入所有在文件配置的服务流程
 * 并且根据服务名称，返回对应的服务流程
 */
package com.breeze.framwork.netserver.process;

import com.breeze.base.log.Logger;
import com.breeze.support.tools.DirManager;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ServerProcessManager {

    private static Logger log = Logger.getLogger("com.breeze.framwork.netserver.process.ServerProcessManager");
    public static ServerProcessManager INSTANCE = new ServerProcessManager();
    private HashMap<String, ServerProcess> serverMap;    
    private DirManager dMgr;
    private String baseDir = null;
    public static HashMap<String,Integer> SInfo = new HashMap<String,Integer>(); 

    private ServerProcessManager() {        
    }
    /**
     * @roseuid 4B979C10030D
     */
    public void init(String rootDir,String processDir) {                
        //基础路径
        this.baseDir = rootDir + processDir;
        log.severe("begin load flow setting in dir:" + baseDir);
        this.reloadMap();
        
    }
    
    public void reloadMap(){
    	HashMap<String, ServerProcess> newMap = new HashMap<String, ServerProcess>(); 
        dMgr = new DirManager(this.baseDir);
        //读入磁盘路径
        ArrayList<File> dirL = dMgr.getAllFile(null);
        if (dirL != null) {
            for (File f : dirL) {
                try {
                    log.severe("begin load flow :" + f);
                    AutoMachineProcess p = AutoMachineProcess.createProcess(new FileInputStream(f),"ISO-8859-1");
                    String serverName = p.getProcessName();
                    if (p != null && serverName != null) {
                    	newMap.put(serverName, new TransProxyProcess( new FilterProxyProcess(p)));
                    	//2017-05-24罗光瑜添加增加统计信息
                    	SInfo.put(serverName, 0);
                    }
                    log.severe("finished load flow :" + f);
                } catch (Exception e) {
                    log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
                }
            }
        }
        
        this.serverMap = newMap; 
    }


    /**
     * 2017-05-24 新增加的功能，可以对每个真实调用的service进行记录
     * @param name
     */
    public static void addStatic(String name){
    	synchronized (SInfo) {
    		Integer old = SInfo.get(name);
        	if (old == null){
        		SInfo.put(name, 1);
        		return;
        	}
        	SInfo.put(name, old + 1);
		}
    }
    
    public ServerProcess getServer(String serverName) {
        return this.serverMap.get(serverName);
    }

    public Collection<ServerProcess> getServiceProcesses(){
        return this.serverMap.values();
    }
}
