package com.zice.password.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64; 

/**
 * AES 是一种可逆加密算法，对用户的敏感信息加密处理 对原始数据进行AES加密后，在进行Base64编码转化；
 * http://www.cnblogs.com/jys509/p/4768120.html
 */
public class AESOperator {
	/*
	 * 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
	 */
	private String sKey;//key
	private String ivParameter;//偏移量
	private static AESOperator instance = null;

	private AESOperator() {

	}

	public static AESOperator getInstance() {
		if (instance == null) {
			instance = new AESOperator();
		}
		return instance;
	}
	
	public static String Encrypt(String encData,String secretKey,String vector) throws Exception{
		if (secretKey==null) {
			return null;
		}
		if (secretKey.length()!=16) {
			return null;
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] raw = secretKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);
		byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
		return Base64.encodeToString(encrypted, Base64.DEFAULT);// 此处使用BASE64做转码。
	}
	
	 // 加密
    public String encrypt(String sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return Base64.encodeToString(encrypted, Base64.DEFAULT);// 此处使用BASE64做转码。
    }

    // 解密
    public String decrypt(String sSrc) throws Exception {
        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 =Base64.decode(sSrc, Base64.DEFAULT); 
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            return null;
        }
    }
}

