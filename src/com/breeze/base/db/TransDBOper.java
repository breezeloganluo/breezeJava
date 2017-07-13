/*
 * TransDBOper.java
 *
 * Created on 2007年6月24日, 上午11:27
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.base.db;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.support.thread.ThreadProcess;

import java.util.*;
import java.sql.*;

/**
 * 
 * @author happy
 */
public class TransDBOper {
	private static ThreadLocal<String> threadSignal = new ThreadLocal<String>();
	private static HashMap<String, TransDBOper> threadMap = new HashMap<String, TransDBOper>();

	private Connection transConnect;
	// 日志对象
	public Logger log = Logger.getLogger("com.breeze.base.db.TransDBOper");

	/** Creates a new instance of TransDBOper */
	public TransDBOper(Connection p_conn) {
		try {
			if (log.isLoggable(Level.FINE)) {
				log.fine("Create one TransDBOper connection is " + p_conn.hashCode() + ",TransDBOper is:" + this);
			}
			this.transConnect = p_conn;
			
		    //log.severe(ThreadProcess.Info.get() + ':' + this.transConnect.hashCode() + "=new TransDBOper");
		    
			this.transConnect.setAutoCommit(false);
			
			//临时代码
			String service = ThreadProcess.Info.get();
			String value = service + '|' + System.currentTimeMillis();
			synchronized (ContextMgr.global) {
				ContextMgr.global.setContextByPath("db.trans."+this.hashCode(), new BreezeContext(value));
			}
			
		} catch (Exception e) {
			log.severe(e.toString());
			throw new RuntimeException("tranDB inti error :" + e.toString());
		}
	}

	public void setThreadTrans() {
		// log.severe("begin trans!"+this.transConnect);
		String sig = threadSignal.get();
		if (sig == null) {
			sig = UUID.randomUUID().toString();
		}
		threadSignal.set(sig);
		synchronized (threadMap) {
			if (log.isLoggable(Level.FINE)) {
				log.fine("setThreadTrans sig is : " + sig + ",TransDBOper is:" + this);
			}
			threadMap.put(sig, this);
		}
		
		//临时代码
		String service = ThreadProcess.Info.get();
		String value = service + '|' + System.currentTimeMillis();
		synchronized (ContextMgr.global) {
			ContextMgr.global.setContextByPath("db.trans."+this.hashCode(), null);
			ContextMgr.global.setContextByPath("db.trans."+sig, new BreezeContext(value));
		}
	}

	public static void closeThreadTrans(boolean isCommit) throws SQLException {
		TransDBOper rm = null;
		String sig = null;
		synchronized (threadMap) {
			sig = threadSignal.get();
		
			if (sig != null) {
				rm = threadMap.remove(sig);
			}
			threadSignal.remove();		
		}
		if (rm != null) {
			if (rm.log.isLoggable(Level.FINE)){
				rm.log.fine("find closeed trans:"+rm);
			}
			// rm.log.severe("close trans!"+isCommit);
			if (isCommit) {
				rm.commit();
			} else {
				rm.roolBack();
			}
			rm.close();
		}
		
		//临时代码
		synchronized (ContextMgr.global) {
			ContextMgr.global.setContextByPath("db.trans."+sig, null);
		}
	}

	public static TransDBOper getTransDBOper() {
		synchronized (threadMap) {
			String sig = threadSignal.get();
			if (sig == null) {
				return null;
			}
			return threadMap.get(sig);
		}
	}

	private void commit() throws SQLException {
		if (log.isLoggable(Level.FINE)){
			log.fine("commit use connection:"+this.transConnect.hashCode());
			try{
			log.fine("commit connection is AutoCommit is:"+ this.transConnect.getAutoCommit() + 
					",isClose is:" + this.transConnect.isClosed());
			}catch(Exception e){
				log.fine("get cnnection is available or is Autocommit fail!");
			}
		}
		//log.severe(ThreadProcess.Info.get() + ':' + this.transConnect.hashCode() + "=commit");
		this.transConnect.commit();
	}

	private void roolBack() throws SQLException {
		if (log.isLoggable(Level.FINE)){
			log.fine("rollback use connection:"+this.transConnect.hashCode());
			try{
			log.fine("rollback connection is AutoCommit is:"+ this.transConnect.getAutoCommit() + 
					",isClose is:" + this.transConnect.isClosed());
			}catch(Exception e){
				log.fine("get cnnection is available or is Autocommit fail!");
			}
		}
		//log.severe(ThreadProcess.Info.get() + ':' + this.transConnect.hashCode() + "=roolBack");
		this.transConnect.rollback();
	}

	private void close() throws SQLException {
		//2017-05-24罗光瑜增加了内部的统计，如果信息关闭，则关闭删除内部统计
		synchronized (ContextMgr.global) {
			ContextMgr.global.setContextByPath("db.connect."+this.transConnect.hashCode(), null);
		}
		this.transConnect.close();
	}

	public Connection getConnection(){
		if (log.isLoggable(Level.FINE)){
			log.fine("get connection :" + this.transConnect.hashCode());
		}
		return this.transConnect;
	}
}
