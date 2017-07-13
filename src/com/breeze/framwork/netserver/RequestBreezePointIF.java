package com.breeze.framwork.netserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestBreezePointIF {
	/**
	 * 处理具体的进入模块配合外面的Servlet使用
	 * @param request ServletReauest
	 * @param response ServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException ;
	/**
	 * 本次调用的业务名称
	 */
	public String getPointName();
}
