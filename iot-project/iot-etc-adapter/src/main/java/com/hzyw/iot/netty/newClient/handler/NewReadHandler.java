package com.hzyw.iot.netty.newClient.handler;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.netty.util.HeadHandlerUtil;
import com.hzyw.iot.netty.util.NewReadHandlerUtil;
import com.hzyw.iot.netty.util.NewReplyHandlerUtil;
import com.hzyw.iot.netty.util.ReadHandlerUtil;
import com.hzyw.iot.netty.util.ReplyHandlerUtil;
import com.hzyw.iot.netty.vo.ModbusInfo;
import com.hzyw.iot.netty.vo.NewModbusInfo;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;


public class NewReadHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewReadHandler.class);
	
	
	/**
	 * 接收server端的消息
	 * @throws Exception 
	 */
	public void readHandler(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		/*NewReadHandlerUtil readUtil = new NewReadHandlerUtil();//指令解析
		NewReplyHandlerUtil replyUtil = new NewReplyHandlerUtil();//回复指令
		HeadHandlerUtil util =new HeadHandlerUtil();
		byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = HeadHandlerUtil.convertByteToHexString(req);
        System.out.println("原数据:"+body);
        String handlebody = HeadHandlerUtil.handlebody(body);//转义
        ByteBuf allDataBuf = Unpooled.wrappedBuffer(Hex.decode(handlebody));//转buf
        
		NewModbusInfo modbusInfo = new NewModbusInfo(allDataBuf);
		System.out.println("LEN DATA长度:" + modbusInfo.getLen_str());
		System.out.println("收到的报文:" + modbusInfo.getFullData_str());
		System.out.println("CRC 校验码:" + modbusInfo.getCrc_str());
		System.out.println("DATA数据域:" + modbusInfo.getData_str());
		int fe =modbusInfo.getData_str().indexOf("fe");
		if(fe != -1) {
			System.out.println("字符串str中包含子"+fe);
		}else {
			System.out.println("字符串str中不包含"+fe);
		}
		
		
		ByteBuf dataBuf = Unpooled.wrappedBuffer(modbusInfo.getData());//dataBuf
		System.out.println("STX 帧报文头:" + modbusInfo.getStx_str());
		System.out.println("com 指令码:" + modbusInfo.getCom_str());
		System.out.println("LEN DATA长度:" + modbusInfo.getLen_str());
		System.out.println("DATA数据域:" + modbusInfo.getData_str());
		System.out.println("CRC 校验码:" + modbusInfo.getCrc_str());
		System.out.println("ETX 结束符:" + modbusInfo.getEtx_str());
		System.out.println("全部数据包:" + modbusInfo.getFullData_str());
		
		switch (modbusInfo.getCom_str()) {
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
		}*/
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
