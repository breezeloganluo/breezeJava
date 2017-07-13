/*
 * jdkLogger.java
 *
 * Created on 2008年8月29日, 上午12:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.base.log.jdklog;

import java.util.logging.*;

/**
 *
 * @author happy
 */
public class JdkLogger extends com.breeze.base.log.Logger{
    private java.util.logging.Logger log;
    
    public static void init(){
        //进行初始化
        com.breeze.base.log.Logger.log = new JdkLogger();
        com.breeze.base.log.Level.ALL = new com.breeze.base.log.Level(java.util.logging.Level.ALL);
        com.breeze.base.log.Level.SEVERE = new com.breeze.base.log.Level(java.util.logging.Level.SEVERE);
        com.breeze.base.log.Level.WARNING = new com.breeze.base.log.Level(java.util.logging.Level.WARNING);
        com.breeze.base.log.Level.INFO = new com.breeze.base.log.Level(java.util.logging.Level.INFO);        
        com.breeze.base.log.Level.CONFIG = new com.breeze.base.log.Level(java.util.logging.Level.CONFIG);
        com.breeze.base.log.Level.FINE = new com.breeze.base.log.Level(java.util.logging.Level.FINE);
        com.breeze.base.log.Level.FINER = new com.breeze.base.log.Level(java.util.logging.Level.FINER);
        com.breeze.base.log.Level.FINEST = new com.breeze.base.log.Level(java.util.logging.Level.FINEST);        
        com.breeze.base.log.Level.OFF = new com.breeze.base.log.Level(java.util.logging.Level.OFF);
        
        
    }
    /** Creates a new instance of jdkLogger */
    public JdkLogger(String logparten) {
        this.log = java.util.logging.Logger.getLogger(logparten);
    }

    public JdkLogger() {//构造一个空的，做种子
        
    }
    public com.breeze.base.log.Logger createLogger(String logparten){
        return new JdkLogger(logparten);
    }
    
    public void severe(String msg){
        this.log.severe(msg);
    }
    public void warning(String msg){
        this.log.warning(msg);
    }
    public void info(String msg){
        this.log.info(msg);
    }
    public void config(String msg){
        this.log.config(msg);
    }
    public void fine(String msg){
        this.log.fine(msg);
    }
    
    public void finer(String msg){
        this.log.finer(msg);
    }
    
    public void finest(String msg){
        this.log.finest(msg);
    }
    
    
    
    
    
    
    
    public boolean isLoggable(com.breeze.base.log.Level level){
        java.util.logging.Level l = (java.util.logging.Level)level.getLevel();
        return this.log.isLoggable(l);
    }
    
}
