package com.hzyw.iot.netty.util;

import java.util.Scanner;

import org.bouncycastle.util.encoders.Hex;

public class CRCUtils {

	public static byte[] hexStrToByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes("ISO8859-1");// getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	private static short crctab16[] = 
		{
		    (short)0x0000, (short)0x1189, (short)0x2312, (short)0x329b, (short)0x4624, (short)0x57ad, (short)0x6536, (short)0x74bf,
		    (short)0x8c48, (short)0x9dc1, (short)0xaf5a, (short)0xbed3, (short)0xca6c, (short)0xdbe5, (short)0xe97e, (short)0xf8f7,
		    (short)0x1081, (short)0x0108, (short)0x3393, (short)0x221a, (short)0x56a5, (short)0x472c, (short)0x75b7, (short)0x643e,
		    (short)0x9cc9, (short)0x8d40, (short)0xbfdb, (short)0xae52, (short)0xdaed, (short)0xcb64, (short)0xf9ff, (short)0xe876,
		    (short)0x2102, (short)0x308b, (short)0x0210, (short)0x1399, (short)0x6726, (short)0x76af, (short)0x4434, (short)0x55bd,
		    (short)0xad4a, (short)0xbcc3, (short)0x8e58, (short)0x9fd1, (short)0xeb6e, (short)0xfae7, (short)0xc87c, (short)0xd9f5,
		    (short)0x3183, (short)0x200a, (short)0x1291, (short)0x0318, (short)0x77a7, (short)0x662e, (short)0x54b5, (short)0x453c,
		    (short)0xbdcb, (short)0xac42, (short)0x9ed9, (short)0x8f50, (short)0xfbef, (short)0xea66, (short)0xd8fd, (short)0xc974,
		    (short)0x4204, (short)0x538d, (short)0x6116, (short)0x709f, (short)0x0420, (short)0x15a9, (short)0x2732, (short)0x36bb,
		    (short)0xce4c, (short)0xdfc5, (short)0xed5e, (short)0xfcd7, (short)0x8868, (short)0x99e1, (short)0xab7a, (short)0xbaf3,
		    (short)0x5285, (short)0x430c, (short)0x7197, (short)0x601e, (short)0x14a1, (short)0x0528, (short)0x37b3, (short)0x263a,
		    (short)0xdecd, (short)0xcf44, (short)0xfddf, (short)0xec56, (short)0x98e9, (short)0x8960, (short)0xbbfb, (short)0xaa72,
		    (short)0x6306, (short)0x728f, (short)0x4014, (short)0x519d, (short)0x2522, (short)0x34ab, (short)0x0630, (short)0x17b9,
		    (short)0xef4e, (short)0xfec7, (short)0xcc5c, (short)0xddd5, (short)0xa96a, (short)0xb8e3, (short)0x8a78, (short)0x9bf1,
		    (short)0x7387, (short)0x620e, (short)0x5095, (short)0x411c, (short)0x35a3, (short)0x242a, (short)0x16b1, (short)0x0738,
		    (short)0xffcf, (short)0xee46, (short)0xdcdd, (short)0xcd54, (short)0xb9eb, (short)0xa862, (short)0x9af9, (short)0x8b70,
		    (short)0x8408, (short)0x9581, (short)0xa71a, (short)0xb693, (short)0xc22c, (short)0xd3a5, (short)0xe13e, (short)0xf0b7,
		    (short)0x0840, (short)0x19c9, (short)0x2b52, (short)0x3adb, (short)0x4e64, (short)0x5fed, (short)0x6d76, (short)0x7cff,
		    (short)0x9489, (short)0x8500, (short)0xb79b, (short)0xa612, (short)0xd2ad, (short)0xc324, (short)0xf1bf, (short)0xe036,
		    (short)0x18c1, (short)0x0948, (short)0x3bd3, (short)0x2a5a, (short)0x5ee5, (short)0x4f6c, (short)0x7df7, (short)0x6c7e,
		    (short)0xa50a, (short)0xb483, (short)0x8618, (short)0x9791, (short)0xe32e, (short)0xf2a7, (short)0xc03c, (short)0xd1b5,
		    (short)0x2942, (short)0x38cb, (short)0x0a50, (short)0x1bd9, (short)0x6f66, (short)0x7eef, (short)0x4c74, (short)0x5dfd,
		    (short)0xb58b, (short)0xa402, (short)0x9699, (short)0x8710, (short)0xf3af, (short)0xe226, (short)0xd0bd, (short)0xc134,
		    (short)0x39c3, (short)0x284a, (short)0x1ad1, (short)0x0b58, (short)0x7fe7, (short)0x6e6e, (short)0x5cf5, (short)0x4d7c,
		    (short)0xc60c, (short)0xd785, (short)0xe51e, (short)0xf497, (short)0x8028, (short)0x91a1, (short)0xa33a, (short)0xb2b3,
		    (short)0x4a44, (short)0x5bcd, (short)0x6956, (short)0x78df, (short)0x0c60, (short)0x1de9, (short)0x2f72, (short)0x3efb,
		    (short)0xd68d, (short)0xc704, (short)0xf59f, (short)0xe416, (short)0x90a9, (short)0x8120, (short)0xb3bb, (short)0xa232,
		    (short)0x5ac5, (short)0x4b4c, (short)0x79d7, (short)0x685e, (short)0x1ce1, (short)0x0d68, (short)0x3ff3, (short)0x2e7a,
		    (short)0xe70e, (short)0xf687, (short)0xc41c, (short)0xd595, (short)0xa12a, (short)0xb0a3, (short)0x8238, (short)0x93b1,
		    (short)0x6b46, (short)0x7acf, (short)0x4854, (short)0x59dd, (short)0x2d62, (short)0x3ceb, (short)0x0e70, (short)0x1ff9,
		    (short)0xf78f, (short)0xe606, (short)0xd49d, (short)0xc514, (short)0xb1ab, (short)0xa022, (short)0x92b9, (short)0x8330,
		    (short)0x7bc7, (short)0x6a4e, (short)0x58d5, (short)0x495c, (short)0x3de3, (short)0x2c6a, (short)0x1ef1, (short)0x0f78
		};
	
	public static byte[] unsignedShortToByte2(int s) { 
		  byte[] targets = new byte[2]; 
		  targets[0] = (byte) (s >> 8 & 0xFF); 
		  targets[1] = (byte) (s & 0xFF); 
		  return targets; 
		  }
	/**
	 * 将byte数组转化为16进制输出
	 *
	 * @param bytes
	 * @return
	 */
	public static String convertByteToHexString(byte[] bytes) {
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			int temp = bytes[i] & 0xff;
			String tempHex = Integer.toHexString(temp);
			if (tempHex.length() < 2) {
				result += "0" + tempHex;
			} else {
				result += tempHex;
			}
		}
		return result;
	}
	/**
	 *计算给定长度数据的16位CRC。
	 * @param pData
	 * @param nLength
	 * @return
	 */
	public static short getCrc16(byte[] pData, int nLength)
	{
		short fcs = (short) 0xffff;   // 初始化
	    int i;
	    for(i=0;i<nLength;i++)
	    {
	        fcs = (short) (((fcs & 0xFFFF) >>> 8) ^ crctab16[(fcs ^ pData[i]) & 0xff]);
	    }
	    return (short) ~fcs;
	}
	
	/**
	 * 计算出crc校验码(CRC16/X25)
	 * 
	 */
	public static String crc(String str) {
		byte[] byteTest;
		String c = null;
		try {
			byteTest = hexStrToByteArr(str);
			short a = getCrc16(byteTest, byteTest.length);
			// short a = crc16_xmodem(byteTest,0,byteTest.length);
			byte[] b = unsignedShortToByte2(a);
			c = convertByteToHexString(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		return c;
	}
	
	  /*public static void main(String[] args) throws Exception {
	    	 //字符串转16进制byte数组
	    	String hex = "c05e97b39d20200416092341010a0f01";
	    	System.out.println(crc(hex));
	    	
	    	byte[] test = hexStrToByteArr(crc(hex));
	    	//HeadHandlerUtil util = new HeadHandlerUtil();
	    	String chp = Hex.toHexString(test);
	    	System.out.println(chp);
 	    	byte[] byteTest =hexStrToByteArr(hex);
	        short a = getCrc16(byteTest,byteTest.length);
	       // short a = crc16_xmodem(byteTest,0,byteTest.length);
	        byte[] b = unsignedShortToByte2(a);
	        String c = convertByteToHexString(b);
	        System.out.println(c);
		}*/
	
	//异域校验
	public static void main(String[] args) throws Exception {
		String hex = "110100320a000048fe01fe012e00000000910501031802010580f6000304";
    	
    	byte[] test = hexStrToByteArr(hex);
		
    	byte a = getXor(hex);
    	System.out.println(byteToHex(a));
    }
	
	//异域校验算法
	public static byte getXor(String str){
		
		byte[] datas = Hex.decode(str);
		 
		byte temp=datas[0];
			
		for (int i = 1; i <datas.length; i++) {
			temp ^=datas[i];
		}
	 
		return temp;
	}
	

/** 
 * 字节转十六进制 
 * @param b 需要进行转换的byte字节 
 * @return  转换后的Hex字符串 
 */  
	public static String byteToHex(byte b){  
    String hex = Integer.toHexString(b & 0xFF);  
    if(hex.length() < 2){  
        hex = "0" + hex;  
    }  
    return hex;  
	}
}
