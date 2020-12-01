package com.hzyw.iot.netty.newClient.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.core.text.StrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;


public class NewActiveHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewActiveHandler.class);
	HeadHandlerUtil util = new HeadHandlerUtil();
	/**
	 * 向server发送消息
	 */
	public void activeHandler(ChannelHandlerContext ctx) {
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
		System.out.println("c0初始化指令:"+util.convertByteToHexString(c0Data));
		sendBuf(ctx,c0Data);
	}
	
	
	/**
	 * 发送数据
	 */
	public void sendBuf(ChannelHandlerContext ctx,byte[] dataByte) {
		ByteBuf byteBuf= ctx.alloc().buffer();
		byteBuf.writeBytes(dataByte);
		ctx.writeAndFlush(byteBuf);
	}
}
