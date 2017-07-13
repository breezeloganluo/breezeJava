//Source file: D:\\my resource\\myproject\\门户网\\src\\wwwlgy\\commspport\\supportif\\eventprocesssystem\\ProcessEventQueue.java

package com.breeze.support.eventprocesssystem;

import com.breeze.support.monitor.ResourceCollectorIF;
import java.util.*;


/**
 * 一个队列对象，主要完成基本的队列功能
 * 入队和出队
 * 同时在入队和出队完成线程阻塞工作。
 */
public class ProcessEventQueue implements ResourceCollectorIF{
    
    /**
     * 队列类型：
     * 0：阻塞式队列
     * 1：非阻塞式队列
     */
    private int queryType;
    private int querySize;
    
    private String queueName;
    
    public static final int QUERYTYPE_NONEBLOCK = 1;//非阻塞队列
    public static final int QUERYTYPE_BLOCK = 0;//阻塞队列
    
    
    private ProcessEventAbs[] eventList;//用循环队列
    int firstElement = 0;
    int lastElement = 0;
    int quereStatus;//0:正常，-1:空 1：满
    public ProcessEventQueue(int p_queryType,int p_querySize,String p_queueName) {
        this.queryType = p_queryType;
        this.querySize = p_querySize;
        this.eventList = new ProcessEventAbs[this.querySize];
        this.queueName = p_queueName;
        this.firstElement = 0;
        this.lastElement = 0;
        this.quereStatus = EventManager.QUEUESTATE_NULL;
        
    }
    
    /**
     * @roseuid 47B836D9000F
     */
    public synchronized  int push(ProcessEventAbs event) {
        if (this.quereStatus == EventManager.QUEUESTATE_FULL){
            //队列满了，增加失败
            this.fullFailTimes++;
            return 1;
        }
        try{
            this.eventList[this.lastElement] = event;
            this.lastElement = (this.lastElement + 1)%this.querySize;
            if (this.lastElement == this.firstElement){
                this.quereStatus = EventManager.QUEUESTATE_FULL;//饱和了
                this.fullTimes++;
            }else{
                this.quereStatus = EventManager.QUERESTATE_NOMAL;
            }
            if (this.queryType == QUERYTYPE_BLOCK){
                //如果是阻塞队列,那么唤醒所有阻塞线程
                this.notifyAll();
            }
            this.notifyAll();
            return 0;
        }catch(Exception e){
            return -100;
        }
    }
    
    /**
     * @roseuid 47B836F6033C
     */
    public synchronized ProcessEventAbs pop()throws InterruptedException{
        if (this.quereStatus == EventManager.QUEUESTATE_NULL && this.queryType == QUERYTYPE_NONEBLOCK){
            //队列是空的，且是非阻塞式队列，无返回空
            this.nullFailTimes++;
            return null;
        }
        while(this.quereStatus ==EventManager.QUEUESTATE_NULL){
            this.wait();
        }
        ProcessEventAbs result = this.eventList[this.firstElement];
        this.firstElement = (this.firstElement + 1)%this.querySize;
        if (this.firstElement == this.lastElement){
            this.quereStatus = EventManager.QUEUESTATE_NULL;
            this.nullTimes++;
        }else{
            this.quereStatus = EventManager.QUERESTATE_NOMAL;
        }
        return result;
    }
    /**
     *返回队列名称
     */
    public String getResourceName(){
        return this.queueName;
    }
    
    public int getQueueStatue(){
        //返回队列状态
        return this.quereStatus;
    }
    private int fullTimes;
    private int fullFailTimes;//因为满增加失败次数
    private int nullTimes;
    private int nullFailTimes;//因为空获取失败次数
    /**
     *返回队列资源信息
     *返回一个长度为6的数组
     *1:队列总长度
     *2:队列当前长度
     *3:本次统计期间队列满次数（每次获取后清0）
     *4:本次统计期间队列空次数（每次获取后清0）
     *5:本次统计期间，因为满而增加失败的次数
     *6:本次统计期间，因为空而获取失败的次数
     */
    public int[] collectorMsg(){
        int [] result = new int[6];
        result[0] = this.querySize;
        result[1] = this.lastElement - this.firstElement;
        result[1] = ((result[1]<0)||this.quereStatus == EventManager.QUEUESTATE_FULL)?result[1]+this.querySize:result[1];
        result[2] = this.fullTimes;this.fullTimes = 0;
        result[3] = this.nullTimes;this.nullTimes = 0;
        result[4] = this.fullFailTimes;this.fullFailTimes = 0;
        result[5] = this.nullFailTimes;this.nullFailTimes = 0;
        return result;
    }
}
