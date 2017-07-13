/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.servicerg;

import com.breeze.base.log.Logger;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Logan
 * 这是2010-08-25日重构时新增加的类
 * 该类是一个管理类，保存了所有的模板map，类似原来的AllServiceCfgRegister
 * temple方面是连接serice和server的双方的唯一纽带
 */
public class AllServiceTemplate {
    private static Logger log = Logger.getLogger("com.breeze.framwork.servicerg.AllServiceTemple");
    public static AllServiceTemplate INSTANCE = new AllServiceTemplate();    
    private HashMap<String, ServiceTemplate> templateMap = new HashMap<String, ServiceTemplate>();
    public static HashMap<String,Integer> SInfo = new HashMap<String,Integer>();

    private String baseDir;

    /**
     * 私有话的一个构造函数，方便单例
     * 同时，所有的业务都将在这个类里面加载
     */
    private AllServiceTemplate() {
    }

    public final void init(String rootdir,String serviceDir){
        //基础路径
        this.baseDir = rootdir + serviceDir;
        this.reloadMap();
    }
    public final void reloadMap(){
        log.severe("begin load service file in base:" + this.baseDir);
        HashMap<String, ServiceTemplate>tmpRegMap = ServiceRegister.registerAllServiceByDir(this.baseDir); 
        //下面处理测试相关的内容        
        this.templateMap = tmpRegMap;
        //2017-05-24增加了登记service的数量统计的信息
        for (String key : this.templateMap.keySet()){
        	SInfo.put(key, 0);
        }
    }

    public ServiceTemplate getTemple(String serviceName) {
        return this.templateMap.get(serviceName);
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

    public Set<String> getTempleNameSet(){
        return this.templateMap.keySet();
    }

    public void removeServiceTemple(String name){
        ServiceTemplate s = this.getTemple(name);
        if (s == null){
            return ;
        }
        this.templateMap.remove(name);
        
        if (s.getFileName() != null){
            File f = new File(s.getFileName());
            f.delete();
        }
    }
    /////////////////////////////////////////////////////////////
    //下面都是为了方便测试使用的方法
    public void fortestSetMap(HashMap<String, ServiceTemplate> m) {
        this.templateMap = m;
    }
}