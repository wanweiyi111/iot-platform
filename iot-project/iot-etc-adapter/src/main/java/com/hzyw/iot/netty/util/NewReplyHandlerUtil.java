package com.hzyw.iot.netty.util;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.json.JSONObject;

public class NewReplyHandlerUtil {
	HeadHandlerUtil util = new HeadHandlerUtil();
	public byte[] c0Reply() {
		String code = "c0";//指令
		int unix =(int) (System.currentTimeMillis()/1000L);
		String Seconds = util.intToHex(unix);//获取当前时间戳
		String Datetime = util.getToday();//获取当前日期时间
		String LaneMode = "01";//车道模式。1-路边，2-入口，3-出口
		String WaitTime = "0a";//最小重读时间 0-199，10-2000ms
		String TxPower = "0f";//功率级数 0-15
		String PLLChannelID = "01";//信道号，01H 指示信道 1，02H 指示信道 2
		StrBuilder builder = StrBuilder.create();
		builder.append(Seconds)
		.append(Datetime)
		.append(LaneMode)
		.append(WaitTime)
		.append(TxPower)
		.append(PLLChannelID);
		//System.out.println("data数据:"+builder.toString());
		byte[] c0Data = util.newRequestHandler(code,builder.toString());
		return c0Data;
	}
	//B0--C1   OBUID为00
	public byte[] c1ReplyB0() {
		String code = "c1";//指令
		String OBUID = "00000000";//00000000
		byte[] c1Data = util.newRequestHandler(code,OBUID);
		return c1Data;
	}

	//B*--C1
	public byte[] c1Reply(String OBUID) {
		String code = "c1";//指令
		String obuidFilter  =util.obuidFilter(OBUID);//过滤fe01，fe00
		byte[] c1Data = util.newRequestHandler(code,obuidFilter);
		return c1Data;
	}

	public byte[] c2Reply(String OBUID,String stopType) {
		String code = "c2";//指令
		String obuidFilter  =util.obuidFilter(OBUID);//过滤fe01，fe00
		String StopType = stopType;//1-重新OBU流程，0-终止OBU流程
		StrBuilder builder = StrBuilder.create();
		builder.append(obuidFilter)
		.append(StopType);
		//System.out.println("data数据:"+builder.toString());
		byte[] c2Data = util.newRequestHandler(code,builder.toString());
		//System.out.println("c2明文："+util.convertByteToHexString(c2Data));
		return c2Data;
	}
	
	
	public byte[] c6Reply(JSONObject c6json) {
		String code = "c6";//指令
		String OBUID=c6json.get("OBUID").toString();
		String ConsumeMoney = "00000000";//扣款额，分为单位   00000064=1元
		String station = c6json.get("OBU0019").toString();//过站信息，0019文件
		String Datetime = util.getToday();//获取当前日期时间
		
		System.out.println("OBUID:"+OBUID);
		System.out.println("ConsumeMoney:"+ConsumeMoney);
		System.out.println("station:"+station);
		System.out.println("Datetime:"+Datetime);
		
		
		StrBuilder builder = StrBuilder.create();
		builder.append(OBUID).append(ConsumeMoney).append(station).append(Datetime);
		//System.out.println("data数据:"+builder.toString());
		byte[] c6Data = util.newRequestHandler(code,builder.toString());
		System.out.println("发送C6明文:"+util.convertByteToHexString(c6Data));
		return c6Data;
	}

}
