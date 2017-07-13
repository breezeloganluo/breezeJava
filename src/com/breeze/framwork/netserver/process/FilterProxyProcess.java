package com.breeze.framwork.netserver.process;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.framwork.netserver.filter.BreezeFilterBean;
import com.breeze.framwork.netserver.filter.BreezeFilterMgr;
import com.breeze.framwork.servicerg.ServiceTemplate;

public class FilterProxyProcess extends ServerProcess {
	
	private ServerProcess instance;
	
	FilterProxyProcess(ServerProcess inp){
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
		BreezeFilterBean bfilter = BreezeFilterMgr.INSTANCE.getFilter(allServiceName);


		if (bfilter == null){
			this.instance.process(context, st);
			return;
		}
		if (bfilter.getBefore() != null){
			//设置之前先配置一下被filter的service
			root.setContext("_FServiceName", new BreezeContext(allServiceName));
			FunctionInvokePoint.getInc().breezeInvokeUsedAllCtx(bfilter.getBefore(), root);
		}
		this.instance.process(context, st);
		if (bfilter.getAfter() != null){
			root.setContext("_FServiceName", new BreezeContext(allServiceName));
			FunctionInvokePoint.getInc().breezeInvokeUsedAllCtx(bfilter.getAfter(), root);
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
