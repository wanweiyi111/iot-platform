package com.hzyw.iot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.hzyw.iot.vo.ResponseCache;
import com.hzyw.iot.vo.dataaccess.ResponseDataVO;
import com.hzyw.iot.vo.dataaccess.ResultMessageVO;
import com.hzyw.iot.vo.dc.ModbusInfo;

import cn.hutool.core.date.DateUtil;

/**
 * 背景及实现目标： 
 * 1，记录 开关灯 指令下发的请求到缓存 （--此功能暂时取消）
 * 2，开关灯响应后，根据返回报文，反向查找在缓存中的请求  （--此功能暂时取消，因为返回的报文内容太少，无法反向查找到缓存中的请求，方案不可行）
 * 3，异常、响应结果、上报到KAFKA
 * @author Administrator
 *
 */
public class ResponseCacheUtils {
	private static final Logger logger = LoggerFactory.getLogger(ResponseCacheUtils.class);
	
	/**
	 * 缓存hash结构：<plcSn+cmcCOde+cCode+nodeId , msgId-gwid-nodeid-timestamp> , 
	 *  ---通过此KEY实现设备响应后反向查找到MSGID，并非完全精准，因为设备没有透传MSGID
	 */
	public static final Map<String,ResponseCache> plc_ResponseCache = new HashMap<String,ResponseCache>();
	
	
	/**
	 * 超过10秒设备还没有响应则清除
	 */
	public static void cleanTimeoutMsg(){//自动清理过期的消息，过期时间默认10秒钟 
		long currentTime = System.currentTimeMillis();
		for(String msgId : plc_ResponseCache.keySet()){
			ResponseCache temp = plc_ResponseCache.get(msgId);
			if((currentTime - temp.getTimestamp()) > 10*1000){
				plc_ResponseCache.remove(msgId);
			}
		}
	}
	
	public static ResponseCache getMsgByPlc(ModbusInfo modbusInfo,String nodeSn){//自动清理过期的消息，过期时间默认10秒钟 
		String cacheKey = modbusInfo.getCacheMsgId() + "_" + nodeSn;
		return plc_ResponseCache.get(cacheKey);
	}
	
	/**
	 * 
	 * 缓存此消息ID ，以供设备响应后反向查找此   
	 * @param modbusInfo
	 * @param logmsg
	 * @param msgid
	 * @param plcid
	 * @param nodeid
	 */
	public static void addResponseCache(ModbusInfo modbusInfo,String method,String msgid,String plcid,String nodeid){
		ResponseCache rc = new ResponseCache(method,modbusInfo.getAddress_str(),modbusInfo.getCmdCode_str(),modbusInfo.getcCode_str());
		/*if(plc_ResponseCache.get(modbusInfo.getCacheMsgId())!= null && timeout(modbusInfo.getCacheMsgId())){
			//上一个请求处理已超时，上报上个超时的消息
			logger.info("---msgid -覆盖上一个消息并返回上一个的通知-----" +msgid);
			ResponseCache ls = plc_ResponseCache.get(modbusInfo.getCacheMsgId());
			sendkafka_by_ResponseCache(ls.getMethod(), ls.getMsgId(), ls.getPlcId(), ls.getNodeId(), ResponseCode.HZYW_PLC_RESPONSE_TIMEOU);//上报超时
		}*/
		plc_ResponseCache.put(modbusInfo.getCacheMsgId(), rc);//任何一个(执行成功的)请求都应该记录，以供跟踪返回
		cleanTimeoutMsg(); 
	}
	 
	
	/**
	 * @param method  操作方法
	 * @param msgid   消息ID
	 * @param plcid   
	 * @param nodeid  
	 * @param messageCode  消息编号
	 */
	public static void sendkafka_by_ResponseCache(String method,String msgid,String plcid,String nodeid,int messageCode){
		try{
			//上报成功或失败
			ResponseDataVO responseDataVo = new ResponseDataVO();
			responseDataVo.setId(nodeid);
			List<Map> ls = new ArrayList<Map>();
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("method", method);
			List<Map> out = new ArrayList<Map>();
			out.add(new HashMap());
			m.put("out",out);
			ls.add(m);
	 		responseDataVo.setMethods(ls);
			Map<String,String> tags = new HashMap<String,String>();
			tags.put(IotInfoConstant.dev_plc_dataaccess_key, IotInfoConstant.dev_plc_dataaccess_value);
			tags.put("api_45/response/plcid/nodeid/msgid", "api_45/response/"+plcid+"/"+nodeid+"/"+msgid);//没有业务含义，只用于跟踪问题，可以通过messageCode区分消息来源
			responseDataVo.setTags(tags);
			//消息结构
			ResultMessageVO messageVo = new ResultMessageVO();
			messageVo.setType("metricInfoResponse");
			messageVo.setTimestamp(DateUtil.currentSeconds());
			messageVo.setMsgId(msgid); 
			messageVo.setData(responseDataVo);
			messageVo.setGwId(plcid);
			messageVo.setMessageCode(messageCode);//返回消息编码
			//kafka处理
			SendKafkaUtils.sendKafka("iot_topic_dataAcess",JSON.toJSONString(messageVo));
		} catch (Exception e) {
			logger.info("===request_42===ResponseCacheUtils.sendkafka_by_ResponseCache()== exception1==,/msgid="
					+ msgid + "," + "plcid=" + plcid + ",nodeid=" + nodeid); //设备指令下发场景
			logger.error("===request_42===ResponseCacheUtils.sendkafka_by_ResponseCache()== exception1==,/msgid="
					+ msgid + "," + "plcid=" + plcid + ",nodeid=" + nodeid, e);
		} 
	}
 
	/**
	 * 获取cacheId消息，上报response请求到前端页面
	 * @param cacheID
	 * @param messageCode
	 */
	public static void sendkafka_by_ResponseCache(ModbusInfo modbusInfo,String port,int messageCode){
		//ResponseCache ls = ResponseCacheUtils.plc_ResponseCache.get(cacheID);从请求缓存里查找对应的msgid
		//开关灯-设备返回的响应报文，根本不具备条件对齐到是哪个灯响应的消息，即你不可能知道是哪个NodeId，也不可能知道是哪个msgId的请求的response
		//--那么这里处理方案调整为：直接返回某个PLC的处理结果即可
		String method = "set_brightness";//默认都是调灯光接口
		String msgId = UUID.randomUUID().toString(); //给个默认值
		String plcId = null;
		try {
			plcId = PlcProtocolsBusiness.getPlcIdByPlcSn(modbusInfo.getAddress_str(), port);
		} catch (Exception e) {
			logger.info("===request_42===ResponseCacheUtils.sendkafka_by_ResponseCache()== exception2==,/msgid=none," + "plcid=" + plcId + ",nodeid=none" );
			logger.error("===request_42===ResponseCacheUtils.sendkafka_by_ResponseCache()== exception2==,/msgid=none", e);//设备响应场景
		} //根据当前的plc_sn获取
		String nodeId = plcId; //因不具备条件取灯的ID，这里默认给plc_sn对应的集中器id
		sendkafka_by_ResponseCache(method, msgId, plcId, nodeId, messageCode);
	}
}
