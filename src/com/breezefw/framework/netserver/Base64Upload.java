package com.breezefw.framework.netserver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breeze.base.log.Logger;
import com.breeze.support.cfg.Cfg;
import com.breeze.support.tools.FileTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64Upload extends HttpServlet {
	private Logger log = Logger.getLogger("com.breezefw.framework.netserver");
	private int sn = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 转到doPost上
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 计算所有的路径
		try {
			String baseDir = Cfg.getCfg().getRootDir();
			
			String urlPrifix = Cfg.getCfg().getString("siteprefix");
			if (urlPrifix == null || "--".equals(urlPrifix)) {
				urlPrifix = request.getServletContext().getContextPath();
			}
			if ("/".equals(urlPrifix)){
				urlPrifix = "";
			}
			
			SimpleDateFormat sf1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sf2 = new SimpleDateFormat("hhmmss");
			Date now = new Date();
			String filePath = "upload/" + sf1.format(now) + "/" ;
			//确保路径被创建
			File d = new File(baseDir + '/' + filePath);
			d.mkdirs();
			filePath = filePath + sf2.format(now) + '_' + (sn++ % 10000) + ".jpg";
			// 获取对应的base64字符串
			String data = FileTools.readFile(request.getInputStream(), "UTF-8");

			// 保存图片
			GenerateImage(data, baseDir + '/' + filePath);
			String result = "{\"succUrl\":\"" + urlPrifix +'/' + filePath + "\"}";
			response.getWriter().write(result);
		} catch (Exception e) {
			log.severe("异常", e);
			String result = "{\"filMsg\":\"上传失败，异常存在\"}";
			response.getWriter().write(result);
		}
	}

	// 图片转化成base64字符串
	public static String GetImageStr() {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		String imgFile = "d://test.jpg";// 待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}

	// base64字符串转化成图片
	public static boolean GenerateImage(String imgStr, String imgFilePath) throws IOException { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return false;
		BASE64Decoder decoder = new BASE64Decoder();

			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
	}

	public static void main(String[] args) {
		String strImg = GetImageStr();
		System.out.println(strImg);
	}
}
