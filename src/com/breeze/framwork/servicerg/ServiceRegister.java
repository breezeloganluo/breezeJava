//Source file: D:\\my resource\\myproject\\千百汇\\研发\\设计\\com\\qbhui\\framwork\\sqlregister\\ServiceRegister.java
package com.breeze.framwork.servicerg;

import com.breeze.base.log.Logger;
import com.breeze.framwork.netserver.process.ServerProcess;
import com.breeze.framwork.netserver.process.ServerProcessManager;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.tools.DirManager;
import com.breeze.support.tools.GsonTools;
import com.breeze.support.tools.GsonTools.OS;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 2010-08-25进行重构 目标： 1。该类的静态成员全部变成map的动态成员 2。该类的参数解析代码全部外移，到专门的参数解析类中实现
 */
public class ServiceRegister {
	String serviceFileDir;
	String packageName;
	private static Logger log = Logger
			.getLogger("com.breeze.framwork.servicerg.ServiceRegister");
	private Map<String, OS> fieldMap;//
	public static final String ITEMNAME_TEST = "test";

	/**
	 * 使用文本创建一个对象
	 * 
	 * @param text
	 *            配置文本文件内容
	 * @param fileDir
	 *            文件路径
	 * @param packageName
	 *            包名
	 * @return
	 */
	public ServiceRegister(String text, String fileDir, String packageName) {
		this.fieldMap = GsonTools.parserJsonMapStrObj(text);
		this.serviceFileDir = fileDir;
		this.packageName = packageName;
	}

	public static HashMap<String, ServiceTemplate> registerAllServiceByDir(
			String pBaseDir) {
		String baseDir = pBaseDir;
		DirManager dMgr = new DirManager(baseDir);

		HashMap<String, ServiceTemplate> result = new HashMap<String, ServiceTemplate>();

		// 读入磁盘路径
		HashMap<String, ArrayList<File>> dMap = dMgr.getAllFileInPackage(null);

		if (dMap == null) {
			return result;
		}
		for (String packagename : dMap.keySet()) {
			ArrayList<File> dirL = dMap.get(packagename);
			if (dirL != null) {
				for (File f : dirL) {
					try {
						log.severe("passer file:" + f);
						ServiceRegister sreg = ServiceRegister.createByFile(
								new FileInputStream(f), f.getAbsolutePath(),
								packagename);

						if (sreg != null) {
							ServiceTemplate st = sreg.parser2temple();
							if ("".equals(packagename)) {
								result.put(st.getServiceName(), st);
							} else {
								result.put(
										packagename + "." + st.getServiceName(),
										st);
							}
							log.severe("passer file(" + f + ")is ok");
						} else {
							log.severe("passer file(" + f
									+ ")is fail:parser template is null");
						}
					} catch (Exception e) {
						log.severe("passer file "
								+ f
								+ "fail:\n"
								+ com.breeze.support.tools.CommTools
										.getExceptionTrace(e));
					}
				}
			}
		}
		return result;
	}

	/**
	 * 这个函数是创造型函数，用于用输入的输入流创建一个本配置文件类
	 * 
	 * @param in
	 *            一个配置文件的输入流
	 * @return 一个本业务注册类
	 */
	public static ServiceRegister createByFile(InputStream in, String fileDir,
			String packageName) {
		// 按行遍历文件
		String chartset = "UTF-8";
		if (Cfg.getCfg() != null) {
			chartset = Cfg.getCfg().getString("DefalutFileChartset");
		}
		try {
			byte[] buff = new byte[2048000];// 2M
			int len = in.read(buff);
			String s = new String(buff, 0, len, chartset);
			in.close();
			return new ServiceRegister(s, fileDir, packageName);
		} catch (Exception e) {
			log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
			return null;
		}
	}

	public String getContent(String name) {
		return this.fieldMap.get(name).toString();
	}

	public static final String SERVICENAME = "serviceName";
	public static final String FLOWNAME = "flowName";

	public String getServiceName() {
		return this.getContent(SERVICENAME);
	}

	public String getServerName() {
		return this.getContent(FLOWNAME);
	}

	public ServiceTemplate parser2temple() {
		GsonTools.OS serviceNameOS = this.fieldMap.get(SERVICENAME);
		if (serviceNameOS == null) {
			String msg = "the field " + SERVICENAME + " is null";
			throw new RuntimeException(msg);
		}
		String serviceName = serviceNameOS.toString();

		GsonTools.OS processorOS = this.fieldMap.get(FLOWNAME);
		if (processorOS == null) {
			String msg = "the field " + FLOWNAME + " is null";
			throw new RuntimeException(msg);
		}
		String processorName = processorOS.toString();

		// 设置结果对象
		ServiceTemplate result = new ServiceTemplate(serviceName,
				processorName, this.packageName, this.serviceFileDir);
		// 从server名称获取服务unit列表
		ServerProcess.WorkFlowUnitDesc[] wfus = this
				.getProcessorUnit(processorName);
		for (int i = 0; wfus != null && i < wfus.length; i++) {
			ServiceTemplate stmp = result;
			WorkFlowUnit wfu = wfus[i].action;
			String alias = wfus[i].alias;

			// 如果这个单元没有设定特殊的定义域就用基础数据，否则使用这个定义域进行对象解析
			Map<String, OS> anaMap = this.getOsMap();
			OS domain = anaMap.get(alias);
			if (domain != null) {
				String domainStr = domain.toString();
				anaMap = GsonTools.parserJsonMapStrObj(domainStr);
				stmp = result.getSub(alias);
			}

			if (wfu == null) {
				continue;
			}
			// 循环每个unit列表，获取解析对象列表
			TemplateItemParserAbs[] tps = wfu.getProcessParser();
			// 每个解析对象进行解析并完成模板设置
			for (int j = 0; tps != null && j < tps.length; j++) {
				TemplateItemParserAbs tp = tps[j];
				TemplateItemBase sti = tp.parserTempLe(anaMap);
				stmp.setItem(tp.getTempleItemName(), sti);
			}
		}

		return result;
	}

	protected void processorNames() {

	}

	/**
	 * 内部的一个函数，实际也是为方便测试用的
	 * 
	 * @param processorName
	 * @return
	 */
	protected ServerProcess.WorkFlowUnitDesc[] getProcessorUnit(
			String processorName) {
		ServerProcess serverInc = ServerProcessManager.INSTANCE
				.getServer(processorName);
		if (serverInc == null) {
			log.severe("server name not found :" + processorName);
			return null;
		}
		ServerProcess.WorkFlowUnitDesc[] wfus = serverInc.getAllWorkFlowUnit();
		return wfus;
	}

	public Map<String, GsonTools.OS> getOsMap() {
		return this.fieldMap;
	}
}
