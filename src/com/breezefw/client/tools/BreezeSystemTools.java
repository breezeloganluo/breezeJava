package com.breezefw.client.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.breeze.support.tools.FileTools;
import com.breezefw.client.moudle.ConfigData;

public class BreezeSystemTools {
	public static String getPostJsonString(String packageName,
			String serviceName, HashMap<String, Object> param) {
		HashMap<String, Object> allObj = new HashMap<String, Object>();
		allObj.put("name", serviceName);
		allObj.put("package", packageName);
		allObj.put("param", param);
		Gson gson = new Gson();
		String result = gson.toJson(allObj);
		return result;
	}
	
	public static String getPostJsonString(
			String allService, HashMap<String, Object> param) {
		String[] paramArr = allService.split("\\.");
		return getPostJsonString(paramArr[0],paramArr[1],param);
	}

	public static ConfigData ConfigData(String fileName) {
		String json = FileTools.readFile(fileName, "UTF-8");
		ConfigData result = ConfigData4Json(json);
		return result;
	}

	private static ConfigData ConfigData4Json(String json) {
		Gson gson = new Gson();
		ConfigData result = gson.fromJson(json, ConfigData.class);
		return result;
	}

	public static void copyFile(String baseDir, InputStream in)
			throws IOException {
		(new File((new File(baseDir)).getParent())).mkdirs();
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(baseDir));
		byte[] buff = new byte[2000];
		int len = 0;
		while (true) {
			len = in.read(buff);
			if (len == -1) {
				break;
			}
			out.write(buff, 0, len);
		}
		out.close();
	}

	/**
	 * 根据传入的路径名找出zip中对应的文件，并将其文本读出
	 * 
	 * @param fileStr
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String getFileText(String fileStr, InputStream in)
			throws FileNotFoundException, IOException {
		ZipInputStream zipin = new ZipInputStream(in);
		ZipEntry zipe = null;
		while ((zipe = zipin.getNextEntry()) != null) {
			if (zipe.isDirectory()) {
				continue;
			}
			String fileName = zipe.getName().replaceAll("[\\\\/]", "");
			fileStr = fileStr.replaceAll("[\\\\/]", "");
			if (!fileStr.equals(fileName)) {
				continue;
			}

			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(fileName));
			return FileTools.readFile(zipin, "UTF-8");
		}
		return null;
	}

	public static byte[] change2ByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		while (true) {
			int len = in.read(buff);
			if (len < 0) {
				return out.toByteArray();
			}
			out.write(buff, 0, len);
		}
	}

	public static List<String> getFilterList(String text) {
		if (text == null) {
			return null;
		}
		String[] array = text.split("[^\\w\\.\\\\/-]");
		List<String> l = new ArrayList<String>();
		for (String v : array) {
			if (v == null || "".equals(v)) {
				continue;
			}
			l.add(v.replaceAll("[\\\\/]", ""));
		}
		return l;
	}

	/**
	 * 解压文件到指定的目录下面
	 * @param baseDir 基础路径
	 * @param in 输入流
	 * @param filter 过滤列表，在此列表中的文件，不做文件覆盖处理
	 * @param blackList 创建黑名单，在黑名单中的文件，不错任何处理
	 * @throws IOException
	 */
	public static void unZIP(String baseDir, InputStream in,
			List<String> filter, List<String> blackList) throws IOException {
		(new File(baseDir)).mkdirs();
		ZipInputStream zipin = new ZipInputStream(in);
		ZipEntry zipe = null;
		while ((zipe = zipin.getNextEntry()) != null) {
			if (zipe.isDirectory()) {
				File d = new File(baseDir + zipe.getName());
				d.mkdirs();
				continue;
			}
			String fileName = baseDir + zipe.getName();
			boolean fileIsExist = (new File(fileName)).exists();
			if (fileIsExist) {
				// System.out.println("file:" + fileName + "is alreade exist!");
			}
			// 下面进行文件是否在黑名单中的处理
			if (filter != null
					&& blackList
							.contains(zipe.getName().replaceAll("[\\\\/]", ""))) {
				System.out.println("file:" + fileName
						+ " in black list no save!");
				continue;
			}
			// 下面判断文件是否在在filter中
			if (filter != null
					&& filter
							.contains(zipe.getName().replaceAll("[\\\\/]", ""))) {
				// 说明文件在filter中，那么要看看这个文件是否是存在，如果存在就不复盖了
				if (fileIsExist) {
					System.out.println("file:" + fileName
							+ " in filter and exist,not rewrite!");
					continue;
				}
			}
			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(fileName));
			byte[] buff = new byte[2048];
			int len = -1;
			while (true) {
				len = zipin.read(buff);
				if (len < 0) {
					break;
				}
				out.write(buff, 0, len);
			}
			out.close();
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		InputStream tmp = new BufferedInputStream(new FileInputStream(
				"C:\\test\\test.zip"));
		byte[] tmpArray = change2ByteArray(tmp);
		tmp.close();
		InputStream in = new ByteArrayInputStream(tmpArray);
		ArrayList<String> l = new ArrayList<String>();
		l.add("/b/b.txt1".replaceAll("[\\\\/]", ""));
		unZIP("c:\\test\\out\\", in, l,null);

		System.out.println(getFilterList("a\n\\b/b.c,d"));
	}

	public static String callRemoteService(String url,String service,HashMap<String,Object> param) throws IOException{
		String paramJson = "["+ getPostJsonString(service,param) +"]";
		HashMap<String, Object> breezeParam = new HashMap<String, Object>();
		
		HashMap<String, String> postParam = new HashMap<String, String>();
		postParam.put("data", paramJson);
		
		return getPostResponseText(url,postParam);
	}
	/**
	 * 单纯的post请求，和breeze无关
	 * @param url post请求的地址
	 * @param postData 请求的数据
	 * @return
	 * @throws IOException
	 */
	public static String getPostResponseText(String url,
			HashMap<String, String> postData) throws IOException {
		URL u = null;
		HttpURLConnection con = null;
		u = new URL(url);
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		StringBuilder postDataString = new StringBuilder();
		boolean isFirst = true;
		for (String key : postData.keySet()) {
			String value = postData.get(key);
			if (isFirst) {
				isFirst = false;
			} else {
				postDataString.append('&');
			}
			postDataString.append(key).append('=')
					.append(URLEncoder.encode(value, "UTF-8"));
		}
		OutputStream out = con.getOutputStream();
		out.write(postDataString.toString().getBytes("UTF-8"));
		out.close();

		InputStream inn = con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(inn,
				"utf-8"));
		String line;
		String result = "";
		while ((line = in.readLine()) != null) {
			result += "\n" + line;
		}
		in.close();
		return result;
	}
	

	public static InputStream getResponseStream(String url) throws IOException {
		URL u = null;
		HttpURLConnection con = null;
		u = new URL(url);
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("GET");
		con.setDoInput(true);
		con.setUseCaches(false);
		InputStream in = con.getInputStream();
		return in;
	}
}
