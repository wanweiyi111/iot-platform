package com.hzyw.iot.service.carParkService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.hzyw.iot.netty.util.HeadHandlerUtil;
import com.hzyw.iot.util.sm2.SM2Utils;
import com.hzyw.iot.util.sm2.Util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class EtcCarParkService {
	String pubk = "04C848306D64058309AE5C83B0870BAE04D1B0C7BC4C9DCD995D77CEF0ECDD3C86AF995D2FC105E1630FAFA63AAF47F5C38170E42D1C8C2C9EFAA2D3CD9AEA478B";//SM2公钥
	String prik = "8707D50382E9E52F21BE83D260098EB101033956CBFB465CA757464ABE2CD3BE";//SM2私钥
	
	
	public String  sm2(String text) {
		//,String privateKeyStr,String publicKeyStr
		SM2 sm2 = SmUtil.sm2();
		//System.out.println("公钥:"+sm2.getPublicKeyBase64());
		//System.out.println("私钥:"+sm2.getPrivateKeyBase64());
		// 公钥加密，私钥解密
		String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
		//System.out.println("公钥："+KeyType.PublicKey);
		//System.out.println("公钥加密后:"+encryptStr);
		//String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
		//System.out.println("私钥解密:"+decryptStr);
		return encryptStr;
	}
	
	//车辆入场信息传入
	public String entryUploadInfo(String url,long parkId,String data) throws Exception {
		byte[] sourceData = data.getBytes("GBK");
		String sm2Str = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);//加密数据
		String sm2 = sm2Str.substring(2, sm2Str.length());//去掉04
		
		/*String plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), Util.hexToByte(sm2Str)),"GBK");
		System.out.println("自行解密:"+plainText);*/
		
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("parkId", parkId);
		jsonObj.put("data", sm2);
		System.out.println("发送的数据"+jsonObj.toString());
		
		String returnData =HttpRequest.post(url).body(jsonObj.toString()).execute().body();//HTTP请求json方式
		System.out.println("收到的数据:"+returnData);
		JSONObject returnJson = JSONUtil.parseObj(returnData);
		String plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), hexStrToByteArr("04"+returnJson.get("data").toString())),"GBK");
		
		return plainText;
	}
	//车辆出场信息传入
	public String exitUploadInfo(String url,long parkId,String data) throws Exception{
		byte[] sourceData = data.getBytes("GBK");
		String sm2Str = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);//加密数据
		String sm2 = sm2Str.substring(2, sm2Str.length());//去掉04
		
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("parkId", parkId);
		jsonObj.put("data", sm2);
		System.out.println("发送的数据"+jsonObj.toString());
		
		String returnData =HttpRequest.post(url).body(jsonObj.toString()).execute().body();//json方式
		System.out.println("收到的数据:"+returnData);
		JSONObject returnJson = JSONUtil.parseObj(returnData);
		String plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), hexStrToByteArr("04"+returnJson.get("data").toString())),"GBK");
		return plainText;
	}
	
	//黑名单验证
	public String checkCard(String url,long parkId,String data) throws Exception{
		byte[] sourceData = data.getBytes("GBK");
		String sm2Str = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);//加密数据
		String sm2 = sm2Str.substring(2, sm2Str.length());//去掉04
		
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("parkId", parkId);
		jsonObj.put("data", sm2);
		
		String returnData =HttpRequest.post(url).body(jsonObj.toString()).execute().body();//json方式
		System.out.println("收到的数据:"+returnData);
		JSONObject returnJson = JSONUtil.parseObj(returnData);
		String plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), hexStrToByteArr("04"+returnJson.get("data").toString())),"GBK");
		return plainText;
	}
	
	
	//交易信息对账
	public String reconciliation(String url,long parkId,String data) throws Exception{
		byte[] sourceData = data.getBytes("GBK");
		String sm2Str = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);//加密数据
		String sm2 = sm2Str.substring(2, sm2Str.length());//去掉04
		
		JSONObject jsonObj=new JSONObject();
		jsonObj.put("parkId", parkId);
		jsonObj.put("data", sm2);
		
		String returnData =HttpRequest.post(url).body(jsonObj.toString()).execute().body();//json方式
		System.out.println("收到的数据:"+returnData);
		JSONObject returnJson = JSONUtil.parseObj(returnData);
		String plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), hexStrToByteArr("04"+returnJson.get("data").toString())),"GBK");
		return plainText;		
	}
	
	//状态监控
		public String laneMonitor(String url,long parkId,String data) throws Exception{
			byte[] sourceData = data.getBytes("GBK");
			String sm2Str = SM2Utils.encrypt(Util.hexToByte(pubk), sourceData);//加密数据
			String sm2 = sm2Str.substring(2, sm2Str.length());//去掉04
			
			JSONObject jsonObj=new JSONObject();
			jsonObj.put("parkId", parkId);
			jsonObj.put("data", sm2);
			
			String returnData =HttpRequest.post(url).body(jsonObj.toString()).execute().body();//json方式
			System.out.println("收到的数据:"+returnData);
			JSONObject returnJson = JSONUtil.parseObj(returnData);
			String plainText = new String(SM2Utils.decrypt(Util.hexToByte(prik), hexStrToByteArr("04"+returnJson.get("data").toString())),"GBK");
			
			return plainText;			
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
	
	public static void main(String[] args) throws Exception {
		EtcCarParkService etc = new EtcCarParkService();
		Date date = DateUtil.date(System.currentTimeMillis());//yyyy-MM-dd HH:mm:ss
		String format = DateUtil.format(date, "yyyyMMdd");
		String format1 = DateUtil.format(date, "yyyyMMddHHmmss");
		String format2 = DateUtil.format(date, "yyyy-MM-dd");
		String format3 = DateUtil.format(date, "HH:mm:ss");
		
		//车辆入场信息传入
		JSONObject json=new JSONObject();
		json.put("programStartTime", date);	
		json.put("recordNo", 1234567);
		json.put("transSn", "5401000002"+"20200518095032"+"05");//format1
		json.put("parkId", "5401000002");
		json.put("recordType", 1);
		json.put("lane", 1);
		json.put("entryTime", "20200518095032");//-----和出场联系	format1
		json.put("statisticDate", format);
		//json.put("operatorId", "");
		//json.put("shift", 0);
		json.put("vehplateNo", "粤B38D86");//VehicleLicencePlateNumber (车牌)
		json.put("vehplateColor", 0);//VehicleLicencePlateColor (车颜色)
		json.put("vehType", 1);//VehicleClass(车辆类型)
		json.put("cardSn", "44011620221000004664");//卡网络编号+0015的cardNum
		json.put("obuTransId", "00000001");//PSAMTransSerial PSAM卡交易序号
		json.put("obuId", "0812eecb");//OBUID
		json.put("passMethod", 2);
		json.put("deviceStatlus", 0);
		json.put("flag", 0);
		//json.put("Spare1", "");
		//json.put("Spare2", "");
		//json.put("Spare3", "");
		//json.put("Spare4", "");
		System.out.println("json明文数据"+json.toString());
		String url ="http://10.233.48.45:8079/liquidation/entry/uploadInfo";
		long parkId =5401000002L;
		String test = etc.entryUploadInfo(url, parkId, json.toString());
		System.out.println("解析的数据:"+test);
		
		/*
		//车辆出场信息传入
		JSONObject json=new JSONObject();
		json.put("programStartTime", date);
		json.put("recordNo", 1234567);
		json.put("transSn", "5401000002"+"20200518095032"+"05");//format1 
		json.put("parkId", "5401000002");
		json.put("recordType", 1);
		json.put("vehplateNo", "粤B38D86");//VehicleLicencePlateNumber车牌号码
		json.put("vehplateColor", 0);//VehicleLicencePlateColor车牌颜色
		json.put("vehType", 1);//VehicleClass车辆类型
		json.put("lane", 1);
		json.put("entryTime", "20200518095032");//----和入场时间匹配
		json.put("exitLane", 1);
		json.put("exitTime", "20200518095532");//出口时间     ----TransTime
		json.put("statisticDate", Integer.parseInt(format));
		json.put("exitOperator", 2);
		json.put("exitShift", 2);
		json.put("parkTime", 300);//-----停车时长,需要计算(秒)
		json.put("specialInf", 1);
		json.put("receivableTotalAmount", 1);//应收金额
		json.put("discountAmount", 0);//优惠金额
		json.put("actualIncomeAmount", 1);//实收金额
		
		JSONObject discountInfo=new JSONObject();//优惠金额组成
		discountInfo.put("discountType", 1);
		discountInfo.put("discountSerialNo", "012345678");
		discountInfo.put("discountDetailAmount", 1);
		discountInfo.put("payCompany", "测试");
		discountInfo.put("discountDetail", "测试");
		//discountInfo.put("discountNo", "");
		json.put("discountInfo", discountInfo);
		
		json.put("payAmount", 1);
		json.put("payMethod", 2);
		json.put("paySerialNo", "012345678");
		json.put("passMethod", 1);
		json.put("cardSn", "44011620221000004664");//卡网络编号+0015的cardNum
		json.put("obuTransId", "00000001");//PSAMTransSerial  PSAM卡交易序号
		json.put("obuId", "0812eecb");//OBUID
		json.put("transactionDate", format2);
		json.put("time", format3);
		json.put("fee", 1);
		json.put("serviceType", 2);
		json.put("transType", "09");//交易类型标识,固定是“０９”
		json.put("terminalNo", "000000000000");//RSUTerminalId  0016文件中的终端机编号
		json.put("issuerIdentifier", "b9e3b6ab44010001");//ContractProvider 发行商代码
		json.put("cardNetNo", "4401");//卡网络编号-----0015文件里cardNetNo
		json.put("cardType", "22");
		json.put("preBalance", 3);
		json.put("postBalance", 2);
		json.put("cardSerialNo", "002e");//ETCTradNo联机交易序号
		json.put("cardRandom", "12345");----CardRandom
		json.put("terminalTradeNo", "12345678");----没有
		json.put("TAC", "38b2cccc");//B5里的TAC码
		json.put("algorithmIdentifier", 2); ---没有
		json.put("deviceType", "1");  
		json.put("deviceNo", "12345"); ---没有
		json.put("EntryDate", Integer.parseInt(format));  
		json.put("ExitStation", 1234567);  ---没有
		json.put("DeviceStatus", 1); ---没有
		json.put("parkTimeStr", "123");  ---没有
		json.put("ProgramVer", "20200518095532");//format1  ---可执行程序生成时间
		System.out.println(json.toString());
		json.put("Spare1", "");
		json.put("Spare2", "");
		json.put("Spare3", "");
		json.put("Spare4", "");
		String url ="http://10.233.48.45:8079/liquidation/exit/uploadInfo";
		long parkId =5401000002L;
		String test = etc.exitUploadInfo(url, parkId, json.toString());
		System.out.println("解析的数据:"+test);*/
		
		/*//黑名单验证
		JSONObject json=new JSONObject();
		json.put("cardId", "630123");
		json.put("obuId", "6301171060801234");
		json.put("issueId", "630123");
		String url ="http://10.233.48.45:8079/blacklist/blacklist/checkCard";
		long parkId =5401000002L;
		String test = etc.checkCard(url, parkId, json.toString());
		System.out.println("解析的数据:"+test);*/
		
		/*//交易信息对账
		JSONObject json=new JSONObject();
		json.put("msgid", format1);
		
		ArrayList<String> list=new ArrayList<String>();
		list.add("54010000022020040115374001");
		json.put("transSns", list);
		
		json.put("fees", 1);
		String url ="http://10.233.48.45:8079/liquidation/exit/Reconciliation";
		long parkId =5401000002L;
		String test = etc.reconciliation(url, parkId, json.toString());
		System.out.println("解析的数据:"+test);*/
		
		/*//状态监控
		JSONObject json=new JSONObject();
		json.put("lanID", 1);
		json.put("laneType", 0);
		json.put("workStatus", 0);
		json.put("cpuCosts", 50);
		json.put("memoryCosts", 50);
		json.put("diskCosts", 50);
		json.put("barStatus", 0);
		json.put("cameraStatus", 0);
		json.put("feeStatus", 0);
		json.put("antStatus", 0);
		json.put("scannerStatus", 0);
		json.put("ringTrigger", 0);
		json.put("ringExist", 0);
		json.put("ringBar", 0);
		json.put("spare1", "");
		json.put("spare2", "");
		json.put("spare3", "");
		json.put("spare4", "");
		String url ="http://10.233.48.45:8079/liquidation/entry/laneMonitor";
		long parkId =5401000002L;
		String test = etc.laneMonitor(url, parkId, json.toString());
		System.out.println("解析的数据:"+test);*/
		
	}
}
