/*
 * Log4jLogger.java
 *
 * Created on 2008年8月29日, 上午1:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.base.log.log4j;

import com.breeze.base.log.BreezeLogQuere;

/**
 *
 * @author 罗光瑜
 * 2013-09-14罗光瑜修改，如果有sign标签的全部输出不考虑级别
 */
public class Log4jLogger  extends com.breeze.base.log.Logger{
    private org.apache.log4j.Logger log;
    private static ThreadLocal<String> threadSignal = new ThreadLocal<String>();
    
    public static void init(){
        //进行初始化
        com.breeze.base.log.Logger.log = new Log4jLogger();
                //进行初始化
        com.breeze.base.log.Level.ALL = new com.breeze.base.log.Level(org.apache.log4j.Level.ALL);
        com.breeze.base.log.Level.SEVERE = new com.breeze.base.log.Level(org.apache.log4j.Level.FATAL);
        com.breeze.base.log.Level.WARNING = new com.breeze.base.log.Level(org.apache.log4j.Level.ERROR);
        com.breeze.base.log.Level.INFO = new com.breeze.base.log.Level(org.apache.log4j.Level.WARN);        
        com.breeze.base.log.Level.CONFIG = new com.breeze.base.log.Level(org.apache.log4j.Level.INFO);
        com.breeze.base.log.Level.FINE = new com.breeze.base.log.Level(org.apache.log4j.Level.DEBUG);
        com.breeze.base.log.Level.FINER = new com.breeze.base.log.Level(org.apache.log4j.Level.DEBUG);
        com.breeze.base.log.Level.FINEST = new com.breeze.base.log.Level(org.apache.log4j.Level.DEBUG);        
        com.breeze.base.log.Level.OFF = new com.breeze.base.log.Level(org.apache.log4j.Level.OFF);
        
    }
    
        /** Creates a new instance of Log4jLogger */
    public Log4jLogger () {
    }
    
    /** Creates a new instance of jdkLogger */
    public Log4jLogger(String logparten) {
        this.log = org.apache.log4j.Logger.getLogger(logparten);
    }

    @Override
    public com.breeze.base.log.Logger createLogger(String logparten){
        return new Log4jLogger(logparten);
    }
    
    @Override
    public void setTreadSignal(String signal){
        threadSignal.set(signal);
    }
    @Override
    public void removeThreadSignal(){
    	String s = threadSignal.get();
    	if (s!=null){
    		BreezeLogQuere.getInc().removeLog(s);
    	}
        threadSignal.remove();
    }
    @Override
    public void severe(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.fatal(msg);
    }
    
    private void fatalLog(String thradSignal , String msg){
    	String signal = "{[" + thradSignal + "]}";
    	
        String className = "className";
        String line = "line";
        
      //获取类名和行号、
    	StackTraceElement[] callArray = Thread.currentThread().getStackTrace();
    	if (callArray != null && callArray.length>3){
    		StackTraceElement one = callArray[3];
    		className = one.getClassName();
    		line = String.valueOf(one.getLineNumber());
    	}
        try {
        	this.log.fatal(signal + msg);
			BreezeLogQuere.getInc().putLogValue(thradSignal, msg, className, line);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void warning(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.error(msg);
    }
    @Override
    public void info(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.warn(msg);
    }
    @Override
    public void config(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.info(msg);
    }
    @Override
    public void fine(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.debug(msg);
    }
    
    @Override
    public void finer(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.debug(msg);
    }
    
    @Override
    public void finest(String msg){
        String signal = threadSignal.get();
        if (signal != null){
            this.fatalLog(signal , msg);
            return;
        }
        this.log.debug(msg);
    }
    
    
    @Override
    public boolean isLoggable(com.breeze.base.log.Level level){
    	String signal = threadSignal.get();
        if (signal != null){
        	return true;
        }
        org.apache.log4j.Level l = (org.apache.log4j.Level)level.getLevel();
        return this.log.isEnabledFor(l);
    }    
}
