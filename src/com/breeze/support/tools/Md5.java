/*
 * Md5.java
 *
 * Created on 2010年5月8日, 上午9:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.tools;
import java.security.*;
/**
 *
 * @author happy
 */
public class Md5 {
    
    /** Creates a new instance of Md5 */
    public Md5() {
    }
    public static String getMd5Str(String src)throws Exception{
        MessageDigest md5Au = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md5Au.digest(src.getBytes("UTF-8"));
        StringBuffer hexValue = new StringBuffer();        
        for (int i=0; i<md5Bytes.length; i++) {
            int val = (~((int) md5Bytes[i]) ) & 0xff;//为保证不容易破解,再加一次取反操作        	
            if (val < 16) hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    /**
     * 提供一个标准MD5加密的方法
     * @param src
     * @return
     * @throws Exception
     */
    public static String getStanderMd5(String src)throws Exception{
        MessageDigest md5Au = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md5Au.digest(src.getBytes("UTF-8"));
        StringBuffer hexValue = new StringBuffer();        
        for (int i=0; i<md5Bytes.length; i++) {            
        	int val = md5Bytes[i] & 0xff;//罗光瑜修改原先的加密虽然好，但是这种不标准的做法，无法通过类似zhifu
            if (val < 16) hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    public static void main(String[] args)throws Exception{
        String a = "abc";
        String ar = getStanderMd5(a);
        System.out.println(ar);
    }
}
