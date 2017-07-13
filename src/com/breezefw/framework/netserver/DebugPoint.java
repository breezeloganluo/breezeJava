package com.breezefw.framework.netserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Logger;
/**
 * 该类用于调试用，将所有客户端传入的内容进行返回
 * @author Logan
 *
 */
public class DebugPoint extends HttpServlet {
	Logger log = Logger.getLogger("com.breezefw.framework.netserver.DebugPoint");
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 转到doPost上
		this.doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		InputStream bin = request.getInputStream();
		byte [] buff = new byte[40000000];
		int len  =  bin.read(buff);
		String d = new String(buff,0,len,"ISO-8859-1");
		log.severe(d);
		OutputStream out = response.getOutputStream();
		out.write(buff, 0, len);
	}
}
