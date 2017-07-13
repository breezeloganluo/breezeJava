package com.breeze.base.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBTester implements Runnable {

	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			String sql = "select * from aa";
			try {
				ResultSet rs = COMMDB.executeSql(sql);
				while (rs.next()) {
					System.out.println(rs.getString(1));
				}
				rs.close();
				
				COMMDB.executeUpdate("insert into aa values('aa','bb')");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// 初始化数据库
		DBCPOper db = new DBCPOper();
		// //数据库启动
		DBCPOper oper = new DBCPOper();
		oper.initDB("com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost:3306/test", "root", "123456");
		COMMDB.initDB(oper);
		//多线程的测试
		for (int i=0;i<100;i++){
			Thread t = new Thread(new DBTester());
			t.run();
		}
	}
}
