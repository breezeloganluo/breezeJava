package com.breeze.support.test;

import java.io.File;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.breeze.base.db.COMMDB;
import com.breeze.support.tools.FileTools;

/**
 * 这个类是辅助类，辅助单元测，当前版本主要解决是数据库相关测试的效率问题 单元测试在数据库的测试中，需要大量的建表，初始化数据，这些工作量太大。
 * 最好是由外部工作完成，然后由这个辅助测试工具类，进行初始化。
 * 
 * @author Administrator
 * 
 */
public class WGTestTools {
	private static Class initClass = null;
	private static boolean hasDBInit = false;

	/**
	 * 初始话case方法，该方法根据输入的class找到对应的数据库脚本，并创建之。同时根据输入的method配套的给对应的方法创建一个视图模拟成一个表
	 * 
	 * @param c
	 *            要测试的测试类
	 * @param method
	 *            要测试的测试方法。注意该方法不能有下划线
	 * @throws SQLException
	 *             sql异常
	 */
	public static void initCase(Class c, String method) throws SQLException {
		// 找出对应的sql文件
		String sqlFileName = getSqlFileName(c);
		// 读出文件
		String fileText = FileTools.readFile(new File(sqlFileName), "UTF-8");
		// 分析该文件找出所有的单个sql语句，以及表名
		ArrayList<String> sqls = new ArrayList<String>();
		ArrayList<String> tables = new ArrayList<String>();
		parserSqlText(fileText, sqls, tables, method);
		// 循环并执行这些sql语句
		if (initClass == null || !(initClass.equals(c))) {
			clearAllTables();
			for (String sql : sqls) {
				System.out.println("++exec:" + sql);
				COMMDB.executeUpdate(sql);
			}
			initClass = c;
		}
		// 根据数据表，创建对应的视图
		for (String t : tables) {
			String sql = "create or replace view " + t + " as select * from "
					+ t + "_test_" + method;
			System.out.println("++exec:" + sql);
			COMMDB.executeUpdate(sql);
		}
		System.out.println(tables);
	}

	private static void parserSqlText(String inText, ArrayList<String> sqls,
			ArrayList<String> tables, String preFix) {
		String[] ss = inText.split(";");
		for (String s : ss) {
			if ("".equals(s.trim())) {
				continue;
			}
			Pattern p = Pattern.compile("DROP\\s+TABLE.+",
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(s);
			if (m.find()) {
				// 这种就直接跳过
				continue;
			}
			sqls.add(s);
			// 创建对应的正则，用createtable加后缀进行判断
			p = Pattern.compile("CREATE\\s+TABLE\\s+`([^`]+)`");
			m = p.matcher(s);
			// if(存在){
			if (m.find()) {
				// 取对应的表明部分
				String table = m.group(1);
				// 判断table是否是目标表
				String tableUp = table.toUpperCase();
				String preFixUp = preFix.toUpperCase();
				int idx = tableUp.indexOf("_TEST_" + preFixUp);
				if (idx > 0) {
					// 加入到tables中
					tables.add(table.substring(0, idx));
				}
			}
			// }
		}
	}

	private static String getSqlFileName(Class c) {
		String className = c.getSimpleName();
		String classDir = URLDecoder.decode(c.getResource("").getPath());
		return classDir.substring(1) + className + ".sql";
	}

	private static void clearAllTables() throws SQLException {
		String sql = "show table status where comment<>'view'";
		ResultSet rs = COMMDB.executeSql(sql);
		while (rs.next()) {
			try {
				COMMDB.executeUpdate("drop table " + rs.getString("Name"));
			} catch (Exception e) {

			}
		}
		rs.close();
	}

	/**
	 * 对比比较两张表是否完全一样
	 * 
	 * @param table1
	 *            被对比的表1
	 * @param table2
	 *            被对比的表2
	 * @param mainKey
	 *            主键值，比较时用这个字段进行统一排序，保证相关记录是相等的。
	 * @return true两个表的值完全相等，否则返回false
	 * @throws SQLException
	 */
	public static boolean tableEquals(String table1, String table2,
			String[] mainKey) throws SQLException {
		// 查看第一张表的数据描述
		String sql = "desc " + table1;
		ResultSet rs = COMMDB.executeSql(sql);
		// 记录字段总数
		ArrayList<String> fields = new ArrayList<String>();
		while (rs.next()) {
			fields.add(rs.getString("Field"));
		}
		rs.close();
		// 合并sql语句
		String sql1 = "select * from " + table1 + " order by ";
		String sql2 = "select * from " + table2 + " order by ";
		boolean isFirst = true;
		for (String keyName : mainKey) {
			if (isFirst) {
				isFirst = false;
			} else {
				sql1 += ",";
				sql2 += ",";
			}
			sql1 = sql1 + keyName;
			sql2 = sql2 + keyName;
		}
		// 分别查询
		ResultSet rs1 = COMMDB.executeSql(sql1);
		ResultSet rs2 = COMMDB.executeSql(sql2);
		// 遍历第一个sql
		// while(rs1.next(){
		while (rs1.next()) {
			// if (!rs2.next()){
			if (!rs2.next()) {
				// 打印日志并返回false
				System.out.println("rs2 has no data!");
				return false;
				// }
			}
			// for(用数字，循环所有字段){
			for (int i = 0; i < fields.size(); i++) {
				String fieldName = fields.get(i);
				String rr1 = rs1.getString(fieldName);
				String rr2 = rs2.getString(fieldName);
				if (rr1 == null && rr2 == null) {
					continue;
				}
				// if (用字符串获取结果集不相等){
				if (rr1 == null || !rr1.equals(rr2)) {
					System.out.println("rs1 is " + rs1.getString(i)
							+ " but rs2 is " + rs2.getString(i));
					return false;
				}
				// }
			}
			// }

		}
		// }
		rs1.close();
		rs2.close();
		return true;
	}
}
