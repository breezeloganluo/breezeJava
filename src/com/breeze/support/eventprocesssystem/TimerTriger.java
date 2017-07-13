/*
 * TimerTriger.java
 *
 * Created on 2008年3月2日, 下午12:12
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.eventprocesssystem;
import com.breeze.base.log.Logger;
import java.util.*;
/**
 *
 * @author happy
 */
public class TimerTriger  extends TimerTask{
    private int lasth = 0;
    private static Logger log = Logger.getLogger("wwwlgy.commspport.supportif.eventprocesssystem.TimerTriger");
    /**
     *时间到达判断的基类
     */
    public static abstract class TimeArrive{
        public abstract boolean isTimeArray(Calendar c);
        
        EventProcessIF process;
        public void setProcess(EventProcessIF p){
            this.process = p;
        }
        public EventProcessIF getProcess(){
            return this.process;
        }
        
        public String toString(){
            return this.process.getClass().toString();
        }
    }
    
    /**
     *小时判断
     */
    public static class HourTimeArray extends TimeArrive{
        private int arrayHour = 0;
        public HourTimeArray(int hour){
            this.arrayHour = hour;
        }
        
        public boolean isTimeArray(Calendar c){
            //如果是负数则每个小时都执行
            if (this.arrayHour < 0){
                return true;
            }
            int h = c.get(Calendar.HOUR_OF_DAY);
            if (h == this.arrayHour){
                return true;
            }
            return false;
        }
    }
    
    /**
     *判断一周的某一个时间
     */
    public static class HourWeekTimeArray extends TimeArrive{
        private int dayOfWeek;
        private int hourOfDay;
        public HourWeekTimeArray(int d,int h){
            this.dayOfWeek = d;
            this.hourOfDay = h;
        }
        public boolean isTimeArray(Calendar c){
            int h = c.get(Calendar.HOUR_OF_DAY);
            int d = c.get(Calendar.DAY_OF_WEEK);
            if (h == this.hourOfDay && d == this.dayOfWeek){
                return true;
            }
            return false;
        }
    }
    /** Creates a new instance of TimerTriger */
    public TimerTriger() {
    }
    
    private ArrayList<TimeArrive> timerProcessList = new ArrayList<TimeArrive>();
    private Timer t = new Timer();
    private long beginTime = 5*60*1000;//开始时间
    private long delay = 30*60*1000;//每次周期 这里的最小周期是半时！
    
    public void addProcess(TimeArrive timer,EventProcessIF process) {
        //倒序排序
        for (int i = 0;i<this.timerProcessList.size();i++){
            if (process.getPriority()>this.timerProcessList.get(i).getProcess().getPriority()){
                timer.setProcess(process);
                this.timerProcessList.add(i,timer);
                return;
            }
        }
        //表示还没有增加
        timer.setProcess(process);
        this.timerProcessList.add(timer);
    }
    
    public EventProcessIF getProcess(int i){
        return this.timerProcessList.get(i).getProcess();
    }
    
    /**
     *定时处理的内容
     */
    public void run() {
        //每半小时一次，保证一个小时内肯定能执行到，防止定时器误差
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);        
        if (this.lasth == h){
            //证明时间还是同一个时间，不处理
            return;
        }        
        this.lasth =h;
        
        for(TimeArrive tt :this.timerProcessList){
            try{
                if(tt.isTimeArray(c)){
                    tt.getProcess().doProcess(null);
                }
            }catch(Exception e){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.severe(err);
            }
        }
    }
    
    public void init(){
        this.t.schedule(this,beginTime,delay);
    }
    
    public String toString(){
        return this.timerProcessList.toString();
    }
}
