//Source file: D:\\my resource\\myproject\\门户网\\src\\wwwlgy\\commspport\\supportif\\eventprocesssystem\\EventManager.java

package com.breeze.support.eventprocesssystem;
import com.breeze.support.monitor.SystemMonitorMsgCollector;
import java.util.*;


/**
 * 这个类有两个方面的作用：
 * 1。对事件队列的管理，向外提供一个封装程序，提供队列入队操作。内部屏蔽队列组织。初期做最简单的单队列操作。
 * 2。对内只负责开一个线程从队列中取数据，并且调用processManager进行队列处理
 */
public class EventManager {
    private ProcessEventQueue eventQueryList = null;
    private ProcessManager process;
    private static final String QUEUERESOURCENAME = "queueresource";
    public static final int QUERESTATE_NOMAL = 0;
    public static final int QUEUESTATE_NULL = -1;
    public static final int QUEUESTATE_FULL = 1;
    
    public EventManager(ProcessManager p_process) {
        this.process = p_process;
    }
    
    /**
     * @roseuid 47B83F8F0119
     */
    public int pushEvent(ProcessEventAbs event) {
        return this.eventQueryList.push(event);
    }
    //下面是初始化的参数
    public static final String PARAM_NAME_QUEUESIZE = "queuesize";
    /**
     * @roseuid 47B8408302CE
     */
    public void init(HashMap param,SystemMonitorMsgCollector mo) {
        int size = (Integer)param.get(PARAM_NAME_QUEUESIZE);
        //创建一个阻塞式的队列
        this.eventQueryList = new ProcessEventQueue(ProcessEventQueue.QUERYTYPE_BLOCK,size,QUEUERESOURCENAME);
    }
    /**
     *事件出队
     */
    ProcessEventAbs popEvent()throws InterruptedException{
        return this.eventQueryList.pop();
    }
    
    /**
     * @roseuid 47B840BE0213
     */
    public int doImmediately(ProcessEventAbs event) throws Exception{
        return this.process.doProcess(event);
    }

    public int getQueueState(){
        return this.eventQueryList.getQueueStatue();
    }
}
