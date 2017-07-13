package com.breezefw.framework.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.framwork.netserver.timing.BreezeScheduleCfg;
import com.breeze.framwork.netserver.timing.BreezeScheduleCfgMgr;
import com.breeze.framwork.netserver.tool.ContextMgr;
import com.breeze.init.SchedulerIF;

/**
 * 这是新的事件调度器，其思路是精度一分钟，误差一分钟，每次执行记录时间，误差一分钟类上次没有执行的，就执行
 * 
 * @author Logan
 *
 */
public class ServiceCallScheduler extends TimerTask implements SchedulerIF {
	private Logger log = Logger.getLogger("com.breezefw.framework.scheduler.ServiceCallScheduler");
	public static int total = 0;
	public static int count = 0;
	public static int lastDay = 0;
	public static int errorCount = 0;

	@Override
	public long getPeriod() {
		// 一分钟执行一次
		return 60 * 1000;
	}

	@Override
	public TimerTask getTask() {
		return this;
	}

	@Override
	public void run() {
		try {
			Calendar curd = Calendar.getInstance();
			int day = curd.get(Calendar.DAY_OF_MONTH);
			if (lastDay != day) {
				lastDay = day;
				count = 0;
				synchronized (ContextMgr.global) {
					BreezeContext last = ContextMgr.global.getContextByPath("scheduler.dayHistory");
					ContextMgr.global.setContextByPath("scheduler.yesterdayHistory", last);
					ContextMgr.global.setContextByPath("scheduler.dayHistory", new BreezeContext());
				}

			}
			count++;
			total++;
			synchronized (ContextMgr.global) {
				ContextMgr.global.setContextByPath("scheduler.static.day", new BreezeContext(lastDay));
				ContextMgr.global.setContextByPath("scheduler.static.count", new BreezeContext(count));
				ContextMgr.global.setContextByPath("scheduler.static.total", new BreezeContext(total));
			}

			ArrayList<BreezeScheduleCfg> serviceArr = BreezeScheduleCfgMgr.INSTANCE.getTiming(0);

			BreezeContext all = null;
			synchronized (ContextMgr.global) {
				all = ContextMgr.global.getContextByPath("scheduler.all");
			}

			if (all == null) {
				all = new BreezeContext();
				synchronized (ContextMgr.global) {
					ContextMgr.global.setContextByPath("scheduler.all", all);
				}

				for (int i = 0; serviceArr != null && i < serviceArr.size(); i++) {
					BreezeScheduleCfg one = serviceArr.get(i);
					String oneService = one.getServiceName() + "(" + one.getParam() + ")";
					all.pushContext(new BreezeContext(oneService));
				}

			}
			executeAllTask(serviceArr);

		} catch (Exception e) {
			log.severe("定时器异常", e);
			errorCount++;
			synchronized (ContextMgr.global) {
				ContextMgr.global.setContextByPath("scheduler.static.err", new BreezeContext(errorCount));
			}

		} catch (Throwable t) {
			log.severe(t.toString());
			errorCount++;
			synchronized (ContextMgr.global) {
				ContextMgr.global.setContextByPath("scheduler.static.err", new BreezeContext(errorCount));
			}

		}
	}

	private void executeAllTask(ArrayList<BreezeScheduleCfg> serviceArr) {
		// 获取当前时间单位秒
		long curTime = System.currentTimeMillis();
		for (int i = 0; serviceArr != null && i < serviceArr.size(); i++) {
			BreezeScheduleCfg one = serviceArr.get(i);

			if (isGo(curTime, one, i)) {
				this.executeOne(one, i);
			}
		}
	}

	/**
	 * 这是一个线程类，内部类，用于执行某个service的
	 * 
	 * @author Logan
	 *
	 */
	private static class RunService extends Thread {
		BreezeScheduleCfg bs;

		RunService(BreezeScheduleCfg one) {
			this.bs = one;
		}

		public void run() {
			BreezeContext param = bs.getParam();
			if (param == null) {
				new BreezeContext();
			}
			FunctionInvokePoint.getInc().breezeInvokeUsedCtxAsParam(bs.getServiceName(), param);
		}
	}

	/**
	 * 具体的执行某一个业务，注意每次启动要单独一个独立线程
	 * 
	 * @param one
	 *            要执行的当前信息
	 */
	private void executeOne(BreezeScheduleCfg one, int idx) {
		RunService s = new RunService(one);
		s.start();
		synchronized (ContextMgr.global) {

			BreezeContext schedulerCtx = ContextMgr.global.getContextByPath("scheduler.lastExec.idx" + idx);
			if (schedulerCtx == null) {
				schedulerCtx = new BreezeContext();
				ContextMgr.global.setContextByPath("scheduler.lastExec.idx" + idx, schedulerCtx);
			}
			// 记录上次执行，便于判断是否要重复执行
			long now = System.currentTimeMillis();
			schedulerCtx.setContext("name", new BreezeContext(one.getServiceName()));
			schedulerCtx.setContext("lasttime", new BreezeContext(now));
			schedulerCtx.setContext("param", one.getParam());
			Calendar curd = new GregorianCalendar();
			curd.setTimeInMillis(now);
			String time = String.valueOf(curd.get(Calendar.HOUR_OF_DAY)) + ':'
					+ String.valueOf(curd.get(Calendar.MINUTE));
			schedulerCtx.setContext("time", new BreezeContext(time));
			// 将执行信息记录到当天的记录中
			BreezeContext curDayHistory = ContextMgr.global.getContextByPath("scheduler.dayHistory");
			if (curDayHistory == null) {
				curDayHistory = new BreezeContext();
				ContextMgr.global.setContextByPath("scheduler.dayHistory", curDayHistory);
			}
			BreezeContext historyOne = new BreezeContext();
			historyOne.setContext("name", new BreezeContext(one.getServiceName()));
			historyOne.setContext("param", one.getParam());
			historyOne.setContext("time", new BreezeContext(time));
			curDayHistory.pushContext(schedulerCtx);
		}
	}

	/**
	 * 判断当前时间是否允许执行这个service
	 * 按照误差一分钟来计算，每次判断当前分钟和上一分钟，当然也要从已执行列表中获取上次执行时间和当前不要差距超过90秒
	 * 注意，全局变量的记录方式为"scheduler.lastExec.idx[n]"={ service:xxx, lasttime:xxx
	 * param:xxx }
	 * 
	 * @param curr
	 *            当前时间的时间戳
	 * @param s
	 *            当前被执行判断的业务
	 * @return true表示可以执行，否则不可执行
	 */
	private boolean isGo(long curr, BreezeScheduleCfg s, int idx) {
		// 向前误差一分钟，也是允许的
		long pre = curr - 60 * 1000;
		Calendar curd = new GregorianCalendar();
		curd.setTimeInMillis(curr);

		Calendar pred = new GregorianCalendar();
		pred.setTimeInMillis(pre);

		// 获取上一次的执行时间
		long lastTime = -1;
		BreezeContext lasttimeCtx = null;
		synchronized (ContextMgr.global) {
			lasttimeCtx = ContextMgr.global.getContextByPath("scheduler.lastExec.idx" + idx + ".lasttime");
		}
		if (lasttimeCtx != null && !lasttimeCtx.isNull()) {
			lastTime = Long.parseLong(lasttimeCtx.toString());
		}

		// 判断上一次如果已经执行过，那么就返回不执行
		if (lastTime != -1 && lastTime + 90 * 1000 > curr) {
			return false;
		}
		// 如果符合条件，当前分钟和向前偏差的一分钟能其中的一个满足，那么就返回可执行
		for (int i = 0; i < s.length(); i++) {
			s.setIdx(i);
			if (((-1 == s.getYear() || curd.get(Calendar.YEAR) == s.getYear())
					&& (-1 == s.getMonth() || curd.get(Calendar.MONTH) + 1 == s.getMonth())
					&& (-1 == s.getDay() || curd.get(Calendar.DAY_OF_MONTH) == s.getDay())
					&& (-1 == s.getWeek() || curd.get(Calendar.DAY_OF_WEEK) - 1 == s.getWeek())
					&& (-1 == s.getHour() || curd.get(Calendar.HOUR_OF_DAY) == s.getHour())
					&& (-1 == s.getMinute() || curd.get(Calendar.MINUTE) == s.getMinute()))
					|| ((-1 == s.getYear() || pred.get(Calendar.YEAR) == s.getYear())
							&& (-1 == s.getMonth() || pred.get(Calendar.MONTH) + 1 == s.getMonth())
							&& (-1 == s.getDay() || pred.get(Calendar.DAY_OF_MONTH) == s.getDay())
							&& (-1 == s.getWeek() || pred.get(Calendar.DAY_OF_WEEK) - 1 == s.getWeek())
							&& (-1 == s.getHour() || pred.get(Calendar.HOUR_OF_DAY) == s.getHour())
							&& (-1 == s.getMinute() || pred.get(Calendar.MINUTE) == s.getMinute()))) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return "ServiceCallScheduler";
	}

}
