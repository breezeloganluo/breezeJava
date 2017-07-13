/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.support.tools;

import java.io.*;
import com.breeze.base.log.Logger;

/**
 * 文件专用工具箱子
 * @author happy
 */
public class FileTools {

	private static Logger log = Logger
			.getLogger("com.breeze.support.tools.FileTools");

	/**
	 * 递归删除某个目录，包括子目录
	 * @param dir 目录名称
	 */
	public static void deleteDir(String dir) {
		File f = new File(dir);
		deleteDir(f);
	}

	/**
	 * 递归删除某个目录，包括子目录
	 * @param dir 目录的file对象
	 */
	public static void deleteDir(File dir) {
		if (dir.isFile()) {
			dir.delete();
			return;
		}
		File[] sub = dir.listFiles();
		if (sub == null || sub.length == 0) {
			dir.delete();
			return;
		}
		for (File f : sub) {
			deleteDir(f);
		}
		dir.delete();
	}

	/**
	 * 将文本写入文件
	 * @param f 文件的file对象
	 * @param content 要写入的文件内容
	 * @param cset 字符集
	 * @return 成功为true，否则false
	 */
	public static boolean writeFile(File f, String content, String cset) {
		BufferedOutputStream out = null;
		try {
			File dir = f.getParentFile();
			dir.mkdirs();
			out = new BufferedOutputStream(new FileOutputStream(f));
			out.write(content.getBytes(cset));
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			try {
				out.close();
			} catch (Exception ee) {
			}
			String exStr = com.breeze.support.tools.CommTools.getExceptionTrace(e);
			throw new RuntimeException(exStr);
		}
	}

	/**
	 * 写入文件信息
	 * @param f 文件的绝对路径和文件名
	 * @param content 写入的内容
	 * @param cset 字符集
	 * @return 成功true 否则false
	 */
	public static boolean writeFile(String f, String content, String cset) {
		return writeFile(new File(f), content, cset);
	}

	/**
	 * 从流中读入文本内容
	 * @param in 输入流
	 * @param cset 输入流的字符集
	 * @return 读入的文本内容
	 */
	public static String readFile(InputStream in, String cset) {
		try {
			byte[] buff = new byte[9000000];// 200M了
			int buffIdx = 0;
			while (true) {
				int len = in.read(buff,buffIdx,buff.length-buffIdx);
				if (len == -1){
					break;
				}
				buffIdx+=len;
			}			
			String result = new String(buff, 0, buffIdx, cset);
			return result;
		} catch (Exception e) {
			try {
				in.close();
			} catch (Exception ee) {
			}
			com.breeze.support.tools.CommTools.getExceptionTrace(e);
		}
		return null;
	}

	/**
	 * 从file对象中读入文本内容
	 * @param f 文件的file对象
	 * @param cset 字符集
	 * @return 返回文件内容
	 */
	public static String readFile(File f, String cset) {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			return readFile(in, cset);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 从文件中读入文件内容
	 * @param f 文件名包含文件路径
	 * @param cset 字符集
	 * @return 文件内容
	 */
	public static String readFile(String f, String cset) {
		return readFile(new File(f), cset);
	}

	/**
	 * 移动文件，该方法先用renameto如果失败就会手工字节拷贝
	 * 目标文件不存在会被自动创建
	 * @param src 被移动的文件名或目录
	 * @param dest 移动到的目标文件
	 * @return
	 */
	public static boolean moveF(String src, String dest) {
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			File srcF = new File(src);
			File destF = new File(dest);
			// 先要保证目标目录已经建立
			destF.getParentFile().mkdirs();
			if (!srcF.renameTo(destF)) {
				// 如果没有成功，那么手动移动
				byte[] buffer = new byte[200000];
				in = new BufferedInputStream(new FileInputStream(srcF));
				out = new BufferedOutputStream(new FileOutputStream(destF));
				while (true) {
					int len = in.read(buffer);
					if (len <= 0) {
						break;
					}
					out.write(buffer, 0, len);
				}
				in.close();
				out.close();
				srcF.delete();
			}

			return true;
		} catch (Exception e) {
			try {
				out.close();
				in.close();
			} catch (Exception ee) {
			}
			log.severe(com.breeze.support.tools.CommTools.getExceptionTrace(e));
			return false;
		}
	}

	public static void copyFile(File destFile,File srcFile)throws IOException {
		if (!srcFile.exists()){
			throw new RuntimeException("src  file not exist");
		}
		//如果是目录就递归
		if (srcFile.isDirectory()){
			if (destFile.exists() && !destFile.isDirectory()){
				throw new RuntimeException("src is dir but dest is file");
			}
			destFile.mkdirs();
			for (File f : srcFile.listFiles()){
				String name = f.getName();
				String destFileName = destFile.getCanonicalPath();
				copyFile(new File(destFileName + "/" + name),f);
			}
			return;
		}
		//剩下的就是文件情况了，首先是目标是目录的情况
		File copy2 = destFile;
		if (destFile.exists() && destFile.isDirectory()){
			String newFile = destFile.getCanonicalPath() + "/" + srcFile.getName();
			copy2 = new File(newFile);
		}
		//如果目标文件不存在，这时候目录判断一定false的，所以要判断的最后一个名字是否有.
		if (!destFile.exists()){
			if (destFile.getName().indexOf('.')<0){
				destFile.mkdirs();
				String newFile = destFile.getCanonicalPath() + "/" + srcFile.getName();
				copy2 = new File(newFile);
			}
		}
		//然后进行拷贝乐
		
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(
				srcFile));
		BufferedOutputStream bo = new BufferedOutputStream(
				new FileOutputStream(copy2));
		byte[] buffer = new byte[1024];
		int len = bi.read(buffer);
		while (len >= 0) {
			bo.write(buffer, 0, len);
			len = bi.read(buffer);
		}
		bi.close();
		bo.close();
	}
	/**
	 * 拷贝文件，注意不能是目录
	 * @param descFile 源文件
	 * @param srcFile 目标文件
	 * 支持递归，移动整个目录
	 * @throws IOException
	 */
	public static void copyFile(String descFile, String srcFile)
			throws IOException {
		copyFile(new File(descFile),new File(srcFile));
	}
	
	public static void main(String[] main) throws IOException{
		copyFile("C:/test/copy/dest/","C:/test/copy/src/breezeJs");
	}
}
