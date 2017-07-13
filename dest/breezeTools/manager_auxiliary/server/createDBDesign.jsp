<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.breeze.framwork.databus.*"%>
<%@ page import="com.breeze.framwork.netserver.tool.ContextMgr"%>
<%@ page import="com.breeze.support.tools.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.breeze.base.db.COMMDB"%>
<%@ page import="java.sql.ResultSet"%>
<style>


table.dataTable {
    border-collapse:collapse;border-spacing:0;border-left:1px solid #888;border-top:1px solid #888;background:#efefef;
	
}

table.dataTable td,
table.dataTable th {
    border:1px solid #888;padding:5px 15px;
}

</style>
<%
	String notNeedTable="'channel','channelClass','EStore','finance','logistics',"
		+"'OrderSuitDistribution','OrderSuitList','pagemgr','Rebate','serviceCenter','ShippingAddress'"
		+",'statusaction','statusinfo','Subscribe','AfterSale','AgencyCoupon','Coupon','SuitDistribution'"
		+",'SuitDistribution','SuitList','SuitSKU','userAddress','userBalance','UserCoupon','userEmail','verification'"
		+",'Withdraw','weixinaccess','weixincommand','weixinreply','Store','Feedback','ProductConsult','scoreProdOrder'"
		+",'StoreProduct','ChoiceQuestion','SubscribeProduct','','',''";
	String sql= "SELECT * FROM cmsmetadata where alias not in("+notNeedTable+") and nodeid!=16";
	ResultSet rs =COMMDB.executeSql(sql);
	int i=0;
	while(rs.next()){
		String dataDesc =rs.getString("dataDesc");
		System.out.println(rs.getString("displayName"));
		BreezeContext dataDescContext = ContextTools.getBreezeContext4Json(dataDesc);
		int length = dataDescContext.getMapSet().size();
		i++;
%>
<h2><%=rs.getString("displayName")%>的设计说明</h2>
<table class="dataTable" style="border:1px solid #ddd;width:650px;">
	<tr>
		<td style="background-color:#ccc;">模型名称</td>
		<td><%=rs.getString("displayName")%></td>
		<td style="background-color:#ccc;">表名</td>
		<td><%=rs.getString("tableName")%></td>
	</tr>
	<tr>
		<td style="background-color:#ccc;">别名</td>
		<td><%=rs.getString("alias")%></td>
		<td style="background-color:#ccc;">挂接别名</td>
		<td><%=rs.getString("parentAlias")%></td>
	</tr>
	<tr>
		<td  style="background-color:#ccc;">字段描述</td>
		<td colspan="3">
			<table class="dataTable" style="border:1px solid #ddd">
			<tr>
				<td style="background-color:#ccc;">字段名</td>
				<td style="background-color:#ccc;">字段中文名</td>
				<td style="background-color:#ccc;">字段类型及长度</td>
			</tr>
			<%
				for(String keyName:dataDescContext.getMapSet()){
					String title = dataDescContext.getContext(keyName).getContext("title").getData().toString();
					String fieldLen = dataDescContext.getContext(keyName).getContext("fieldLen").getData().toString();
					if(fieldLen.equals("")){
						fieldLen="256";
					}
			%>
				<tr>
					<td><%=keyName%></td>
					<td><%=title%></</td>
					<td><%=fieldLen%></td>
				</tr>
			<%
				}
			%>
			</table>
		
		</td>
		
	</tr>
	
</table>
<br/>




<%
		
	}
	rs.close();
%>