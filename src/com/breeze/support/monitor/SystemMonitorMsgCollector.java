/*
 * SystemMonitorMsgCollector.java
 *
 * Created on 2008年2月19日, 下午8:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.monitor;
import java.util.*;
/**
 *
 * @author happy
 */
public class SystemMonitorMsgCollector extends TimerTask{
    
    /** Creates a new instance of SystemMonitorMsgCollector */
    public SystemMonitorMsgCollector(long monitorDelay) {
        this.delay = monitorDelay;
    }
    
    private ThreadGroup threadMonitorGroup = new ThreadGroup("Monitor");
    //关键资源控制信息
    private HashMap<String,int[]> resourceMsgMap;
    private HashMap<String,ResourceCollectorIF> resourceMsgCollectorMap = new HashMap<String,ResourceCollectorIF>();
    private ArrayList<ResourceMsgSubscriberIF> resourceMsgSubscriberList = new ArrayList<ResourceMsgSubscriberIF>();
    //线程信息控制器
    private HashMap<String,Thread.State> threadMsgMap;
    private ArrayList<ThreadMsgSubscriberIF> threadMsgSubscriberList = new ArrayList<ThreadMsgSubscriberIF>();
    long lastRecordTime = 0;
    
    /**
     *注册关键资源信息收集器
     */
    public void registerResourceCollector(ResourceCollectorIF col){
        this.resourceMsgCollectorMap.put(col.getResourceName(),col);
    }
    /**
     *注册关键资源订阅器
     */
    public void registerResourceSubscriber(ResourceMsgSubscriberIF subscriber){
        this.resourceMsgSubscriberList.add(subscriber);
    }
    
    /**
     * 注册线程信息订阅器
     */
    public void registerThreadMsgSubscriber(ThreadMsgSubscriberIF subscriber){
        this.threadMsgSubscriberList.add(subscriber);
    }
    
    /**
     *返回线程组
     */
    public ThreadGroup getMonitorThreadGroup(){
        return this.threadMonitorGroup;
    }
    
    /**
     *收集所有关键资源信息
     */
    private void collectorResource(){
        for (ResourceCollectorIF col: this.resourceMsgCollectorMap.values()){
            int[]msg = col.collectorMsg();
            this.resourceMsgMap = new HashMap<String,int[]>();
            this.resourceMsgMap.put(col.getResourceName(),msg);
            //将收集到的信息放入到每个订阅器里面
            for(ResourceMsgSubscriberIF sub:this.resourceMsgSubscriberList){
                sub.subscriberResource(col.getResourceName(),msg);
            }
        }
    }
    
    /**
     *收集所有线程相关信息
     */
    private void collectorThread(){
        this.threadMsgMap = new HashMap<String,Thread.State>();
        int threadCount = this.threadMonitorGroup.activeCount()+10;
        Thread[]threads = new Thread[threadCount];
        int count = this.threadMonitorGroup.enumerate(threads,true);
        for(int i=0;i<count;i++){
            this.threadMsgMap.put(threads[i].getName(),threads[i].getState());
            for(ThreadMsgSubscriberIF sub:this.threadMsgSubscriberList){
                sub.subscriberThread(threads[i].getName(),threads[i].getState());
            }
        }
    }
    /**
     *定时处理的内容
     */
    public void run() {
        //信息收集开始
        for(ResourceMsgSubscriberIF sub:this.resourceMsgSubscriberList){
            sub.beginSubscriberResource();
        }
        //收集所有信息并且通知相应列表
        collectorResource();
        //信息结束
        for(ResourceMsgSubscriberIF sub:this.resourceMsgSubscriberList){
            sub.endSubscriberResource();
        }
        
        //线程监控开始
        for(ThreadMsgSubscriberIF sub:this.threadMsgSubscriberList){
            sub.beginSubscriberThread();
        }
        collectorThread();
        for(ThreadMsgSubscriberIF sub:this.threadMsgSubscriberList){
            sub.endSubscriberThread();
        }
    }
    
    private Timer t = new Timer();
    private long beginTime = 5*60*1000;//开始时间
    private long delay = 5*60*1000;//每次周期
    public void start(){
        this.t.schedule(this,beginTime,delay);
    }
}
