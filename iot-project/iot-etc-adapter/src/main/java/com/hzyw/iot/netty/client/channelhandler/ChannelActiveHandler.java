package com.hzyw.iot.netty.client.channelhandler;

import com.hzyw.iot.netty.util.HeadHandlerUtil;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ChannelActiveHandler {

	/**
	 * 向server发送消息
	 */
	public void activeHandler(ChannelHandlerContext ctx) {
		 try {
			//String str = "ffff001000000016c05e423b4b20200211132739110a0f01010400010000ffff";
			//String data ="c05e423b4b20200211132739110a0f01010400010000";//data数据
			HeadHandlerUtil util =new HeadHandlerUtil();
			
			String cmdType = "c0";//指令
			int unix =(int) (System.currentTimeMillis()/1000L);
			String unixStr = util.intToHex(unix);//获取当前时间戳
			String datetime = util.getToday();//获取当前日期时间
			String laneMode = "11";//车道工作模式：11H-路段 ETC 门架；12H-省界入口 ETC
			String BSTInterval = "0a";//路侧单元自动发送 BST 的间隔，单位 ms
			String txPower = "0f";//路侧单元功率级数，最大值 31（1FH）
			String PLLChannelID = "01";//信道号，01H 指示信道 1，02H 指示信道 2
			String transMode = "01";//01H：复合交易
			String flagID = "040001";//ETC 门架编号
			String reserved = "0000";//保留字节，填充 00H
			
			StrBuilder builder = StrBuilder.create();
			builder.append(cmdType)
			.append(unixStr)
			.append(datetime)
			.append(laneMode)
			.append(BSTInterval)
			.append(txPower)
			.append(PLLChannelID)
			.append(transMode)
			.append(flagID)
			.append(reserved);
			
			//System.out.println("data数据:"+builder.toString());
			
			
			byte[] allData = util.requestHandler(builder.toString());
			System.out.println("c0初始化指令发送:"+util.convertByteToHexString(allData));
		    ByteBuf byteBuf = ctx.alloc().buffer();
		    byteBuf.writeBytes(allData);
		    ctx.writeAndFlush(byteBuf);
		    
		    //Thread.sleep(3000);
		    
		    String c4 = "c4";//指令
		    String controlType ="01";//开关路测单元指令 1开,0关
		    String mergeC4 = c4+controlType;//合并
        	byte[] allDataC4 = util.requestHandler(mergeC4);
        	System.out.println("c4开关指令发送:"+util.convertByteToHexString(allDataC4));
		    ByteBuf byteBuf1 = ctx.alloc().buffer();
		    byteBuf1.writeBytes(allDataC4);
		    ctx.writeAndFlush(byteBuf1);
		    
		    /*//psam初始化指令
		    String ca = "ca";//指令
		    String psamChannel = "0001";//授权的 PSAM 通道号
		    String mergeCa = ca+psamChannel;//合并
        	byte[] allDataCa = util.requestHandler(mergeCa);
        	System.out.println("CA  PSAM 初始化指令:"+util.convertByteToHexString(allDataCa));
		    ByteBuf byteBuf2 = ctx.alloc().buffer();
		    byteBuf2.writeBytes(allDataCa);
		    ctx.writeAndFlush(byteBuf2);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	}
	

	
}
