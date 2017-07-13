package com.breeze.framwork.netserver.process;

import java.sql.SQLException;



import com.breeze.base.db.COMMDB;
import com.breeze.base.db.TransDBOper;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.dbtransprocess.BreezeDBTransProcessMgr;
import com.breeze.framwork.servicerg.ServiceTemplate;

public class TransProxyProcess extends ServerProcess {
private ServerProcess instance;
	private Logger log = Logger.getLogger("com.breeze.framwork.netserver.process.TransProxyProcess");
TransProxyProcess(ServerProcess inp){
		this.instance = inp;
	}
	@Override
	public void process(BreezeContext context, ServiceTemplate st) {
		BreezeContext root = context;
		String allServiceName = st.getServiceName();
		String pkName = st.getPackageName();
		if (pkName !=null && !"".equals(pkName)){
			allServiceName = pkName+'.'+st.getServiceName();
		}
		if (!BreezeDBTransProcessMgr.INSTANCE.hasTrans(allServiceName)){
			this.instance.process(context, st);
			return;
		}
		TransDBOper trans = COMMDB.getDBTrances();
		if (trans == null){
			this.instance.process(context, st);
			return;
		}
		trans.setThreadTrans();
		this.instance.process(root, st);
		//假象code是系统的默认，如果code为0到9就是成功，否则失败
		boolean isCommit = true;
		BreezeContext codeCtx = root.getContext("code");
		if (codeCtx != null){
			int code = (Integer)codeCtx.getData();
			if (code <0 || code >9){
				isCommit = false;
			}
		}
		try {
			TransDBOper.closeThreadTrans(isCommit);
		} catch (SQLException e) {
			log.severe("", e);
		}
	}

	@Override
	public WorkFlowUnitDesc[] getAllWorkFlowUnit() {		
		return this.instance.getAllWorkFlowUnit();
	}

	@Override
	public String getProcessName() {
		return this.instance.getProcessName();
	}

}
