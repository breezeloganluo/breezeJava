package com.breezefw.framework.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.breeze.base.db.COMMDB;
import com.breeze.base.db.TransDBOper;
import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breeze.support.thread.ThreadProcess;
import com.breezefw.framework.template.DBOperateExtItem;
import com.breezefw.framework.template.DBOperateItem;
import com.breezefw.ability.btl.BTLExecutor;

public class ExecSqlFlow extends WorkFlowUnit {
	private static final Logger log = Logger
			.getLogger("com.brezefw.framework.workflow.ExecSqlFlow");
	private final static String FLOWNAME = "sqlFlow";

	@Override
	public String getName() {
		return FLOWNAME;
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { new CommTemplateItemParser(
				FLOWNAME, DBOperateExtItem.class) };
		return result;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias,
			int lastResult) {
		// 获取根上下文信息
		BreezeContext root = context;

		if (log.isLoggable(Level.FINE)) {
			log.fine("go Process [" + this.getName() + "]lastResult["
					+ lastResult + "]");
		}
		ResultSet result = null;
		TransDBOper trans = null;
		try {
			DBOperateExtItem items = (DBOperateExtItem) this.getItem(context,
					st, FLOWNAME);
			//items为空说明没有配置
			if (items == null){
				log.fine("DB Item is null no sql config return 0");
				return 0;
			}
			String resultContextName = items.getSqlResultName();
			// 获得解析sql模版语句后的执行体
			BTLExecutor[] exes = items.getExec();
			// 获取上下文的实例

			BreezeContext array = new BreezeContext();
			// 把array放入上下文中
			root.setContext(resultContextName, array);
			trans = COMMDB.getDBTrances();
			if (trans != null){
				trans.setThreadTrans();
			}
			
			log.fine("the context [" + resultContextName
					+ "]will be set as new empty array first");
			for (int ti = 0; ti < exes.length; ti++) {
				BTLExecutor exe = exes[ti];
				// 创建存储参数的列表
				ArrayList<Object> params = new ArrayList<Object>();
				// 得到解析后的sql语句
				String sql = (String) exe
						.execute(new Object[] { root }, params);
				log.fine("sql:" + sql);
				log.fine("param:" + params);
				BreezeContext oneSqlContext = new BreezeContext();

				if (items.getSqlType()[ti] == DBOperateExtItem.QUERY) {
					// 执行解析后的sql语句，把执行结果赋值给result
					result = COMMDB.executeSql(sql, params);
					if (result == null) {
						return 100;
					}
					// 创建用于存储一条sql查询语句的结果集
					while (result.next()) {
						BreezeContext oneRecord = new BreezeContext();
						for (int i = 0; i < result.getMetaData()
								.getColumnCount(); i++) {
							// 得到结果集中的列名
							String culomnName = result.getMetaData()
									.getColumnName(i + 1);
							// 得到每个列名的值，并把值放入BreezeContext
							oneRecord.setContext(culomnName, new BreezeContext(
									result.getString(culomnName)));
						}
						// 把得到的每条记录都放入resultSetContext
						oneSqlContext.pushContext(oneRecord);
					}
					result.close();
					result = null;
					if (exes.length > 1) {
						// 2014-07-22罗光瑜修改，如果为空为保证sql的顺序，这里要设置一个值
						if (oneSqlContext.isEmpty()) {
							oneSqlContext = new BreezeContext("NULL");
						}
						// 把每条sql查询结果都放在数组中
						array.pushContext(oneSqlContext);
					} else {
						root.setContextByPath(resultContextName, oneSqlContext);
					}
				}else{
					result = null;
					int[] upresult = COMMDB.executeUpdateGetGenrateKey(sql, params);
					for (int rone : upresult){
						oneSqlContext.pushContext(new BreezeContext(rone));
					}
					// 如果只有一条记录就不要设置成数组
					if (exes.length > 1) {
						// 把每条sql结果都放在数组中
						array.pushContext(oneSqlContext);
					} else {
						root.setContextByPath(resultContextName, oneSqlContext);
					}					
				}
			}
			// 如果只有一条记录就不要设置成数组
			if (log.isLoggable(Level.FINE)) {
				log.fine("final result from query is [" + resultContextName
						+ "]" + root.getContextByPath(resultContextName));
			}
			
			if (trans != null){
				TransDBOper.closeThreadTrans(true);
			}
		} catch (SQLException sqle) {
			log.severe(ThreadProcess.Info.get(), sqle);
			try {
				if (result != null) {
					result.close();
				}
			} catch (Throwable e1) {
				log.severe("close result fail!");
			}
			
			try{
				if (trans != null){
					TransDBOper.closeThreadTrans(false);
				}
			}catch(Exception e){
				log.severe("close TransDBOper fail!");
			}
			return 10000 + sqle.getErrorCode();
			
		} catch (Exception ex) {
			log.severe(ThreadProcess.Info.get(), ex);
			try {
				if (result != null) {
					result.close();
				}
			} catch (Throwable e2) {
				log.severe("close result fail!");
			}
			
			
			try{
				if (trans != null){
					TransDBOper.closeThreadTrans(false);
				}
			}catch(Exception e){
				log.severe("close TransDBOper fail!");
			}
			return 999;
		}
		return 0;
	}

}
