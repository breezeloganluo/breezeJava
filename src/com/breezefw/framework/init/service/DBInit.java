package com.breezefw.framework.init.service;

import java.util.HashMap;

import com.breeze.base.db.COMMDB;
import com.breeze.base.db.DBCPOper;
import com.breeze.init.Initable;
import com.breeze.support.cfg.Cfg;

public class DBInit implements Initable {
	public static final String DRIVER = "DB.Driver";
    public static final String URL = "DB.Url";
    public static final String USER = "DB.User";
    public static final String PWD = "DB.Pwd";
    private static boolean hasInit = false;
    private static Object lock = new Object();
    
	
	public int getInitOrder() {
		return 2;
	}

	
	public void doInit(HashMap<String, String> paramMap) {
		synchronized (lock){
            if (hasInit)
            	return;
            String dev = Cfg.getCfg().getString(DRIVER);
            String url = Cfg.getCfg().getString(URL);
            String user = Cfg.getCfg().getString(USER);
            String pwd = Cfg.getCfg().getString(PWD);
            
            //dbcp似乎有问题，在没有事务处理的时候，用原先的应该没有问题
            DBCPOper oper = new DBCPOper();
            //SimpleDbOper oper = new SimpleDbOper();
            oper.initDB(dev,url,user,pwd);
            COMMDB.initDB(oper);
            hasInit = true;
        }

	}

	public void innerInit(String dev,String url,String user,String pwd){
		DBCPOper oper = new DBCPOper();
        oper.initDB(dev,url,user,pwd);
        COMMDB.initDB(oper);
        hasInit = true;
	}
	
	public String getInitName() {
		return "DataBase";
	}

}
