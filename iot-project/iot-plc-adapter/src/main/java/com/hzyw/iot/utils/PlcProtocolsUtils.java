package com.hzyw.iot.utils;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.netty.channelhandler.CommandHandler;
import com.hzyw.iot.util.constant.*;
import com.hzyw.iot.vo.dataaccess.*;

import java.net.InetSocketAddress;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.kafka.KafkaCommon;
import com.hzyw.iot.netty.channelhandler.ChannelManagerHandler;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.util.ByteUtils;
import com.hzyw.iot.vo.dc.ModbusInfo;

import cn.hutool.core.date.DateUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * 初始化PLC配置： 1 集中器配置：向集中器写入经纬度信息 2 设置集中器时间：手动同步集中器时间 3 清除节点 4 删除flash节点 5
 * 开始组网（等待节点数*20秒） 6 查询组网(当全部节点都组网成功) (略) 7 停止组网 8 存储节点 9 下发节点 10
 * 配置节点（等待节点数*30秒） 11 查询节点 (略)
 * 
 * @author Administrator
 *
 */
public class PlcProtocolsUtils {
	private static final Logger logger = LoggerFactory.getLogger(PlcProtocolsUtils.class);
	public static final Map<String, String> gloable_dev_status = new HashMap<String, String>(); // PLC设备是否上线或离线
	@Autowired
	private static KafkaCommon kafkaCommon;

	@Autowired
	private ApplicationConfig applicationConfig;
	
	
	/**
	 * 设备是否已登陆成功
	 * 
	 * @param modbusInfo
	 * @return
	 */
	public static boolean isLogin(ModbusInfo modbusInfo) {
		String flag = gloable_dev_status.get(modbusInfo.getAddress_str() + _login); // 1表示已登陆
		// 0
		// 表示未登陆
		// （通过心跳判断是否掉线来设置）
		if (flag != null && flag.equals("1")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 设置设备登陆状态
	 * @param plcSN
	 * @param status
	 */
	public static void setLoginStatus(String plcSN,String status) {
		gloable_dev_status.put(plcSN + _login,status); // 1表示已登陆,0未登陆
	}
	public static final String _login = "_login";
	public static final String rediskey_plc_isconfig_ = "plc_isconfig_";
	
	/**
	 * 设备是否已配置好
	 * 
	 * @param modbusInfo
	 * @return
	 */
	public static boolean isConfig(ModbusInfo modbusInfo,RedisService redisService) {
		String flag = (String)redisService.get(rediskey_plc_isconfig_+modbusInfo.getAddress_str());
		// 0表示未下发配置过（只需要配置一次即可，配置以后才能控灯）
		if (flag != null && flag.equals("1")) {
			return true;
		}
		return false;
	}

	/**
	 * 直接发送字节流报文
	 * 
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	public static void ctxWriteByte(ChannelHandlerContext ctx, byte[] msg, String type) throws Exception {
		try {
			ctx.write(msg);
		} catch (Exception e1) {
			logger.error(
					">>>PlcProtocolsUtils::ctxWriteStr(),byteMsg=" + ConverUtil.convertByteToHexString(msg) + ",异常 ",
					e1);
		}
	}

	// 直接定义在内存:<iot_init_Configkey,<field=initconfig+100+cmdCode,json>>
	public static final Map<String, Map<String, String>> iot_init_Config_chache = new HashMap<String, Map<String, String>>();

	public static void ctxWriteByte_ChannelFuture(Channel channel, byte[] msg, String type, final int step)
			throws Exception {
		try {
			channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> { // 监听执行结果！
				if (future.isSuccess()) {
					logger.info("ok");
				} else {
					logger.error("send data to client exception occur: {}", future.cause());
				}
			});
		} catch (Exception e1) {
			logger.error(
					">>>PlcProtocolsUtils::ctxWriteStr(),byteMsg=" + ConverUtil.convertByteToHexString(msg) + ",异常 ",
					e1);
		}
	}

	/**
	 * 直接发送16进制的报文
	 * 
	 * @param ctx
	 * @param hexMsg
	 * @throws Exception
	 */
	public static void ctxWriteStr(ChannelHandlerContext ctx, String hexMsg, String type) throws Exception {
		try {
			logger.info(">>>响应设备,cmdCode=" + type + ",byteMsg=" + hexMsg);
			ctx.write(ConverUtil.hexStrToByteArr(hexMsg));
		} catch (Exception e1) {
			logger.error(">>>PlcProtocolsUtils::ctxWriteStr(),strMsg=" + hexMsg + ",异常 ", e1);
		}
	}

	private static final String logger_type_request = "request";
	private static final String logger_type_response = "response";

	/**
	 * 报文
	 * 
	 * @param modbusInfo
	 * @return
	 */
	public static String loggerBaseInfo(ModbusInfo modbusInfo) {
		return ("plc_sn/cCode/cmdCode/hexMsg = /" + modbusInfo.getAddress_str() + "/" + modbusInfo.getcCode_str() + "/"
				+ modbusInfo.getCmdCode_str() + "/" + modbusInfo.toStringBW());
	}
	
	public static String loggerBaseInfo2(ModbusInfo modbusInfo) {
		return ("plc_sn/cCode/cmdCode/ = /" + modbusInfo.getAddress_str() + "/" + modbusInfo.getcCode_str() + "/"
				+ modbusInfo.getCmdCode_str() );
	}

	public static String getPort(Channel channel) {
		InetSocketAddress insocket = (InetSocketAddress) channel.localAddress();
		return String.valueOf(insocket.getPort());
	}

	/**
	 * @param ctx
	 * @param modbusInfo
	 * @return true 成功 false 失败
	 */
	public static boolean resonseLogin(ChannelHandlerContext ctx, ModbusInfo modbusInfo) {
		ModbusInfo modbusInfo_FOh = new ModbusInfo();
		String plc_sn = modbusInfo.getAddress_str(); // "000000000100";
		Map<String, Boolean> resultFlagM = new HashMap<String, Boolean>();
		resultFlagM.put("resultFlag", true);
		try {
			// 登陆成功处理
			// 响应登陆成功请求
			setProtocalsAuto(modbusInfo_FOh, ConverUtil.hexStrToByteArr(plc_sn) // PLC SN
					, ConverUtil.hexStrToByteArr("80") // 控制码
					, ConverUtil.hexStrToByteArr("f0") // 操作码
					, ConverUtil.hexStrToByteArr("01")); // Pdt部分 01H：登陆成功 02H：登陆失败。03H：主机忙

			ctx.channel().writeAndFlush(modbusInfo_FOh.getNewFullDataWithByteBuf())
					.addListener((ChannelFutureListener) future -> { // 监听是否成功
						if (future.isSuccess()) {
							logger.info(">>>PlcProtocolsUtils::login()," + loggerBaseInfo(modbusInfo_FOh)
									+ ",登陆成功，并已成功响应到设备! ");
							// 获取节点ID(设备ID)
							InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
							String gwId = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "")
									.get(plc_sn + "_defAttribute").get(IotInfoConstant.dev_plc_plc_id);

							// 上线成功需要上报消息到KAFKA
							devOnline(plc_sn, gwId);
							// 设备属性与控制方法上报
							devInfoResponse(plc_sn, gwId, insocket, ctx);

							// todo
							gloable_dev_status.put(modbusInfo.getAddress_str() + "_login", "1"); // 设置设备上线状态 1-上线 0-下线
							ChannelManagerHandler.setRTUChannelInfo(ctx, plc_sn); // 登陆成功，建立设备和通道的全局映射关系
							Thread.currentThread().sleep(1000 * 5);
						} else {
							resultFlagM.put("resultFlag", false);
							logger.info(">>>PlcProtocolsUtils::login(),type=FO,登陆成功,响应并通知设备....调用失败! ");
							logger.error(">>>send data to client exception occur: {}", future.cause());
							gloable_dev_status.put(modbusInfo.getAddress_str() + "_login", "0");
						}
					});
		} catch (Exception e) {
			resultFlagM.put("resultFlag", false);
			gloable_dev_status.put(modbusInfo.getAddress_str() + "_login", "0");
			logger.error(">>>PlcProtocolsUtils::login()," + loggerBaseInfo(modbusInfo_FOh) + ",登陆异常 ", e);
			try {
				setProtocalsAuto(modbusInfo_FOh, ConverUtil.hexStrToByteArr(plc_sn) // PLC SN
						, ConverUtil.hexStrToByteArr("80") // 控制码
						, ConverUtil.hexStrToByteArr("f0") // 操作码
						, ConverUtil.hexStrToByteArr("02")); // Pdt部分 01H：登陆成功 02H：登陆失败。03H：主机忙
				ctx.channel().writeAndFlush(modbusInfo_FOh.getNewFullDataWithByteBuf())
						.addListener((ChannelFutureListener) future -> {
							if (future.isSuccess()) {
								logger.info(">>>PlcProtocolsUtils::login()," + loggerBaseInfo(modbusInfo_FOh)
										+ ",登陆异常...响应并成功通知到设备!");
								gloable_dev_status.put(modbusInfo.getAddress_str() + "_login", "0");
							} else {
								logger.error(">>>PlcProtocolsUtils::login(),type=FO,登陆异常...响应并通知设备失败....调用失败!: {}",
										future.cause());
							}
						});
			} catch (Exception e1) {
				logger.error(">>>PlcProtocolsUtils::login::ctxWriteStr()," + loggerBaseInfo(modbusInfo_FOh)
						+ ",登陆异常...响应过程中再次发生异常! ", e1);
			}
		}
		return resultFlagM.get("resultFlag");
	}

	/**
	 * 设备上线通知
	 */
	public static void devOnline(String plc_sn, String gwId) {
		// 设备上下VO
		DevOnOffline devOnline = new DevOnOffline();
		// 初始化设备属性
		IotInfoConstant iotInfoConstant = new IotInfoConstant();
		Map<String, Object> tags = new HashMap<String, Object>();// tags
		tags.put("agreement", "plc");

		devOnline.setId(gwId);
		devOnline.setStatus("online");
		devOnline.setTags(tags);
		// 消息结构
		MessageVO messageVo = new MessageVO<>();
		messageVo.setType("devOnline");
		messageVo.setTimestamp(DateUtil.currentSeconds());
		messageVo.setMsgId(UUID.randomUUID().toString());
		messageVo.setData(devOnline);
		messageVo.setGwId(gwId);
		
		// 集中器下的灯节点应该也要上报上线
		// todo

		System.out.println(JSON.toJSONString(messageVo));
		SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));

	}

	/**
	 * 设备属性与控制方法上报
	 */
	public static void devInfoResponse(String plc_sn, String gwId, InetSocketAddress insocket,
			ChannelHandlerContext ctx) {
		String currentConnetionPort = insocket.getPort()+"";
		// 集中器数据
		DevInfoDataVO devInfoDataVo = new DevInfoDataVO();// 设备属性和控制方法VO
		IotInfoConstant iotInfoConstant = new IotInfoConstant();// 初始化设备属性

		List<Map> attributers = new ArrayList<Map>();// 基本属性
		//Map<String, Object> attributersMap = new HashMap<String, Object>();
		Map<String, Object> attribute = IotInfoConstant.allDevInfo.get(currentConnetionPort).get(plc_sn + "_attribute");
		attribute.put(IotInfoConstant.dev_base_online, 1);//在线
		attributers.add(attribute);

		List<String> methods = new ArrayList<String>();// 方法名
		Map<String, Object> method = IotInfoConstant.allDevInfo.get(currentConnetionPort).get(plc_sn + "_method");
		String methodStr = method.keySet().toString().substring(1, method.keySet().toString().length() - 1);
		methods.add(methodStr);

		List<Map> definedAttributers = new ArrayList<Map>();// 自定义属性
		Map<String, Object> defAttribute = IotInfoConstant.allDevInfo.get(currentConnetionPort).get(plc_sn + "_defAttribute");// 集中器数据
		
		for (Map.Entry<String, Object> entry :defAttribute.entrySet()) {
			System.out.println("key:"+entry.getKey()+"   value:"+entry.getValue());
			Map<String,Object> typeValue =new HashMap<String,Object>();
			typeValue.put("type", entry.getKey());
			typeValue.put("value", entry.getValue());
			if(IotInfoConstant.getUnit(entry.getKey())==null) {
				typeValue.put("company", "");
			}else {
				typeValue.put("company", IotInfoConstant.getUnit(entry.getKey()));
			}
			definedAttributers.add(typeValue);
		}

		List<Map> signals = new ArrayList<Map>();// 信号
		Map<String, Object> signalsMap = IotInfoConstant.allDevInfo.get(currentConnetionPort).get(plc_sn + "_signl");
		signals.add(signalsMap);

		Map<String, Object> tags = new HashMap<String, Object>();// tags
		tags.put(IotInfoConstant.dev_plc_dataaccess_key, IotInfoConstant.dev_plc_dataaccess_value);

		devInfoDataVo.setId(gwId);
		devInfoDataVo.setAttributers(attributers);
		devInfoDataVo.setMethods(methods);
		devInfoDataVo.setDefinedAttributers(definedAttributers);
		devInfoDataVo.setSignals(signals);
		devInfoDataVo.setTags(tags);

		// 消息结构
		MessageVO messageVo = new MessageVO<>();
		messageVo.setType("devInfoResponse");
		messageVo.setTimestamp(DateUtil.currentSeconds());
		messageVo.setMsgId(UUID.randomUUID().toString());
		messageVo.setData(devInfoDataVo);
		messageVo.setGwId(gwId);
		System.out.println(JSON.toJSONString(messageVo));
		SendKafkaUtils.sendKafka("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageVo));
		//sendKafka(JSON.toJSONString(messageVo), "iot_topic_dataAcess_devInfoResponse");// 发送集中器属性

		// 节点数据
		List<Map<String, String>> nodelist = IotInfoConstant.plc_relation_plcsnToNodelist.get(getPort(ctx.channel())).get(plc_sn);
		for (int i = 0; i < nodelist.size(); i++) {
			Map<String, String> item = nodelist.get(i);
			String node_sn = item.get(IotInfoConstant.dev_plc_node_sn); // 节点SN
			//基本属性
			attributers = new ArrayList<Map>();// 基本属性
			attribute = IotInfoConstant.allDevInfo.get(currentConnetionPort).get(node_sn + "_attribute");
			attribute.put(IotInfoConstant.dev_base_online, 1);//在线
			attributers.add(attribute);
			//def属性
			List<Map> definedAttributersNode = new ArrayList<Map>();    // 自定义属性
			String deviceId = (String)IotInfoConstant.allDevInfo.get(currentConnetionPort).get(node_sn + "_defAttribute").get(IotInfoConstant.dev_plc_node_id);// 根据节点SN找出节点ID
			Map<String, Object> defAttributeNode = IotInfoConstant.allDevInfo.get(currentConnetionPort).get(node_sn + "_defAttribute");// 节点数据
			for (Map.Entry<String, Object> entry :defAttributeNode.entrySet()) {
				Map<String,Object> typeValueNode =new HashMap<String,Object>();
				typeValueNode.put("type", entry.getKey());
				typeValueNode.put("value", entry.getValue());
				if(IotInfoConstant.getUnit(entry.getKey())==null) {
					typeValueNode.put("company", "");
				}else {
					typeValueNode.put("company", IotInfoConstant.getUnit(entry.getKey()));
				}
				definedAttributersNode.add(typeValueNode);
			}
			//构造消息模型并上报到KAFKA
			DevInfoDataVO devInfoDataVoNode = new DevInfoDataVO();
			devInfoDataVoNode.setId(deviceId);
			devInfoDataVoNode.setAttributers(attributers);
			devInfoDataVoNode.setMethods(methods);
			devInfoDataVoNode.setDefinedAttributers(definedAttributersNode);
			devInfoDataVoNode.setSignals(signals);
			devInfoDataVoNode.setTags(tags);
			MessageVO messageNode = new MessageVO<>();
			messageNode.setType(DataType.DevInfoResponse.getMessageType());
			messageNode.setTimestamp(DateUtil.currentSeconds());
			messageNode.setMsgId(UUID.randomUUID().toString());
			messageNode.setData(devInfoDataVoNode);
			messageNode.setGwId(gwId);
			SendKafkaUtils.sendKafka("iot_topic_dataAcess_devInfoResponse", JSON.toJSONString(messageNode));
			//sendKafka(JSON.toJSONString(messageNode), "iot_topic_dataAcess_devInfoResponse");// 发送节点属性
			System.out.println(JSON.toJSONString(messageNode));
		}

	}
 
	/**
	 * PLC 响应 设备实时状态数据上报 发kafka
	 * 
	 * @param plc_sn
	 *            集中器地址
	 * @param definedAttrList
	 * @throws Exception
	 */
	public static void plcStateResponseSend(String plc_sn, String nodeID, List<Map> definedAttrList, ChannelHandlerContext ctx)
			throws Exception {
		// 获取基本属性
		List<Map> attributers = new ArrayList<Map>();// 基本属性
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
		Map<String, Object> attribute = IotInfoConstant.allDevInfo.get((insocket.getPort()) + "")
				.get(plc_sn + "_defAttribute");
		attributers.add(attribute);

		if(!"".equals(nodeID) && nodeID!=null){
			nodeID=ConverUtil.isNumeric(nodeID)?nodeID:nodeID.toUpperCase();
			nodeID = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(nodeID+"_defAttribute")
					.get(IotInfoConstant.dev_plc_node_id);
		}else{
			// 如果没有节点ID属性,gwId(集中器ID)属性值替代
			nodeID = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(plc_sn + "_defAttribute")
					.get(IotInfoConstant.dev_plc_plc_id);
		}

		Map<String, String> tags = new HashMap<String, String>(); // tags 标识属性
		tags.put("agreement", "plc");

		MetricInfoResponseDataVO metricInfoResponseDataVO = new MetricInfoResponseDataVO();
		metricInfoResponseDataVO.setId(nodeID);
		//增加forLogTag，方便跟踪，此字段没有业务含义，只是为了跟踪问题
		/*if(definedAttrList != null){
			Map<String,String> forLogTag = new HashMap<String,String>();
			forLogTag.put("type", "api_f7/plcSn/plcNodeSn");
			forLogTag.put("value", "api_f7/"+plc_sn+"/"+nodeID);
			definedAttrList.add(forLogTag);
		}*/
		metricInfoResponseDataVO.setDefinedAttributers(definedAttrList);
		metricInfoResponseDataVO.setTags(tags);
		// 消息结构
		MessageVO<MetricInfoResponseDataVO> messageVo = T_ResponseResult.getResponseVO(ctx, plc_sn,
				DataType.MetricInfoResponse.getMessageType(), metricInfoResponseDataVO);

		// kafka处理
		logger.info("========消息ID:"+messageVo.getMsgId()+", ==调SendKafkaUtils.sendKafka方法, 开始发送设备状态上报数据 :"+ JSON.toJSONString(messageVo));
		SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
		logger.info("========消息ID:"+messageVo.getMsgId()+", ==调SendKafkaUtils.sendKafka方法, 发送设备状态上报数据完成！");
	}

	/**
	 * PLC 响应 设备信号上报 发kafka
	 * 
	 * @param plc_sn
	 *            集中器地址
	 * @param nodeID  00000200053a
	 *            节点ID
	 * @param definedAttrList
	 * @throws Exception
	 */
	public static void plcSignlResponseSend(String plc_sn, String nodeID, List<Map> definedAttrList, ChannelHandlerContext ctx) throws Exception {
		Map<String, String> tags = new HashMap<String, String>(); // tags 标识属性
		tags.put("agreement", "plc");
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
		if(!"".equals(nodeID) && nodeID!=null){
			nodeID=ConverUtil.isNumeric(nodeID)?nodeID:nodeID.toUpperCase();
			nodeID = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(nodeID+"_defAttribute")
					.get(IotInfoConstant.dev_plc_node_id);
		}else{
			// 如果没有节点ID属性,gwId(集中器ID)属性值替代
			nodeID = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(plc_sn + "_defAttribute")
					.get(IotInfoConstant.dev_plc_plc_id);
		}
		DevSignlResponseDataVO devSignlResponseDataVO = new DevSignlResponseDataVO();
		devSignlResponseDataVO.setId(nodeID);
		devSignlResponseDataVO.setSignals(definedAttrList);
		devSignlResponseDataVO.setTags(tags);
		// 消息结构
		MessageVO<DevSignlResponseDataVO> messageVo = T_ResponseResult.getResponseVO(ctx, plc_sn, DataType.DevSignalResponse.getMessageType(),
				devSignlResponseDataVO);

		// kafka处理
		logger.info("========消息ID:"+messageVo.getMsgId()+", ==调SendKafkaUtils.sendKafka方法, 开始发送设备信号上报数据 :"+ JSON.toJSONString(messageVo));
		SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
		logger.info("========消息ID:"+messageVo.getMsgId()+", ==调SendKafkaUtils.sendKafka方法, 发送设备信号上报数据完成!");
	}

    /**
     * PLC ACK响应
     * 01H：集中器成功受理.(0)
     * 02H：命令或数据格式无效.(10005)
     * 03H：集中器忙.   10010（已经存在）/20324(（冲突）
     * @param plc_sn
     * @param cmd
     * @param nodeID
     * @param resultCode 0:成功，10005:失败，20324:忙
     * @param outList
     * @param ctx
     * @throws Exception
     */
    public static void plcACKResponseSend(String plc_sn,String cmd,String nodeID,Integer resultCode,String messageID, List<Map<String,Object>> outList,ChannelHandlerContext ctx)throws Exception{
        Map<String, String> tags = new HashMap<String, String>(); // tags 标识属性
        tags.put("agreement", "plc");

		if(ctx!=null){  //ctx不为空，表示从PLC 返回的， 否则从请求端手动返回的
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
			//算法后的 集中器SN
			plc_sn = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(plc_sn + "_defAttribute")
					.get(IotInfoConstant.dev_plc_plc_id);
			if(ConverUtil.parseNumeric(nodeID)==0) nodeID=plc_sn;

			if(!nodeID.equalsIgnoreCase(plc_sn)&& nodeID!=null && !"".equals(nodeID)){
				nodeID=ConverUtil.isNumeric(nodeID)?nodeID:nodeID.toUpperCase();
				nodeID = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(nodeID+"_defAttribute")
						.get(IotInfoConstant.dev_plc_node_id);
			}
		}
		if(ConverUtil.parseNumeric(nodeID)==0) nodeID=plc_sn;
        ResponseDataVO devResponseDataVO = new ResponseDataVO();
        devResponseDataVO.setId(nodeID);
        devResponseDataVO.setMethods(responseMethodBody(cmd,outList));
        devResponseDataVO.setTags(tags);

        // 消息结构
        ResultMessageVO<ResponseDataVO> messageVo = T_ResponseResult.getACKResponseVO(plc_sn,nodeID,resultCode,messageID,DataType.Response.getMessageType(),
                devResponseDataVO);

        // kafka处理
        SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
    }

	/**
	 * 批处理 下发指令 PLC执行
	 * @param requestMesg 下发的指令报文
	 * @param plcId 集中器ID
	 * @param cmd 指令码
	 * @param mesgID 消息ID
	 */
	public static void plcRequestCMDSend (String requestMesg,String plcId,String cmd,String mesgID,ChannelHandlerContext ctx){
		if(StringUtils.trimToNull(requestMesg)==null || StringUtils.trimToNull(plcId)==null || StringUtils.trimToNull(cmd)==null || ctx==null)
			return;
		String serverPort_plcSn = null,plcSN=null,plcPort=null,key="",alg_plcId="";
		try {
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
			//算法后的 集中器SN
			alg_plcId = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "").get(plcId + "_defAttribute")
					.get(IotInfoConstant.dev_plc_plc_id);

			plcSN=PlcProtocolsBusiness.getPlcSnByPlcID(alg_plcId);
			plcPort=PlcProtocolsBusiness.getPortByPlcID(alg_plcId);
			if(StringUtils.trimToNull(plcSN)!=null && StringUtils.trimToNull(plcPort)!=null) {
				serverPort_plcSn = plcPort.concat(plcSN).trim();
				key=plcId.concat("_").concat(cmd); //集中器SN+指令码 做为缓存KEY
				if(StringUtils.trimToNull(mesgID)!=null) ProtocalAdapter.setCacheReqID(key,mesgID);//缓存 下发指令 的请求消息ID
				CommandHandler.writeCommand(serverPort_plcSn, requestMesg, mesgID);
			}else{
				logger.warn("==批处理的下发指令:"+cmd+",==plcRequestCMDSend方法,消息ID:"+mesgID+", Not found serverPort_plcSn from Gloable cache, 下发失败!,messageVo/{}", requestMesg);
			}
		} catch (Exception e) {
			logger.error("=====批处理的下发指令:"+cmd+",==plcRequestCMDSend方法,消息ID:"+mesgID+",下发异常! "+e.getMessage());
		}
	}

	/**
	 * PLC Methods响应消息组装
	 * @param cmd 指令码
	 * @param outBody out出参
	 * @return
	 */
    private static List<Map> responseMethodBody(String cmd,List<Map<String,Object>>outBody){
    	List<Map>resMethodList=new ArrayList<Map>();
		LinkedHashMap<String, Object> bodyMap = new LinkedHashMap<String, Object>();
		bodyMap.put("method", PLC_METHOD_CMD_CONFIG.CMD2Method(cmd)); //指令码 对应操作方法名
		bodyMap.put("out",outBody); //响应出参, 如：TN 任务号
		resMethodList.add(bodyMap);
		return resMethodList;
	}

	/**
	 * @param modbusInfo
	 * @param address
	 * @param cCode
	 * @param cmdCode
	 * @param _PDT
	 * @throws Exception
	 */
	public static void setProtocalsAuto(ModbusInfo modbusInfo, byte[] address, byte[] cCode, byte[] cmdCode,
			byte[] _PDT) throws Exception {
		// 1,devId,cCode,cmdCode
		modbusInfo.setAddress(address);// 设备ID
		modbusInfo.setcCode(cCode);// 广播
		modbusInfo.setCmdCode(cmdCode);
		// 2,Pdt部分
		modbusInfo.setPdt(_PDT);
	}

	/**
	 * 是否已在配置中
	 */
	public static final Map<String, Boolean> plc_config_threadruning_flag = new HashMap<String, Boolean>();

	public static void init_1(ChannelHandlerContext ctx, ModbusInfo modbusInfo,RedisService redisService) {
		/*
		 * if(PlcProtocolsUtils.plc_config_threadruning_flag!=null &&
		 * PlcProtocolsUtils.plc_config_threadruning_flag.get(modbusInfo.getAddress_str(
		 * )) != null &&
		 * PlcProtocolsUtils.plc_config_threadruning_flag.get(modbusInfo.getAddress_str(
		 * )) == true ){
		 * logger.warn("设备【"+modbusInfo.getAddress_str()+"】已存在运行的配置线程,取消本次执行!" +
		 * loggerBaseInfo(modbusInfo)); return; }
		 */
		plc_config_threadruning_flag.put(modbusInfo.getAddress_str(), true);// 处理完毕后设置为false

		// 判断控制码，根据控制码来引导配置流程
		if (("8e".equals(modbusInfo.getCmdCode_str().toLowerCase())
				&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init1 集中器配置参数
				|| ("8c".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init2 设置时钟
				|| ("99".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init3 删除所有节点
				|| ("69".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init4 删除FLASH节点
				|| ("62".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init5 开始组网
				|| ("63".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase()))
				|| ("66".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase()))
				|| ("96".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase()))) {
			initX_config_jzq_response(ctx, modbusInfo,redisService);
		} else {
			// 发起init1请求 ，成功继续，失败退出流程
			init1_config_jzq(ctx, modbusInfo.getAddress_str());
		}

		plc_config_threadruning_flag.put(modbusInfo.getAddress_str(), false);// 处理完毕后设置为false
	}

	public static void init_2_9(ChannelHandlerContext ctx, ModbusInfo modbusInfo,RedisService redisService) {
		// 判断控制码，根据控制码来引导配置流程
		if (("8e".equals(modbusInfo.getCmdCode_str().toLowerCase())
				&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init1 集中器配置参数
				|| ("8c".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init2 设置时钟
				|| ("99".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init3 删除所有节点
				|| ("69".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init4 删除FLASH节点
				|| ("62".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())) // init5 开始组网
				|| ("63".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase()))
				|| ("66".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase()))
				|| ("96".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase()))
				|| ("98".equals(modbusInfo.getCmdCode_str().toLowerCase())
						&& "80".equals(modbusInfo.getcCode_str().toLowerCase())))
		{
			initX_config_jzq_response(ctx, modbusInfo,redisService);
		}
	}

	public static boolean init1_config_jzq(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo(); // 8e表示命令码 00表示请求 80表示响应
		try {
			// 获取设备信息 通过chennelID ==> devinfo
			Map<String, Object> def_attributers = IotInfoConstant.allDevInfo.get(getPort(ctx.channel()))
					.get(plc_SN + "_defAttribute");// 从自定义的字段里面获取值

			logger.info("=====>>>initstep1(" + plc_SN + ")集中器配置参数...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("8e"));
			// 获取设备信息 构造PDT
			/*
			 * 名称 说明 长度 经度 取小数后2位(-180.00 - +180.00) 再*100 整型 2 Bs 如：+9068 纬度 取小数后2位 (-90.00
			 * - +90.00) 再*100整型 2 Bs 如：-XXXX 时区 -11 - +12整型 1 B 短信中心号 如: +8613010888500
			 * 字符串型 14 Bs --不考虑 管理员1 如:13812345678 字符串型 11 Bs --不考虑 管理员2 如:13812345678 字符串型
			 * 11 Bs --不考虑 操作员1 如:13812345678 字符串型 11 Bs --不考虑 操作员2 如:13812345678 字符串型 11 Bs
			 * --不考虑 操作员3 如:13812345678 字符串型 11 Bs --不考虑 短信操作回复 0 否 1是 1 B --不考虑 光控时段
			 * 前2Byte：开始 时分 2Bs 后2Byte：结束 时分 2 Bs
			 */
			String plc_cfg_step1_longitude = (String)def_attributers.get(IotInfoConstant.dev_plc_cfg_longitude); // 经度
			String plc_cfg_step1_latitude = (String)def_attributers.get(IotInfoConstant.dev_plc_cfg_latitude); // 纬度
			String plc_cfg_step1_sq = (String)def_attributers.get(IotInfoConstant.dev_plc_cfg_sq); // "-8"; //时区 转化成整形表示
			String plc_cfg_step1_gksd_start = (String)def_attributers.get(IotInfoConstant.dev_plc_cfg_gksd_start); // "8:10";
																											// //光控时段-开始时分
			int gksd_start_h = Integer.parseInt(plc_cfg_step1_gksd_start.split(":")[0]);
			int gksd_start_s = Integer.parseInt(plc_cfg_step1_gksd_start.split(":")[1]);
			String plc_cfg_step1_gksd_end = (String)def_attributers.get(IotInfoConstant.dev_plc_cfg_gksd_end); // "8:10"; //光控时段
																										// 结束时分
			int gksd_end_h = Integer.parseInt(plc_cfg_step1_gksd_end.split(":")[0]);
			int gksd_end_s = Integer.parseInt(plc_cfg_step1_gksd_end.split(":")[1]);

			byte[] temp1 = new byte[2];
			temp1 = ByteUtils.varIntToByteArray(9068); // 经度
			byte[] temp2 = new byte[2];
			temp2 = ByteUtils.varIntToByteArray(-2536); // 纬度
			byte[] temp3 = new byte[1];
			temp3 = ByteUtils.varIntToByteArray(-10); // 时区
			byte[] temp4_10 = new byte[14 + 11 * 5 + 1];
			/*
			 * for(int p=0;p<temp4_10.length;p++){ //不需要给值，报文的相应位置默认给0 temp4_10[p]= 0; }
			 */
			byte[] temp11_0 = new byte[1];
			byte[] temp11_1 = new byte[1];
			temp11_0 = ByteUtils.varIntToByteArray(gksd_start_h); // 时
			temp11_1 = ByteUtils.varIntToByteArray(gksd_start_s); // 分
			byte[] temp12_0 = new byte[1]; // 时
			byte[] temp12_1 = new byte[1]; // 时
			temp12_0 = ByteUtils.varIntToByteArray(gksd_end_h); // 时
			temp12_1 = ByteUtils.varIntToByteArray(gksd_end_s); // 分

			int len = temp1.length + temp2.length + temp3.length + temp4_10.length + 2 + 2;
			ByteBuf byteBuf = Unpooled.buffer(len);
			byteBuf.writeBytes(temp1).writeBytes(temp2).writeBytes(temp3).writeBytes(temp4_10).writeBytes(temp11_0)
					.writeBytes(temp11_1).writeBytes(temp12_0).writeBytes(temp12_1); // CRC计算，根据CRC所在报文位置--》headStart所有的数据
			byte[] temp = byteBuf.array();
			if (byteBuf != null)
				ReferenceCountUtil.release(byteBuf);
			modbusInfo.setPdt(temp);
			// 响应
			logger.info(">>>请求设备,cmdCode=8e_00  ,byteMsg="
					+ ConverUtil.convertByteToHexString(modbusInfo.getNewFullData()));
			logger.info(">>>请求设备,cmdCode=8e_00  ,byteMsg.len=" + modbusInfo.getNewFullData().length);

			ctxWriteAndFlush(ctx, modbusInfo, "集中器配置参数", 1);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")响应设备,cmdCode=8e_00,exception ！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	/**
	 * 打印跟踪日志
	 * 
	 * @param id
	 * @param cCmdCode
	 * @param cCode
	 * @param step
	 */
	static void _logInfo(String id, String cCmdCode, String cCode, int step) {
		logger.info(">>>initstep_" + step + " (" + id + "-配置流程)响应设备,cmdCode=" + cCmdCode + ",cCode=" + cCode
				+ ",集中器反馈：集中器成功受理!");
	}

	/**
	 * 配置过程,指令响应后进入此处理
	 * 
	 * @param ctx
	 * @param modbusInfo
	 * @return
	 */
	public static boolean initX_config_jzq_response(ChannelHandlerContext ctx, ModbusInfo modbusInfo,RedisService redisService) {
		boolean excuSeccess = true;
		String cCode = modbusInfo.getcCode_str();
		String cCmdCode = modbusInfo.getCmdCode_str();
		int step = 0;
		try {

			logger.info(">>>initstep(" + modbusInfo.getAddress_str() + "-配置流程)响应设备,cmdCode=" + cCmdCode + ",cCode="
					+ cCode + ",byteMsg=" + ConverUtil.convertByteToHexString(modbusInfo.getNewFullData()));
			
			//做下一步之前休眠下
			Thread.currentThread().sleep(1000*10);
			
			// PDT ack
			/*
			 * 01H：集中器成功受理。 02H：命令或数据格式无效。 03H：集中器忙。
			 */			
			String hex_ptd = ConverUtil.convertByteToHexString(modbusInfo.getPdt()); // ack
			if ("01".equals(hex_ptd)) {
				// 8e 请求集中器配置
				if ("8e".equals(cCmdCode) && "80".equals(cCode)) { // 请求集中器配置成功
					step = 2;
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备 设置时间
					init2_config_setTime(ctx, modbusInfo.getAddress_str());
				}
				if ("8c".equals(cCmdCode) && "80".equals(cCode)) { // 设置时间成功
					step = 3;
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，删除所有节点
					init3_config_cleanNode(ctx, modbusInfo.getAddress_str());
				}
				if ("99".equals(cCmdCode) && "80".equals(cCode)) { // 清除所有节点成功
					step = 4;
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，删除所有节点
					init4_config_delFlash(ctx, modbusInfo.getAddress_str());
				}
				if ("69".equals(cCmdCode) && "80".equals(cCode)) { // 清除缓存成功
					step = 5;
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，开始组网
					init5_CfgNetwork(ctx, modbusInfo.getAddress_str());
				}
				if ("62".equals(cCmdCode) && "80".equals(cCode)) { // 组网执行成功 ，不管失败还是成功，都需要手动做停止组网的动作 ，2~300节点一般要好几分钟
					step = 6;
					Thread.currentThread().sleep(1000*30);
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，停止组网
					init6_stopCfgNetwork(ctx, modbusInfo.getAddress_str());
				}
				if ("63".equals(cCmdCode) && "80".equals(cCode)) { // 停止组网成功
					step = 7;
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，存储节点
					init7_saveNode(ctx, modbusInfo.getAddress_str());
				}
				if ("66".equals(cCmdCode) && "80".equals(cCode)) { // 存储节点成功
					step = 8;
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，下发节点
					init8_sendDownNode(ctx, modbusInfo.getAddress_str());
				}
				if ("96".equals(cCmdCode) && "80".equals(cCode)) { // 下发节点成功
					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);
					// 请求设备，配置节点
					ini9_configNode(ctx, modbusInfo.getAddress_str());
				}
				if ("98".equals(cCmdCode) && "80".equals(cCode)) { // 配置节点成功
 					_logInfo(modbusInfo.getAddress_str(), cCmdCode, cCode, step);//打印结果即可
 					//gloable_dev_status.put(modbusInfo.getAddress_str() + "_config", "1");// 配置节点后 设置为已配置即可
 					redisService.set(rediskey_plc_isconfig_ + modbusInfo.getAddress_str() , "1");  
 					logger.info(">>>===============initstep all over ,seccessful!!=================");//打印结果即可
 				}
			} else if ("02".equals(hex_ptd)) {
				logger.info(">>>initstep(" + modbusInfo.getAddress_str() + "-配置流程)响应设备,cmdCode=" + cCmdCode + ",cCode="
						+ cCode + " ,集中器反馈：命令或数据格式无效");
			} else if ("03".equals(hex_ptd)) {
				logger.info(">>>initstep(" + modbusInfo.getAddress_str() + "-配置流程)响应设备,cmdCode=" + cCmdCode + ",cCode="
						+ cCode + " ,集中器反馈：集中器忙！");
			} else {
				logger.info(">>>initstep(" + modbusInfo.getAddress_str() + "-配置流程)响应设备,cmdCode=" + cCmdCode + ",cCode="
						+ cCode + " ,集中器反馈：返回数据异常！");
			}

		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + "-配置流程)响应设备,cmdCode=" + cCmdCode + ",cCode="
					+ cCode + " ,exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	public static boolean init2_config_setTime(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo_8c_00 = new ModbusInfo();
		try {
			// 获取设备信息 通过chennelID ==> devinfo
			Map<String, Object> def_attributers = IotInfoConstant.allDevInfo.get(getPort(ctx.channel()))
					.get(plc_SN + "_defAttribute");// 从自定义的字段里面获取值

			logger.info("=====>>>initstep(" + plc_SN + ")集中器设置时钟...===============");
			modbusInfo_8c_00.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo_8c_00.setcCode(ConverUtil.hexStrToByteArr("00"));
			modbusInfo_8c_00.setCmdCode(ConverUtil.hexStrToByteArr("8c"));
			// 构造PDT 7个字节
			/*
			 * 年，月，日，周，时，分，秒
			 */
			long timestamp = System.currentTimeMillis();// 输入定为时间戳
			Calendar ca = Calendar.getInstance();
			ca.setTimeInMillis(timestamp);
			int hour = ca.get(Calendar.DATE); // 时
			int min = ca.get(Calendar.MINUTE); // 分
			int sec = ca.get(Calendar.SECOND);// 秒
			int week = ca.get(Calendar.DAY_OF_WEEK);// 周 默认从1开始 是否要-1？
			int day = ca.get(Calendar.DAY_OF_MONTH);// 日
			int month = ca.get(Calendar.MONTH);// 月 默认0开始 是否要 +1？
			int year = ca.get(Calendar.YEAR);// 年

			byte[] temp1 = new byte[7];
			temp1[0] = (byte) year;
			temp1[1] = (byte) month;
			temp1[2] = (byte) day;
			temp1[3] = (byte) week;
			temp1[4] = (byte) hour;
			temp1[5] = (byte) min;
			temp1[6] = (byte) year;
			modbusInfo_8c_00.setPdt(temp1);

			ctxWriteAndFlush(ctx, modbusInfo_8c_00, "集中器设置时钟", 2);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo_8c_00.getAddress_str() + ")请求设备,,cmdCode="
					+ modbusInfo_8c_00.getCmdCode_str() + ",ccode=" + modbusInfo_8c_00.getcCode_str() + ",exception1！",
					e);
			excuSeccess = false;
		}
		return excuSeccess;
	}

	public static boolean init3_config_cleanNode(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			// 删除所有节点，只需要指定集中器ID即可
			logger.info("=====>>>initstep(" + plc_SN + ")清除节点...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("03")); // 01删某个ID，02删除一组 03删除所有
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("99"));
			// 构造PDT 6个字节
			byte[] temp1 = new byte[6];
			modbusInfo.setPdt(temp1);

			ctxWriteAndFlush(ctx, modbusInfo, "清除节点", 3);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	public static void ctxWriteAndFlush(ChannelHandlerContext ctx, ModbusInfo modbusInfo, String logmsg, int step)
			throws Exception {
		ctx.channel().writeAndFlush(modbusInfo.getNewFullDataWithByteBuf())
				.addListener((ChannelFutureListener) future -> {
					if (future.isSuccess()) {
						logger.info(">>>initstep" + step + " (" + modbusInfo.getAddress_str() + ")请求设备成功/" + logmsg
								+ "！,cmdCode=" + modbusInfo.getCmdCode_str() + ",ccode=" + modbusInfo.getcCode_str()
								+ ",byteMsg=" + ConverUtil.convertByteToHexStringPrint(modbusInfo.getNewFullData()));
					} else {
						logger.info(">>>initstep" + step + " (" + modbusInfo.getAddress_str() + ")请求设备失败/" + logmsg
								+ "！,cmdCode=" + modbusInfo.getCmdCode_str() + ",ccode=" + modbusInfo.getcCode_str()
								+ ",byteMsg=" + ConverUtil.convertByteToHexStringPrint(modbusInfo.getNewFullData()));
					}
				});
	}

	public static boolean init4_config_delFlash(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			// 删除所有节点，只需要指定集中器ID即可
			logger.info("=====>>>initstep(" + plc_SN + ")删除FLASH节点...===============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("69"));
			byte[] temp1 = new byte[0];
			modbusInfo.setPdt(temp1);// 注意这里，没有PDT的时候 是否需要给值0

			ctxWriteAndFlush(ctx, modbusInfo, "删除FLASH节点", 4);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	/**
	 * 68 00 00 00 00 00 01 68 00 02 62 02 37 16
	 * 
	 * @param ctx
	 * @param plc_SN
	 * @return
	 */
	public static boolean init5_CfgNetwork(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN + ") 开始组网...===============");
			// 获取设备信息
			Map<String, Object> attributers = IotInfoConstant.allDevInfo.get(getPort(ctx.channel()))
					.get(plc_SN + "_defAttribute");

			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("62"));
			// PDT 组网个数
			byte[] groupNum = new byte[1];
			int x = Integer.parseInt((String)attributers.get(IotInfoConstant.dev_plc_cfg_step5_groupAtuo));
			logger.info("     >>>组网个数 = " + x);
			groupNum[0] = (byte) x;
			modbusInfo.setPdt(groupNum);

			ctxWriteAndFlush(ctx, modbusInfo, "开始组网", 5);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	/**
	 * 停止组网
	 * 
	 * @param ctx
	 * @param plc_SN
	 * @return
	 */
	public static boolean init6_stopCfgNetwork(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN + ") 停止组网...==63,00=============");
			Thread.currentThread().sleep(1000 * 5);// 等待5秒钟后在做停止组网操作 ，不管组网是否达到要求的数量，这里暂时做成强制停止组网
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("63"));
			// PDT
			byte[] temp1 = new byte[0];
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx, modbusInfo, "停止组网", 6);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;
	}

	public static boolean init7_saveNode(ChannelHandlerContext ctx, String plc_SN) { // 未完成。。。。。
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN + ") 存储节点...==66,00=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("00"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("66"));
			// PDT
			byte[] temp1 = new byte[0];
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx, modbusInfo, "存储节点", 7);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	public static boolean init8_sendDownNode(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep8(" + plc_SN + ") 下发节点...==96,03=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("03"));//全部节点
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("96"));

			// 获取设备信息 通过chennelID ==> devinfo
			Map<String, Object> def_attributers = IotInfoConstant.allDevInfo.get(getPort(ctx.channel()))
					.get(plc_SN + "_defAttribute");// 从自定义的字段里面获取值
			List<Map<String, String>> nodelist = IotInfoConstant.plc_relation_plcsnToNodelist
					.get(getPort(ctx.channel())).get(plc_SN);

			// PDT 节点列表
			int pdtLen = 8 * nodelist.size();// 一个节点占8个字节
			if (nodelist != null && nodelist.size() > 0) {
				byte[] temp1 = new byte[pdtLen];
				if (nodelist.size() > 0) {
					ByteBuf byteBuf = Unpooled.buffer(pdtLen);
					for (int i = 0; i < nodelist.size(); i++) {
						Map<String, String> item = nodelist.get(i);
						int len = 8;
						String nodeid = item.get(IotInfoConstant.dev_plc_node_sn); // 6
						int groupid = Integer.parseInt(item.get(IotInfoConstant.dev_plc_node_group)); // 1
						String devCode = item.get(IotInfoConstant.dev_plc_node_devCode); // 1
						logger.info("=====  nodesn /groupid /devCode ;" + nodeid +"/"+groupid+"/"+devCode );
						byte[] t1 = new byte[6];
						t1=ConverUtil.hexStrToByteArr(nodeid);
						byte[] t2 = new byte[1];
						t2=ByteUtils.varIntToByteArray(groupid);
						byte[] t3 = new byte[1];
						t3 = ConverUtil.hexStrToByteArr(devCode);
						byteBuf.writeBytes(t1) // 节点ID
							.writeBytes(t2) // 组号
							.writeBytes(t3); // 设备码
						/*byteBuf.writeBytes(ConverUtil.hexStrToByteArr(nodeid)) // 节点ID   这样这届赋值给byteBuf貌似不行
								.writeBytes(ByteUtils.varIntToByteArray(groupid)) // 组号
								.writeBytes(ConverUtil.hexStrToByteArr(devCode)); // 设备码
*/					}
					temp1 = byteBuf.array();
					if (byteBuf != null){
						ReferenceCountUtil.release(byteBuf);
					}
				}
				modbusInfo.setPdt(temp1);
				ctxWriteAndFlush(ctx, modbusInfo, "下发节点", 8);
			} else {
				//
				logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备, cmdCode="
						+ modbusInfo.getCmdCode_str() + "ccode=" + modbusInfo.getcCode_str() + ",下发节点失败，节点列表不能为空！");
			}
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	public static boolean ini9_configNode(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>initstep(" + plc_SN + ") 配置节点...==98,03=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("03"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("98"));
			// PDT
			byte[] temp1 = new byte[0];
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx, modbusInfo, "配置节点", 9);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess;

	}

	/**
	 * 心跳处理
	 * 
	 * @param ctx
	 * @param modbusInfo
	 */
	public static void heartBeat(ChannelHandlerContext ctx, ModbusInfo modbusInfo) {
		if (("f1".equals(modbusInfo.getCmdCode_str().toLowerCase())
				&& "04".equals(modbusInfo.getcCode_str().toLowerCase()))) { // 上报的是心跳
			heartBeatResponse(ctx, modbusInfo.getAddress_str());
		}
	}

	public static boolean heartBeatResponse(ChannelHandlerContext ctx, String plc_SN) {
		boolean excuSeccess = true;
		final ModbusInfo modbusInfo = new ModbusInfo();
		try {
			logger.info("=====>>>heartbeat 收到心跳...==f1,04=============");
			modbusInfo.setAddress(ConverUtil.hexStrToByteArr(plc_SN));
			modbusInfo.setcCode(ConverUtil.hexStrToByteArr("80"));
			modbusInfo.setCmdCode(ConverUtil.hexStrToByteArr("f1"));
			// PDT
			byte[] temp1 = new byte[0];
			temp1 = ConverUtil.hexStrToByteArr("01");
			modbusInfo.setPdt(temp1);
			ctxWriteAndFlush(ctx, modbusInfo, "心跳下发==>", -1);
		} catch (Exception e) {
			logger.error(">>>initstep(" + modbusInfo.getAddress_str() + ")请求设备,cmdCode=" + modbusInfo.getCmdCode_str()
					+ ",ccode=" + modbusInfo.getcCode_str() + ",exception1！", e);
			excuSeccess = false;
		}
		return excuSeccess; 

	}

	public static byte[] getPdt(byte[]... ls) throws Exception {
		int len = 0;
		for (byte[] it : ls) {
			len = len + it.length;
		}
		ByteBuf byteBuf = Unpooled.buffer(len);
		for (byte[] it : ls) {
			byteBuf.writeBytes(it);
		}
		byte[] temp = byteBuf.array();
		if (byteBuf != null)
			ReferenceCountUtil.release(byteBuf);
		return temp;
	}

}
