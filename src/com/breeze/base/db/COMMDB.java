/**
 * 数据库操作接口<br>
 * 根据不同的数据库操作实例，实现对应的数据库操作<br>
 * 数据库操作的原则：下面的所有操作都是没有事物的，需要事物的数据库操作使用存储过程完成<br>
 */
package com.breeze.base.db;

import com.breeze.base.log.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class COMMDB {
    private static DbOper oper;
    //日志对象
    private static Logger log = Logger.getLogger("support.db.BARGANDB");
    
    /**
     * 初始化数据库
     * 传入的是一个数据库操作实际类
     * @param p_oper 数据库操作实际类
     */
    public static void initDB(DbOper p_oper) {
        try{
            COMMDB.oper = p_oper;
        } catch (Exception e){
            log.severe("init DB Err:" + e.toString());
            throw new RuntimeException("init db err");
        }
        
    }
     
 
    
    /**
     * 执行查询语句，使用的非预编译模式查询
     * @param sql语句
     */
    public static ResultSet executeSql(String sql) throws SQLException{
        return oper.executeSql(sql);
    }
    
    /**
     * 执行查询语句，使用的预编译模式
     * @param sql 传入的sql语句
     * @param param 传入的sql的语句列表
     */
    public static ResultSet executeSql(String sql, ArrayList param) throws SQLException{
        return oper.executeSql(sql,param);
    }
    
    /**
     * 数据库更新操作<br>
     * @param sql 要操作的sql语句
     * 返回影响sql的记录数
     */
    public static int executeUpdate(String sql) throws SQLException{
        return oper.executeUpdate(sql);
    }
    
    public static int[] executeUpdateGetGenrateKey(String sql) throws SQLException{
        return oper.executeUpdateGetGenrateKey(sql);
    }
    
    /**
     * 带结果集的返回，本函数将会把执行sql语句中一些带自动递增之类的字段结果返回
     * @roseuid 一个数组，第一个元素是返回的sql结果，第二个参数开始是自动递增结果
     */
    public static int executeUpdate(String sql, ArrayList param) throws SQLException{
        return oper.executeUpdate(sql,param);
    }
    
    /**
     * 带结果集的返回，本函数将会把执行sql语句中一些带自动递增之类的字段结果返回
     * @roseuid 一个数组，第一个元素是返回的sql结果，第二个参数开始是自动递增结果
     */
    public static int[] executeUpdateGetGenrateKey(String sql, ArrayList param) throws SQLException{
        return oper.executeUpdateGetGenrateKey(sql,param);
    }
    
    public static void closeQuery(ResultSet rs){
    	oper.closQuery(rs);
    }
    /**
     * @roseuid 467D317A0157
     */
    public static TransDBOper getDBTrances() {
        return oper.getTrans();
    }   
    
}
