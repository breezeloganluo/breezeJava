/*
 * ThreadProcess.java
 *
 * Created on 2008年2月19日, 上午9:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.thread;
import com.breeze.base.log.Logger;

/**
 *
 * @author happy
 */
public class ThreadProcess extends Thread{
	public static ThreadLocal<String> Info = new ThreadLocal<String>();
    private ThreadProcessIF pf;
    private static Logger log = Logger.getLogger("wwwlgy.commspport.supportif.thread.ThreadProcess");
    private boolean runflag = true;
    
    /** Creates a new instance of ThreadProcess */
    public ThreadProcess(String name,ThreadProcessIF ppf) {
        super(name);
        this.pf = ppf;
    }
    
    public ThreadProcess(ThreadGroup group,String name,ThreadProcessIF ppf) {
        super(group,name);
        this.pf = ppf;
    }
    
    public void run(){
        while(!interrupted() && runflag){
            try{
                this.pf.process();
            }catch(InterruptedException ie){
                //结束线程
                this.runflag = false;
            }catch(Exception e){
                String eStr = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.severe(eStr);
            }
        }
        log.warning("thread "+ this.getName() + "is stoped");
    }
    
    
    public ThreadProcess reset(){
        //先停止原来的应用
        this.interrupt();
        //新建应用
        ThreadProcess result = new ThreadProcess(this.getThreadGroup(),this.getName(),this.pf);
        result.start();
        return result;
    }
}
