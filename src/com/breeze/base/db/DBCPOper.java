package com.breeze.base.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.support.thread.ThreadProcess;
import com.breeze.support.tools.CommTools;

public class DBCPOper extends DbOper {

	// 日志对象
	private Logger log = Logger
			.getLogger("com.breeze.base.db.DBCPOper");

	private DataSource datasource;

	private String driver, DBUrl, dbuser, dbpwd;

	private Object Lock = new Object();

	private long lastResetTimeStamp = 0;

	public DBCPOper() {
	}

	/**
	 * 对外sql执行接口
	 */
	public ResultSet executeSql(String sql) throws SQLException {
		return executeSql(sql, true);
	}

	/**
	 * 内部处理类，如果是第一次调用发生异常，那么会进行重连，并第二次调用
	 */
	private ResultSet executeSql(String sql, boolean isFirst)
			throws SQLException {
		log.fine("executeSqlStr");
		Connection c = null;
		try {
			c = this.getConnection();
			Statement s = c.createStatement();
			DBResultSet r = new DBResultSet(s.executeQuery(sql), s, c);
			return r;
		} catch (SQLException e) {
			if (c != null) {
				this.closeConnection(c);
			}
			if (e.getErrorCode() == 0) {
				// 说明已经断连,恢复连接
				this.reset();
				if (isFirst) {
					// 如果是第一次，那么再调用
					return executeSql(sql, false);
				}
			} else {
				// 数据库重连就不打印日志了
				if (log.isLoggable(Level.FINE)) {
					String err = com.breeze.support.tools.CommTools
							.getExceptionTrace(e);
					log.fine("SqlErrCode:" + e.getErrorCode() + "Exception:"
							+ err);
				}
			}

			throw e;
		} catch (RuntimeException e) {
			if (c != null) {
				this.closeConnection(c);
			}
			if (log.isLoggable(Level.INFO)) {
				String err = com.breeze.support.tools.CommTools
						.getExceptionTrace(e);
				log.info(err);
			}
			this.reset();
			if (isFirst) {
				// 如果是第一次，那么再调用
				return executeSql(sql, false);
			}
			throw e;
		}
	}

	/**
	 * 对外接口stament方式的sql查询语句
	 */
	public ResultSet executeSql(String sql, ArrayList param)
			throws SQLException {
		return executeSql(sql, param, true);
	}

	/**
	 * 重属接口，连接无效能够重连
	 */
	private ResultSet executeSql(String sql, ArrayList param, boolean isFirst)
			throws SQLException {
		log.fine("executeSqlArrayList");
		Connection c = null;
		try {
			c = this.getConnection();
			PreparedStatement pst = c.prepareStatement(sql);

			for (int i = 0; i < param.size(); i++) {
				// 注意，PreparedStatement的底是1
				pst.setObject(i + 1, param.get(i));
			}
			DBResultSet result = new DBResultSet(pst.executeQuery(), pst, c);
			return result;
		} catch (SQLException e) {
			if (c != null) {
				this.closeConnection(c);
			}
			if (e.getErrorCode() == 0) {
				// 说明已经断连,恢复连接
				this.reset();
				if (isFirst) {
					return executeSql(sql, param, false);
				}
			} else {
				// 数据库重连就不打印日志了
				if (log.isLoggable(Level.FINE)) {
					String err = com.breeze.support.tools.CommTools
							.getExceptionTrace(e);
					log.fine("SqlErrCode:" + e.getErrorCode() + "Exception:"
							+ err);
				}
			}
			throw e;
		} catch (RuntimeException e) {
			if (c != null) {
				this.closeConnection(c);
			}
			if (log.isLoggable(Level.INFO)) {
				log.info("dbException", e);
			}
			this.reset();
			if (isFirst) {
				return executeSql(sql, param, false);
			}
			throw e;
		}
	}

	public int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql, true);
	}

	/**
	 * @roseuid 467D0A5F004E
	 */
	private int executeUpdate(String sql, boolean isFirst) throws SQLException {
		Connection connection = null;
		Statement st = null;
		log.fine("executeUpdatStr");
		try {
			connection = this.getConnection();
			st = connection.createStatement();
			int result = st.executeUpdate(sql);
			return result;
		} catch (SQLException e) {
			if (e.getErrorCode() == 0) {
				// 说明已经断连,恢复连接
				this.reset();
				if (isFirst) {
					return executeUpdate(sql, false);
				}
			} else {
				// 数据库重连就不打印日志了
				if (log.isLoggable(Level.FINE)) {
					String err = com.breeze.support.tools.CommTools
							.getExceptionTrace(e);
					log.fine("SqlErrCode:" + e.getErrorCode() + "Exception:"
							+ err);
				}
			}
			throw e;
		} catch (RuntimeException e) {
			if (log.isLoggable(Level.INFO)) {
				String err = com.breeze.support.tools.CommTools
						.getExceptionTrace(e);
				log.info(err);
			}
			this.reset();
			if (isFirst) {
				return executeUpdate(sql, false);
			}
			throw e;
		} finally {
			if (st != null) {
				st.close();
			}
			if (connection != null) {
				this.closeConnection(connection);
			}
		}
	}

	public int[] executeUpdateGetGenrateKey(String sql) throws SQLException {
		return executeUpdateGetGenrateKey(sql, true);
	}

	private int[] executeUpdateGetGenrateKey(String sql, boolean isFrist)
			throws SQLException {
		log.fine("executeUpdateGetGenrateKeyStr");
		Connection connection = null;
		Statement noneTransStatement = null;
		try {
			int[] resultArray;
			connection = this.getConnection();
			noneTransStatement = connection.createStatement();
			int result = noneTransStatement.executeUpdate(sql,
					Statement.RETURN_GENERATED_KEYS);
			ResultSet genrateKeySet = noneTransStatement.getGeneratedKeys();
			int setCount = genrateKeySet.getMetaData().getColumnCount();
			resultArray = new int[setCount + 1];
			resultArray[0] = result;
			if (genrateKeySet.next()) {
				for (int i = 1; i <= setCount; i++) {
					resultArray[i] = genrateKeySet.getInt(i);
				}
			} else {
				// 如果没有自动索引,要从新给返回结果赋值
				resultArray = new int[] { result };
			}
			genrateKeySet.close();
			return resultArray;
		} catch (SQLException e) {
			if (e.getErrorCode() == 0) {
				// 说明已经断连,恢复连接
				this.reset();
				if (isFrist) {
					return executeUpdateGetGenrateKey(sql, false);
				}
			} else {
				// 数据库重连就不打印日志了
				if (log.isLoggable(Level.FINE)) {
					String err = com.breeze.support.tools.CommTools
							.getExceptionTrace(e);
					log.fine("SqlErrCode:" + e.getErrorCode() + "Exception:"
							+ err);
				}
			}

			throw e;
		} catch (RuntimeException e) {
			if (log.isLoggable(Level.INFO)) {
				String err = com.breeze.support.tools.CommTools
						.getExceptionTrace(e);
				log.info(err);
			}
			this.reset();
			if (isFrist) {
				return executeUpdateGetGenrateKey(sql, false);
			}
			throw e;
		} finally {
			if (noneTransStatement != null) {
				noneTransStatement.close();
			}
			if (connection != null) {
				this.closeConnection(connection);
			}
		}
	}

	public int executeUpdate(String sql, ArrayList param) throws SQLException {
		return executeUpdate(sql, param, true);
	}

	/**
	 * @roseuid 467D0CC7035B
	 */
	private int executeUpdate(String sql, ArrayList param, boolean isFirst)
			throws SQLException {
		log.fine("executeUpdateArrayList");
		Connection connection = null;
		PreparedStatement pst = null;
		try {
			connection = this.getConnection();
			pst = connection.prepareStatement(sql);

			// 2010-4-11日增加：允许输入数组进行操作
			int arrayLen = -1;
			HashMap<Integer, Object[]> arrayObjMap = new HashMap<Integer, Object[]>();
			for (int i = 0; i < param.size(); i++) {
				Object o = param.get(i);
				if (o instanceof Object[]) {
					Object[] oo = (Object[]) o;
					int oLen = oo.length;
					// 设置最小的长度
					if (oLen < arrayLen || arrayLen < 0) {
						arrayLen = oLen;
					}
					arrayObjMap.put(i + 1, oo);
					continue;
				}
				// 注意，PreparedStatement的底是1
				pst.setObject(i + 1, o);
			}
			int result = 0;
			if (arrayLen < 0) {
				result = pst.executeUpdate();
			} else {
				// 要进行数组循环处理
				for (int i = 0; i < arrayLen; i++) {
					Iterator itor = arrayObjMap.entrySet().iterator();
					while (itor.hasNext()) {
						Map.Entry en = (Map.Entry) itor.next();
						Integer key = (Integer) en.getKey();
						Object[] arrayObj = (Object[]) en.getValue();
						Object o = arrayObj[i];
						pst.setObject(key, o);
					}
					result += pst.executeUpdate();
				}
			}
			return result;
		} catch (SQLException e) {
			if (e.getErrorCode() == 0) {
				// 说明已经断连,恢复连接
				this.reset();
				if (isFirst) {
					return executeUpdate(sql, param, false);
				}
			} else {
				// 数据库重连就不打印日志了
				if (log.isLoggable(Level.FINE)) {
					String err = com.breeze.support.tools.CommTools
							.getExceptionTrace(e);
					log.fine("SqlErrCode:" + e.getErrorCode() + "Exception:"
							+ err);
				}
			}
			throw e;
		} catch (RuntimeException e) {
			if (log.isLoggable(Level.INFO)) {
				String err = com.breeze.support.tools.CommTools
						.getExceptionTrace(e);
				log.info(err);
			}
			this.reset();
			if (isFirst) {
				return executeUpdate(sql, param, false);
			}
			throw e;
		} finally {
			if (pst != null) {
				pst.close();
			}
			if (connection != null) {
				this.closeConnection(connection);
			}
		}
	}

	public int[] executeUpdateGetGenrateKey(String sql, ArrayList param)
			throws SQLException {
		return executeUpdateGetGenrateKey(sql, param, true);
	}

	private int[] executeUpdateGetGenrateKey(String sql, ArrayList param,
			boolean isFirst) throws SQLException {
		log.fine("executeUpdateGetGenrateKeyArrayList");
		Connection connection = null;
		PreparedStatement pst = null;
		try {
			connection = this.getConnection();
			pst = connection.prepareStatement(sql,
					Statement.RETURN_GENERATED_KEYS);

			// 加锁，获取对应的自动增长值
			int[] resultArray;
			synchronized (pst) {
				int arrayLen = -1;
				HashMap<Integer, Object[]> arrayObjMap = new HashMap<Integer, Object[]>();
				for (int i = 0; i < param.size(); i++) {
					Object o = param.get(i);
					if (o instanceof Object[]) {
						Object[] oo = (Object[]) o;
						int oLen = oo.length;
						// 设置最小的长度
						if (oLen < arrayLen || arrayLen < 0) {
							arrayLen = oLen;
						}
						arrayObjMap.put(i + 1, oo);
						continue;
					}
					// 注意，PreparedStatement的底是1
					pst.setObject(i + 1, o);
				}
				ArrayList<Integer> arrList = new ArrayList<Integer>();
				int tmpRs = 0;

				if (arrayLen < 0) {
					tmpRs = pst.executeUpdate();
					arrList.add(tmpRs);
					ResultSet genrateKeySet = pst.getGeneratedKeys();
					int setCount = genrateKeySet.getMetaData().getColumnCount();
					if (genrateKeySet.next()) {
						for (int i = 1; i <= setCount; i++) {
							arrList.add(genrateKeySet.getInt(i));
						}
					}
					genrateKeySet.close();
				} else {
					// 要进行数组循环处理
					for (int i = 0; i < arrayLen; i++) {
						Iterator itor = arrayObjMap.entrySet().iterator();
						while (itor.hasNext()) {
							Map.Entry en = (Map.Entry) itor.next();
							Integer key = (Integer) en.getKey();
							Object[] arrayObj = (Object[]) en.getValue();
							Object o = arrayObj[i];
							pst.setObject(key, o);
						}

						tmpRs = pst.executeUpdate();
						arrList.add(tmpRs);
						ResultSet genrateKeySet = pst.getGeneratedKeys();
						int setCount = genrateKeySet.getMetaData()
								.getColumnCount();
						if (genrateKeySet.next()) {
							for (int j = 1; j <= setCount; j++) {
								arrList.add(genrateKeySet.getInt(j));
							}
						}
						genrateKeySet.close();
					}
				}
				// 整理输出结果
				resultArray = new int[arrList.size()];
				for (int i = 0; i < resultArray.length; i++) {
					resultArray[i] = arrList.get(i);
				}
			}
			// ////////////////
			return resultArray;
		} catch (SQLException e) {
			if (e.getErrorCode() == 0) {
				// 说明已经断连,恢复连接
				this.reset();
				if (isFirst) {
					return executeUpdateGetGenrateKey(sql, param, false);
				}
			} else {
				// 数据库重连就不打印日志了
				if (log.isLoggable(Level.FINE)) {
					String err = com.breeze.support.tools.CommTools
							.getExceptionTrace(e);
					log.fine("SqlErrCode:" + e.getErrorCode() + "Exception:"
							+ err);
				}
			}
			throw e;
		} catch (RuntimeException e) {
			if (log.isLoggable(Level.INFO)) {
				String err = com.breeze.support.tools.CommTools
						.getExceptionTrace(e);
				log.info(err);
			}
			this.reset();
			if (isFirst) {
				return executeUpdateGetGenrateKey(sql, param, false);
			}
			throw e;
		} finally {
			if (pst != null) {
				pst.close();
			}
			if (connection != null) {
				this.closeConnection(connection);
			}
		}
	}

	/**
	 * @roseuid 467D0CF7007D
	 */
	public TransDBOper getTrans() {
		TransDBOper result = TransDBOper.getTransDBOper();
		if (result == null) {
			Connection conn = getConnection();
			result = new TransDBOper(conn);
		}else{
			//如果已经有TransDB了，那么返回空，让上层去使用TransDBOper去获取
			//--因为上层在多次Trans嵌套后，在可以重复创建，但是关闭一次后，后续的操作
			//--就无法继续事务操作了，带来很大的麻烦，所以这里 要显示的告诉上层
			return null;
		}
		return result;
	}

	/**
	 * @roseuid 467D0D660138
	 */
	public void initDB(String p_driver, String p_DBUrl, String p_dbuser,
			String p_pwd) {
		this.driver = p_driver;
		this.DBUrl = p_DBUrl;
		this.dbuser = p_dbuser;
		this.dbpwd = p_pwd;
		try {
			this.datasource = this.setupDataSource(this.driver, this.DBUrl,
					this.dbuser, this.dbpwd);
		} catch (Exception e) {
			log.severe(CommTools.getExceptionTrace(e));
			throw new RuntimeException("init error!");
		}
	}

	/**
	 * 自动恢复连接设置
	 */
	private synchronized void reset() {

	}

	private Connection getConnection() {
		try {
			Connection result;
			// 2014-08-26罗光瑜修改，支持事务链接的获取，即先从事务中获取连接，如果为空再到池子中获取连接
			TransDBOper transOper = TransDBOper.getTransDBOper();
			if (transOper == null) {
				log.fine("begin getConnection , use no trans");
				result = this.datasource.getConnection();
				result.setAutoCommit(true);
				//2017-05-24增加的统计代码，所有借用的链接都记录下来
				String s = ThreadProcess.Info.get();
				String value = s + '|' + System.currentTimeMillis();
				synchronized (ContextMgr.global) {
					ContextMgr.global.setContextByPath("db.connect."+result.hashCode(), new BreezeContext(value));
				}
				
			} else {
				log.fine("begin getConnection , use trans");
				result = transOper.getConnection();
			}
			if (log.isLoggable(Level.FINE)){
				
			}
			
			if (log.isLoggable(Level.FINE)){
				log.fine("end getConnection , the connection is:"+result.hashCode());
				try{
				log.fine("end getConnection is AutoCommit is:"+ result.getAutoCommit() + 
						",isClose is:" + result.isClosed()+";TransactionIsolation:"+result.getTransactionIsolation());
				}catch(Exception e){
					log.fine("get cnnection is available or is Autocommit fail!");
				}
			}
			return result;
		} catch (Exception e) {
			log.severe(CommTools.getExceptionTrace(e));
			return null;
		}
	}
	/**
	 * 罗光瑜2014-08-26日添加，connection只有在非事务的时候才主动关闭，否则是外部统一关闭的
	 * @param con
	 * @throws SQLException
	 */
	private void closeConnection(Connection con) throws SQLException{
		if (con.getAutoCommit()){
			//2017-05-24罗光瑜增加了内部的统计，如果信息关闭，则关闭删除内部统计
			synchronized (ContextMgr.global) {
				ContextMgr.global.setContextByPath("db.connect."+con.hashCode(), null);
			}
			con.close();
		}
	}

	private DataSource setupDataSource(String p_driver, String p_DBUrl,
			String p_dbuser, String p_pwd) {
		BasicDataSource ds = new BasicDataSource();
		try {
			ds.setDriverClassName(p_driver);
			ds.setUrl(p_DBUrl);
			ds.setUsername(p_dbuser);
			ds.setPassword(p_pwd);

			ds.setInitialSize(5);
			ds.setMaxActive(50);
			ds.setMaxIdle(30);
			ds.setMinIdle(10);
			ds.setMaxWait(1000);

			ds.setValidationQuery("select 3");
			ds.setValidationQueryTimeout(5);
			ds.setPoolPreparedStatements(true);
			ds.setMaxOpenPreparedStatements(1000);
			ds.setTestOnBorrow(true);

			ds.setRemoveAbandoned(true);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return ds;
	}

	@Override
	public void closQuery(ResultSet rs) {
		Connection con = null;
		try {
			if (rs != null) {
				Statement st = rs.getStatement();
				con = st.getConnection();
				if (!rs.isClosed()) {
					rs.close();
				}
				st.close();
			}
		} catch (Exception e) {
			log.severe("", e);
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					throw new RuntimeException(e.toString());
				}
			}
		}

	}
}
