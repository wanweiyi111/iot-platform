package com.hzyw.iot.netty.util.md5;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.digest.DigestUtils;

public class QuickSDKSignUtil {
	static final String encode="UTF-8";	
	//对传递的数据进行md5加密
	public static String sign(String signStr){
		//DigestUtils.md5Hex()方法Java调用Apache commons codec实现md5加密，计算MD5摘要并返回值为32个字符的十六进制字符串
		return shuffleSign(DigestUtils.md5Hex(signStr));
	} 
private static byte[][] shufflePos=new byte[][]{{1,13},{5,17},{7,23}};
	private static String shuffleSign(String src){
		if(src == null || src.length() == 0){
			return src;
		}
		try {
			//得到一个指定的编码格式的字节数组
			byte[] bytes=src.getBytes("utf-8");
			byte temp;
			//循环遍历shufflePos，将二维数组中每位一维数组中的每个元素进行换位
			for(int i=0; i<shufflePos.length; i++){
				temp=bytes[shufflePos[i][0]];
				bytes[shufflePos[i][0]]=bytes[shufflePos[i][1]];
				bytes[shufflePos[i][1]]=temp;
			}
			return new String(bytes);
		} catch (UnsupportedEncodingException e) {
			return src;
		}
	}
	
}
