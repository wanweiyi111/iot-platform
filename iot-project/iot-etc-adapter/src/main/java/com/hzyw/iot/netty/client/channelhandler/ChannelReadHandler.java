package com.hzyw.iot.netty.client.channelhandler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.netty.util.HeadHandlerUtil;
import com.hzyw.iot.netty.vo.FilterBufVo;
import com.hzyw.iot.netty.vo.ModbusInfo;

import cn.hutool.json.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ChannelReadHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelReadHandler.class);
	/**
	 * 接收server端的消息
	 */
	public void readHandler(ChannelHandlerContext ctx, ByteBuf buf) {
		HeadHandlerUtil util = new HeadHandlerUtil();
		/*FilterBufVo fullData = new FilterBufVo();
		//拿到全部数据
		byte[] getbyte = new byte[buf.readableBytes()];
		buf.readBytes(getbyte);
		fullData.setBody(getbyte);
		String bodyStr = util.convertByteToHexString(fullData.getBody());
		System.out.println("完整数据:"+bodyStr);
		FilterBufVo vo = util.filter(bodyStr);
		//完整的数据做出响应
		for(int i = 0;i < vo.getListStr().size(); i ++){
		    System.out.println("vo上拿的数据:"+vo.getListStr().get(i));
		    try {
		    byte[]	voByte = util.hexStrToByteArr(vo.getListStr().get(i));
		    ByteBuf voBuf = Unpooled.wrappedBuffer(voByte);*/
		    ModbusInfo modbusInfo = new ModbusInfo(buf);
		    
		    String dataStr = modbusInfo.getData_str();//data数据
		    Map<String,String> dataMap = new HashMap<String,String>();//data数据解析出的数据
		   
			/*String dataStr = modbusInfo.getData_str();
			String strCode = StrUtil.sub(dataStr, 0, 2);//指令str
			System.out.println("收到的指令:" + strCode);*/
			String code = modbusInfo.getDataType_str();//指令
			System.out.println("收到的指令:" + code);
			
			/*System.out.println("STX 帧报文头:" + modbusInfo.getStx_str());
			System.out.println("VER 协议版本:" + modbusInfo.getVer_str());
			System.out.println("SEQ 帧序号:" + modbusInfo.getSeq_str());
			System.out.println("LEN DATA长度:" + modbusInfo.getLen_str());
			System.out.println("DATA数据域:" + modbusInfo.getData_str());
			System.out.println("CRC 校验码:" + modbusInfo.getCrc_str());
			System.out.println("全部数据包:" + modbusInfo.getFullData_str());*/
			switch (code) {
			case "b0":// 设备状态信息帧,初始化返回的数据
				if(dataStr.length()==36) {
				String rsuStatus = dataStr.substring(2, 4);//路侧单元主状态参数；00H 表示正常，否则表示异常
				String psamNum = dataStr.substring(4, 6);//当前路侧单元中 PSAM 数量，记为 n
				//String PSAMInfo = dataStr;//#这个数据暂无
				String rsuAlgId = dataStr.substring(6, 8);//算法标识，默认填写 00H
				String rsuManuID = dataStr.substring(8, 10);//路侧单元厂商代码
				String rsuID = dataStr.substring(10, 16);//路侧单元编号
				String rsuVersion = dataStr.substring(16, 20);//路侧单元软件版本号，采用本标准版本不低于 20 00H
				String workstatus = dataStr.substring(20,22);//工作模式返回状态，默认填写 00H
				String flagID = dataStr.substring(22, 28);//ETC 门架编号（由 C0 帧中获取，获取失败填充 00H）
				String reserved = dataStr.substring(28, 36);//保留字节 00000000
				
				dataMap.put("rsuStatus", rsuStatus);
				dataMap.put("psamNum", psamNum);
				dataMap.put("psamInfo", "");
				dataMap.put("rsuAlgId", rsuAlgId);
				dataMap.put("rsuManuID", rsuManuID);
				dataMap.put("rsuID", rsuID);
				dataMap.put("rsuVersion", rsuVersion);
				dataMap.put("workstatus", workstatus);
				dataMap.put("flagID", flagID);
				dataMap.put("reserved", reserved);
				
				JSONObject jsonObj=new JSONObject(dataMap);
				System.out.println("json上报数据:"+jsonObj);
				}else if(dataStr.length()==54) {//多了PSAM数据
					String rsuStatus = dataStr.substring(2, 4);//路侧单元主状态参数；00H 表示正常，否则表示异常
					String psamNum = dataStr.substring(4, 6);//当前路侧单元中 PSAM 数量，记为 n
					String psamInfo = dataStr.substring(6, 24);;//PSAM数据(详细还需看文档)
					String rsuAlgId = dataStr.substring(24, 26);//算法标识，默认填写 00H
					String rsuManuID = dataStr.substring(26, 28);//路侧单元厂商代码
					String rsuID = dataStr.substring(28, 34);//路侧单元编号
					String rsuVersion = dataStr.substring(34, 38);//路侧单元软件版本号，采用本标准版本不低于 20 00H
					String workstatus = dataStr.substring(38,40);//工作模式返回状态，默认填写 00H
					String flagID = dataStr.substring(40, 46);//ETC 门架编号（由 C0 帧中获取，获取失败填充 00H）
					String reserved = dataStr.substring(46, 54);//保留字节 00000000
					
					dataMap.put("rsuStatus", rsuStatus);
					dataMap.put("psamNum", psamNum);
					dataMap.put("psamInfo", psamInfo);
					dataMap.put("rsuAlgId", rsuAlgId);
					dataMap.put("rsuManuID", rsuManuID);
					dataMap.put("rsuID", rsuID);
					dataMap.put("rsuVersion", rsuVersion);
					dataMap.put("workstatus", workstatus);
					dataMap.put("flagID", flagID);
					dataMap.put("reserved", reserved);
					
					JSONObject jsonObj=new JSONObject(dataMap);
					System.out.println("json上报数据:"+jsonObj);
					
				}else {
					LOGGER.error("b0长度有问题:"+dataStr.length()+"data数据:"+dataStr);
				}
				break;
			case "b1":// 心跳包接收
				//String a = "b100110101010000010100000f0000";
				if(dataStr.length()==30) {
				String rsuControlStatus1 = dataStr.substring(2, 4);//路侧单元控制器 1（IP 地址小的为控制器 1）状态，00H：主机+正常，01：主机+异常，10H：从机+正常，11H：从机+异常
				String rsuControlStatus2 = dataStr.substring(4, 6);//路侧单元控制器 2
				String rsuControlStatus3 = dataStr.substring(6, 8);//路侧单元控制器 3
				String psamNum1 = dataStr.substring(8, 10);//路侧单元控制器 1 PSAM 数量，记为 n
				String psamStatus1 = dataStr.substring(10, 16);//1 字节 PSAM 通道号；1 字节 PSAM 运行状态，00H 正常，01H 异常（含密钥锁定等无法正常工作的情况） 1 字节 PSAM 卡授权状态，00H 已授权，01H 未授权（含授权失败）
				String psamNum2 = dataStr.substring(16, 18);//2
				String psamStatus2 = dataStr.substring(18, 24);//2
				String rsuAntennaNum = dataStr.substring(24,26);//路侧单元配置的天线数量，记为 h
				String RSUAntennaNum2 = dataStr.substring(26, 28);//主用路侧单元控制器所连接天线中正常工作的数量
				String antennaStatus = dataStr.substring(28, 30);//路侧单元天线状态信息
				
				
				dataMap.put("rsuControlStatus1", rsuControlStatus1);
				dataMap.put("rsuControlStatus2", rsuControlStatus2);
				dataMap.put("rsuControlStatus3", rsuControlStatus3);
				dataMap.put("psamNum1", psamNum1);
				dataMap.put("psamStatus1", psamStatus1);
				dataMap.put("psamNum2", psamNum2);
				dataMap.put("psamStatus2", psamStatus2);
				dataMap.put("rsuAntennaNum", rsuAntennaNum);
				dataMap.put("RSUAntennaNum2", RSUAntennaNum2);
				dataMap.put("antennaStatus", antennaStatus);
				
				JSONObject jsonObj=new JSONObject(dataMap);
				System.out.println("json上报数据:"+jsonObj);
				}else {
					LOGGER.error("b1长度有问题:"+dataStr.length()+"data数据:"+dataStr);
				}
				
				//响应数据cf
				String cmdCf = "cf";//指令
				String status = "00";//00：接收成功，其他保留
				String combinationCf = cmdCf+status;//组合数据
				byte[] b1byte = util.requestHandler(combinationCf);
				sendBuf(ctx,b1byte);
				System.out.println("应答cf指令数据:" + util.convertByteToHexString(b1byte));// 心跳应答
				break;
			case "b2":// 解析车载单元信息帧 
				if(dataStr.length()==74) {
				String obuMac = dataStr.substring(2, 10);//OBU MAC 地址       *
				String errorCode = dataStr.substring(10, 12);//执行状态代码，取值 00H
				String antennaID = dataStr.substring(12, 14);//天线 ID 编号
				String deviceType = dataStr.substring(14, 16);//车载单元类型
				String issuerIdentifier = dataStr.substring(16, 32);//发行商代码
				String contractType = dataStr.substring(32, 34);//OBU 协约类型
				String contractVersion = dataStr.substring(34, 36);//OBU 合同版本
				String serialNumber = dataStr.substring(36,52);//OBU 合同序列号
				String dteofIssue = dataStr.substring(52, 60);//OBU 合同签署日期
				String dteofExpire = dataStr.substring(60, 68);//OBU 合同过期日期
				String equitmentCV = dataStr.substring(68, 70);//OBU 设备类型及版本
				String obuStatus = dataStr.substring(70, 74);//OBU 状态
				dataMap.put("obuMac", obuMac);
				dataMap.put("errorCode", errorCode);
				dataMap.put("antennaID", antennaID);
				dataMap.put("deviceType", deviceType);
				dataMap.put("issuerIdentifier", issuerIdentifier);
				dataMap.put("contractType", contractType);
				dataMap.put("contractVersion", contractVersion);
				dataMap.put("serialNumber", serialNumber);
				dataMap.put("dteofIssue", dteofIssue);
				dataMap.put("dteofExpire", dteofExpire);
				dataMap.put("equitmentCV", equitmentCV);
				dataMap.put("obuStatus", obuStatus);
				JSONObject jsonObj=new JSONObject(dataMap);
				System.out.println("json上报数据:"+jsonObj);
				//响应数据c1
				String cmdC1 = "c1";//指令
				String OBUDivFactor = "b2e2cad4b2e2cad4";//车载单元一级分散因子
				String combinationC1 = cmdC1+obuMac+OBUDivFactor;//组合数据
				byte[] b2byte = util.requestHandler(combinationC1);
				sendBuf(ctx,b2byte);// 继续交易接口
				System.out.println("应答c1指令数据" + util.convertByteToHexString(b2byte));
				}else {
					
					LOGGER.error("b0长度有问题,data数据:"+dataStr);
					//响应数据c1
					String cmdC1 = "c1";//指令
					String OBUDivFactor = "b2e2cad4b2e2cad4";//车载单元一级分散因子
					String obuMac = "F1500C52";
					String combinationC1 = cmdC1+obuMac+OBUDivFactor;//组合数据
					byte[] b2byte = util.requestHandler(combinationC1);
					sendBuf(ctx,b2byte);// 继续交易接口
					System.out.println("应答c1指令数据" + util.convertByteToHexString(b2byte));
				}
				
				// System.out.println("发c2指令");//停止交易接口
				break;
			case "b4"://解析用户信息帧
				if(dataStr.length()==128) {
				String obuMac = dataStr.substring(2, 10);//OBU MAC 地址  *
				String errorCode = dataStr.substring(10, 12);//执行状态代码
				String transType = dataStr.substring(12, 14);//交易类型
				//String VehicleInfo = dataStr.substring(10, 12);//#车辆信息文件 79 字节
				//String CardRestMoney = dataStr.substring(10, 12);//#电子钱包文件
				//String IssuerInfo = dataStr.substring(10, 12);//#卡片发行信息
				String lastStation = dataStr.substring(14, 100);//出入口信息
				String EF02 = dataStr.substring(100, 108);//EF02过站信息
				String EF04 = dataStr.substring(108, 128);//EF04计费信息
				
				dataMap.put("obuMac", obuMac);
				dataMap.put("errorCode", errorCode);
				dataMap.put("transType", transType);
				dataMap.put("lastStation", lastStation);
				dataMap.put("EF02", EF02);
				dataMap.put("EF04", EF04);
				JSONObject jsonObj=new JSONObject(dataMap);
				System.out.println("json上报数据:"+jsonObj);
				//响应数据c1
				String cmdC2 = "c2";//指令
				String stopType = "01";//车载单元一级分散因子
				int unix = (int) (System.currentTimeMillis() / 1000L);
				String unixStr = util.intToHex(unix);// 获取当前时间戳
				String combinationC2 = cmdC2+obuMac+stopType+unixStr;//组合数据
				byte[] b4byte = util.requestHandler(combinationC2);
				sendBuf(ctx,b4byte);
				System.out.println("应答c2指令数据:" + util.convertByteToHexString(b4byte));// 停止交易
				// System.out.println("发c6指令");// 消费交易指令
				}else {
					int unix = (int) (System.currentTimeMillis() / 1000L);
					String unixStr = util.intToHex(unix);// 获取当前时间戳
					String b4 = "C2F1500C5201" + unixStr;
					byte[] b4byte = util.requestHandler(b4);
					sendBuf(ctx,b4byte);
					System.out.println("应答c2指令数据:" + util.convertByteToHexString(b4byte));// 停止交易
					// System.out.println("发c6指令");// 消费交易指令
				}
				
				break;
			case "b5":// 解析车载单元信息帧
				System.out.println("应答c1指令，交易完成");// 指令确认交易完成
				break;
				
			case "ba":// PSAM授权指令
				System.out.println("PSAM授权");
				if(dataStr.length()==90) {
				String errorCode = dataStr.substring(2, 4);//执行状态代码
				String datetime = dataStr.substring(4, 18);//当前日期时间
				String psamCount = dataStr.substring(18, 20);//授权的 PSAM 数量，记为 n
				String psamInfo = dataStr.substring(20, 90);//PSAM 初始化信息 35 字节
				
				dataMap.put("errorCode", errorCode);
				dataMap.put("datetime", datetime);
				dataMap.put("psamCount", psamCount);
				dataMap.put("psamInfo", psamInfo);
				JSONObject jsonObj=new JSONObject(dataMap);
				System.out.println("json上报数据:"+jsonObj);
				
				}else {
					LOGGER.error("ba长度有问题:"+dataStr.length()+"data数据:"+dataStr);
				}
				
				break;
			default:
				System.out.println("没有这个指令,错误数据包:" + modbusInfo.getFullData_str());
				break;
			}
		   /* } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		/*if(vo.getListBugStr()!=null&&vo.getListBugStr().size()!=0) {
		for(int i = 0;i < vo.getListBugStr().size(); i ++){
			System.out.println();
		    System.out.println("bug数据:"+vo.getListBugStr().get(i));
		}
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
