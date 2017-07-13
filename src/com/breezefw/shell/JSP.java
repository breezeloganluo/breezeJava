package com.breezefw.shell;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.databus.ContextTools;
import com.breeze.framwork.netserver.FunctionInvokePoint;
import com.breeze.support.tools.FileTools;

import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * 这是一个用于jsp的辅助类，可以帮组在jsp中打印日志，等操作。不错的哦
 * 
 * @author 罗光瑜
 * 
 */
public class JSP {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String url;
	private static Logger log = Logger.getLogger("com.breezefw.shell");
	private String threadSignal = null;

	private static Object gLock = new Object();
	private static HashMap<String, Object> serviceLock = new HashMap<String, Object>();
	private static BreezeContext cacheCtx = new BreezeContext();
	
	private BreezeContext allResult = new BreezeContext();
	
	private BreezeContext cycleData;
	private int cycleIdx;
	private int cycleLen;
	
	/**
	 * 返回所有的结果，这个函数，会将所有的结果，全部放到allResult中
	 * @return
	 */
	public BreezeContext getResult(){
		return this.allResult;
	}
	
	
	
	public int getCode(String domain){
		BreezeContext codeCtx = this.allResult.getContextByPath("code."+domain);
		if (codeCtx == null || codeCtx.isNull()){
			
		}
		return Integer.parseInt(codeCtx.toString());
	}
	
	
	/**
	 * 构造函数
	 * @param req jsp页面的reqeust对象
	 * @param rsp jsp页面的reqeust对象
	 */
	public JSP(HttpServletRequest req, HttpServletResponse rsp) {
		this.request = req;
		this.response = rsp;
		if (req  == null){
			return;
		}
		this.url = req.getRequestURL().toString();
		

		threadSignal = this.request.getParameter("threadSignal");
		if (threadSignal != null) {
			log.setTreadSignal(threadSignal);
		}
	}

	/**
	 * 不带缓存调用，返回的是调用的结果BreezeContext
	 * 同时会将结果设置到全局的结果中，可以通过WHILE和getxxx函数通过访问设置的resultDomain进行路径访问
	 * resultDomain会将结果设置到这个域中，通过el表达式可以访问
	 * @param serviceName 要调用的serviceName
	 * @param resultDomain 结果的domain域，结果都记录到这里
	 * @return
	 */
	public BreezeContext call(String serviceName, String resultDomain) {
		BreezeContext resultCtx = FunctionInvokePoint.getInc()
				.breezeInvokeUseRequestAsParam(serviceName, this.request,
						this.response);
		if (resultCtx == null) {
			BreezeContext errCtx = new BreezeContext();
			errCtx.setContext("code", new BreezeContext(999));
			return errCtx;
		}
		BreezeContext data = resultCtx.getContext("data");
		if (data != null) {
			if (data.getType() == BreezeContext.TYPE_ARRAY) {
				request.setAttribute(resultDomain, data.createList());
			} else if (data.getType() == BreezeContext.TYPE_MAP) {
				request.setAttribute(resultDomain, data.createMap());
			}
		}
		//复制结果
		this.allResult.setContext(resultDomain, data);
		this.allResult.setContextByPath("code."+resultDomain, resultCtx.getContext("code"));
		//2014-10-09罗光瑜修改，返回的结果集过滤一下，只保留和data和dcode，方便获取结果后的处理
		BreezeContext returnResult = new BreezeContext();
		returnResult.setContext("data",data);
		returnResult.setContext("code",resultCtx.getContext("code"));
		return returnResult;
	}

	/**
	 * 带缓存的call方法，每次请求都会把结果放入缓存，并记入当前时间戳，下次调用检查该缓存是否过期，过期则发送真正请求。
	 * cache的key值，以serviceName和cacheTime为标识
	 * @param serviceName 
	 * @param resultDomain
	 * @param cacheTime
	 * @return
	 */
	public BreezeContext call(String serviceName, String resultDomain,
			long cacheTime) {
		long now = System.currentTimeMillis();
		String sig = serviceName + cacheTime;
		// 缓存的地址格式为{sig:{regTime:xxx,result:{xxx}}}
		Object lock = null;
		// 获取锁
		synchronized (gLock) {
			lock = serviceLock.get(sig);
			if (lock == null) {
				lock = new Object();
				serviceLock.put(sig, lock);
			}
		}
		synchronized (lock) {
			try {
				BreezeContext one = cacheCtx.getContext(sig);
				if (one != null && !one.isNull()) {
					BreezeContext regTimeCtx = one.getContext("regTime");
					long regTime = (Long)regTimeCtx.getData();
					//if (缓存时间未到){直接返回
					if (regTime+cacheTime > now){
						BreezeContext resultCtx = one.getContext("result");
						BreezeContext data = resultCtx.getContext("data");
						if (data != null) {
							if (data.getType() == BreezeContext.TYPE_ARRAY) {
								request.setAttribute(resultDomain, data.createList());
							} else if (data.getType() == BreezeContext.TYPE_MAP) {
								request.setAttribute(resultDomain, data.createMap());
							}
						}
						//复制结果
						this.allResult.setContext(resultDomain, data);
						this.allResult.setContextByPath("code."+resultDomain, resultCtx.getContext("code"));
						return resultCtx;
					}
					//}
				}
			} catch (Exception e) {
				//清除掉缓存
				cacheCtx.setContext(sig, null);
			}
			//重新获取
			BreezeContext returnCtx = this.call(serviceName, resultDomain);
			//加入缓存
			BreezeContext resultCtx = new BreezeContext();
			int code = Integer.parseInt(returnCtx.getContext("code").toString());
			if (code <10 && code >=0){
				//成功才记录缓存
				resultCtx.setContext("code", new BreezeContext(code));
				resultCtx.setContext("data", returnCtx.getContext("data"));
				BreezeContext oneCacheCtx = new BreezeContext();
				oneCacheCtx.setContext("result", resultCtx);
				oneCacheCtx.setContext("regTime", new BreezeContext(now));
				cacheCtx.setContext(sig, oneCacheCtx);
			}
			return resultCtx;
		}
	}
	/**
	 * 循环函数，这个函数暂时不支持嵌套
	 * 使用方法：
	 * while(JSP.WHILE(path)){
	 *   循环的对象放入到d中，索引放入到i中
	 * }
	 * @param path
	 */
	public boolean WHILE (String path){
		if (this.cycleData == null){
			this.cycleData = this.allResult.getContextByPath(path);
			if (this.cycleData.getType() != BreezeContext.TYPE_ARRAY){
				log.fine("data path："+path +" is not array");
				return false;
			}
			this.cycleIdx = -1;
			this.cycleLen = this.cycleData.getArraySize();
			request.setAttribute("l", this.cycleLen);
		}
		this.cycleIdx ++;
		if (cycleIdx >= this.cycleData.getArraySize()){
			this.cycleData = null;
			return false;
		}
		BreezeContext nowData = this.cycleData.getContext(this.cycleIdx);
		//将数据放入
		request.setAttribute("i", this.cycleIdx);
		if (nowData.getType() == BreezeContext.TYPE_MAP){
			request.setAttribute("d", nowData.createMap());
		}
		else if (nowData.getType() == BreezeContext.TYPE_ARRAY){
			request.setAttribute("d", nowData.createList());
		}else{
			request.setAttribute("d", nowData.getData());
		}
		return true;
		
	}
	
	/**
	 * 获取当前循环的索引
	 * @return 当前循环的索引
	 */
	public int wgetI(){
		return this.cycleIdx;
	}
	
	/**
	 * 获取当前的循环的长度
	 * @return 获取当前循环的长度
	 */
	public int wgetL(){
		return this.cycleLen;
	}
	
	/**
	 * 针对有些返回的结果，其值是一个大的json字符串（如cms设置的图片列表），这些值要进行处理和转换很麻烦，提供该方法
	 * 对该类型值进行转换，转换后的值，将被保留到全局中。后续将会被当作转换后的值处理
	 * 注意该方法值用于WHILE中
	 * @param path 以WHILE为根的该值路径
	 */
	public void wevalJson(String path){
		if (this.cycleData == null){
			return ;
		}
		BreezeContext nowData = this.cycleData.getContext(this.cycleIdx);
		if (nowData == null){
			return ;
		}
		
		BreezeContext evalData = nowData.getContextByPath(path);
		if (evalData == null){
			return;
		}
		
		String evalDataStr = evalData.toString();
		BreezeContext evalDataCtx = ContextTools.getBreezeContext4Json(evalDataStr);
		if (evalDataCtx == null){
			return ;
		}
		
		nowData.setContextByPath(path, evalDataCtx);
		//重新设置，因为map和arra可能要重新获取
		if (nowData.getType() == BreezeContext.TYPE_MAP){
			request.setAttribute("d", nowData.createMap());
		}
		else if (nowData.getType() == BreezeContext.TYPE_ARRAY){
			request.setAttribute("d", nowData.createList());
		}else{
			request.setAttribute("d", nowData.getData());
		}
	}
	
	/**
	 * 以循环WHILE当前值为根获取下面的整形值
	 * @param path 以WHILE当前值为根的访问值
	 * @param dVal 该值不存在的默认值
	 * @return 对应访问路径的值
	 */
	public int wgetInt(String path,int dVal){
		if (this.cycleData == null){
			return dVal;
		}
		BreezeContext nowData = this.cycleData.getContext(this.cycleIdx);
		if (nowData == null){
			return dVal;
		}
		
		BreezeContext resultCtx = nowData.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		int result = Integer.parseInt(resultCtx.toString());
		return result;
	}
	
	/**
	 * 以循环WHILE当前值为根获取下面的整形值
	 * @param path 以WHILE当前值为根的访问值
	 * @param dVal 该值不存在的默认值
	 * @return 对应访问路径的值
	 */
	public long wgetLong(String path,long dVal){
		if (this.cycleData == null){
			return dVal;
		}
		BreezeContext nowData = this.cycleData.getContext(this.cycleIdx);
		if (nowData == null){
			return dVal;
		}
		
		BreezeContext resultCtx = nowData.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		long result = Long.parseLong(resultCtx.toString());
		return result;
	}
	
	
	/**
	 * 以循环WHILE当前值为根获取下面的dobule值
	 * @param path 以WHILE当前值为根的访问值
	 * @param dVal 该值不存在的默认值
	 * @return 对应访问路径的值
	 */
	public double wgetDouble(String path,double dVal){
		if (this.cycleData == null){
			return dVal;
		}
		BreezeContext nowData = this.cycleData.getContext(this.cycleIdx);
		if (nowData == null){
			return dVal;
		}
		
		BreezeContext resultCtx = nowData.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		double result = Double.parseDouble(resultCtx.toString());
		return result;
	}
	
	/**
	 * 以循环WHILE当前值为根获取下面的字符串值
	 * @param path 以WHILE当前值为根的访问值
	 * @param dVal 该值不存在的默认值
	 * @return 对应访问路径的值
	 */
	public String wgetString(String path,String dVal){
		if (this.cycleData == null){
			return dVal;
		}
		BreezeContext nowData = this.cycleData.getContext(this.cycleIdx);
		if (nowData == null){
			return dVal;
		}
		
		BreezeContext resultCtx = nowData.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		String result = resultCtx.toString();
		return result;
	}
	
	/**
	 * 获取以path为路径的整形数据，这个路径是全局的，之前的调用结果的data放入到对应的域中
	 * @param path 访问路径
	 * @param dVal 该值不存在的时候的默认值
	 * @return 返回对应的值
	 */
	public int getInt(String path,int dVal){
		BreezeContext resultCtx = this.allResult.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		int result = Integer.parseInt(resultCtx.toString());
		return result;
	}
	
	/**
	 * 获取以path为路径的长整形数据，这个路径是全局的，之前的调用结果的data放入到对应的域中
	 * @param path 访问路径
	 * @param dVal 该值不存在的时候的默认值
	 * @return 返回对应的值
	 */
	public long getLong(String path,long dVal){
		BreezeContext resultCtx = this.allResult.getContextByPath(path);
		if (resultCtx == null){
			throw new RuntimeException(path + " has no value");
		}
		long result = Long.parseLong(resultCtx.toString());
		return result;
	}
	
	public double getDouble (String path,double dVal){
		BreezeContext resultCtx = this.allResult.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		double result = Double.parseDouble(resultCtx.toString());
		return result;
	}
	
	/**
	 * 获取以path为路径的字符串数据，这个路径是全局的，之前的调用结果的data放入到对应的域中
	 * @param path 访问路径
	 * @param dVal 该值不存在的时候的默认值
	 * @return
	 */
	public String getString (String path,String dVal){
		BreezeContext resultCtx = this.allResult.getContextByPath(path);
		if (resultCtx == null){
			log.fine(path + " has no value");
			return dVal;
		}
		
		return resultCtx.toString();
	}
	

	/**
	 * 对外提供的日志接口，注意，接口的日志级别只打开了fine类型
	 * 同样，在threadSignal中才能把日志打开
	 * @param msg
	 */
	public void log(String msg) {
		log.fine(msg);
	}

	public void close() {
		log.removeThreadSignal();
	}
	
	
	
	/**
	 * 加载js，通过这个方法，可以加载一个js文件到指定的域里面，后续可以使用这个域名进行js调用
	 * 注意，本方法还在完善中。。。
	 * @param dir js所在路径，是web路径
	 * @param domain 域标识
	 */
	public void importJs(String dir,String domain){
		
	}
	
	/**
	 * 调用某个域内的js函数函数，并返回其结果
	 * 注意：在java中调用js实际能力是很受限制的，某些实际的脚步对象，这是不起作用的。
	 * 比如调用alert，dom节点操作，jquery等，这些都不会被支持
	 * 这里只适合做简单的脚步调用
	 * 注意：本方法还在完善中
	 * @param domain 对应域，即某个js文件的别名
	 * @param method 这个域的某个方法
	 * @param p 调用的参数信息
	 * @return js的返回结果
	 */
	public Object callJS(String domain,String method,Object... p){
		return null;
	}
	
	public static void main(String[] args) throws ScriptException, NoSuchMethodException{
		String fileContent = FileTools.readFile("C:\\work\\WGFramework\\framework\\breeze_core\\myScript.js", "UTF-8");		
		
		ScriptEngineManager factory = new ScriptEngineManager();		
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		engine.eval(fileContent);
		
		Invocable inv = (Invocable)engine;
		Object re = inv.invokeFunction("say", "你妹");
		System.out.println(re);
	}
}
