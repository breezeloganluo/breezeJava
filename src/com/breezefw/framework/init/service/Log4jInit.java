package com.breezefw.framework.init.service;

import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;



import com.breeze.init.Initable;
import com.breeze.support.cfg.Cfg;

public class Log4jInit implements Initable {
	public static String logDir = null;

	public Log4jInit() {

	}


	
	public int getInitOrder() {
		return 1;
	}

	
	public void doInit(HashMap<String, String> paramMap) {
		try {
			logDir = Cfg.getCfg().getRootDir() + "WEB-INF/breeze.log";
			com.breeze.base.log.log4j.Log4jLogger.init();
			PatternLayout pat = new PatternLayout(
					"%-d{yyyy-MM-dd HH:mm:ss} [%c]-[%p]%m%n");

			RollingFileAppender fapp = new RollingFileAppender(pat, logDir,
					true);
			fapp.setMaxBackupIndex(10);
			fapp.setMaxFileSize("10MB");
			fapp.setEncoding("UTF-8");

			Logger.getRootLogger().removeAllAppenders();
			Logger.getLogger("com").addAppender(fapp);

			Logger.getRootLogger().setLevel(Level.FATAL);

			/**不打屏了，以后大家看日志
			ConsoleAppender conPat = new ConsoleAppender(pat);
			Logger.getLogger("com.breeze").addAppender(conPat);
			Logger.getLogger("com.weiguang").addAppender(conPat);
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public String getInitName() {
		return "log system";
	}
}
