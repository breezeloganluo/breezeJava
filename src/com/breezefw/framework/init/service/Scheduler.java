package com.breezefw.framework.init.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.breeze.base.log.Logger;
import com.breeze.init.Initable;
import com.breeze.init.LoadClasses;
import com.breeze.init.SchedulerIF;

public class Scheduler implements Initable {

	private Logger log = Logger.getLogger("com.breezefw.framework.init.service.Scheduler");

	public int getInitOrder() {
		return 10;
	}

	private static class TRun implements Runnable {
		private TimerTask t;
		private Logger log;

		TRun(TimerTask pt, Logger l) {
			this.t = pt;
			this.log = l;
		}

		public void run() {
			try {
				t.run();
			} catch (Exception e) {
				log.severe("one task exception:" + t, e);
			}
		}
	}

	public void doInit(HashMap<String, String> paramMap) {
		ArrayList<SchedulerIF> tList = LoadClasses.createObject("com.breezefw.framework.scheduler", SchedulerIF.class);
		ArrayList<SchedulerIF> tListService = LoadClasses.createObject("com.breezefw.service", SchedulerIF.class);
		tList.addAll(tListService);

		if (tList != null && tList.size() > 0) {
			ScheduledExecutorService t = Executors.newScheduledThreadPool(1);
			for (SchedulerIF ts : tList) {
				long start = 1 * 60 * 1000;
				long period = ts.getPeriod();
				TRun tss = new TRun(ts.getTask(), log);
				if (period > 0) {
					log.severe(ts + " V2 add to timer");
					t.scheduleAtFixedRate(tss, start, ts.getPeriod(), TimeUnit.MILLISECONDS);
				} else {
					t.schedule(tss, start, TimeUnit.MILLISECONDS);
				}
			}
		}

	}

	public String getInitName() {
		return "Scheduler";
	}

}
