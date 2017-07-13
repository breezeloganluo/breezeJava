package com.breeze.init;

import java.util.TimerTask;

public interface SchedulerIF {
	public long getPeriod();
	public TimerTask getTask();
}