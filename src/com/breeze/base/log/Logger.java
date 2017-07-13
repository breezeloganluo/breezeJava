/*
 * Logger.java
 *
 * Created on 2008年8月28日, 下午11:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.base.log;

/**
 *
 * @author happy
 * 提供一个抽象的日志操作类，方便上层代码更换日志系统
 */
public class Logger {
    
    /** Creates a new instance of Logger */
    public Logger() {
    }
    
    protected static Logger log = new com.breeze.base.log.log4j.Log4jLogger();//这是一个种子类，创建的日志子类方法全靠他,默认使用logger4J
    public static Logger getLogger(String logparten){
        return log.createLogger(logparten);//这里实际就是要调用子类的本方法
    }
    public void setTreadSignal(String signal){
        
    }
    public void removeThreadSignal(){
        
    }
    public Logger createLogger(String logparten){//种子方法
        throw new RuntimeException("必须使用子类");
    }
    
    public void severe(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    
    public void severe(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.severe(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    public void warning(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    public void warning(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.warning(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    public void info(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    public void info(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.info(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    public void config(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    public void config(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.config(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    public void fine(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    public void fine(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.fine(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    public void finer(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    public void finer(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.finer(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    public void finest(String msg){
        throw new RuntimeException("必须用子类实例");
    }
    public void finest(String msg,Exception e){
        String head = msg==null?"":msg+" the exception is \n";
        this.finest(head+com.breeze.support.tools.CommTools.getExceptionTrace(e));
    }
    
    
    
    
    
    
    public boolean isLoggable(Level level){
        throw new RuntimeException("必须用子类实例");
    }
    
}
