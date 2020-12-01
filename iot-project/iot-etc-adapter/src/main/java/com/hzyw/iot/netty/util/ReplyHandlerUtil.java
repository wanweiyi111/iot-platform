package com.hzyw.iot.netty.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;

public class ReplyHandlerUtil {
	HeadHandlerUtil util = new HeadHandlerUtil();
	public final static Map<String,String> map = new HashMap<String,String>();//MAC地址---车载单元一级分散因子
	//b0响应，还未拿到mac地址
	public byte[] c1Reply() {
		String code = "c1";//指令
		String obuId = "00000000";//车载单元MAC地址
		String obuDivFactor = "0000000000000000";//车载单元一级分散因子
		String combinationC1 = code+obuId+obuDivFactor;//组合数据
		byte[] c1data = util.requestHandler(combinationC1);
		System.out.println("c1指令响应:"+util.convertByteToHexString(c1data));
		return c1data;
	}
	
	//b2响应,拿到mac地址和单元,并存起来
		public byte[] c1Replyb2(JSONObject b2json) {
			String code = "c1";//指令
			String obuId = b2json.get("obuId").toString();//车载单元MAC地址
			String obuDivFactor = b2json.get("issuerIdentifier").toString();//车载单元一级分散因子
			if(map==null || map.size()<1) {
				map.put(obuId, obuDivFactor);
			}else {
			Iterator keys = map.keySet().iterator();
	        while(keys.hasNext()){
	            String key = (String)keys.next();
	            if(!obuId.equals(key)){
	            	map.put(obuId, obuDivFactor);
	            }
	        }
			}
			StrBuilder builder = StrBuilder.create();
			builder.append(code)
			.append(obuId).append(obuDivFactor);
			
			byte[] c1data = util.requestHandler(builder.toString());
			System.out.println("c1指令响应:"+util.convertByteToHexString(c1data));
			return c1data;
		}
	//根据MAC地址去找单元数据
	public byte[] c1ReplyJson(JSONObject json) {
		String code = "c1";//指令
		String obuId = json.get("obuId").toString();//车载单元MAC地址
		String obuDivFactor = map.get(obuId).toString();
		StrBuilder builder = StrBuilder.create();
		builder.append(code)
		.append(obuId).append(obuDivFactor);
		byte[] c1data = util.requestHandler(builder.toString());
		System.out.println("c1指令响应:"+util.convertByteToHexString(c1data));
		return c1data;
	}
	

	public byte[] c6Reply(JSONObject c6json) {
		String code = "c6";//指令
		String obuId = c6json.get("obuId").toString();//车载单元MAC地址
		String cardDivFactor = map.get(obuId).toString();//CPU用户卡一级分散因子
		String writeRecord = "00";//写0019记录
		String consumeMoney = "00000000";//扣款额，高字节在前
		String purchaseTime = util.getToday();//用此时间去计算TAC码
		String station = c6json.get("lastStation").toString();//国标卡过站信息0019 文件记录
		
		StrBuilder builder = StrBuilder.create();
		builder.append(code)
		.append(obuId)
		.append(cardDivFactor)
		.append(writeRecord)
		.append(consumeMoney)
		.append(purchaseTime)
		.append(station);
		//System.out.println("data数据:"+builder.toString());
		byte[] c6Data = util.requestHandler(builder.toString());
		System.out.println("c6指令响应:"+util.convertByteToHexString(c6Data));
		return c6Data;
	}

	public byte[] c2Reply(JSONObject b5json) {
		String code = "c2";//指令
		String obuId = b5json.get("obuId").toString();//车载单元MAC地址
		String stopType = "01";//1：重新搜索车载单元；2：重新发送当前帧
		StrBuilder builder = StrBuilder.create();
		builder.append(code)
		.append(obuId)
		.append(stopType);
		//System.out.println("data数据:"+builder.toString());
		byte[] c2Data = util.requestHandler(builder.toString());
		System.out.println("c2指令响应:"+util.convertByteToHexString(c2Data));
		return c2Data;
	}

	
	

	
}
