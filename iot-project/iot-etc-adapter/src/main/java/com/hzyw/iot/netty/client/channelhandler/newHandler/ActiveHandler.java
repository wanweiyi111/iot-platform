package com.hzyw.iot.netty.client.channelhandler.newHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.core.text.StrBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;


public class ActiveHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveHandler.class);
	
	HeadHandlerUtil util = new HeadHandlerUtil();
	/**
	 * 向server发送消息
	 */
	public void activeHandler(ChannelHandlerContext ctx) {
		String code = "c0";//指令
		int unix =(int) (System.currentTimeMillis()/1000L);
		String unixStr = util.intToHex(unix);//获取当前时间戳
		String datetime = util.getToday();//获取当前日期时间
		String laneMode = "06";//车道工作模式：3-封闭式ETC入口；4-封闭式ETC出口；6-ETC开放式；7-标识站；8-省界合建站
		String BSTInterval = "0a";//路侧单元自动发送BST的间隔，单位ms，建议值10ms
		String txPower = "0f";//路侧单元功率级数，最大值 31（1FH）
		String PLLChannelID = "01";//信道号，01H 指示信道 1，02H 指示信道 2
		String transMode = "01";//01H：复合交易
		String worktype = "81";//bit7: 是否有标识站应用 1-有 0-无；bit6：国密PSAM卡是否需要授权:1是，0否；bit5~ bit2：保留；bit1:路径识别文件选择, 1-EF04+0008, 0-EF04bit0：是否清除标识内容 1-清除 0-不清除
		String b0Reserved = "00";//保留字节为WaitTime值，设置OBU重复交易时间
		String Len_EF04 = "0000";//处理EF04长度
		String Len_0008 = "00";//处理0008长度
		String Len_0009 = "0000";//处理0009长度
		StrBuilder builder = StrBuilder.create();
		builder.append(code)
		.append(unixStr)
		.append(datetime)
		.append(laneMode)
		.append(BSTInterval)
		.append(txPower)
		.append(PLLChannelID)
		.append(transMode)
		.append(worktype)
		.append(b0Reserved)
		.append(Len_EF04)
		.append(Len_0008)
		.append(Len_0009);
		//System.out.println("data数据:"+builder.toString());
		byte[] c0Data = util.requestHandler(builder.toString());
		System.out.println("c0初始化指令:"+util.convertByteToHexString(c0Data));
		sendBuf(ctx,c0Data);
		 
		 
		String psamAuthorization =util.hexStringToByte(worktype).substring(6); //PSAM卡是否需要授权:1是，0否
		if(Integer.parseInt(psamAuthorization)==1) {//授权初始化
				String codeCd = "cd";//指令
				String channelId ="01";//PSAM卡插槽通道号
				StrBuilder builderCd = StrBuilder.create();
				builder.append(codeCd)
				.append(channelId);
				//System.out.println("data数据:"+builder.toString());
				byte[] cdData = util.requestHandler(builderCd.toString());
				System.out.println("cd授权初始化:"+util.convertByteToHexString(c0Data));
				sendBuf(ctx,cdData);
		}
		
		/*String  controlType = "01";//0：关闭路侧单元，1：打开路侧单元
		byte[] c4 = c4(controlType);
		sendBuf(ctx,c4);*/
	}
	
	//路侧单元开关
		public byte[] c4(String controlType) {
			String code = "c4";//0：关闭路侧单元，1：打开路侧单元
			StrBuilder builder = StrBuilder.create();
			builder.append(code)
			.append(controlType);
			byte[] c0Data = util.requestHandler(builder.toString());
			return c0Data;
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
