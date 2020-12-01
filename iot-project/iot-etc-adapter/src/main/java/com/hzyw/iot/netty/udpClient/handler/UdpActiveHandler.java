package com.hzyw.iot.netty.udpClient.handler;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.hzyw.iot.netty.util.HeadHandlerUtil;
import com.hzyw.iot.netty.util.NewReplyHandlerUtil;
import com.hzyw.iot.util.Sm4;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SocketUtils;

public class UdpActiveHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(UdpActiveHandler.class);
	HeadHandlerUtil util = new HeadHandlerUtil();
	NewReplyHandlerUtil reply = new NewReplyHandlerUtil();
	String key = "5358474C4554435041524B9529140773";//秘银
	/**
	 * 向server发送消息
	 * @throws Exception 
	 */
	public void activeHandler(ChannelHandlerContext ctx) throws Exception {
		/*//打开天线
		byte[] c2Data = reply.c2Reply("ffffffff","01");//获取c2数据(开关天线)  01开，00关
		String sm4EncryptC2 = util.sm4Encrypt(key,c2Data);//sm4加密
        byte[] cxJsonByteC2= util.cxJson(sm4EncryptC2);//json数据，以及补充00
		sendBuf(ctx,cxJsonByteC2);*/
		//Thread.sleep(2000);//睡眠3秒											
		//c0初始化
		byte[] c0Data = reply.c0Reply();//获取c0数据(初始化指令)
		String sm4Encrypt = util.sm4Encrypt(key,c0Data);//sm4加密
		System.out.println("加密后数据:"+sm4Encrypt);
        String sm4Decrypt = util.sm4Decrypt(key,sm4Encrypt);//sm4解密
        System.out.println("发送的sm4解密出来的数据:"+sm4Decrypt);
        byte[] cxJsonByte= util.cxJson(sm4Encrypt);//json数据，以及补充00
		sendBuf(ctx,cxJsonByte);
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
