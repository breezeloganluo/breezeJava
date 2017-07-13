package com.breezefw.client.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UploadFile {
	private String url = null;
	
	private String boundary = "---------------------------7d73d8192026e";

	private String contentType = "multipart/form-data; boundary=" + boundary;
	private String method = "post";

	private HashMap<String, byte[]> fileMap = new HashMap<String, byte[]>();

	private byte[] content = new byte[50000000];
	private int contentLen;
	private String defaultContentType = "text/plain";
	private HashMap<String, String> contenttypeMap = new HashMap<String, String>();
	protected HashMap<String, String> parameterMap = new HashMap<String, String>();

	private boolean finished = false;

	/** Creates a new instance of UploadServletProxy */
	public UploadFile(String _url) {
		this.url = _url;
	}

	/**
	 * 设置参数
	 */
	public void setParameter(String pName, String pValue) {
		if (finished) {
			throw new RuntimeException("finished setting");
		}
		this.parameterMap.put(pName, pValue);
	}

	/**
	 * 设置文件,要求文件名和上面的参数名相同,而上面的值为文件名
	 */
	public void setFiles(String fieldName,String fileName, byte[] file) {
		if (finished) {
			throw new RuntimeException("finished setting");
		}
		this.fileMap.put(fieldName, file);
		this.parameterMap.put(fieldName, fileName);
	}
	
	/**
	 * 设置文件,要求文件名和上面的参数名相同,而上面的值为文件名
	 * @throws IOException 
	 */
	public void setFiles(String fieldName,File file) throws IOException {
		String fileName = file.getName();
		byte[] buff = new byte[50000000];
		int len = 0;
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		len = in.read(buff);
		byte[] fileByte = new byte[len];
		System.arraycopy(buff, 0, fileByte, 0, len);
		this.setFiles(fieldName, fileName, fileByte);
	}
	
	public void setFiles(String fieldName,String filePath) throws IOException{
		File file = new File(filePath);
		this.setFiles(fieldName, file);
	}

	/**
	 * 设置文件的内容类型
	 */
	public void setFilesContentType(String name, String type) {
		if (finished) {
			throw new RuntimeException("finished setting");
		}
		this.contenttypeMap.put(name, type);
	}

	/**
	 * 完成所有设置
	 */
	public void finished() throws Exception {
		// 处理普通参数
		Set<String> nameSet = this.parameterMap.keySet();
		String cd = "Content-Disposition: form-data; name=\"";
		String finishedStr = "--" + boundary + "--\r\n";

		this.contentLen = 0;
		for (String name : nameSet) {
			byte[] file = this.fileMap.get(name);
			String value = this.parameterMap.get(name);

			StringBuilder sb = new StringBuilder();
			sb.append("--").append(this.boundary).append("\r\n");
			sb.append(cd).append(name).append("\"");
			if (file == null) {
				// 不是文件
				sb.append("\r\n\r\n");
				sb.append(value).append("\r\n");
			} else {
				// 是文件
				sb.append("; filename=\"" + value + "\"").append("\r\n");
				String type = this.contenttypeMap.get(name);
				if (type == null) {
					type = this.defaultContentType;
				}
				sb.append("Content-Type: ").append(type).append("\r\n\r\n");
			}

			// 将内容拷贝到数组中
			byte[] tmpContent = sb.toString().getBytes();
			System.arraycopy(tmpContent, 0, this.content, this.contentLen,
					tmpContent.length);
			this.contentLen += tmpContent.length;

			// 如果是文件,将文件拷入到内容数组中
			if (file != null) {
				tmpContent = file;
				System.arraycopy(tmpContent, 0, this.content, this.contentLen,
						tmpContent.length);
				this.contentLen += tmpContent.length;
				this.content[this.contentLen++] = 13;
				this.content[this.contentLen++] = 10;
			}
		}

		byte[] tmpContent = finishedStr.getBytes();
		System.arraycopy(tmpContent, 0, this.content, this.contentLen,
				tmpContent.length);
		this.contentLen += tmpContent.length;

		this.finished = true;
	}

	public String getMethod() {
		return this.method;
	}

	public String getContentType() {
		return this.contentType;
	}

	public int getContentLength() {
		return this.contentLen;
	}

	
	public String send() throws IOException{
		URL u = null;
		HttpURLConnection con = null;
		u = new URL(url);
		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(false);
		con.setRequestProperty("Content-Type",this.contentType);
		OutputStream out = con.getOutputStream();
		out.write(this.content, 0, this.contentLen);
		out.flush();
		//下面获取返回信息
		InputStream inn = con.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(inn,
				"utf-8"));
		String line;
		String response = "";
		while ((line = in.readLine()) != null) {
			response += "\n" + line;
		}
		in.close();
		//用正则表达式，将返回的url获取到
		Pattern p = Pattern.compile(":\"([^\"]+)\"");
		Matcher m = p.matcher(response);
		if (m.find()){
			return m.group(1);
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception{
		UploadFile up = new UploadFile("http://localhost:8080/servercenter/upload.up");
		up.setFiles("upload","abc.tst", "你妹".getBytes("utf-8"));
		up.finished();
		String result = up.send();
		System.out.println(result);
	}
}
