package com.hzyw.iot.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.net.URL;
import java.net.URLConnection;


public class AudioSender {
	
	public static void main(String[] args) {
		// 下载网络文件
		int byteread = 0;
		String total = null;
		byte[] totalbyte = new byte[0];
		try {
		URL url = new URL("http://47.106.189.255/test.mp3");
		URLConnection conn = url.openConnection();
		InputStream inStream = conn.getInputStream();
		byte[] buffer = new byte[1204];
		while ((byteread = inStream.read(buffer)) != -1) {
		//拼接流，这样写是保证文件不会被篡改
		totalbyte = byteMerger(totalbyte,buffer,byteread);
		}
		inStream.close();
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		}
		String tobase64=new BASE64Encoder().encode(totalbyte);
		//这就是最终得到的结果了
		System.out.println(tobase64);
		//如果要保存到本地的话就放开下面的代码
		
		try {
			decoderBase64File(tobase64, "D:/MP3.mp3");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 拼接byte[]类型数据
	 */
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2,int byteread){
		byte[] byte_3 = new byte[byte_1.length+byteread];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byteread);
		return byte_3;
	}

	/**
	 * 解析base64,并保存到本地
	 */
	public static void decoderBase64File(String base64Code, String targetPath) throws Exception {
		byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
		FileOutputStream out = new FileOutputStream(targetPath);
		out.write(buffer);
		out.close();
	}

}
