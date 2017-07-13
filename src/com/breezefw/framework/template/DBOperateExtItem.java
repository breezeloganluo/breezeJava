package com.breezefw.framework.template;

import com.breeze.base.log.Logger;
import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLParser;

public class DBOperateExtItem extends TemplateItemBase {
	private static Logger log = Logger.getLogger("com.breezefw.framework.template.DBOperateExtItem");
	public static class SqlInfo extends TemplateItemBase{
		@FieldDesc(desc = "sql操作类型",type="Select", title = "操作类型", valueRange = "[{'query':'query','update':'update'}]")
		private String sqlType;
		
		@FieldDesc(desc = "sql语句BTL表达式为$｛str（_R.cid）｝", title = "sql", valueRange = "[{failTips:'sql不能为空',checkers:['']}]",type="TextArea")
		private String sqlConfig;
		
	}
	
	
	@FieldDesc(desc = "结果名称，即sql执行完后，结果放入在什么地方", title = "sqlResultName", valueRange = "[{failTips:'sqlResultName不能为空',checkers:['']}]",type="Text")
	private String sqlResultName;
	
	@FieldDesc(desc = "做更新操作时，操作完需要刷新什么", title = "refreshList", valueRange = "")
	private String[] refreshList;
	
	
	private SqlInfo[] sqlInfo;


	public String getSqlResultName() {
		return sqlResultName;
	}


	public String[] getRefreshList() {
		return refreshList;
	}


	public SqlInfo[] getSqlInfo() {
		return sqlInfo;
	}
	
	private BTLExecutor[] exec;	
	public BTLExecutor[] getExec(){
		return exec;
	}
	
	public static final int QUERY = 0;
	public static final int UPDATE = 1;
	private int[] sqlType;
	
	@Override
	public void loadingInit()
	{
		log.severe("--------------biegin init dboperItem");
		exec=new BTLExecutor[sqlInfo.length];
		sqlType = new int[sqlInfo.length];
		log.severe("sql is:"+sqlInfo.length);
		for (int i=0;i<sqlInfo.length;i++)
		{
			BTLExecutor exe = BTLParser.INSTANCE("sql").parser(sqlInfo[i].sqlConfig);
			exec[i]=exe;
			if ("query".equals(sqlInfo[i].sqlType)){
				sqlType[i] = QUERY;
			}else{
				sqlType[i] = UPDATE;
			}
		}
	}
	
	public int[] getSqlType(){
		return this.sqlType;
	}
}
