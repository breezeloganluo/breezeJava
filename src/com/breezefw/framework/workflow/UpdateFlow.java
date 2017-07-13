package com.breezefw.framework.workflow;

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
import com.breeze.init.LoadClasses;
import com.breeze.support.thread.ThreadProcess;
import com.breezefw.framework.template.DBOperateItem;
import com.breezefw.framework.workflow.sqlfunction.SqlFunctionAbs;
import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLFunctionAbs;
import com.breezefw.ability.btl.BTLParser;
import com.breezefw.ability.datarefresh.DataRefreshIF;
import com.breezefw.ability.datarefresh.DataRefreshMgr;

/**
 * 更新函数增加刷新能力，做某些业务操作后， 要求能够刷新部分数据
 * 
 * @author Administrator
 * 
 */
public class UpdateFlow extends WorkFlowUnit {
	private Logger log = Logger
			.getLogger("com.breezefw.framework.workflow.UpdateFlow");
	private final static String ITEMNAME = "updateItem";

	@Override
	public String getName() {
		return "updateFlow";
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { new CommTemplateItemParser(
				ITEMNAME, DBOperateItem.class) };
		return result;
	}

	/**
	 * 增加了更新数据的刷新功能 通过调用initer来初始化刷新列表，到时可以根据刷新的配置来刷新数据
	 */
	@Override
	protected void loadingInit() {
		DataRefreshMgr.getInstance().init();
		// 下面进行BTL的初始化
		BTLParser.init("sql");
		// 获取所有的初始化资源
		ArrayList<SqlFunctionAbs> initList = LoadClasses.createObject(
				"com.breezefw.framework.workflow.sqlbtlfun",
				SqlFunctionAbs.class);
		for (BTLFunctionAbs btl : initList) {
			BTLParser.INSTANCE("sql").addFunction(btl);
		}

		// 获取所有的初始化资源
		initList = LoadClasses.createObject("com.breezefw.service",
				SqlFunctionAbs.class);
		for (BTLFunctionAbs btl : initList) {
			BTLParser.INSTANCE("sql").addFunction(btl);
		}
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
		int i = 0;
		int size = 0;
		TransDBOper trans = null;
		try {
			DBOperateItem items = (DBOperateItem) this.getItem(context, st,
					ITEMNAME);
			//items为空说明没有配置
			if (items == null){
				log.fine("DB Item is null no sql config return 0");
				return 0;
			}
			String resultContextName = items.getSqlResultName();
			// 获得解析sql模版语句后的执行体
			BTLExecutor[] exes = items.getExec();
			size = exes.length;
			// 获取上下文的实例

			BreezeContext array = new BreezeContext();
			// 把array放入上下文中
			root.setContextByPath(resultContextName, array);
			log.fine("the context [" + resultContextName + "]will be set as new empty array first");
			//罗光瑜2014-多条update语句支持事务处理
			trans = COMMDB.getDBTrances();
			if (trans != null){
				trans.setThreadTrans();
			}
			for (BTLExecutor exe : exes) {
				// 创建存储参数的列表
				ArrayList<Object> params = new ArrayList<Object>();
				// 得到解析后的sql语句
				String sql = (String) exe
						.execute(new Object[] { root }, params);
				log.fine(sql);
				log.fine(params.toString());
				// 执行解析后的sql语句，把执行结果赋值给result
				// 罗光瑜 2013-11-30修改，增加自动递增字段的返回值
				int[] result = COMMDB.executeUpdateGetGenrateKey(sql, params);				
				// 创建用于存储一条sql语句的结果集
				BreezeContext oneSqlContext = new BreezeContext();
				for (int rone : result){
					oneSqlContext.pushContext(new BreezeContext(rone));
				}
				// 如果只有一条记录就不要设置成数组
				if (size > 1) {
					// 把每条sql结果都放在数组中
					array.pushContext(oneSqlContext);
				} else {
					root.setContextByPath(resultContextName, oneSqlContext);
				}
				i++;
			}

			// 最后要建立刷新机制刷新对应内存
			for (i = 0; items.getRefreshList() != null
					&& i < items.getRefreshList().length; i++) {
				String rName = items.getRefreshList()[i];
				DataRefreshIF refresh = DataRefreshMgr.getInstance()
						.getRefresh(rName);
				if (refresh != null) {
					refresh.refresh(context);
				}
			}
			
			if (trans != null){
				TransDBOper.closeThreadTrans(true);
			}

		} catch (SQLException e) {
			log.severe(ThreadProcess.Info.get(), e);
			try{
				if (trans != null){
					TransDBOper.closeThreadTrans(false);
				}
			}catch(Exception se){}
			return 10000 + e.getErrorCode();
		} catch (Exception ex) {
			log.severe(ThreadProcess.Info.get(), ex);
			try{
				if (trans != null){
					TransDBOper.closeThreadTrans(false);
				}
			}catch(Exception se){}
			if (i == 0 || size < 2) {
				return 999;
			}
			return 101;
		}
		return 0;
	}
}
