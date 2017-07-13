package com.breezefw.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DynamicClassLoader extends ClassLoader {

	public DynamicClassLoader(String pclassPath) {
		super(DynamicClassLoader.class.getClassLoader());
		this.classPath = pclassPath;
	}
	
	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	private String classPath;

	/**
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public Class findClass(String className) throws ClassNotFoundException {
		try {
			String url = classPathParser(this.classPath) + classNameParser(className);
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			InputStream input = connection.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();
			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}
			input.close();
			byte[] classData = buffer.toByteArray();
			return defineClass(className, classData, 0, classData.length);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 整理成标准的classpath格式
	 * @param path
	 * @return
	 */
	private String classPathParser(String path) {
		String classPath = path.replaceAll("\\\\", "/"); 
		if (!classPath.startsWith("file:")) {
			classPath = "file:" + classPath;
		}
		if (!classPath.endsWith("/")) {
			classPath = classPath + "/";
		}
		return classPath;
	}

	private String classNameParser(String className) {
		return className.replaceAll("\\.", "/")+".class";
	}

	public static void main(String[] arguments) throws Exception {
		String classPath = "C:/soft/apache-tomcat-8.0.28/webapps/f/WEB-INF/dclasses";
		String className = "com.compile.CodeText";
		new DynamicClassLoader(classPath).loadClass(className).newInstance();
	}
}