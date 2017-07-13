//Source file: D:\\my resource\\myproject\\百万方格\\外包项目夹\\design\\support\\db\\DbOper.java

package com.breeze.base.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.*;

/**
 * 这是一个虚基操作类，只有函数接口
 */
public abstract class DbOper {
    
    
    /**
     * @roseuid 467D021D038A
     */
    public abstract ResultSet executeSql(String sql)throws SQLException ;
    
    /**
     * @roseuid 467D0A360167
     */
    public abstract ResultSet executeSql(String sql, ArrayList param) throws SQLException;
    
    /**
     * @roseuid 467D0A5F004E
     */
    public abstract int executeUpdate(String sql)throws SQLException ;
    /**
     *能返回自动增长值得语句
     *返回一个结果集合：
     *0是原update结果
     *其他是自动增长值
     */
    public abstract int[] executeUpdateGetGenrateKey(String sql)throws SQLException ;
    public abstract int[] executeUpdateGetGenrateKey(String sql, ArrayList param)throws SQLException ;
    /**
     * @roseuid 467D0CC7035B
     */
    public abstract int executeUpdate(String sql, ArrayList param)throws SQLException ;
    
    /**
     * @roseuid 467D0CF7007D
     */
    public abstract TransDBOper getTrans() ;
    
    public abstract void closQuery(ResultSet rs);
    
    /**
     * @roseuid 467D0D660138
     */
    public abstract void initDB(String driver,String DBUrl,String user,String pwd) ;
}
