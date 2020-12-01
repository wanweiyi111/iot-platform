package com.hzyw.iot.util;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class Sm4Test {
	public static void main(String[] args) {
	    try {
	       /* Map<String, Object> dataMap = new LinkedHashMap<>();
	        dataMap.put("cardId", "1801232204653283");
	        dataMap.put("random", "5E525E0B");
	        dataMap.put("treadNo", "0008");
	        dataMap.put("time", getToday());
	        dataMap.put("money", "00000002");
	        dataMap.put("keyInfo", "0100");
	        dataMap.put("issueId", "B9E3B6ABB9E3B6AB");
	        JSONObject dataJson = JSONUtil.parseObj(dataMap);
	        System.out.println("data数据:"+dataJson.toString());
	        // 自定义的32位16进制密钥
	        String key = "5358474C4554435041524B0234760002";
	        //String key = "0123456789abcdeffedcba9876543210";
	        String cipher = Sm4Utils.encryptEcb(key, dataJson.toString());
	        System.out.println("SM4数据:"+cipher);//05a087dc798bb0b3e80553e6a2e73c4ccc7651035ea056e43bea9d125806bf41c45b4263109c8770c48c5da3c6f32df444f88698c5c9fdb5b0055b8d042e3ac9d4e3f7cc67525139b64952a3508a7619
	        Map<String, Object> map = new LinkedHashMap<>();
	        map.put("rsuId", "93a5cabdc2f747ae");
	        map.put("frameId", "1");
	        map.put("data", cipher);
	        JSONObject jsonObject = JSONUtil.parseObj(map);
	        System.out.println("发送的数据:"+jsonObject.toString());
	       String test =HttpRequest.post("http://hynas.imdo.co:10087/park/rsu/getMac").body(jsonObject.toString()).execute().body();//json方式
	        System.out.println("返回的数据:"+test);
	        */
	        //JSONObject getVehInfo = JSONUtil.parseObj(test);
	       //System.out.println(Sm4Utils.verifyEcb(key, cipher, getVehInfo.get("data")));// true
	        //System.out.println(jsonObject.get("data").toString());
	        
	        String content = "5358474C4554435041524B0697668609";
			String data = "DB5FABDAE7A43A74C27C54D4619554B47E5834907A6D5AA3931E891DFD88D88C9AA99BD42C8AE963772CE26A3BE42EDDE9EB3EE7792B489DDA427F1CB9EFE91EF0DF733E7395C629068434A1B6536926F0326D6085EB795123D2B14C0E7B1AAB456DC5099FC097715140CF5F87BA97732F8C3D25972C0220E006C30BFEC91F6C7D5CDE093826A3410E3DB2CB32041E8813A6992595FFA776321D9C16EF1326AC01CF660C7E540DB4831DEDC77BCDD6642EDE63E920F5639F564DBC7F5579ABBC";
			
	        String test1 = Sm4Utils.decryptEcb(content, data);
	        
	        System.out.println("发送解出来的数据"+test1);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * 当前时间格式化
	 */
	public static String getToday() {
		Date date = DateUtil.parse(DateUtil.now().toString());
		String format = DateUtil.format(date, "yyyyMMddHHmmss");// 日期格式
		return format;
	}
}
