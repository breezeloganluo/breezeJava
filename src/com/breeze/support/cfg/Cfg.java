/*
 * Cfg.java
 *
 * Created on 2007年12月16日, 下午3:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.breeze.support.cfg;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author happy 这是一个配置文件的基类 默认实现就是自己定义一个Map，然后从里面获取配置信息
 * 这个类使用单件模式，在默认的情况下，可以实例化子类，返回子类的实例 这个类采用惰性初始化的方式
 */
public abstract class Cfg {
    private static Cfg instance;
    
    public static void initCfg(Cfg pc){
        Cfg.instance = pc;
    }

    public static Cfg getCfg() {
        return instance;
    }
    /**
     * 配置项值 value有三种情况，String,Array,HashMap
     */
    protected Map<String, Object> cfgMap;
    //惰性获取配置锁
    private final Object getItemLock = new Object();
    public String webRootDir = null;

    /**
     * 使用保护方式的构造函数，防止外部使用者对他进行初始化
     */
    protected Cfg(String rootdir) {
        cfgMap = new HashMap<String, Object>();
        this.webRootDir = rootdir;
    }

    /**
     * 下面是几个获取方法，不允许重构
     */
    private Object getObject(String pItem) {
        Object objValue = this.cfgMap.get(pItem);

        //下面处理配置为空情况，留下接口，允许惰性载入配置，但是默认实现上不考虑
        if (objValue == null) {
            synchronized (getItemLock) {
                objValue = this.cfgMap.get(pItem);
                if (objValue == null) {
                    objValue = this.loadItem(pItem);
                }
            }
        }
        return objValue;
    }

    public Object[] getArray(String pItem) {
        Object obj = this.getObject(pItem);
        return (Object[]) obj;
    }

    public String getString(String pItem) {
        Object obj = this.getObject(pItem);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public int getInt(String pItem) {
        Object objValue = this.getObject(pItem);
        if (objValue == null) {
            return -1;
        }
        try {
            Integer intObj = (Integer) objValue;
            return intObj.intValue();
        } catch (Exception e) {
            return Integer.parseInt(objValue.toString());
        }
    }

    /**
     * 给自己的子类做静态初始化用
     */
    protected Object loadItem(String pItem) {
        this.reload();
        return cfgMap.get(pItem);
    }

    //重新加载相关的值，该函数可以被继承重载
    public abstract  String reload();
    /*
     * {
     * //默认从文件中读取配置，并且是一个json格式的配置信息 File cfgFile = new File(this.getRootDir() +
     * Cfg.cfgFilePath); if (cfgFile.exists()){ this.cfgType =
     * this.reloadJson(cfgFile); }else{ this.cfgType = this.reloadDefalut(); }
     * this.cfgMap.put("WebRootDir", this.getRootDir()); return this.cfgType; }
     *
     *
     * private String reloadJson(File f){ //下面开始处理这个json文件了 String cfgText =
     * FileTools.readFile(f, "GB2312"); if (cfgText != null){ this.cfgMap =
     * GsonTools.parserJsonMapObj(cfgText); } return Cfg.CFGTYPE_JSONFILE;
     *
     * }
     * private String reloadDefalut(){ //下面是系统的一些配置
     * this.cfgMap.put("DefalutFileChartset", "GB2312"); return
     * Cfg.CFGTYPE_DEFALULT; }
     */

    public String getRootDir() {
        return webRootDir;
    }
}