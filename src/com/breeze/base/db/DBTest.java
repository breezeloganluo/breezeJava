package com.breeze.base.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBTest implements Runnable{
	public int number = 0;
	private static int count = 1000;
	DBTest(int n){
		this.number =n;
	}
	private static void init(){
		String dev = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String pwd = "123456";
        
        //dbcp似乎有问题，在没有事务处理的时候，用原先的应该没有问题
        DBCPOper oper = new DBCPOper();
        //SimpleDbOper oper = new SimpleDbOper();
        oper.initDB(dev,url,user,pwd);
        COMMDB.initDB(oper);
	}

	@Override
	public void run() {
		try{
			System.out.println("go " + this.number);
			String sql = "insert into DBTest(idx,name)values(?,?)";
			for (int i=0;i<count;i++){
				ArrayList param = new ArrayList();
				param.add(this.number);
				param.add(i);
				COMMDB.executeUpdate(sql, param);
			}
			int i=0;
			sql = "select * from DBTest  where idx=" + this.number + " order by name";
			ResultSet rs = COMMDB.executeSql(sql);
			while(rs.next()){
				int idx = rs.getInt("name");
				if (idx != i){
					System.out.println("thread " +this.number +"error while i="+i + " and idx="+idx);
					rs.close();
					return;
				}
				i++;
			}
			if (i!=count){
				System.out.println("thread " +this.number +"error while i="+i);
			}
			rs.close();
			System.out.println("finished " + this.number);
		}catch(Exception e){
			System.out.println("thread " +this.number +"error!");
			e.printStackTrace();
		}
	}
	
	public static void main(String[]args) throws SQLException{
		init();
		int execCount = 200;
		String sql = "delete from DBTest";
		COMMDB.executeUpdate(sql);
		for(int i= 0;i<execCount;i++){
			Thread one = new Thread(new DBTest(i));
			one.start();
		}
	}
	
}
