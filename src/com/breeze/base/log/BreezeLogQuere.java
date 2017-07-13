package com.breeze.base.log;

import java.util.HashMap;

/**
 * 这是一个用于服务器调试阶段的消息中断调试器
 * 他会按照threadSignal的数据，完成中断操作，即往里面设置数据将会被中断，获取数据时，中断将结束。
 * @author Logan
 *
 */
public class BreezeLogQuere {
   public static class LogStruct{
	   public long lastModify;
	   public String msg;
	   public String className;
	   public String line;
	   public int status;//状态0初始，1放值，2取完
   }
   
   public final static int STATUS_INIT = 0;
   public final static int STATUS_PUTKEY = 1;
   public final static int STATUS_GETKEY =2;
   
   public static Object lock = new Object();
   
   public static BreezeLogQuere  inc = null;
  
   public static  BreezeLogQuere getInc(){
	   if (inc == null){
		   synchronized (lock) {
			if (inc == null){
				inc = new BreezeLogQuere();
			}
		}
	   }
	   return inc;
   }
   
   public static final long TIMEPREA = 10*1000*60;//最大间隔时间10分钟
   
   private HashMap<String,LogStruct> qMap;
   
   private BreezeLogQuere(){
	   qMap = new HashMap<String,LogStruct>();
   }
   /**
    * 给外部使用，设置某个中断标识
    * @param thread
    */
   public synchronized void setLog(String thread){
	   LogStruct one = new LogStruct();
	   one.lastModify = System.currentTimeMillis();
	   one.status = STATUS_INIT;
	   this.qMap.put(thread, one);
   }
   
   /**
    * 取消跟踪
    * @param thread
    */
   public synchronized void removeLog(String thread){
	   this.qMap.remove(thread);
	   this.notifyAll();
   }
   /**
    * 给日志类用，打印一个日志的同时，向这里设置值，然后阻塞住
    * @param msg
    * @param className
    * @param line
    * @throws InterruptedException 
    */
   public synchronized void putLogValue(String threadSignal,String msg,String className,String line) throws InterruptedException{

	   LogStruct one = this.qMap.get(threadSignal);

	   //如果没有设置，即map为空则阻塞
	   if (one == null){
		   return;
	   }
	   //如果超时，取消跟踪
	   if (one.lastModify + TIMEPREA < System.currentTimeMillis()){

		   this.qMap.remove(threadSignal);
		   return;
	   }
	   //设置值
	   one.lastModify = System.currentTimeMillis();
	   one.msg = msg;
	   one.className = className;
	   one.line = line;
	   one.status = STATUS_PUTKEY;
	   this.notifyAll();

	   //等待
	   while(this.qMap.get(threadSignal)!=null && one.status == STATUS_PUTKEY && one.lastModify + TIMEPREA > System.currentTimeMillis()){   
		   this.wait(TIMEPREA);
	   }
   }
   /**
    * 外面程序取数据函数
    * 返回的是一个字符串数组，第一个msg第二个是className第三个是line
    * @param threadSignal
 * @throws InterruptedException 
    */
   public synchronized String[] getLogValue(String threadSignal) throws InterruptedException{
	   LogStruct one = this.qMap.get(threadSignal);
	   if (one == null){
		   return null;
	   }
	   //为空或者状态不是有值状态都阻塞住
	   while(one != null && one.status!=STATUS_PUTKEY){
		   this.wait(TIMEPREA);
		   one = this.qMap.get(threadSignal);
	   }
	   if (one == null){
		   return null;
	   }
	   
	   String[] result= new String[]{one.msg,one.className,one.line};
	   one.status = STATUS_GETKEY;
	   this.notifyAll();
	   return result;
   }
}
