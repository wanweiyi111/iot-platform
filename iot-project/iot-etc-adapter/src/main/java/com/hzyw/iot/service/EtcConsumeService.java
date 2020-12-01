package com.hzyw.iot.service;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Hex;

import com.hzyw.iot.util.Sm4;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

public class EtcConsumeService {
	Sm4 sm4 =new Sm4();
	/**
	 * 当前时间格式化
	 */
	public static String getToday() {
		Date date = DateUtil.parse(DateUtil.now().toString());	
		String format = DateUtil.format(date, "yyyyMMddHHmmss");// 日期格式
		return format;
	}
	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * @param strIn 需要转换的字符串
	 * @return 转换后的byte数组
	 * @throws Exception 本方法不处理任何异常，所有异常全部抛出
	 * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>
	 */
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
	/**
	 * 合并byte数据
	 *
	 */
	public static byte[] getPdt(byte[]... ls) throws Exception {
		int len = 0;
		for (byte[] it : ls) {
			len = len + it.length;
		}
		ByteBuf byteBuf = Unpooled.buffer(len);
		for (byte[] it : ls) {
			byteBuf.writeBytes(it);
		}
		byte[] temp = byteBuf.array();
		if (byteBuf != null)
			ReferenceCountUtil.release(byteBuf);
		return temp;
	}
	/**
	 * 获取工作密银
	 */
	public String getWorkKey(String rsuId) {
		JSONObject json = JSONUtil.createObj();
		json.put("rsuId", rsuId);
		System.out.println("发送:"+json.toString());
		String returnData =HttpRequest.post("http://10.240.36.99:8081/park/rsu/getWorkKey").body(json.toString()).execute().body();//json方式
		System.out.println("返回:"+returnData);
		JSONObject jsonKey = JSONUtil.parseObj(returnData);
		JSONObject key = JSONUtil.parseObj(jsonKey.get("data").toString());
		return key.get("key").toString();
	}
	
	
	/**
	 *MAC1在线计算接口
	 * @throws Exception 
	 */
	public String getMac(String rsuId,String frameId,String jsonData) throws Exception {
		 String keyStr =getWorkKey(rsuId);
		 byte[] keyByte = Hex.decode(keyStr);
		 byte[] dataByte = jsonData.getBytes("UTF-8");
		 byte[] byte80 = {(byte) 128,00,00,00};//补充字节
		 byte[] merge =getPdt(dataByte,byte80);
		 byte[] enc =sm4.encrypt_Ecb_NoPadding(keyByte,merge);//加密
         String chp = Hex.toHexString(enc);//转16进制码
         
         /*//解密
          * System.out.println("SM4:"+chp);
         byte[] decrypt = sm4.decrypt_Ecb_NoPadding(keyByte,enc);
         String  decryptStr = new String(decrypt, "UTF-8");
         System.out.println("发送的sm4解密出来的数据:"+decryptStr);*/
         
		 Map<String, Object> map = new LinkedHashMap<>();
	     map.put("rsuId", rsuId);
	     map.put("frameId", frameId);
	     map.put("data", chp);
	     JSONObject jsonObject = JSONUtil.parseObj(map);
	     System.out.println("发送的数据:"+jsonObject);
		String returnData =HttpRequest.post("http://10.240.36.99:8081/park/rsu/getMac").body(jsonObject.toString()).execute().body();//json方式
		
		JSONObject returnJson = JSONUtil.parseObj(returnData);
		byte[] hex = hexStrToByteArr(returnJson.get("data").toString());
		byte[] decrypt = sm4.decrypt_Ecb_NoPadding(keyByte,hex);
        String  decryptStr = new String(decrypt, "UTF-8");
        System.out.println("返回data数据解密:"+decryptStr);
		return returnData;
	}
	
	
	
	/**
	 *车辆信息在线解密接口
	 * @throws Exception 
	 */
	public String getVehInfo(String rsuId,String frameId,String jsonData) throws Exception {
		String keyStr =getWorkKey(rsuId);
		 byte[] keyByte = Hex.decode(keyStr);
		 byte[] dataByte = jsonData.getBytes("UTF-8");
		 byte[] byte80 = {(byte) 128,00,00,00};//补充字节
		 byte[] merge =getPdt(dataByte,byte80);//合并数据
		 byte[] enc =sm4.encrypt_Ecb_Padding(keyByte,merge);//加密
        String chp = Hex.toHexString(enc);//转16进制码
        
        /*//解密
        byte[] decrypt = sm4.decrypt_Ecb_NoPadding(keyByte,enc);
        String  decryptStr = new String(decrypt, "UTF-8");
        System.out.println("发送的sm4解密出来的数据:"+decryptStr);*/
        
		 Map<String, Object> map = new LinkedHashMap<>();
	     map.put("rsuId", rsuId);
	     map.put("frameId", frameId);
	     map.put("data", chp);
	     JSONObject jsonObject = JSONUtil.parseObj(map);
	     System.out.println("发送的数据:"+jsonObject);
		String returnData =HttpRequest.post("http://10.240.36.99:8081/etckey/card/getVehInfo").body(jsonObject.toString()).execute().body();//json方式
		
		JSONObject returnJson = JSONUtil.parseObj(returnData);
		byte[] hex = hexStrToByteArr(returnJson.get("data").toString());
		byte[] decrypt = sm4.decrypt_Ecb_NoPadding(keyByte,hex);
        String  decryptStr = new String(decrypt, "UTF-8");
        System.out.println("返回data数据解密:"+decryptStr);
		return returnData;
	}
	
	public static void main(String[] args) throws Exception {
		//MAC1在线计算接口
		Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("cardId", "1801232204653283");
        dataMap.put("random", "5E525E0B");
        dataMap.put("treadNo", "0008");
        dataMap.put("time", getToday());
        dataMap.put("money", "00000002");
        dataMap.put("keyInfo", "0100");
        dataMap.put("issueId", "B9E3B6ABB9E3B6AB1");
        JSONObject dataJson = JSONUtil.parseObj(dataMap);
        EtcConsumeService etc = new EtcConsumeService();
        String test =etc.getMac("93a5cabdc2f747ae","1",dataJson.toString());
        System.out.println("返回:"+test);
		
		
		/*//车辆信息在线解密接口
		Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("vehInfo", "87C28F6A5873E29EAC00ED7959E523A13C623C7E53F5230E6E1DFEDC5D85221425DAE1EE5457CEE7F7565688913985B92457963AB8A69E929F29A6FF5364F11D9619EFF0BAF67CA70CD0E2F531E9C4A7C2190808FCD04074");
        dataMap.put("random", "30478FBAD358FCCD");
        dataMap.put("tradNo", "86030061201484AE");
        dataMap.put("divCode", "B9E3B6ABB9E3B6AB");
        JSONObject dataJson = JSONUtil.parseObj(dataMap);
        EtcConsumeService etc = new EtcConsumeService();
        String test =etc.getVehInfo("93a5cabdc2f747ae","1",dataJson.toString());
        System.out.println("返回:"+test);*/
		
	}
		
	
}
