/*
 * 这是一个上下文类，后面所有操作，都是围绕上下文进行处理
 * 每个操作流程都可以从上下文中获取自己需要的信息，也可以将自己处理结果写入到上下文中
 */
package com.breeze.framwork.databus;

import com.breeze.base.log.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 * @version 0.10 罗光瑜 这个版本追加了代理模式
 */
public class BreezeContext implements InvocationHandler, Serializable {

	/**
	 * 对象类型常量：数据类型
	 */
	public static final int TYPE_DATA = 0;
	/**
	 * 对象类型常量：map类型
	 */
	public static final int TYPE_MAP = 1;
	/**
	 * 对象类型常量：array类型
	 */
	public static final int TYPE_ARRAY = 2;
	private static final Logger log = Logger
			.getLogger("com.breeze.framwork.databus.BreezeContext");
	private int type = -1;// Context类型,0最终原子对象，1Map，2Array
	private HashMap<String, BreezeContext> memberMap = null;
	private ArrayList<BreezeContext> memberArray = null;
	private Object data = null;
	private boolean isEmpty = true;// 是否是空对象

	public BreezeContext() {
	}

	public BreezeContext(Object pdata) {
		if (pdata != null) {
			this.type = BreezeContext.TYPE_DATA;
		}
		this.data = pdata;
		this.isEmpty = false;
	}

	public int getType() {
		return this.type;
	}

	/**
	 * 一个静态方法用于根据路径获取最终的节点对象<br>
	 * 这里使用普通对象访问路径方式，不包含root本身，从传入的root对象下面的路径开始访问<br>
	 * 例如：a.b[3].c
	 *
	 * @param path
	 *            要返回的上下文路径
	 * @param root
	 *            要访问的上下文对象，就是从该对象下，的path路径开始访问
	 */
	public static BreezeContext getObjectByPath(String path, BreezeContext root) {
		if (path == null) {
			log.severe("the path is null!!!!");
		}
		String[] names = path.split("\\.");
		BreezeContext result = root;
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			if (result == null) {
				return null;
			}
			// 这里要处理数组情况
			// 2014-04-02 罗光瑜修改 要支持多个中括号的情况
			Pattern p = Pattern.compile("([^\\[\\]]+)(\\[.+?\\]$)");
			Matcher m = p.matcher(name);
			if (m.find()) {
				// 表示是数组
				result = result.getContext(m.group(1));
				if (result == null) {
					return null;
				}
				// 用第二个数据继续找所有的中括号
				String kuohao = m.group(2);
				p = Pattern.compile("\\[(\\d+)\\]");
				m = p.matcher(kuohao);
				while (m.find()) {
					result = result.getContext(Integer.parseInt(m.group(1)));
				}
			} else {
				result = result.getContext(name);
			}
		}
		return result;
	}

	/**
	 * 一个静态方法用于在对应的路径上设置一个最终的节点对象<br>
	 * 这里使用普通对象访问路径方式，不包含root本身，从传入的root对象下面的路径开始访问<br>
	 * 例如：a.b[3].c<br>
	 * 这里对数组的设置比较特殊。如果在路径中写明数组的下标值，如a[3], 那么，会首先获取该下标对应的Context进行设置，如果该下标
	 * 不存在，则会添加失败，抛出异常<br>
	 * 另外，数组的另外一种标识方式如:a[]表示就要在该下标下增加一个元素
	 *
	 * @param path
	 *            路径
	 * @param value
	 *            要设置的值
	 * @param root
	 *            根对象
	 */
	public static void setObjectByPath(String path, BreezeContext value,
			BreezeContext root) {
		String[] names = path.split("\\.");
		BreezeContext destObj = root;
		// 要判断是否有数组情况
		Pattern p = Pattern.compile("([^\\[\\]]+)\\[(\\d*)\\]$");
		for (int i = 0; i < names.length - 1; i++) {
			String name = names[i];
			BreezeContext tmpObj = null;
			Matcher m = p.matcher(name);
			if (m.find()) {
				// 表示是数组
				tmpObj = destObj.getContext(m.group(1));
				// 先判断数组本身是否被创建
				if (tmpObj == null) {
					tmpObj = new BreezeContext();
					destObj.setContext(m.group(1), tmpObj);
				}
				destObj = tmpObj;
				// 下面再处理数组的下标成员
				if ("".equals(m.group(2))) {
					// []情况，表示要新增加一个
					tmpObj = new BreezeContext();
					destObj.pushContext(tmpObj);
				} else {
					int idx = Integer.parseInt(m.group(2));
					tmpObj = destObj.getContext(idx);
					if (tmpObj == null) {
						// 没有对应的下标的context，抛出异常
						throw new RuntimeException("idx not in array:" + name);
					}
				}
			} else {
				tmpObj = destObj.getContext(name);
				if (tmpObj == null) {
					if (tmpObj == null) {
						tmpObj = new BreezeContext();
						destObj.setContext(name, tmpObj);
					}
				}
			}
			destObj = tmpObj;
		}
		// 这时处理最后一级,这里也是要判断数组情况
		String lastName = names[names.length - 1];
		Matcher m = p.matcher(lastName);
		if (m.find()) {
			BreezeContext tmp = destObj.getContext(m.group(1));
			if (tmp == null) {
				tmp = new BreezeContext();
				destObj.setContext(m.group(1), tmp);
			}
			destObj = tmp;
			if ("".equals(m.group(2))) {
				destObj.pushContext(value);
			} else {
				int idx = Integer.parseInt(m.group(2));
				destObj.setContext(idx, value);
			}
		} else {
			destObj.setContext(lastName, value);
		}
	}

	public ContextIterator createIterator(String path) {
		ContextIterator result = new ContextIterator(path);
		result.setContext(this);
		return result;
	}

	/**
	 * 以本节点为根，获取本节点下对应路径的BreezeContext对象
	 * 
	 * @param path
	 *            要获取的路径名
	 * @return 路径下对应的BreezeContext对象
	 */
	public BreezeContext getContextByPath(String path) {
		return BreezeContext.getObjectByPath(path, this);
	}

	/**
	 * 以本节点为根，设置本节点下对应路径的BreezeContext对象 注意：path中如果是数组，比如a[3].b如果a中没有3号元素，则会返回错误
	 * 如果是a[].b则会在a下自动pushu一个空的Breeze对象
	 * 
	 * @param path
	 *            要设置的数组路径，注意[]表示路径中的数组，不填数字表示直接push元素进去，否则获取不到数字则会报错
	 * @param value
	 *            要设置的对象值
	 */
	public void setContextByPath(String path, BreezeContext value) {
		BreezeContext.setObjectByPath(path, value, this);
		this.isEmpty = false;
	}

	/**
	 * 根据name名称，返回对应的子节点
	 *
	 * @param name
	 *            子节点的名称
	 * @return 返回对应的子节点信息
	 */
	public BreezeContext getContext(String pname) {
		if (this.memberMap != null) {
			return this.memberMap.get(pname);
		}
		if (this.data == null) {
			return null;
		}
		// 否则以代理方式访问
		char f = pname.charAt(0);
		f = (char) (((int) f) - 32);
		String methodName = "get" + f + pname.substring(1);
		Method[] m = this.data.getClass().getMethods();
		try {
			Object result = m[0].invoke(this.data, new Object[] {});
			return new BreezeContext(result);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取当前节点的某个下表数组的节点内容，注意BreezeContext必须是Array类型
	 * 
	 * @param idx
	 *            数组下标索引
	 * @return 返回对应下标的BreezeContext对象
	 */
	public BreezeContext getContext(int idx) {
		if (this.memberArray != null) {
			return this.memberArray.get(idx);
		}
		if (this.data == null) {
			return null;
		}
		// 下面处理data情况
		if (this.data instanceof Object[]) {
			return new BreezeContext(((Object[]) this.data)[idx]);
		}
		if (this.data instanceof AbstractList) {
			return new BreezeContext(((AbstractList) this.data).get(idx));
		}
		return null;
	}

	/**
	 * 设置上下文对象的值，如果是第一次设置，那么这个BreezeContext对象将被改成map类型
	 * 如果是一个非map类型BreezeContext对象调用此方法，就会报错
	 * 
	 * @param pname
	 *            设置的BreezeContext的name值
	 * @param value
	 *            设置的BreezeContext的value对象
	 */
	public void setContext(String pname, BreezeContext pvalue) {
		if (this.type < 0 || this.type == BreezeContext.TYPE_MAP) {
			if (this.memberMap == null) {
				synchronized (this) {
					if (this.memberMap == null) {
						this.memberMap = new HashMap<String, BreezeContext>();
					}
				}
			}
			
			if (pvalue == null){
				this.memberMap.remove(pname);
			}else{
				this.memberMap.put(pname, pvalue);
			}

			
			this.type = BreezeContext.TYPE_MAP;
			this.isEmpty = false;
			return;
		}
		throw new RuntimeException(
				"this BreezeConext not a correct type ,type is" + this.type);
	}

	/**
	 * 设定数组下某个下标的值，注意数组下标不能操过当前最大下标值。第一次设置时该BreezeContext将会被修改成
	 * 数组类型，如果其他类型的BreezeContext调用此方法，将会报错
	 * 
	 * @param idx
	 *            数组下标
	 * @param value
	 *            实际的值
	 */
	public void setContext(int idx, BreezeContext pvalue) {
		if (this.type < 0 || this.type == BreezeContext.TYPE_ARRAY) {
			if (this.memberArray == null) {
				this.memberArray = new ArrayList<BreezeContext>();
			}
			this.memberArray.set(idx, pvalue);
			this.type = BreezeContext.TYPE_ARRAY;
			this.isEmpty = false;
			return;
		}
		throw new RuntimeException(
				"this BreezeConext not a correct type ,type is" + this.type);
	}
	
	
	/**
	 * 删除指定下标的数组
	 * 
	 * @param idx
	 *            数组下标
	 * @param value
	 *            实际的值
	 */
	public void delContext(int idx) {
		if (this.type < 0 || this.type == BreezeContext.TYPE_ARRAY) {
			if (this.memberArray == null) {
				return;
			}
			this.memberArray.remove(idx);
			
			this.type = BreezeContext.TYPE_ARRAY;
			this.isEmpty = false;
			
			if (this.memberArray.size() == 0){
				this.isEmpty = true;
			}
			return;
		}
		throw new RuntimeException(
				"this BreezeConext not a correct type ,type is" + this.type);
	}
	
	
	/**
	 * 删除指定名字的map
	 * @param idx
	 *            数组下标
	 * @param value
	 *            实际的值
	 */
	public void delContext(String pname) {
		this.setContext(pname, null);
	}

	/**
	 * BreezeContext的堆栈入栈操作，即往数组末端add一个对象，注意第一次调用此方法BreezeContext对象将被设置成数组类型
	 * 其他类型的BreezeContext调用此方法将报错
	 * 
	 * @param value
	 */
	public void pushContext(BreezeContext pvalue) {
		if (this.type < 0 || this.type == BreezeContext.TYPE_ARRAY) {
			if (this.memberArray == null) {
				this.memberArray = new ArrayList<BreezeContext>();
			}
			this.memberArray.add(pvalue);
			this.type = BreezeContext.TYPE_ARRAY;
			this.isEmpty = false;
			return;
		}
		throw new RuntimeException(
				"this BreezeConext not a correct type ,type is" + this.type);
	}
	
	
	/**
	 * BreezeContext的堆栈入栈操作，即往数组末端add一个对象，注意第一次调用此方法BreezeContext对象将被设置成数组类型
	 * 其他类型的BreezeContext调用此方法将报错
	 * 
	 * @param value
	 */
	public BreezeContext popContext() {
		if (this.type < 0 || this.type == BreezeContext.TYPE_ARRAY) {
			if (this.memberArray == null) {
				this.memberArray = new ArrayList<BreezeContext>();
			}
			this.type = BreezeContext.TYPE_ARRAY;
			
			if (this.memberArray.size() == 0){
				return null;
			}
			BreezeContext result = this.memberArray.get(this.memberArray.size()-1);
			this.memberArray.remove(this.memberArray.size()-1);
			
			if (this.memberArray.size() == 0){
				this.isEmpty = true;
			}
			return result;
		}
		throw new RuntimeException(
				"this BreezeConext not a correct type ,type is" + this.type);
	}

	/**
	 * 获取对象中数组中的长度小于0表示数组不可用
	 * 
	 * @return 数组中的长度小于0表示数组不可用
	 */
	public int getArraySize() {
		if (this.memberArray == null) {
			return -1;
		}
		return this.memberArray.size();
	}

	public Set<String> getMapSet() {
		if (this.memberMap == null) {
			return null;
		}
		return this.memberMap.keySet();
	}

	/**
	 * 合并上下文 如果是数组，将数组合并在一起 如果是map将map的内容合并在一起
	 * 
	 * @param x
	 */
	public void combindContext(BreezeContext x) {
		// 如果初始化没有类型，那么取被合并对象的类型
		if (this.type < 0) {
			this.type = x.type;
		}
		if (this.type != x.type) {
			// 底层抛异常不写日志
			throw new RuntimeException("type error source this.type is:"
					+ this.type + "param.type is" + x.type);
		}

		if (this.type == BreezeContext.TYPE_MAP) {
			if (this.memberMap == null) {
				this.memberMap = new HashMap<String, BreezeContext>();
			}
			this.memberMap.putAll(x.memberMap);
			this.isEmpty = false;
			return;
		}
		if (this.type == BreezeContext.TYPE_ARRAY) {
			if (this.memberArray == null) {
				this.memberArray = new ArrayList<BreezeContext>();
			}
			this.memberArray.addAll(x.memberArray);
			this.isEmpty = false;
			return;
		}
	}

	/**
	 * 获取DataType的data对象，如果这个不是TYPE_DATA类型就会报错
	 * 
	 * @return 实际的对象值
	 */
	public Object getData() {
		if (this.type != BreezeContext.TYPE_DATA) {
			throw new RuntimeException("type error this is not data type");
		}
		return data;
	}

	/**
	 * 对象的Data值，如果这个不是TYPE_DATA类型就会报错
	 * 
	 * @param data
	 *            要设置的值
	 */
	public void setData(Object data) {
		if (this.type != BreezeContext.TYPE_DATA) {
			throw new RuntimeException("type error this is not data type");
		}
		this.isEmpty = false;
		this.data = data;
	}

	// 内部使用，便于工具处理
	protected Object getDataObj() {
		if (this.type == BreezeContext.TYPE_ARRAY) {
			return this.memberArray;
		}
		if (this.type == BreezeContext.TYPE_MAP) {
			return this.memberMap;
		}
		return this.data;
	}

	/**
	 * 判断对象是否为空值，这个判断是针对三个类型操作的。 如果是map对象，则判断map的对象的值是否为空
	 * 如果是array对象，则判断array的对象是否为空 如果是data类型，则判断data是否为空
	 * 
	 * @return true 当对象为空否则返回false
	 */
	public boolean isNull() {
		return this.getDataObj() == null;
	}

	/**
	 * 返回判断该对象是否被设置过值
	 * 
	 * @return true 当对象仍处于初始化状态，没有被设置过值否则返回false
	 */
	public boolean isEmpty() {
		return this.isEmpty;
	}

	public String toString() {
		Object pObj = this.getDataObj();
		if (pObj == null) {
			return "NullObject";
		}
		return pObj.toString();
	}

	/**
	 * 深度遍历判断两个对象是否相等
	 * 
	 * @param o
	 *            被比较的对象
	 */
	public boolean equals(Object o) {
		try {
			// 2014-03-11罗光瑜修改，如果自己是一个空对象，且传入的是一个null那么判断要通过
			if (this.isNull()) {
				if (o == null) {
					return true;
				}
				if (((BreezeContext) o).isNull()) {
					return true;
				}
			}
			BreezeContext other = (BreezeContext) o;
			if (other.type != this.type) {
				return false;
			}
			if (this.type == BreezeContext.TYPE_DATA) {
				return this.getData().equals(other.getData());
			} else if (this.type == BreezeContext.TYPE_ARRAY) {
				if (this.getArraySize() != other.getArraySize()) {
					return false;
				}
				for (int i = 0; i < this.getArraySize(); i++) {
					if (!this.getContext(i).equals(other.getContext(i))) {
						return false;
					}
				}
				return true;
			} else {
				HashSet<String> cSet = new HashSet<String>();
				for (String key : this.getMapSet()) {
					cSet.add(key);
					BreezeContext octx = other.getContext(key);
					BreezeContext thisCtx = this.getContext(key);
					if (thisCtx == null && octx == null) {
						continue;
					}
					if (octx == null && thisCtx != null && thisCtx.isNull()) {
						continue;
					}
					if (thisCtx == null && octx != null && octx.isNull()) {
						continue;
					}
					if (octx == null || thisCtx == null) {
						return false;
					}
					if (!octx.equals(thisCtx)) {
						return false;
					}
				}
				// 反过来再比较一次
				for (String key : other.getMapSet()) {
					if (cSet.contains(key)) {
						continue;
					}
					BreezeContext octx = other.getContext(key);
					BreezeContext thisCtx = this.getContext(key);
					if (thisCtx == null && octx == null) {
						continue;
					}
					if (octx == null && thisCtx != null && thisCtx.isNull()) {
						continue;
					}
					if (thisCtx == null && octx != null && octx.isNull()) {
						continue;
					}
					if (octx == null || thisCtx == null) {
						return false;
					}
					if (!octx.equals(thisCtx)) {
						return false;
					}
				}
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 这个方法主要是给子类用的 比如在子类里面使用了该方法包含的是httprequest
	 * 有事要copy这个context，但copy的实际的httprequest对象，这个是不对的
	 * 所以针对这类方法，提供一个getSelf的方法，进行一个对象复制
	 * 
	 * @return
	 */
	public BreezeContext getSelf() {
		return this;
	}

	

	/**
	 * 使用代理的模式，创建list
	 * 
	 * @return
	 */
	public Map createMap() {
		if (getType() != 1) {
			return null;
		}
		return (Map) Proxy.newProxyInstance(Map.class.getClassLoader(),
				new Class[] { Map.class }, this);
	}

	/**
	 * 使用代理的模式，创建list
	 * 
	 * @return
	 */
	public List createList() {
		if (getType() != 2) {
			return null;
		}
		return (List) Proxy.newProxyInstance(List.class.getClassLoader(),
				new Class[] { List.class }, this);
	}

	/**
	 * 代理函数，模拟map或list的操作
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (getType() == BreezeContext.TYPE_ARRAY) {
			if ("get".equals(method.getName())) {
				int idx = ((Integer) args[0]).intValue();
				BreezeContext son = getContext(idx);
				if (son == null || son.isEmpty || son.isNull()) {
					return null;
				}
				if (son.getType() == BreezeContext.TYPE_ARRAY) {
					return son.createList();
				}
				if (son.getType() == BreezeContext.TYPE_MAP) {
					return son.createMap();
				}
				return son.getData();
			}

			return method.invoke(this.memberArray, args);
		}
		if (getType() == BreezeContext.TYPE_MAP) {
			if ("get".equals(method.getName())) {
				String key = (String) args[0];
				BreezeContext son = getContext(key);
				if (son == null || son.isEmpty || son.isNull()) {
					return null;
				}
				if (son.getType() == BreezeContext.TYPE_ARRAY) {
					return son.createList();
				}
				if (son.getType() == BreezeContext.TYPE_MAP) {
					return son.createMap();
				}
				return son.getData();
			}

			return method.invoke(this.memberMap, args);
		}
		return null;
	}
	
	/**
	 * 从指定的路径中获取值，如果为空则返回后面默认值
	 * @param path 对象路径，如果为空，就从自己那获取
	 * @param defaultValue
	 * @return
	 */
	public String getStringValue(String path,String defaultValue){
		if (path == null){
			if (this.type != BreezeContext.TYPE_DATA || this.data == null){
				return defaultValue;
			}
			return this.toString();
		}
		BreezeContext vCtx = this.getContextByPath(path);
		if (vCtx == null){
			return defaultValue;
		}
		return vCtx.getStringValue(null, defaultValue);
	}
	
	/**
	 * 从指定的路径中获取值，如果为空则返回后面默认值
	 * @param path 对象路径，如果为空，就从自己那获取
	 * @param defaultValue
	 * @return
	 */
	public int getIntValue(String path,int defaultValue){
		try{
			String sval = this.getStringValue(path, null);
			if (sval == null){
				return defaultValue;
			}
		
			return Integer.parseInt(sval);
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	/**
	 * 从指定的路径中获取值，如果为空则返回后面默认值
	 * @param path 对象路径，如果为空，就从自己那获取
	 * @param defaultValue
	 * @return
	 */
	public long getLongValue(String path,long defaultValue){
		try{
			String sval = this.getStringValue(path, null);
			if (sval == null){
				return defaultValue;
			}
			return Long.parseLong(sval);
		}catch(Exception e){
			return defaultValue;
		}
	}

	
	public static void main(String[] args) {
		BreezeContext root = new BreezeContext();
		BreezeContext ctx = new BreezeContext();
		ctx.setContext("money", new BreezeContext("1476677470570"));

		BreezeContext a1 = new BreezeContext();
		a1.pushContext(ctx);

		BreezeContext a2 = new BreezeContext();
		a2.pushContext(a1);

		root.setContext("s", a2);

		System.out.println(root);

		System.out.println(root.getContextByPath("s[0][0].money"));
		
		String v1 = root.getStringValue("s[0][0].money", "11");
		System.out.println(v1);
		long v2 = root.getLongValue("s[0][0].money", -1);
		System.out.println("v2:"+v2);
		
		
		Pattern p = Pattern.compile("\\d+_(\\d+)\\.");
		Matcher m = p.matcher("20161012153019_20.bak");
		if (m.find()){
			String d = m.group(1);
			System.out.println("result :"+d);
		}
	}

}
