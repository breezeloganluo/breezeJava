package com.breezefw.framework.workflow.checker.single;

import java.util.ArrayList;

import com.breeze.base.db.COMMDB;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breezefw.framework.workflow.checker.SingleContextCheckerAbs;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLParser;

public class SqlChecker extends SingleContextCheckerAbs {
	private static final Logger log=Logger.getLogger("com.breezefw.framework.checker.SqlChecker");
	@Override
	public String getName() {
		return "sqlChecker";
	}

	@Override
	public boolean check(BreezeContext root,BreezeContext checkValue, Object[] param) {
		if (param == null || param.length == 0 || param[0] == null){
			return false;
		}
		String sqlTmp = param[0].toString();
		BTLExecutor exe = BTLParser.INSTANCE("sql").parser(sqlTmp);
		ArrayList<Object> sqlParam = new ArrayList<Object>();
		String sql = exe.execute(new Object[]{root}, sqlParam);
		log.fine("sql:"+sql);
		log.fine("sqlParam:" + sqlParam.toString());
		ResultSet rs = null;
		try{
			rs = COMMDB.executeSql(sql,sqlParam);
			return rs.next();
		}catch(Exception e){
			log.severe("", e);
			return false;
		}finally{
			if (rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					log.severe("in finally", e);
				}
			}
		}
	}

}
