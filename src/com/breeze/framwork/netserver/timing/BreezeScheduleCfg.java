package com.breeze.framwork.netserver.timing;

import java.io.File;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.support.tools.FileTools;

public class BreezeScheduleCfg {
	private Logger log = Logger.getLogger("com.breeze.framwork.netserver.timing.BreezeScheduleCfg");
	private String serviceName;
	private int time;//这是以前的数据保持兼容，以前写死固定是3表示凌晨3点，那么3表示以前的配置，现在表示配置信道，即配置为0，取第0组数据的意思
	private int idx = 0;
	private int len = 0;
	private BreezeContext ctx = null;
	private BreezeContext param = null;
	
	public void setIdx(int i){
		this.idx = i;
	}
	
	public int length(){
		return this.len;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public int getTime() {
		return time;
	}
	
	public int getYear() {
		return this.getValue(this.ctx.getContext(this.idx).getContext("year"));
	}
	
	public int getMonth() {
		return this.getValue(this.ctx.getContext(this.idx).getContext("month"));
	}
	
	public int getWeek() {
		return this.getValue(this.ctx.getContext(this.idx).getContext("week"));
	}

	public int getDay() {
		return this.getValue(this.ctx.getContext(this.idx).getContext("day"));
	}
	
	public int getHour() {
		return this.getValue(this.ctx.getContext(this.idx).getContext("hour"));
	}
	
	public int getMinute() {
		return this.getValue(this.ctx.getContext(this.idx).getContext("minute"));
	}
	
	public BreezeContext getParam(){
		return this.param;
	}
	
	private int getValue(BreezeContext c){
		if (c == null || c.isNull()){
			return -1;
		}
		try{
			return Integer.parseInt(c.toString());
		}catch(Exception e){
			log.severe("时间配置获取值异常", e);
			return -1;
		}
	}
	
	public String toString(){
		return this.ctx.toString();
	}
	
	public static BreezeScheduleCfg createTiming(File in){
		String content = FileTools.readFile(in, "UTF-8");
		
		BreezeContext settingCtx = ContextTools.getBreezeContext4Json(content);
		BreezeScheduleCfg fc = new BreezeScheduleCfg();
		fc.time = fc.getValue(settingCtx.getContext("time"));
		BreezeContext serviceCtx = settingCtx.getContext("serviceName");
		if (serviceCtx == null || serviceCtx.isNull() || fc.time == -1){
			return null;
		}
		
		BreezeContext ctx = settingCtx.getContext("timeset");
		if (ctx != null && !ctx.isNull() && ctx.getType() == BreezeContext.TYPE_ARRAY){
			fc.len = ctx.getArraySize();
		}
		fc.serviceName = serviceCtx.toString();
		fc.ctx = ctx;
		fc.param = settingCtx.getContext("param");
		
		return fc;
	}
}
