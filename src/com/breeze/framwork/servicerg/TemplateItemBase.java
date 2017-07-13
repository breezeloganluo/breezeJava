/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.breeze.framwork.servicerg;

import com.breeze.framwork.databus.BreezeContext;
import com.google.gson.Gson;

/**
 *
 * @author Logan
 * 这是一个业务模板项，即模板中某一个具体配置项的基本类<br>
 * 具体不同业务需要重载这个类，然后定义自己的具体属性和方法
 */
public class TemplateItemBase {
    public String baseValue;
    public TemplateItemBase(){
        
    }
    public TemplateItemBase(String p){
        this.baseValue = p;
    }
    
    /**
     * 产生具体实例时会被自动调用<br>
     * 在具体的子类被构造时，会直接对成员变量进行构造，而不会调用对应的构造函数<br>
     * 因此，如果子类在获得具体成员变量后，有些初始化逻辑，需要重载这个方法
     */
    public void loadingInit(){
        
    }
    
    public TemplateItemBase runingInit(BreezeContext context){
    	return this;
    }
    
    /**
     * 这是一个让子类测试自身能否被序列化的测试类，使用方法：
     * 在子类中调用this.testCfg2Class注意一定要用this调用
     * @param 传入的配置字符串
     * @return 自己
     */
    public TemplateItemBase testCfg2Class(String s){
        Gson gson = new Gson();
        Class<TemplateItemBase> itemClass = (Class<TemplateItemBase>) this.getClass();
        TemplateItemBase result = gson.fromJson(s, itemClass);
        result.baseValue = s;
        result.loadingInit();
        return result;
    }
    
    public String toString(){
    	return this.baseValue;
    }
}
