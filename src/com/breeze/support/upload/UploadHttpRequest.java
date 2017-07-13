/*
 * UploadHttpRequest.java
 *
 * Created on 2008年1月20日, 下午9:46
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.upload;

/**
 *
 * @author happy
 */
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.lang.reflect.*;

/**
 * 
 * @author Logan 这个类是一个专用作文件上传的封装类，用他来屏蔽一些普通请求和特殊文件请求的差异
 */
public class UploadHttpRequest implements InvocationHandler {
	public static int RTYPE_UPLOAD = 1;
	public static int RTYPE_HTML5UPLOAD = 2;
	public static int RTYPE_NORMAL = 0;

	HttpServletRequest request = null;
	int requestType;
	long filesize;
	HashMap<String, String> parameter = new HashMap<String, String>();
	HashMap<String, FileItem> files = new HashMap<String, FileItem>();
	String charSet;

	public static Upload createRequest(HttpServletRequest rq, long p_filesize)
			throws IOException, FileUploadException {
		return (Upload) Proxy.newProxyInstance(Upload.class.getClassLoader(),
				new Class[] { Upload.class }, new UploadHttpRequest(rq,
						p_filesize));
	}

	/** Creates a new instance of UploadHttpRequest */
	private UploadHttpRequest(HttpServletRequest rq, long p_filesize)
			throws FileUploadException, UnsupportedEncodingException {
		this.request = rq;
		requestType = ServletFileUpload.isMultipartContent(this.request) ? 1
				: 0;
		this.filesize = p_filesize;
		this.charSet = rq.getCharacterEncoding();
		// 如果是上传文件的格式,那么要解析这些文件
		if (requestType == RTYPE_UPLOAD) {
			DiskFileItemFactory dfc = new DiskFileItemFactory();
			ServletFileUpload sfu = new ServletFileUpload(dfc);
			if (this.filesize > 0) {
				sfu.setSizeMax(this.filesize);
			}
			List<FileItem> li = (List<FileItem>) sfu.parseRequest(this.request);
			// System.out.println("li length is:"+ li.size());
			for (FileItem fi : li) {
				// System.out.println(fi.getFieldName()+" format is "+fi.isFormField()+" name is "+fi.getName()
				// +" value is :\n"+fi.getString());
				if (fi.isFormField()) {// 判断是否是正常格式
					if (charSet != null) {
						this.parameter.put(fi.getFieldName(),
								fi.getString(charSet));
					} else {
						this.parameter.put(fi.getFieldName(), fi.getString());
					}
				}// end if isFormField
				else {// 这是文件
					this.parameter.put(fi.getFieldName(), fi.getName());
					this.files.put(fi.getFieldName(), fi);
				}
			}
			return;
		}
		// 判断是否是html的上传模式
		if ("application/octet-stream".equals(request.getContentType())) {
			requestType = RTYPE_HTML5UPLOAD;
			String dispoString = request.getHeader("Content-Disposition");
			Pattern p = Pattern
					.compile("name=\"([^\"]+)\".+?filename=\"([^\"]+)\"");
			Matcher m = p.matcher(dispoString);
			if (m.find()) {
				this.parameter.put(m.group(1), m.group(2));
			}
		}
	}

	private Object innerInvoker(Object proxy, Method method, Object[] args,
			Object moke) throws Throwable {
		try {
			Method thisMethod = moke.getClass().getMethod(method.getName(),
					method.getParameterTypes());
			return thisMethod.invoke(moke, args);
		} catch (NoSuchMethodException e) {
			return method.invoke(this.request, args);
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return this.innerInvoker(proxy, method, args, this);
	}

	public String getParameter(String p) {
		if (this.requestType == RTYPE_UPLOAD) {
			return this.parameter.get(p);
		} else if (this.requestType == RTYPE_HTML5UPLOAD) {
			String value = this.parameter.get(p);
			if (value != null) {
				return value;
			}
		}
		return this.request.getParameter(p);
	}

	public void saveFile(String pFieldName, File file) throws Exception {
		if (this.requestType == RTYPE_UPLOAD) {
			FileItem fi = this.files.get(pFieldName);
			if (fi == null) {
				throw new RuntimeException("can not found FileItem for "
						+ pFieldName);
			}
			fi.write(file);
			return;
		}
		InputStream in = this.request.getInputStream();
		byte[] buffer = new byte[200000];

		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		while (true) {
			int len = in.read(buffer);
			if (len <=0){
				break;
			}
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

	public InputStream getFileInputStream(String pFieldName) throws Exception {
		FileItem fi = this.files.get(pFieldName);
		if (fi == null) {
			throw new RuntimeException("can not found FileItem for "
					+ pFieldName);
		}
		return fi.getInputStream();
	}

	public static interface Upload extends HttpServletRequest {
		public void saveFile(String pFieldName, File file) throws Exception;

		public InputStream getFileInputStream(String pFieldName)
				throws Exception;
	}
}
