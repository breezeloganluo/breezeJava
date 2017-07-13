//Source file: D:\\my resource\\myproject\\门户网\\src\\wwwlgy\\commspport\\supportif\\eventprocesssystem\\ProcessManager.java

package com.breeze.support.eventprocesssystem;

import com.breeze.support.thread.ThreadProcessIF;
import com.breeze.support.thread.ThreadProcess;
import com.breeze.support.monitor.ThreadMsgSubscriberIF;
import com.breeze.support.monitor.SystemMonitorMsgCollector;
import com.breeze.base.log.Logger;
import java.util.*;


/**
 * 事件处理器管理类，有以下作用
 * 1。收集所有类的事件处理，并组织好内部处理优先级
 * 2。封装内部处理线程，必要时提高并发处理效率，初期不用开多个线程
 */
public class ProcessManager implements ThreadProcessIF,ThreadMsgSubscriberIF{
    private static Logger log = Logger.getLogger("wwwlgy.commspport.supportif.monitor.ProcessManager");
    private ArrayList<EventProcessIF> processList = null;
    private EventManager eventM;
    private ThreadProcess tProcess;
    public ProcessManager() {
        this.processList = new ArrayList<EventProcessIF>();
    }
    
    void setEventManager(EventManager p_eventM){
        this.eventM = p_eventM;
    }
    
    /**
     *实现线程接口，由另外一个线程调用
     */
    public void process()throws Exception{
        ProcessEventAbs event = this.eventM.popEvent();
        this.doProcess(event);
    }
    /**
     * @外面调用这个函数进行事件处理
     */
    public int doProcess(ProcessEventAbs event) throws Exception{
        int result = 0;
        for (EventProcessIF m:this.processList){
            result += m.doProcess(event);
        }
        return result;
    }
    
    /**
     * @roseuid 47B83E9D000F
     */
    public void addProcess( EventProcessIF process) {
        //倒序排序
        for (int i = 0;i<this.processList.size();i++){
            if (process.getPriority()>this.processList.get(i).getPriority()){
                this.processList.add(i,process);
                return;
            }
        }
        //表示还没有增加
        this.processList.add(process);
    }
    
    /**
     * @初始化事件处理类，主要是要启动线程
     */
    public void init(HashMap param,SystemMonitorMsgCollector mo) {
        if (mo!=null){
            this.tProcess = new ThreadProcess(mo.getMonitorThreadGroup(),PROCESSNAME,this);
        }else{
            this.tProcess = new ThreadProcess(PROCESSNAME,this);
        }
        this.tProcess.start();
    }
    
    public static final String PROCESSNAME = "EventProcessThread";
    
    private boolean keyThreadExist;
    private int notRunningTimes = 0;
    //下面处理线程监控的逻辑，主要是ThreadSubscriberIf的实现
    public void beginSubscriberThread(){
        this.keyThreadExist = false;//开始时先去位
    }
    public void endSubscriberThread(){
        if (!this.keyThreadExist){
            //说明线程不存在，要杀掉线程了
            this.tProcess = this.tProcess.reset();
        }
    }
    public void subscriberThread(String threadName,Thread.State state){
        if (threadName.equals(ProcessManager.PROCESSNAME)){
            if (this.keyThreadExist){
                //存在多个这样的线程，没有释放
                log.severe("another thread "+ProcessManager.PROCESSNAME+" is existed!");
            }
            //如果是目标线程
            this.keyThreadExist = true;
            if (!Thread.State.RUNNABLE.equals(state)){
                if (this.notRunningTimes >=5 && this.eventM.getQueueState()!=EventManager.QUEUESTATE_NULL){
                    //大于5次要重启动了
                    log.severe("Thread blocak 5 times,must be reset");
                    this.tProcess = this.tProcess.reset();
                    this.notRunningTimes = 0;
                }else{
                    this.notRunningTimes ++;
                }
            }else{
                this.notRunningTimes = 0;
            }
        }
    }
    
    Thread fortest_getThread(){
        return this.tProcess;
    }
    public EventProcessIF getProcess(int i){
        return this.processList.get(i);
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (EventProcessIF p : this.processList){
            sb.append(p.getClass().toString()).append(" ; ");
        }
        return sb.toString();
    }
    
    public ArrayList<EventProcessIF> fortest_getProcessList(){
        return this.processList;
    }
}
