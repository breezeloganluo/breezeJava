package com.breezefw.framework.template;

import com.breeze.base.log.Logger;
import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breezefw.shell.ServiceDescTools;
import com.breezefw.ability.btl.BTLExecutor;
import com.breezefw.ability.btl.BTLParser;

public class DBOperateItem extends  TemplateItemBase{
	private static Logger log = Logger.getLogger("com.breezefw.framework.template.DBOperateItem");
	
	@FieldDesc(desc = "结果名称，即sql执行完后，结果放入在什么地方", title = "sqlResultName", valueRange = "[{failTips:'sqlResultName不能为空',checkers:['']}]",type="Text")
	private String sqlResultName;
	
	@FieldDesc(desc = "sql语句BTL表达式为$｛str（_R.cid）｝", title = "sql", valueRange = "[{failTips:'sql不能为空',checkers:['']}]",type="TextArea")
	private String[] sqlConfig;
	
	@FieldDesc(desc = "做更新操作时，操作完需要刷新什么", title = "refreshList", valueRange = "")
	private String[] refreshList;
	
	/**
	 * @return the refreshList
	 */
	public String[] getRefreshList() {
		return refreshList;
	}
	/**
	 * @param refreshList the refreshList to set
	 */
	public void setRefreshList(String[] refreshList) {
		this.refreshList = refreshList;
	}

	private BTLExecutor[] exec;	
	public BTLExecutor[] getExec(){
		return exec;
	}
	public String getSqlResultName(){
		return this.sqlResultName;
	}

	@Override
	public void loadingInit()
	{
		log.severe("--------------biegin init dboperItem");
		exec=new BTLExecutor[sqlConfig.length];
		log.severe("sql is:"+sqlConfig.length);
		for (int i=0;i<sqlConfig.length;i++)
		{
			BTLExecutor exe = BTLParser.INSTANCE("sql").parser(sqlConfig[i]);
			exec[i]=exe;
		}
	}
	
	public static void main(String[]args){
		System.out.println(ServiceDescTools.parserItemDesc(DBOperateItem.class));
	}
}
