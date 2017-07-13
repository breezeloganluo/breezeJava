/*
 * 业务模板类<br>
 * 包含该业务的所有模板项信息<br>
 * 该类可以根据模板项名称返回对应的模板项
 */
package com.breeze.framwork.servicerg;

import java.util.HashMap;

/**
 *
 * @author l00162771
 */
public class ServiceTemplate {

    HashMap<String, TemplateItemBase> itemMap;
    private String serviceName;
    private String packageName;
    private String flowName;
    private String serviceFileDir;
    
    private HashMap<String,ServiceTemplate>sub;
    
    
    
    
    public ServiceTemplate(String psic, String psver,String ppackage,String sdir) {
        this.serviceName = psic;
        this.flowName = psver;
        this.packageName = ppackage;
        this.serviceFileDir = sdir;
        
        this.itemMap = new HashMap<String, TemplateItemBase>();
        this.sub = new HashMap<String,ServiceTemplate>();
    }

    public void setItem(String key, TemplateItemBase value) {
        this.itemMap.put(key, value);
    }

    /**
     * 根据模板项名称，获取对应的模板类对象
     * @param key 模板项名称
     * @return 对应的模板类
     */
    public TemplateItemBase getItem(String key) {
        return this.itemMap.get(key);
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getServerName() {
        return this.flowName;
    }

    public String getFileName() {
        return this.serviceFileDir;
    }
    
    
    public String getPackageName() {
		return packageName;
	}

	public ServiceTemplate getSub(String subName){
    	ServiceTemplate result = this.sub.get(subName);
    	if (result == null){
    		result = new ServiceTemplate(this.serviceName,this.flowName,this.packageName,this.serviceFileDir);
        	this.sub.put(subName, result);
    	}    	
    	return result;
    }
    
    public String toString(){
    	return this.itemMap.toString() + "\n" + this.sub.toString();
    }
}
