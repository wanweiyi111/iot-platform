package com.hzyw.iot.netty.processor;

import com.hzyw.iot.netty.channelhandler.CommandHandler;
import com.hzyw.iot.netty.processor.Impl.IDataProcessor;
import com.hzyw.iot.netty.processor.Impl.IplcDataProcessor;
import com.hzyw.iot.netty.processor.Impl.ProcessorAbstract;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.util.constant.ConverUtil;
import com.hzyw.iot.util.constant.ProtocalAdapter;
import com.hzyw.iot.utils.CRCUtils;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.utils.PlcProtocolsUtils;
import com.hzyw.iot.vo.dc.ModbusInfo;
import com.hzyw.iot.vo.dc.RTUInfo;
import com.hzyw.iot.vo.dc.enums.ERTUChannelFlag;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.UUID;

/**
 * ===PLC设备接入的消息处理：==== 1，协议报文 转化成 ModbusInfo 2，CRC校验数据 3，把modbusInfo.getData
 * 数据转化成十进制数，赋值给设备的某个字段 4，构造MessageVO消息 【消息类型（根据指令码来判断），】 如，节点信息上报： {
 * deviceid:000000000001 集中器地址 methods: [{method:operator_70H,
 * out:{输入电压：100，,...}
 * 
 * }] }, tags:{key:value,key:value} //扩展 }
 * 
 * 5， kafka.send(json.toJSONstring(MessageVO)) ;上报消息到KAFKA
 * 
 */
public class PlcDataProcessor extends ProcessorAbstract implements IDataProcessor, IplcDataProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlcDataProcessor.class);
	private int type;
	private RedisService redisService;
	public PlcDataProcessor() {
		super(ERTUChannelFlag.PLC);
	}
    
	public void setType(int type,RedisService redisService){
		this.type = type;
		this.redisService = redisService;
	}
  
	
	/* 
	 * 设备上报数据
	 * - 集中器 -》主机
	 * 
	 * (non-Javadoc)
	 * @see com.hzyw.iot.netty.processor.Impl.IDataProcessor#translate(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, com.hzyw.iot.vo.dc.RTUInfo)
	 */
	@Override
	public void translate(ChannelHandlerContext ctx, ByteBuf source, RTUInfo rtuInfo) throws Exception {
		System.out.println("------设备接入类型(端口)-------------"+this.type);
		if (checkAndToProcess(this.type))
		{  
			try{
				ModbusInfo modbusInfo = new ModbusInfo(source);  
				if(modbusInfo == null){
	                return; 
				}
				String serverPort = String.valueOf(PlcProtocolsUtils.getPort(ctx.channel()));
				Map<String, Map<String, Object>> Alldevinfo = IotInfoConstant.allDevInfo.get(serverPort);
	            // 判断此设备是否已经在平台注册
				if(Alldevinfo==null || Alldevinfo.get(modbusInfo.getAddress_str()+"_attribute") == null){
					LOGGER.warn("设备【"+modbusInfo.getAddress_str()+"】设备未注册到平台!" );
	                return;
				}
	            // CRC校验
	            if (!CRCUtils.checkCRC(modbusInfo.getFullData(), modbusInfo.getCrc())) {
	                LOGGER.warn("plc bad data: {}", ConverUtil.convertUUIDByteToHexString(modbusInfo.getFullData()));
	                return;
	            }
	            // 头部、尾部校验
	            if(!"68".equals(modbusInfo.getHeadStart_str()) && !"68".equals(modbusInfo.getHeadEnd_str()) && !"16".equals(modbusInfo.getEnd_str())){
	            	//取命令码、应答码、然后响应
	            	LOGGER.warn("设备【"+modbusInfo.getAddress_str()+"】头部、尾部报文无效!" + PlcProtocolsUtils.loggerBaseInfo(modbusInfo));
	            	return;
	            }
	            // 验证当前命令码、控制码是否存在，不存在直接响应失败
	            if(!IotInfoConstant.allDevInfo.get(serverPort).get(modbusInfo.getAddress_str()+"_cmd").containsKey(modbusInfo.getCmdCode_str())){
	            	//取命令码、应答码、然后响应
	            	LOGGER.warn("设备【"+modbusInfo.getAddress_str()+"】命令码无效!" + PlcProtocolsUtils.loggerBaseInfo(modbusInfo));
	            	return;
	            }
	            if(IotInfoConstant.allDevInfo.get(serverPort).get(modbusInfo.getAddress_str()+"_req_ack").get(modbusInfo.getcCode_str())==null){
	            	LOGGER.warn("设备【"+modbusInfo.getAddress_str()+"】控制码无效!" + PlcProtocolsUtils.loggerBaseInfo(modbusInfo));
	            	return;
	            }
	             
	            // ====登陆、设备配置过程=======start >>===
	            boolean isLogin = PlcProtocolsUtils.isLogin(modbusInfo);   //是否已登陆
	            boolean isConfig = PlcProtocolsUtils.isConfig(modbusInfo,redisService); //true;//是否已配置设备
	            if(!isLogin && "f0".equals(modbusInfo.getCmdCode_str().toLowerCase())){
	            	//isLogin = true;
	            	boolean seccess = PlcProtocolsUtils.resonseLogin(ctx, modbusInfo);//通知设备已成功登陆
	            	if(!seccess)return;//通知登陆失败，直接退出
	            	//登陆成功，如果是第一次登陆，则自动进入配置设备流程
	            	 if(!isConfig){
	            		//发起初始化配置流程
	            		 PlcProtocolsUtils.init_1(ctx, modbusInfo,redisService);
	            		 return;
	            	 }else{
	                 	LOGGER.warn("设备【"+modbusInfo.getAddress_str()+"】已配置!");
	            	 }
	            }else if(isLogin && "f0".equals(modbusInfo.getCmdCode_str().toLowerCase())){
	            	//登陆成功后，是否还会一直发登陆？和客户沟通下
	            	LOGGER.warn("设备【" + modbusInfo.getAddress_str()+"】，====已登陆  ===为什么还一直请求登陆？===" );
	            }
	            if(!isLogin){
	            	LOGGER.warn("设备【" + modbusInfo.getAddress_str()+"】未登陆，请检查登陆报文是否符合规范!" );
	            	return;
	            }
	            
	            if(!isConfig){
	            	PlcProtocolsUtils.init_2_9(ctx, modbusInfo,redisService);
	            	LOGGER.warn("设备【" + modbusInfo.getAddress_str()+"】未配置，请检查自动配置过程日志，定位失败原因!!" );
	            	return;
	            }
	            if(isConfig && isLogin){
	            	LOGGER.warn("设备【" + modbusInfo.getAddress_str()+"】已登陆 ，已配置!!" );
	            }
	            //心跳
	            PlcProtocolsUtils.heartBeat(ctx, modbusInfo);
	            
	            //报文解析并上报消息到KAFKA
				//--1,校验报文格式合理性    比较简单,上面已经做了校验了，这里不需要这个步骤
				//--2,处理（集中器-》主机）的请求
				//--3,响应（集中器）请求
				//--4,组装要上报到KAFKA的消息
				//--5,上报消息到KAFKA ,类型如下
				/*
				设备->主机
				    response	           上报下发请求结果       ResultMessageVO<ResponseDataVO>
					devInfoResponse	 属性上报   		  ResultMessageVO<MetricInfoResponseDataVO>     登陆成功上报一次
					metricInfoResponse	设备状态数据上报          ResultMessageVO<MetricInfoResponseDataVO>
					devSignlResponse	设备信号上报                    ResultMessageVO<DevSignlResponseDataVO>
				*/
	            boolean flag = PlcProtocolsBusiness.transformTemplateSelect(ctx, modbusInfo);
	            if(flag){
	            	PlcProtocolsBusiness.protocals_process_Response(ctx, modbusInfo);
	            }else{
	            	String cmd_strs=modbusInfo.getCmdCode_str();
					//if(!"45".equalsIgnoreCase(cmd_strs) && !"f7".equalsIgnoreCase(cmd_strs)){
                   // if("42".equalsIgnoreCase(cmd_strs)) {
					if("f7".equalsIgnoreCase(cmd_strs)) {
				//	if("96".equalsIgnoreCase(cmd_strs) || "97".equalsIgnoreCase(cmd_strs)
				//		|| "98".equalsIgnoreCase(cmd_strs) || "99".equalsIgnoreCase(cmd_strs)) {
					    source.resetReaderIndex();
						byte[] requestBytes = new byte[source.readableBytes()];
						source.readBytes(requestBytes);
						long FIRST_TIME=System.currentTimeMillis();
						String resposeMesg=ProtocalAdapter.messageRespose(requestBytes, ctx);
						LOGGER.info("*******指令码:"+modbusInfo.getCmdCode_str()+",******上报指令,调协义模板 (上报和响应的)messageRespose方法,消耗时间(s):"+
								Math.round(System.currentTimeMillis()-FIRST_TIME)/1000);
						 
					}
	            }
			}catch(Exception e){
				LOGGER.error("translate exception,",e); 
			}
		} else {
			if (super.getNextProcessor() != null)
				super.getNextProcessor().translate(ctx, source, rtuInfo);
		}
	}
	
	//ByteBuffer 转换 String：
	public static String decode(ByteBuffer bb){ 
		Charset charset = Charset.forName("ISO8859-1");
	    return charset.decode(bb).toString();
	}
	
	public static String convertByteBufToString(ByteBuf buf) {
		String str;
		if(buf.hasArray()) { // 处理堆缓冲区
			str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
		} else { // 处理直接缓冲区以及复合缓冲区
			byte[] bytes = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), bytes);
			str = new String(bytes, 0, buf.readableBytes());
		}
		
		/*ByteBuf bf =req.content();
        byte[] byteArray = new byte[bf.capacity()];  
        bf.readBytes(byteArray);  
        String result = new String(byteArray);*/
        
			return str;
	}
}
