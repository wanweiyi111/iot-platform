package com.hzyw.iot.netty.util.md5;

import java.security.MessageDigest;

public class Demo {
	/**
	 * @param args
	 *//*
	public static void main(String[] args) {
		//双方约定好的签名
		final String key ="asd";
		//数据
		final String data ="{a:21}";
		
		//对数据和签名进行加密处理
		String nt_data = QuickSDKDesUtil.encode(data, key);
		System.out.println("加密为:"+nt_data);
		
		//对加密后的数据进行md5处理
		System.out.println("对数据进行md5加密后在进行加密"+QuickSDKSignUtil.sign(nt_data));
		
		//对进过md5处理后的数据进行签名后加密
		String nt_data2 = QuickSDKDesUtil.encode(QuickSDKSignUtil.sign(nt_data), key);
		System.out.println(nt_data2);
				
		//对加密后的数据进行解密处理
		String sign_en = QuickSDKDesUtil.decode(nt_data2, key);
		System.out.println(new String(sign_en));
		
		//对加密后的数据进行解密处理
		String sign_en1 = QuickSDKDesUtil.decode(sign_en, key);
	    System.out.println(sign_en1);
	}*/
	
	
	
	
	/***
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];
 
        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16){
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
 
    }
 
    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr) {
 
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;
 
    }
 
   /* // 测试主函数
    public static void main(String args[]) {
        String s = "{a:21}";
        System.out.println("原始：" + s);
        System.out.println("MD5后：" + string2MD5(s));
        System.out.println("加密的：" + convertMD5(s));
        System.out.println("解密的：" + convertMD5(convertMD5(s)));
    }*/


}
