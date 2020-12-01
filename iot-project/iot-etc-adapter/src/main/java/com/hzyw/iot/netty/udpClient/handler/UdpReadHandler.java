package com.hzyw.iot.netty.udpClient.handler;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.listener.ListenerService;
import com.hzyw.iot.netty.newClient.handler.NewReadHandler;
import com.hzyw.iot.netty.util.HeadHandlerUtil;
import com.hzyw.iot.netty.util.NewReadHandlerUtil;
import com.hzyw.iot.netty.util.NewReplyHandlerUtil;
import com.hzyw.iot.netty.vo.NewModbusInfo;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class UdpReadHandler {
private static final Logger LOGGER = LoggerFactory.getLogger(NewReadHandler.class);
	String key = "5358474C4554435041524B9529140773";//秘钥
	
	/**
	 * 接收server端的消息
	 * @throws Exception 
	 */
	public void readHandler(ChannelHandlerContext ctx, String msg) throws Exception {
		NewReadHandlerUtil readUtil = new NewReadHandlerUtil();//指令解析
		NewReplyHandlerUtil replyUtil = new NewReplyHandlerUtil();//回复指令
		
		//TimedCache<String, String> timedCache = CacheUtil.newTimedCache(10000);//创建缓存，默认10秒过期(保证数据不完整后关闭连接)
		
		try {
		JSONObject jsonObj=new JSONObject(msg);
		System.out.println("接收到的JSON数据:"+jsonObj);
		HeadHandlerUtil util =new HeadHandlerUtil();
		if(jsonObj.containsKey("msg")||!jsonObj.get("cmd".toString()).equals("BX")) {
			LOGGER.info("心跳数据:"+jsonObj);
			return;
		}
		String sm4Data = jsonObj.get("data").toString();
		String data = util.sm4Decrypt(key,sm4Data);//SM4解码,并过滤00
		String handlebody = HeadHandlerUtil.handlebody(data);//转义  (fe01--ff	,fe00--fe)
		System.out.println("SM4解码,00过滤,以及转义后的数据:"+handlebody);
		ByteBuf allDataBuf = Unpooled.wrappedBuffer(Hex.decode(handlebody));//转buf
        
		NewModbusInfo modbusInfo = new NewModbusInfo(allDataBuf);
		System.out.println("RSU端命令:" + modbusInfo.getCom_str());
		/*System.out.println("STX 帧报文头:" + modbusInfo.getStx_str());
		System.out.println("com 指令码:" + modbusInfo.getCom_str());
		System.out.println("LEN DATA长度:" + modbusInfo.getLen_str());
		System.out.println("DATA数据域:" + modbusInfo.getData_str());
		System.out.println("xor 校验码:" + modbusInfo.getXor_str());
		System.out.println("全部数据包:" + modbusInfo.getFullData_str());*/
		ByteBuf dataBuf = Unpooled.wrappedBuffer(modbusInfo.getData());//dataBuf
		
		switch (modbusInfo.getCom_str()) {
		case "b0"://设备状态信息帧,初始化返回的数据
			JSONObject b0json = readUtil.b0Analysis(dataBuf);
			System.out.println("b0数据:"+b0json);
			byte[] c1ByteB0 =replyUtil.c1ReplyB0();//c1指令
			String sm4Encrypt = util.sm4Encrypt(key,c1ByteB0);//sm4加密
			byte[] cxJsonByte= util.cxJson(sm4Encrypt);//json数据，以及补充00
			sendBuf(ctx,cxJsonByte);
		break;
		case "b2"://车载单元信息帧
			JSONObject b2json = readUtil.b2Analysis(dataBuf);
			if(b2json==null) {
				System.out.println("b2数据有误:"+modbusInfo.getFullData_str());
				break;
			}
			// 放进缓存
			if(ListenerService.timedCache1.get(b2json.get("OBUID").toString()) == null) {
			ListenerService.timedCache1.put(b2json.get("OBUID").toString(),b2json.get("OBUID").toString());
			ListenerService.timedCache2.put(b2json.get("OBUID").toString(),b2json.get("OBUID").toString());
			}
			
			System.out.println("b2数据:"+b2json);
			byte[] c1Replyb2 =replyUtil.c1Reply(b2json.get("OBUID").toString());//c1指令
			String sm4EncryptB2 = util.sm4Encrypt(key,c1Replyb2);//sm4加密
			byte[] cxJsonByteB2= util.cxJson(sm4EncryptB2);//json数据，以及补充00
			sendBuf(ctx,cxJsonByteB2);
			break;
		case "b3"://车辆信息帧
			JSONObject b3json = readUtil.b3Analysis(dataBuf);
			if(b3json==null) {
				System.out.println("b3数据有误:"+modbusInfo.getFullData_str());
				break;
			}
			System.out.println("b3数据:"+b3json);
			
			ListenerService.timedCache1.get(b3json.get("OBUID").toString());
			ListenerService.timedCache2.get(b3json.get("OBUID").toString());
			
			byte[] c1Replyb3 =replyUtil.c1Reply(b3json.get("OBUID").toString());//c1指令
			String sm4EncryptB3 = util.sm4Encrypt(key,c1Replyb3);//sm4加密
			byte[] cxJsonByteB3= util.cxJson(sm4EncryptB3);//json数据，以及补充00
			sendBuf(ctx,cxJsonByteB3);
			break;
		case "b4"://用户卡信息帧
			JSONObject b4json = readUtil.b4Analysis(dataBuf);
			if(b4json==null) {
				System.out.println("b4数据有误:"+modbusInfo.getFullData_str());
				break;
			}
			System.out.println("b4数据:"+b4json);
			
			ListenerService.timedCache1.get(b4json.get("OBUID").toString());
			ListenerService.timedCache2.get(b4json.get("OBUID").toString());
			
			//c6
			byte[] c6Reply =replyUtil.c6Reply(b4json);//C6
			String sm4EncryptB4 = util.sm4Encrypt(key,c6Reply);//sm4加密
			byte[] cxJsonByteB4= util.cxJson(sm4EncryptB4);//json数据，以及补充00
			sendBuf(ctx,cxJsonByteB4);
			
			break;
			
		case "b5"://用户卡信息帧
			JSONObject b5json = readUtil.b5Analysis(dataBuf);
			if(b5json==null) {
				System.out.println("b5数据有误:"+modbusInfo.getFullData_str());
				break;
			}
			
			System.out.println("b5数据:"+b5json);
			//c2终止
			byte[] c2Replyb5 =replyUtil.c2Reply(b5json.get("OBUID").toString(),"00");//c2指令     1-重新OBU流程，0-终止OBU流程。
			String sm4EncryptB5 = util.sm4Encrypt(key,c2Replyb5);//sm4加密
			byte[] cxJsonByteB5= util.cxJson(sm4EncryptB5);//json数据，以及补充00
			sendBuf(ctx,cxJsonByteB5);
			ListenerService.timedCache1.remove(b5json.get("OBUID").toString());
			ListenerService.timedCache2.remove(b5json.get("OBUID").toString());
			/*//关闭天线
			Thread.sleep(2000);//睡眠2秒
			byte[] c2Data = replyUtil.c2Reply("ffffffff","00");//获取c2数据(开关天线)  01开，00关
			String sm4EncryptC2 = util.sm4Encrypt(key,c2Data);//sm4加密
	        byte[] cxJsonByteC2= util.cxJson(sm4EncryptC2);//json数据，以及补充00
			sendBuf(ctx,cxJsonByteC2);*/
			break;
		default:
			System.out.println("没有这个指令:" + modbusInfo.getFullData_str());
			break;
		}
		
		}catch(Exception e){
        	LOGGER.error("解析报文有误,报错========================"+msg,e);
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
