/*
 * DirManager.java
 *
 * Created on 2010年6月3日, 上午7:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.breeze.support.tools;

import java.io.*;
import java.util.*;

/**
 *
 * @author happy
 */
public class DirManager {

    private String baseDir;

    /**
     * Creates a new instance of DirManager
     */
    public DirManager(String base) {
        this.baseDir = base;
    }

    /**
     * 递归获取该路径下所有的文件File
     * @param pdir 初始顶层目录文件
     * @return
     */
    public ArrayList<File> getAllFile(File pdir) {
    	if (pdir!=null && pdir.getName().startsWith(".")){
    		return null;
    	}
        File d = pdir == null ? new File(this.baseDir) : pdir;
        ArrayList<File> result = new ArrayList<File>();
        File[] l = d.listFiles();
        if (l == null) {
            return null;
        }
        for (File f : l) {
            if (f.isFile()) {
                result.add(f);
            } else {
                ArrayList<File> tmpResult = getAllFile(f);
                if (tmpResult != null && tmpResult.size() > 0) {
                    result.addAll(tmpResult);
                }
            }
        }
        return result;
    }

    /**
     * 按照初始目录，目录和目录之间，用.间隔，遍历所有的文件返回文件内容。
     * @param pdir 
     * @return
     */
    public HashMap<String,ArrayList<File>> getAllFileInPackage(File pdir) {
    	if (pdir!=null && pdir.getName().startsWith(".")){
    		return null;
    	}
        File d = pdir == null ? new File(this.baseDir) : pdir;
        String pName = pdir == null?"":pdir.getName();
        ArrayList<File> resultList = new ArrayList<File>();
        HashMap<String,ArrayList<File>> result = new HashMap<String,ArrayList<File>>();
        
        File[] l = d.listFiles();
        if (l == null) {
            return null;
        }
        for (File f : l) {
            if (f.isFile()) {
                resultList.add(f);
            } else {
                HashMap<String,ArrayList<File>> tmpResult = getAllFileInPackage(f);
                if (tmpResult == null){
                	continue;
                }
                for (String key : tmpResult.keySet()){
                	if (pdir != null){
                		result.put(pName+"."+key, tmpResult.get(key));
                	}else{
                		result.put(key, tmpResult.get(key));
                	}
                }
            }
        }
        if (!resultList.isEmpty()){
        	result.put(pName, resultList);
        }        
        return result;
    }
    
    public static void deleteSub(File f) {
        if (!f.exists()) {
            return;
        }
        if (f.isFile()) {
            f.delete();
            return;
        }
        File[] sub = f.listFiles();
        for (int i = 0; sub != null && i < sub.length; i++) {
            File subF = sub[i];
            if (subF.isDirectory()) {
                deleteSub(subF);
            }
            subF.delete();
        }
        return;
    }
}
