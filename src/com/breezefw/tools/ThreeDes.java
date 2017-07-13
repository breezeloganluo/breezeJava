package com.breezefw.tools;



import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;  
import java.security.Key;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
  
/*字符串 DESede(3DES) 加密 
 * ECB模式/使用PKCS7方式填充不足位,目前给的密钥是192位 
 * 3DES（即Triple DES）是DES向AES过渡的加密算法（1999年，NIST将3-DES指定为过渡的 
 * 加密标准），是DES的一个更安全的变形。它以DES为基本模块，通过组合分组方法设计出分组加 
 * 密算法，其具体实现如下：设Ek()和Dk()代表DES算法的加密和解密过程，K代表DES算法使用的 
 * 密钥，P代表明文，C代表密表，这样， 
 * 3DES加密过程为：C=Ek3(Dk2(Ek1(P))) 
 * 3DES解密过程为：P=Dk1((EK2(Dk3(C))) 
 * */  
public class ThreeDes {    
    // 向量  
    private final static String iv = "01234567";  
    // 加解密统一使用的编码方式  
    private final static String encoding = "utf-8";  
    
    
    
    /** 
     * 3DES加密 
     *  
     * @param plainText 普通文本 
     * @return 
     * @throws Exception  
     */  
    public static String encrypt(String plainText,String secretKey) throws Exception {  
        Key deskey = null;
        secretKey = secretKey + "000000000000000000000000".substring(secretKey.length());        
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());  
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
        deskey = keyfactory.generateSecret(spec);  
  
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);  
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));  
        return Base64.encode(encryptData);  
    } 
    
    
    /** 
     * 3DES解密 
     *  
     * @param encryptText 加密文本 
     * @return 
     * @throws Exception 
     */  
    public static String decrypt(String encryptText,String secretKey) throws Exception {  
        Key deskey = null;  
        secretKey = secretKey + "000000000000000000000000".substring(secretKey.length());
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());  
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");  
        deskey = keyfactory.generateSecret(spec);  
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");  
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());  
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
  
        byte[] decryptData = cipher.doFinal(Base64.decode(encryptText));  
  
        return new String(decryptData, encoding);  
    }  
    
    
    public static void main(String[] args) throws Exception {
    	//String secretKey = "liuyunqiang@lx100$#365#$";
    	String secretKey = "lanay";
    	String src = "password";
    	String sec = encrypt(src,secretKey);
    	System.out.println("加密后的结果："+sec);
    	String org = decrypt(sec,secretKey);
    	System.out.println("解密后的结果："+org);
    }  
}  
