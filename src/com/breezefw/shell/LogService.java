package com.breezefw.shell;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.tools.FileTools;

/**
 * 这个类就是要把客户端发送的消息转换成Json的格式 并记录到目模拟数据中，作为一个模拟数据存在
 * 
 * @author Logan
 *
 */
public class LogService {
	private static Logger log = Logger.getLogger("com.breezefw.shell.LogService");
	private static Object lock = new Object();


	public static void log(String service, String pkg, String scene, BreezeContext root) {
		File f = new File(Cfg.getCfg().getRootDir() + "/manager_auxiliary/data/service/" + pkg + '/' + service + '/'
				+ scene + ".brr");

		try {
			
			String logcontent = ContextTools.getJsonString(root, new String[] { "_R", "code", "data" });
			FileTools.writeFile(f, logcontent, "UTF-8");

			BreezeContext requestCtx = root.getContext("_Req");
			HttpServletRequest request = (HttpServletRequest) requestCtx.getData();
			String url = request.getHeader("REFERER");
			url = url.replaceAll("http[s]?://[^\\/]+/", "");
			url = url.replaceAll("[\\/\\.]", "_");
		
		} catch (Exception e) {
			log.severe("记录日志失败", e);
			log.severe("_R" + root.getContext("_R"));
			log.severe("code" + root.getContext("code"));
			log.severe("data" + root.getContext("data"));
			FileTools.writeFile(f, "$NULl", "UTF-8");
		}
	}
	
}
