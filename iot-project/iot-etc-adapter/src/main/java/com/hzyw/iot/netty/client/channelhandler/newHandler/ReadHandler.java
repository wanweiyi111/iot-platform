package com.hzyw.iot.netty.client.channelhandler.newHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.netty.util.ReadHandlerUtil;
import com.hzyw.iot.netty.util.ReplyHandlerUtil;
import com.hzyw.iot.netty.vo.ModbusInfo;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ReadHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadHandler.class);
	
	/**
	 * 接收server端的消息
	 */
	public void readHandler(ChannelHandlerContext ctx, ByteBuf buf) {
		//HeadHandlerUtil util = new HeadHandlerUtil();
		ReadHandlerUtil readUtil = new ReadHandlerUtil();//指令解析
		ReplyHandlerUtil replyUtil = new ReplyHandlerUtil();//回复指令
		ModbusInfo modbusInfo = new ModbusInfo(buf);
		String code = modbusInfo.getDataType_str();//指令
		System.out.println("收到的指令:" + code);
		System.out.println("收到的报文:" + modbusInfo.getFullData_str());
		
		ByteBuf dataBuf = Unpooled.wrappedBuffer(modbusInfo.getData());//dataBuf
		/*System.out.println("STX 帧报文头:" + modbusInfo.getStx_str());
		System.out.println("VER 协议版本:" + modbusInfo.getVer_str());
		System.out.println("SEQ 帧序号:" + modbusInfo.getSeq_str());
		System.out.println("LEN DATA长度:" + modbusInfo.getLen_str());
		System.out.println("DATA数据域:" + modbusInfo.getData_str());
		System.out.println("CRC 校验码:" + modbusInfo.getCrc_str());
		System.out.println("全部数据包:" + modbusInfo.getFullData_str());*/
		
		switch (code) {
		case "b0"://设备状态信息帧,初始化返回的数据
			JSONObject b0json = readUtil.b0Analysis(dataBuf);
			System.out.println("b0数据:"+b0json);
			byte[] c1byte3 =replyUtil.c1Reply();//c1指令
			sendBuf(ctx,c1byte3);
		break;
		case "b2"://车载单元信息帧
			JSONObject b2json = readUtil.b2Analysis(dataBuf);
			if(b2json==null) {
				System.out.println("心跳幀:"+modbusInfo.getFullData_str());
				break;
			}
			System.out.println("b2数据:"+b2json);
			byte[] c1byte =replyUtil.c1Replyb2(b2json);//c1指令
			sendBuf(ctx,c1byte);
			break;
		case "b3"://车辆信息帧
			JSONObject b3json = readUtil.b3Analysis(dataBuf);
			System.out.println("b3数据:"+b3json);
			byte[] c1byte1 =replyUtil.c1ReplyJson(b3json);//c1指令
			sendBuf(ctx,c1byte1);
			break;
		case "b4"://用户卡信息帧
			JSONObject b4json = readUtil.b4Analysis(dataBuf);
			System.out.println("b4数据:"+b4json);
			byte[] c6byte =replyUtil.c6Reply(b4json);//c6指令
			sendBuf(ctx,c6byte);
			break;
		case "b5"://交易信息帧
			JSONObject b5json = readUtil.b5Analysis(dataBuf);
			System.out.println("b5数据:"+b5json);
			//byte[] c2byte =replyUtil.c2Reply(b5json);
			byte[] c1byte2 =replyUtil.c1ReplyJson(b5json);//c1指令
			sendBuf(ctx,c1byte2);
			break;
		case "d0":
			JSONObject d0json = readUtil.d0Analysis(dataBuf);
			System.out.println("d0数据:"+d0json);
			break;
		case "bd"://PSAM授权初始化
			JSONObject bdjson = readUtil.bdAnalysis(dataBuf);
			System.out.println("bd数据:"+bdjson);
			break;
		default:
			System.out.println("没有这个指令:" + modbusInfo.getFullData_str());
			break;
		}
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
