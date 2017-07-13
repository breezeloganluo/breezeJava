package com.breezefw.framework.netserver;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Logger;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.upload.UploadHttpRequest;

public class UploadPoint extends HttpServlet {

	private static class UploadStruct {
		UploadStruct(String n, String p) {
			this.name = n;
			this.resultPartern = p;
		}

		public String name;
		public String resultPartern;
	}

	private static HashMap<String, UploadStruct> cfgMap = new HashMap<String, UploadStruct>();

	private Logger log = Logger.getLogger("com.breeze.framwork.netserver.UploadPoint");

	public void init(ServletConfig conf) {
		String urlPrifix = Cfg.getCfg().getString("siteprefix");
		if (urlPrifix == null) {
			urlPrifix = conf.getServletContext().getContextPath();
		}
		if ("/".equals(urlPrifix)){
			urlPrifix = "";
		}

		cfgMap.put("upload.up", new UploadStruct("upload", "{\"succUrl\":\"`dir`\"}"));
		cfgMap.put("upload.php", new UploadStruct("filedata",
				"{'err':'','msg':{'url':'!" + urlPrifix + "/`dir`','localname':'`file`','id':'1'}}"));
		cfgMap.put("upload.mup", new UploadStruct("Filedata", "{succUrl:'`dir`'}"));
	}

	/**
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 * @roseuid 4B9209D90204
	 * @J2EE_METHOD -- doGet Called by the server (via the service method) to
	 *              allow a servlet to handle a GET request. The servlet
	 *              container must write the headers before committing the
	 *              response, because in HTTP the headers must be sent before
	 *              the response body. The GET method should be safe and
	 *              idempotent. If the request is incorrectly formatted, doGet
	 *              returns an HTTP 'Bad Request' message.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 转到doPost上
		this.doPost(request, response);
	}

	/**
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 * @roseuid 4B9209D90252
	 * @J2EE_METHOD -- doPost Called by the server (via the service method) to
	 *              allow a servlet to handle a POST request. The HTTP POST
	 *              method allows the client to send data of unlimited length to
	 *              the Web server a single time and is useful when posting
	 *              information such as credit card numbers. If the HTTP POST
	 *              request is incorrectly formatted, doPost returns an HTTP
	 *              'Bad Request' message.
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String threadSignal = null;
		try {
			request.setCharacterEncoding(Cfg.getCfg().getString("DefalutFileChartset"));
			String uri = request.getRequestURI();
			int lastSig = uri.lastIndexOf('/');
			String upFile = uri.substring(lastSig + 1);
			log.fine("the path is :" + upFile);

			UploadStruct upInfo = cfgMap.get(upFile);
			if (upInfo == null) {
				throw new RuntimeException("not support this file type:" + upFile);
			}

			// 设置字符集
			UploadHttpRequest.Upload re = UploadHttpRequest.createRequest(request, 100000000L);
			String baseDir = Cfg.getCfg().getRootDir();
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			String dDir = "upload/" + sf.format(new Date()) + "/";
			File dir = new File(baseDir + dDir);
			dir.mkdirs();
			log.fine("file field name :" + upInfo.name);
			String srcFileName = re.getParameter(upInfo.name);
			if (srcFileName == null) {
				srcFileName = "bc.jpg";
			}
			log.fine("file name :" + srcFileName);
			String fExt = srcFileName.substring(srcFileName.lastIndexOf("."));
			// 2015-02-04 罗光瑜修改，上传如果是扩展名为.jsp的不允许
			if (fExt.equalsIgnoreCase(".jsp") || fExt.equalsIgnoreCase(".jspx")) {
				throw new RuntimeException(".jsp not allow to upload!");
			}
			StringBuilder fileSb = new StringBuilder();
			fileSb.append(System.currentTimeMillis()).append(fExt);
			re.saveFile(upInfo.name, new File(baseDir + dDir + fileSb.toString()));

			String result = upInfo.resultPartern.replaceAll("`dir`", dDir + fileSb.toString()).replaceAll("`file`",
					srcFileName);
			response.setContentType("text/html; charset=UTF-8");

			Writer w = response.getWriter();
			w.write(result);
			w.close();
		} catch (Exception e) {
			log.severe("Exception in upload", e);
			throw new RuntimeException(e);
		} finally {
			if (threadSignal != null) {
				log.removeThreadSignal();
			}
		}
	}
}
