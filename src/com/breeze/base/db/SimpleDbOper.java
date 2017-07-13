/*
 * SimpleDbOper.java
 *
 * Created on 2007年12月16日, 下午3:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.base.db;

import com.breeze.base.log.Logger;
import com.breeze.base.log.Level;
import com.breeze.support.tools.CommTools;

import java.util.*;
import java.sql.*;


public class SimpleDbOper extends DbOper {
    //日志对象
    private Logger log = Logger.getLogger("wwwlgy.commspport.supportif.db.SimpleDbOper");
    
    private Connection noneTransConnect;
    private Statement noneTransStatement;
    private Statement noneTransgenrateKeyStatement;
    private HashMap<String,PreparedStatement> NoneTransPreparedStatementMap = new HashMap<String,PreparedStatement>(30);
    private HashMap<String,PreparedStatement> NoneTransGenrateKeyPreparedStatementMap = new HashMap<String,PreparedStatement>(30);
    private String driver,DBUrl,dbuser,dbpwd;
    
    private Object Lock = new Object();
    
    private long lastResetTimeStamp = 0;
    
    public SimpleDbOper() {
    }
    
    /**
     *对外sql执行接口
     */
    public ResultSet executeSql(String sql)throws SQLException{
        return executeSql(sql,true);
    }
    /**
     * 内部处理类，如果是第一次调用发生异常，那么会进行重连，并第二次调用
     */
    private ResultSet executeSql(String sql,boolean isFirst)throws SQLException{
        try{
            return this.noneTransStatement.executeQuery(sql);
        }catch(SQLException e){
            
            if (e.getErrorCode() == 0){
                //说明已经断连,恢复连接
                this.reset();
                if (isFirst){
                    //如果是第一次，那么再调用
                    return executeSql(sql,false);
                }
            }else{
                //数据库重连就不打印日志了
                if (log.isLoggable(Level.FINE)){
                    String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                    log.fine("SqlErrCode:"+e.getErrorCode()+"Exception:"+err);
                }
            }
            
            throw e;
        }catch(RuntimeException e){
            if (log.isLoggable(Level.INFO)){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.info(err);
            }
            this.reset();
            if (isFirst){
                //如果是第一次，那么再调用
                return executeSql(sql,false);
            }
            throw e;
        }
    }
    
    /**
     * 对外接口stament方式的sql查询语句
     */
    public ResultSet executeSql(String sql, ArrayList param)throws SQLException{
        return  executeSql(sql,param,true);
    }
    /**
     * 重属接口，连接无效能够重连
     */
    private ResultSet executeSql(String sql, ArrayList param,boolean isFirst)throws SQLException{
        try{
            PreparedStatement pst = this.NoneTransPreparedStatementMap.get(sql);
            if (pst == null){
                synchronized (this.Lock){
                    //注意,这里要加上同步后再进行一次判断
                    pst = this.NoneTransPreparedStatementMap.get(sql);
                    if (pst == null){
                        pst = this.noneTransConnect.prepareStatement(sql);
                        //然后要放入map中
                        this.NoneTransPreparedStatementMap.put(sql,pst);
                    }
                }
            }
            
            for (int i=0;i<param.size();i++){
                //注意，PreparedStatement的底是1
                pst.setObject(i+1,param.get(i));
            }
            
            return pst.executeQuery();
        }catch(SQLException e){
            if (e.getErrorCode() == 0){
                //说明已经断连,恢复连接
                this.reset();
                if (isFirst){
                    return  executeSql(sql,param,false);
                }
            }else{
                //数据库重连就不打印日志了
                if (log.isLoggable(Level.FINE)){
                    String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                    log.fine("SqlErrCode:"+e.getErrorCode()+"Exception:"+err);
                }
            }
            throw e;
        }catch(RuntimeException e){
            if (log.isLoggable(Level.INFO)){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.info(err);
            }
            this.reset();
            if (isFirst){
                return  executeSql(sql,param,false);
            }
            throw e;
        }
    }
    
    public int executeUpdate(String sql)throws SQLException{
        return executeUpdate(sql,true);
    }
    /**
     * @roseuid 467D0A5F004E
     */
    private int executeUpdate(String sql,boolean isFirst)throws SQLException{
        try{
            return this.noneTransStatement.executeUpdate(sql);
        }catch(SQLException e){
            if (e.getErrorCode() == 0){
                //说明已经断连,恢复连接
                this.reset();
                if (isFirst){
                    return executeUpdate(sql,false);
                }
            }else{
                //数据库重连就不打印日志了
                if (log.isLoggable(Level.FINE)){
                    String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                    log.fine("SqlErrCode:"+e.getErrorCode()+"Exception:"+err);
                }
            }
            throw e;
        }catch(RuntimeException e){
            if (log.isLoggable(Level.INFO)){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.info(err);
            }
            this.reset();
            if (isFirst){
                return executeUpdate(sql,false);
            }
            throw e;
        }
    }
    
    public int[] executeUpdateGetGenrateKey(String sql)throws SQLException{
        return executeUpdateGetGenrateKey(sql,true);
    }
    private int[] executeUpdateGetGenrateKey(String sql,boolean isFrist)throws SQLException{
        try{
            int[] resultArray;
            synchronized (this.noneTransgenrateKeyStatement){
                int result = this.noneTransgenrateKeyStatement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
                ResultSet genrateKeySet = this.noneTransStatement.getGeneratedKeys();
                int setCount = genrateKeySet.getMetaData().getColumnCount();
                resultArray = new int[setCount+1];
                resultArray[0]=result;
                if(genrateKeySet.next()){
                    for (int i=1;i<=setCount;i++){
                        resultArray[i]=genrateKeySet.getInt(i);
                    }
                }else{
                    //如果没有自动索引,要从新给返回结果赋值
                    resultArray = new int[]{result};
                }
                genrateKeySet.close();
            }
            return resultArray;
        }catch(SQLException e){
            if (e.getErrorCode() == 0){
                //说明已经断连,恢复连接
                this.reset();
                if (isFrist){
                    return executeUpdateGetGenrateKey(sql,false);
                }
            }else{
                //数据库重连就不打印日志了
                if (log.isLoggable(Level.FINE)){
                    String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                    log.fine("SqlErrCode:"+e.getErrorCode()+"Exception:"+err);
                }
            }
            
            throw e;
        }catch(RuntimeException e){
            if (log.isLoggable(Level.INFO)){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.info(err);
            }
            this.reset();
            if (isFrist){
                return executeUpdateGetGenrateKey(sql,false);
            }
            throw e;
        }
    }
    
    public int executeUpdate(String sql, ArrayList param)throws SQLException{
        return executeUpdate(sql,param,true);
    }
    /**
     * @roseuid 467D0CC7035B
     */
    private int executeUpdate(String sql, ArrayList param,boolean isFirst)throws SQLException{
        try{
            PreparedStatement pst = this.NoneTransPreparedStatementMap.get(sql);
            if (pst == null){
                synchronized (this.Lock){
                    //注意,这里要加上同步后再进行一次判断
                    pst = this.NoneTransPreparedStatementMap.get(sql);
                    if (pst == null){
                        pst = this.noneTransConnect.prepareStatement(sql);
                        //然后要放入map中
                        this.NoneTransPreparedStatementMap.put(sql,pst);
                    }
                }
            }
            //2010-4-11日增加：允许输入数组进行操作
            int arrayLen = -1;
            HashMap<Integer,Object[]>arrayObjMap = new HashMap<Integer,Object[]>();
            for (int i=0;i<param.size();i++){
                Object o = param.get(i);
                if (o instanceof Object[]){
                    Object[] oo = (Object[])o;
                    int oLen = oo.length;
                    //设置最小的长度
                    if (oLen < arrayLen || arrayLen <0){
                        arrayLen = oLen;
                    }
                    arrayObjMap.put(i+1,oo);
                    continue;
                }
                //注意，PreparedStatement的底是1
                pst.setObject(i+1,o);
            }
            int result = 0;
            if (arrayLen < 0){
                result = pst.executeUpdate();
            }else{
                //要进行数组循环处理                
                for(int i=0;i<arrayLen;i++){
                    Iterator itor = arrayObjMap.entrySet().iterator();
                    while(itor.hasNext()){
                        Map.Entry en = (Map.Entry)itor.next();
                        Integer key = (Integer)en.getKey();
                        Object[] arrayObj = (Object[])en.getValue();
                        Object o = arrayObj[i];
                        pst.setObject(key,o);
                    }
                    result += pst.executeUpdate();
                }
            }
            return result;
        }catch(SQLException e){
            if (e.getErrorCode() == 0){
                //说明已经断连,恢复连接
                this.reset();
                if (isFirst){
                    return executeUpdate(sql,param,false);
                }
            }else{
                //数据库重连就不打印日志了
                if (log.isLoggable(Level.FINE)){
                    String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                    log.fine("SqlErrCode:"+e.getErrorCode()+"Exception:"+err);
                }
            }
            throw e;
        }catch(RuntimeException e){
            if (log.isLoggable(Level.INFO)){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.info(err);
            }
            this.reset();
            if (isFirst){
                return executeUpdate(sql,param,false);
            }
            throw e;
        }
    }
    
    public int[] executeUpdateGetGenrateKey(String sql, ArrayList param)throws SQLException{
        return executeUpdateGetGenrateKey(sql,param,true);
    }
    private int[] executeUpdateGetGenrateKey(String sql, ArrayList param,boolean isFirst)throws SQLException{
        try{
            PreparedStatement pst = this.NoneTransGenrateKeyPreparedStatementMap.get(sql);
            if (pst == null){
                synchronized (this.Lock){
                    //注意,这里要加上同步后再进行一次判断
                    pst = this.NoneTransGenrateKeyPreparedStatementMap.get(sql);
                    if (pst == null){
                        pst = this.noneTransConnect.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                        //然后要放入map中
                        this.NoneTransGenrateKeyPreparedStatementMap.put(sql,pst);
                    }
                }
            }
            //加锁，获取对应的自动增长值
            int[] resultArray;
            synchronized (pst){
                for (int i=0;i<param.size();i++){
                    //注意，PreparedStatement的底是1
                    pst.setObject(i+1,param.get(i));
                }
                int result = pst.executeUpdate();
                ResultSet genrateKeySet = pst.getGeneratedKeys();
                int setCount = genrateKeySet.getMetaData().getColumnCount();
                resultArray = new int[setCount+1];
                resultArray[0]=result;
                if(genrateKeySet.next()){
                    for (int i=1;i<=setCount;i++){
                        resultArray[i]=genrateKeySet.getInt(i);
                    }
                }
                genrateKeySet.close();
            }
            //////////////////
            
            
            return resultArray;
        }catch(SQLException e){
            if (e.getErrorCode() == 0){
                //说明已经断连,恢复连接
                this.reset();
                if (isFirst){
                    return executeUpdateGetGenrateKey(sql,param,false);
                }
            }else{
                //数据库重连就不打印日志了
                if (log.isLoggable(Level.FINE)){
                    String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                    log.fine("SqlErrCode:"+e.getErrorCode()+"Exception:"+err);
                }
            }
            throw e;
        }catch(RuntimeException e){
            if (log.isLoggable(Level.INFO)){
                String err = com.breeze.support.tools.CommTools.getExceptionTrace(e);
                log.info(err);
            }
            this.reset();
            if (isFirst){
                return executeUpdateGetGenrateKey(sql,param,false);
            }
            throw e;
        }
    }
    /**
     * @roseuid 467D0CF7007D
     */
    public TransDBOper getTrans() {
        Connection conn = getConnection();
        return new TransDBOper(conn);
    }
    
    /**
     * @roseuid 467D0D660138
     */
    public void initDB(String p_driver,String p_DBUrl,String p_dbuser,String p_pwd) {
        this.driver = p_driver;
        this.DBUrl = p_DBUrl;
        this.dbuser = p_dbuser;
        this.dbpwd = p_pwd;
        try{
            this.noneTransConnect = getConnection();
            this.noneTransStatement = this.noneTransConnect.createStatement();
            this.noneTransgenrateKeyStatement = this.noneTransConnect.createStatement();
        } catch (Exception e){
            log.severe(CommTools.getExceptionTrace(e));
            throw new RuntimeException("init error!");
        }
    }
    /**
     *自动恢复连接设置
     */
    private synchronized void reset(){
        //要保证连接在一定时间内不能经常初始化
        long timeStamp = System.currentTimeMillis();
        long okTime = 5000;//5秒
        if (timeStamp-this.lastResetTimeStamp <= okTime){
            this.lastResetTimeStamp = timeStamp;
            return;
        }
        this.lastResetTimeStamp = timeStamp;
        long beforeReset = System.currentTimeMillis();
        try{
            //首先释放原有资源
            this.NoneTransGenrateKeyPreparedStatementMap.clear();
            this.NoneTransPreparedStatementMap.clear();
            this.noneTransConnect.close();
        }catch(Exception ee){
        	log.severe(CommTools.getExceptionTrace(ee));
        }
        try{
            this.noneTransConnect = getConnection();
            this.noneTransStatement = this.noneTransConnect.createStatement();
            this.noneTransgenrateKeyStatement = this.noneTransConnect.createStatement();
            
            
            log.severe("DB Link was reset!it last "+(System.currentTimeMillis() - beforeReset) + "minSecond!");
            
        } catch (Exception e){
        	log.severe(CommTools.getExceptionTrace(e));
            throw new RuntimeException("init error!");
        }
    }
    
    private Connection getConnection(){
        try{
            Class.forName(driver).newInstance();
            return java.sql.DriverManager.getConnection(DBUrl,dbuser,dbpwd);
        } catch(Exception e){
        	log.severe(CommTools.getExceptionTrace(e));
            return null;
        }
    }

	@Override
	public void closQuery(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}