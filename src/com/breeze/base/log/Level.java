/*
 * Level.java
 *
 * Created on 2008年8月29日, 上午12:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.base.log;
/**
 *
 * @author happy
 */
public class Level {
    private Object l;
    /** Creates a new instance of Level */
    public Level(Object p) {
        this.l = p;
    }
    //含默认实现
    public static Level ALL = new Level(java.util.logging.Level.ALL);;
    public static Level SEVERE = new Level(java.util.logging.Level.SEVERE);
    public static Level WARNING = new Level(java.util.logging.Level.WARNING);
    public static Level INFO = new Level(java.util.logging.Level.INFO);
    public static Level CONFIG = new Level(java.util.logging.Level.CONFIG);
    public static Level FINE = new Level(java.util.logging.Level.FINE);
    public static Level FINER = new Level(java.util.logging.Level.FINER);
    public static Level FINEST = new Level(java.util.logging.Level.FINEST);    
    public static Level OFF = new Level(java.util.logging.Level.OFF);

    
    public Object getLevel(){
        return this.l;
    }
}
