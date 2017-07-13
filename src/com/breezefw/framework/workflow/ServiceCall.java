package com.breezefw.framework.workflow;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breezefw.framework.template.ServiceCallItem;

public class ServiceCall extends WorkFlowUnit implements Runnable {

	private final static String SINGLEITEMNAME = "ServiceCall";
	private static final Logger log = Logger.getLogger("com.brezefw.framework.workflow.ServiceCall");

	@Override
	public String getName() {
		return SINGLEITEMNAME;
	}

	@Override
	protected void loadingInit() {
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {
		TemplateItemParserAbs resultAbs = new CommTemplateItemParser(SINGLEITEMNAME, ServiceCallItem.class);
		TemplateItemParserAbs tp = resultAbs;
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] { tp };
		return result;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias, int lastResult) {
		// 获取根上下文信息
		BreezeContext root = context;
		try {
			if (log.isLoggable(Level.FINE)) {
				log.fine("go Process [" + this.getName() + "]lastResult[" + lastResult + "]");
				for (String key : root.getMapSet()) {
					if (key.startsWith("_")) {
						continue;
					}
					log.fine("root Ctx[" + key + "]" + root.getContext(key));
				}
			}

			ServiceCallItem item = (ServiceCallItem) this.getItem(root, st, SINGLEITEMNAME);

			// 获取调用的业务名称
			String serviceName = item.getServiceName();
			if (serviceName != null && !"".equals(serviceName) && "1".equals(item.getServiceNameType())) {
				BreezeContext serviceNameCtx = root.getContextByPath(serviceName);
				if (serviceNameCtx != null && !serviceNameCtx.isNull()
						&& serviceNameCtx.getType() == BreezeContext.TYPE_DATA) {
					serviceName = serviceNameCtx.toString();
				} else {
					if (log.isLoggable(Level.FINE)) {
						log.severe("could not find service in path:" + item.getServiceName() + "(code "
								+ item.getServiceNameNoFoundResult() + " will return)");
					}
					return item.getServiceNameNoFoundResult();
				}
			}
			log.fine("call serviceName is:" + serviceName);
			if (serviceName == null || "".equals(serviceName)) {
				if (log.isLoggable(Level.FINE)) {
					log.severe("could not find service [:" + serviceName + "](code "
							+ item.getServiceNameNoFoundResult() + " will return)");
				}
				return item.getServiceNameNoFoundResult();
			}
			// 重新设定系统的servicename
			String oldService = context.getContext("_ServiceName").toString();
			BreezeContext _serviceNameHistory = context.getContext("_ServiceNameHistory");
			if (_serviceNameHistory == null) {
				_serviceNameHistory = new BreezeContext(oldService);
			} else {
				String nStr = _serviceNameHistory.toString() + "|" + oldService;
				_serviceNameHistory = new BreezeContext(nStr);
			}
			context.setContext("_ServiceNameHistory", _serviceNameHistory);
			context.setContext("_ServiceName", new BreezeContext(serviceName));
			// 设置权限标志位，这样后面被调用的serviceCall将不会启动权限操作
			context.setContext("ServiceCall", new BreezeContext(serviceName));
			// 根据不同的配置，确定是否要新建context
			BreezeContext callCtx = new BreezeContext();
			String callMode = item.getCallMode();
			if (callMode == null || "".equals(callMode.trim())) {
				callMode = "sync";
			}
			if ("sync".equals(callMode)) {
				callCtx = context;
			} else {
				callCtx = new BreezeContext();
				callCtx.setContext("_O", context);
				// 复制所有_开头的的上下文
				for (String n : context.getMapSet()) {
					if (n.startsWith("_")) {
						callCtx.setContext(n, context.getContext(n));
					}
				}
			}
			// 调用函数进行业务权限调用
			if ("async".equals(callMode)) {
				log.fine("call for async:");
				ServiceCall run = new ServiceCall();
				run.runservicename = serviceName;
				run.runcallCtx = callCtx;
				Thread t = new Thread(run);
				t.start();
				return 0;
			}
			FunctionInvokePoint.getInc().breezeInvokeUsedAllCtx(serviceName, callCtx);
			BreezeContext resultCtx = callCtx.getContext("code");
			log.fine("resultCtx:" + resultCtx);
			if (resultCtx == null) {
				return 998;
			} else {
				return (Integer) resultCtx.getData();
			}
		} catch (Exception e) {
			return 999;
		} finally {

		}
	}

	private String runservicename;
	private BreezeContext runcallCtx;

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
