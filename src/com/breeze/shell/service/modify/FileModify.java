/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.shell.service.modify;

import com.breeze.support.cfg.Cfg;
import com.breeze.support.tools.FileTools;
import com.google.gson.Gson;
import java.io.File;
import java.util.ArrayList;

import java.util.HashMap;
import com.breeze.support.tools.DirManager;
import com.breeze.base.log.Logger;

/**
 *
 * @author lili
 * 这个类用于对文件系统中的service的配置信息进行修改
 * 但他不关心进行那个具体修改，只给出修改框架
 */
public class FileModify {

    private static Logger log = Logger.getLogger("com.breeze.shell.service.modify.Modify");
    
    public FileModify(ModifyItem m){
        this.modI = m;
    }
    
    private ModifyItem modI;

    /**
     * 总的处理函数
     * 给定目录处理该目录下的所有文档
     * @param baseDir基础路径，从web路径开始
     */
    public void doModify(String baseDir) {
        String allDir =  Cfg.getCfg().getString("WebRootDir") + baseDir;
        //读入磁盘路径
        DirManager dMgr = new DirManager(allDir);
        ArrayList<File> dirL = dMgr.getAllFile(null);
        if (dirL != null) {
            for (File f : dirL) {
                try {
                    String text = FileTools.readFile(f, "GB2312");
                    String afterModified = this.pricessorOneFile(text);
                    if (afterModified != null) {
                        FileTools.writeFile(f, afterModified, "GB2312");
                        log.severe("modify f:" + f + " ok!");
                    }
                } catch (Exception e) {
                    log.severe("passer file " + f + " fail:\n" + com.breeze.support.tools.CommTools.getExceptionTrace(e));
                }
            }
        }
    }

    /**
     * 处理其中一个文本对象
     * 如果本文本有改动则返回对应的文本内容，否则返回null
     * @param text 
     */
    private String pricessorOneFile(String text) {
        if (text == null) {
            return null;
        }
        Gson gson = new Gson();
        HashMap<String, String> tmpMap = gson.fromJson(text,
                new com.google.gson.reflect.TypeToken<HashMap<String, String>>() {
                }.getType());
        boolean isModify = this.modI.modify(tmpMap);
        if (isModify) {
            String result = gson.toJson(tmpMap, new com.google.gson.reflect.TypeToken<HashMap<String, String>>() {
            }.getType());
            return result;
        }
        return null;
    }
}
