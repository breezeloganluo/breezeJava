package com.breezefw.compile;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.breeze.support.cfg.Cfg;

import javax.tools.JavaCompiler.CompilationTask;

public class BreezeCompile {
	// private Logger log = Logger.getLogger("com.breezefw.BreezeCompile");
	public final static String SDIR = "/WEB-INF/classes/djava";
	public final static String CDIR = "WEB-INF/dclasses";
	public final static String CLASSPATH = "/WEB-INF/classes";
	public final static String LIB = "/WEB-INF/lib";

	public static BreezeCompile INC = new BreezeCompile();

	private static ClassLoader curLoder = null;

	private String baseDir;
	public HashMap<String, String> fileDirMap;
	private HashMap<String, String> classDirMap;
	private URLClassLoader classloder;

	private BreezeCompile() {
		this.baseDir = Cfg.getCfg().getRootDir();
	}

	public BreezeCompile(String dir) {
		this.baseDir = dir;
	}

	/**
	 * 扫描所有的指定目录下的动态java文件 该方法用于对比编译时使用，用于区分哪些文件已经编译过，哪些文件还未编译过
	 * 
	 * @param baseImport
	 *            原始的导入路径，这个路径代表了真正的java累的
	 *            包名，比如com.java.cddd....，如果为空，或者为空字符串则表示根路径
	 */
	public synchronized void searchAllJavaSrc(String baseImport) {

		// 整理好文件名
		String fileDir = this.baseDir + "/" + SDIR + "/";
		if (baseImport == null || "".equals(baseImport)) {
			this.fileDirMap = new HashMap<String, String>();
			baseImport = "";
		}
		if (this.fileDirMap == null) {
			this.fileDirMap = new HashMap<String, String>();
		}
		// 变成文件对象，进行文件遍历
		File f = new File(fileDir + baseImport);
		File[] fArray = f.listFiles();
		for (int i = 0; i < fArray.length; i++) {
			File one = fArray[i];
			String name = one.getName();
			if (one.isFile()) {
				String pName = null;
				Pattern p = Pattern.compile("(\\w+)\\.java$");
				Matcher m = p.matcher(name);
				if (m.find()) {
					pName = m.group(1);
				} else {
					// 不是java文件忽略掉
					continue;
				}
				String javapath = baseImport.replaceAll("[\\\\\\/]", ".");
				if ("".equals(javapath)) {
					javapath = pName;
				} else {
					javapath = javapath + "." + pName;
				}
				this.fileDirMap.put(javapath, one.getAbsolutePath());
			} else {
				if ("".equals(baseImport)) {
					this.searchAllJavaSrc(name);
				} else {
					this.searchAllJavaSrc(baseImport + "/" + name);
				}
			}
		}
	}

	/**
	 * 扫描所有的java编译后的文件 该方法用于对比编译，以及当进行类加载的时候使用
	 * 
	 * @param baseImport
	 *            原始的导入路径，这个路径代表了真正的java累的
	 *            包名，比如com.java.cddd....，如果为空，或者为空字符串则表示根路径
	 */
	public synchronized void searchAllJavaClass(String baseImport) {

		// 整理好文件名
		String fileDir = this.baseDir + "/" + CDIR + "/";
		if (baseImport == null || "".equals(baseImport)) {
			this.classDirMap = new HashMap<String, String>();
			baseImport = "";
		}
		if (this.classDirMap == null) {
			this.classDirMap = new HashMap<String, String>();
		}
		// 变成文件对象，进行文件遍历
		File f = new File(fileDir + baseImport);
		File[] fArray = f.listFiles();
		for (int i = 0; fArray != null && i < fArray.length; i++) {
			File one = fArray[i];
			String name = one.getName();
			if (one.isFile()) {
				String pName = null;
				Pattern p = Pattern.compile("([\\w\\$]+)\\.class$");
				Matcher m = p.matcher(name);
				if (m.find()) {
					pName = m.group(1);
				} else {
					// 不是java文件忽略掉
					continue;
				}
				String javapath = baseImport.replaceAll("[\\\\\\/]", ".");
				if ("".equals(javapath)) {
					javapath = pName;
				} else {
					javapath = javapath + "." + pName;
				}
				this.classDirMap.put(javapath, one.getAbsolutePath());
			} else {
				if ("".equals(baseImport)) {
					this.searchAllJavaClass(name);
				} else {
					this.searchAllJavaClass(baseImport + "/" + name);
				}
			}
		}
	}

	/**
	 * 对原来有缺失部分的源代码进行编译 编译成功为true 编译失败为false
	 */
	public synchronized boolean compileDiff() {
		return false;
	}

	/**
	 * 指定文件进行编译
	 * 
	 * @param filePath
	 *            文件的路径
	 * @return 编译成功返回true；编译失败返回false
	 */
	public synchronized boolean compileFile(String filePath, Writer out) {
		boolean result = false;
		try {
			String javaFile = this.fileDirMap.get(filePath);

			if (javaFile == null) {
				return false;
			}

			// 创建目录
			this.createClassDir();

			// java编译器
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			// 文件管理器，参数1：diagnosticListener 监听器,监听编译过程中出现的错误
			StandardJavaFileManager manager = compiler.getStandardFileManager(null, null, null);
			// java文件转换到java对象，可以是多个文件
			Iterable<? extends JavaFileObject> it = manager.getJavaFileObjects(javaFile);
			// 编译任务,可以编译多个文件
			Iterable<String> options = Arrays.asList("-d", this.baseDir + "/" + CDIR + "/", "-sourcepath",
					this.baseDir + "/" + SDIR + "/", "-classpath", this.getClassPath(), "-encoding", "utf-8");
			CompilationTask t = compiler.getTask(out, null, null, options, null, it);

			// 执行任务
			result = t.call();
			manager.close();
			if (result) {
				this.searchAllJavaClass(null);
			}
		} catch (IOException e) {
			// log.severe("", e);
		}
		return result;
	}

	/**
	 * 该方法用于将根据全路径的类名，用类加载器，加载对应的类
	 * 
	 * @param className
	 *            类名，注意是全路径的
	 * @param t
	 *            对应的类的class
	 * @return 返回实际实例化的对象
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public <T> T loadClassInc(String className, Class<T> t) throws InstantiationException, IllegalAccessException {
		return t.newInstance();
	}

	/**
	 * 获取java jar 包的字符串列表，用:间隔
	 * 
	 * @return 用:间隔开的classpath
	 */
	private String getClassPath() {
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append(this.baseDir + "/" + CLASSPATH);
		File jardir = new File(this.baseDir + "/" + LIB);
		File[] jars = jardir.listFiles();
		for (int i = 0; jars != null && i < jars.length; i++) {
			File one = jars[i];
			resultBuilder.append(System.getProperty("path.separator"))
					.append(one.getAbsolutePath().replaceAll("\\\\", "/"));
		}
		return resultBuilder.toString();
	}

	/**
	 * 
	 */
	private ArrayList<String> getLibs() {
		ArrayList<String> result = new ArrayList<String>();
		File jardir = new File(this.baseDir + "/" + LIB);
		File[] jars = jardir.listFiles();
		for (int i = 0; jars != null && i < jars.length; i++) {
			File one = jars[i];
			result.add(one.getAbsolutePath().replaceAll("\\\\", "/"));
		}
		return result;
	}

	/**
	 * 创建类路径目录，如果目录不存在就创建
	 */
	private void createClassDir() {
		File classDir = new File(this.baseDir + this.CDIR);
		if (!classDir.exists()) {
			classDir.mkdirs();
		}
	}

	public synchronized void loadAndInitAll() {
		for (String classname : this.classDirMap.keySet()) {
			this.loadAndInit(classname);
		}
	}

	/**
	 * 加载类，并且判断该类是否是能被加载的内容，如果能被加载，就立即进行初始化
	 * 
	 * @param className
	 */
	public synchronized void loadAndInit(String className) {
		try {
			Class clazz = null;
			// 加载到内存
			DynamicClassLoader loader = new DynamicClassLoader(this.baseDir + "/" + CDIR);
			BreezeCompile.curLoder = loader;
			clazz = loader.loadClass(className);
			CompileObjInitor.INSTANCE().loadObj(clazz);
			// 判断是否需要实例化加载
		} catch (Exception e) {
			// log.severe("", e);
		}
	}

	public static ClassLoader getCurLoader() {
		synchronized (BreezeCompile.INC) {

			if (BreezeCompile.curLoder == null) {
				return BreezeCompile.class.getClassLoader();
			}
			return BreezeCompile.curLoder;
		}
	}

	public static void main(String[] args) {
		BreezeCompile c = new BreezeCompile("C:/soft/apache-tomcat-8.0.28/webapps/f");
		c.loadAndInit("com.breezefw.framework.workflow.TestFlowUnit");
	}
}
