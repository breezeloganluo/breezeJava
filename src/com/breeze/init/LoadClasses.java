package com.breeze.init;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

import com.breeze.support.cfg.Cfg;

public class LoadClasses {

	/***
	 * 获取包下的所有类
	 * 
	 * @param pack
	 * @param recursive
	 */
	public static <T> ArrayList<T> createObject(String pack, Class<T> father) {
		String packageName =  pack;
		String packageDirName = "/" + packageName.replace('.', '/');
		URL u = LoadClasses.class.getResource(packageDirName);
		if (u == null){
			return new ArrayList<T>();
		}
		String classDir = URLDecoder.decode(LoadClasses.class.getResource(packageDirName).getPath());
		return getClassByFile(packageName, classDir, father);
	}
	
	/**
	 * @param pkgName
	 * @param pkgPath
	 * @param father
	 * @return
	 */
	public static <T> ArrayList<T> createObject(String pack, Class<T> father,String classDir) {
		String packageName =  pack;
		String packageDirName = "/" + packageName.replace('.', '/');
		URL u = LoadClasses.class.getResource(packageDirName);
		if (u == null){
			return new ArrayList<T>();
		}
		return getClassByFile(packageName, classDir, father);
	}

	/*
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param pkgName 包名
	 * 
	 * @param pkgPath 包路径
	 * 
	 * @param recursive 是否迭代
	 */

	@SuppressWarnings("unchecked")
	protected static <T> ArrayList<T> getClassByFile(String pkgName,
			String pkgPath, Class<T> father) {
		File dir = new File(pkgPath);
		if (!dir.exists() || !dir.isDirectory()) {
			return null;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory() || (file.getName().endsWith(".class"));
			}
		});
		ArrayList<T> obj = new ArrayList<T>();
		if (dirfiles == null){
			return obj;
		}
		for (File file : dirfiles) {
			// 是目录则继续迭代
			if (file.isDirectory()) {
				obj.addAll(getClassByFile(pkgName + "." + file.getName(),
						file.getAbsolutePath(), father));
			} else {
				String className = file.getName();
				if (!className.endsWith(".class") || className.indexOf("Test")>=0 || className.indexOf('$')>=0){
					continue;
				}
				className = className.substring(0,
						file.getName().length() - 6);
				Class cc = null;
				try {
					className = pkgName + '.' + className;			
					cc = Class.forName(className);
					if (!father.isAssignableFrom(cc) || Modifier.isAbstract(cc.getModifiers())){
						continue;
					}
					obj.add((T)cc.newInstance());

				} catch (Throwable e) {
					System.out.println("passer class error , name:"+className);
					e.printStackTrace();
				}
			}

		}
		return obj;
	}
	
	public static void main(String [] args){
//		ArrayList<BTLFunctionAbs> unit = createObject("com.weiguang.ablity.btl", BTLFunctionAbs.class);
//		System.out.println(unit.size()); 
		
		String packageName = "/" + "com.weiguang.ability";
		String packageDirName = packageName.replace('.', '/');
		System.out.println(LoadClasses.class.getResource(packageDirName));
		String classDir = URLDecoder.decode(LoadClasses.class.getResource(packageDirName).getPath());
		//String classDir = Thread.currentThread().getContextClassLoader().getResource("").getPath();

		File dir = new File(classDir);

		System.out.println(classDir);
		System.out.println(dir);
		System.out.println(dir.exists());
	}
}