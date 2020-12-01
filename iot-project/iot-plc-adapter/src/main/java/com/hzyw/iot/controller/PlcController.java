package com.hzyw.iot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.service.ObjectService;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.utils.PlcProtocolsUtils;
import com.hzyw.iot.utils.md5.DeviceIdGenerator;
import com.hzyw.iot.vo.dc.GlobalInfo;
import com.hzyw.iot.vo.dc.RTUChannelInfo;

import io.netty.channel.ChannelHandlerContext;

@RestController
public class PlcController {
	
	private static final Logger logger = LoggerFactory.getLogger(PlcController.class);
	
    @Autowired
	private RedisService redisService; //redis工具类
    
    @Autowired
   	private ObjectService objectService; //mogoDb
    
    @Autowired
    ApplicationConfig  appConfig;
    
	/**
	 * 手工设置PLC集中器的后台自动配置流程的是否已配置状态
	 * 
	 * @param jsonParam
	 * 入参：
	 *  {
			"plc_sn":"xxxxxxxxx",
			"isConfig":"0"   0-未配置  1-已配置  留空则不做设置
			"isLogin":"0"    0-未登陆  1-已登陆  留空则不做设置
		}
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/resetConfig", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String resetConfigStatusByPlcId(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null || jsonParam.getString("isConfig") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			logger.info("curent plc isconfig="
					+ redisService.get(PlcProtocolsUtils.rediskey_plc_isconfig_ + jsonParam.getString("plc_sn"))
					+ " /sn=" + jsonParam.getString("plc_sn"));
			
			//设置是否已配置
			if(jsonParam.getString("isConfig")!=null && !"".equals(jsonParam.getString("isConfig").trim())){
				redisService.set(PlcProtocolsUtils.rediskey_plc_isconfig_ + jsonParam.getString("plc_sn") , jsonParam.getString("isConfig"));
			}
			//设置是否已登陆
			if(jsonParam.getString("isLogin")!=null  && !"".equals(jsonParam.getString("isLogin").trim())){
				PlcProtocolsUtils.gloable_dev_status.put(jsonParam.getString("plc_sn") + PlcProtocolsUtils._login,jsonParam.getString("isLogin"));
			}
			
			logger.info("curent plc update seccess, isconfig="
					+ redisService.get(PlcProtocolsUtils.rediskey_plc_isconfig_ + jsonParam.getString("plc_sn"))
					+ " /sn=" + jsonParam.getString("plc_sn")); 
			String data = "isconfig="+redisService.get(PlcProtocolsUtils.rediskey_plc_isconfig_ + jsonParam.getString("plc_sn"));
			data = data + "/isLogin=" + PlcProtocolsUtils.gloable_dev_status.get(jsonParam.getString("plc_sn") + PlcProtocolsUtils._login);
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", data);
		}catch(Exception e){
			logger.warn("PlcController::resetConfigStatusByPlcId ; curent plc update exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	/**
	 * 查询所有设备信息
	 * 查询初始化的集中器和灯具的数据
	 * 
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/initData/allDevInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String queryAllDevices(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String requestMessageVOJSON = jsonParam.toJSONString();
			Map<String, Map<String, Map<String, Object>>> alldevices = IotInfoConstant.allDevInfo;
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", alldevices);
		}catch(Exception e){
			logger.error(""   ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	/**
	 * 查询PLCID和SN关系
	 * 
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/initData/plc_relation_deviceToSn", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String plc_relation_deviceToSn(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String requestMessageVOJSON = jsonParam.toJSONString();
			Map<String, String> plc_relation_deviceToSn = IotInfoConstant.plc_relation_deviceToSn;
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", plc_relation_deviceToSn);
		}catch(Exception e){
			logger.error(""   ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	/**
	 * 查询PLC下的节点详情列表
	 * 
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/initData/plc_relation_plcsnToNodelist", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String plc_relation_plcsnToNodelist(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String requestMessageVOJSON = jsonParam.toJSONString();
			Map<String, Map<String, List<Map<String,String>>>> rs = IotInfoConstant.plc_relation_plcsnToNodelist;
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", rs);
		}catch(Exception e){
			logger.error(""   ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	/**
	 * 下发指令（通过HTTP REST）
	 * 
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/request", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String _request(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{ 
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("gwId") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			PlcProtocolsBusiness.all.clear();
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String requestMessageVOJSON = jsonParam.toJSONString();
			PlcProtocolsBusiness.protocals_process(requestMessageVOJSON,appConfig);
			
			logger.info("===手工====执行指令,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
			
			//因为是异步的返回，是无法直接获取不到返回的数据，那么为了通过简单的方式获取设备响应的结果，这里做暂停等待处理
			Thread.currentThread().sleep(1000*6);
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", PlcProtocolsBusiness.all);
		}catch(Exception e){
			logger.error("===手工====执行指令,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	@ResponseBody
	@RequestMapping(value = "/plc/request/return", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String _request_return(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{ 
			logger.info("param: " + jsonParam.toJSONString());
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", PlcProtocolsBusiness.all);
		}catch(Exception e){
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	 
	//查询SN下的已下发的节点
	
	//手工发起某个PLC的配置流程
	
	//
	/**
	 * PLC配置流程- 手工单步操作- 清除节点和flash存储
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/cleanConfig", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String cleanConfigByPlcId(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String plc_SN = (String)jsonParam.getString("plc_sn");
			String plc_port = (String)jsonParam.getString("plc_port");
			RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.get(plc_port+plc_SN);   //获取当前SN对应的通道信息 
			ChannelHandlerContext ctx = rTUChannelInfo.getCtx();
			PlcProtocolsUtils.init3_config_cleanNode(ctx,plc_SN);
			PlcProtocolsUtils.init4_config_delFlash(ctx, plc_SN);
			
			logger.info("===手工====清除节点和flash存储,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "清除节点和flash存储,请求发送完毕 ");
		}catch(Exception e){
			logger.error("===手工====清除节点和flash存储 ,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	
	/**
	 * PLC配置流程- 手工单步操作- 组网
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/autoGroupNet", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String init5_CfgNetwork(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String plc_SN = (String)jsonParam.getString("plc_sn");
			String plc_port = (String)jsonParam.getString("plc_port");
			RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.get(plc_port+plc_SN);   //获取当前SN对应的通道信息 
			ChannelHandlerContext ctx = rTUChannelInfo.getCtx();
			PlcProtocolsUtils.init5_CfgNetwork(ctx,plc_SN);
			
			logger.info("===手工====组网,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "组网,请求发送完毕 ");
		}catch(Exception e){
			logger.error("===手工====组网 ,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	 
	/**
	 * PLC配置流程- 手工单步操作- 停止组网 
	 * 停止组网前，可以先查看组网节点个数，够数了再停止
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/stopAutoGroupNet", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String init6_stopCfgNetwork(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String plc_SN = (String)jsonParam.getString("plc_sn");
			String plc_port = (String)jsonParam.getString("plc_port");
			RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.get(plc_port+plc_SN);   //获取当前SN对应的通道信息 
			ChannelHandlerContext ctx = rTUChannelInfo.getCtx();
			PlcProtocolsUtils.init6_stopCfgNetwork(ctx,plc_SN);
			
			logger.info("===手工====停止组网,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "停止组网,请求发送完毕 ");
		}catch(Exception e){
			logger.error("===手工====停止组网 ,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
 
	/**
	 * PLC配置流程- 手工单步操作- 存储节点
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/saveNodeToPlc", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String init7_saveNode(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String plc_SN = (String)jsonParam.getString("plc_sn");
			String plc_port = (String)jsonParam.getString("plc_port");
			RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.get(plc_port+plc_SN);   //获取当前SN对应的通道信息 
			ChannelHandlerContext ctx = rTUChannelInfo.getCtx();
			PlcProtocolsUtils.init7_saveNode(ctx,plc_SN);
			
			logger.info("===手工====存储节点,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "存储节点,请求发送完毕 ");
		}catch(Exception e){
			logger.error("===手工====存储节点 ,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	 
	/**
	 * PLC配置流程- 手工单步操作- 下发节点
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/plcSenddownNode", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String init8_sendDownNode(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String plc_SN = (String)jsonParam.getString("plc_sn");
			String plc_port = (String)jsonParam.getString("plc_port");
			RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.get(plc_port+plc_SN);   //获取当前SN对应的通道信息 
			ChannelHandlerContext ctx = rTUChannelInfo.getCtx();
			PlcProtocolsUtils.init8_sendDownNode(ctx,plc_SN);
			
			logger.info("===手工====下发节点,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "下发节点,请求发送完毕 ");
		}catch(Exception e){
			logger.error("===手工====下发节点 ,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	 
	/**
	 * PLC配置流程- 手工单步操作- 配置节点
	 * @param jsonParam
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/plc/config/plcSendowdnNodeCfg", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public String ini9_configNode(@RequestBody JSONObject jsonParam) {
		JSONObject resultJsonParam = new JSONObject();
		try{
			logger.info("param: " + jsonParam.toJSONString());
			if(jsonParam.getString("plc_sn") == null){
				logger.info("param is invalid!!! " );
				return resultJsonParam.toJSONString();
			}
			//前提是设备已经和主机建立了连接，然后已经登陆到主机
			String plc_SN = (String)jsonParam.getString("plc_sn");
			String plc_port = (String)jsonParam.getString("plc_port");
			RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.get(plc_port+plc_SN);   //获取当前SN对应的通道信息 
			ChannelHandlerContext ctx = rTUChannelInfo.getCtx();
			PlcProtocolsUtils.ini9_configNode(ctx,plc_SN);
			
			logger.info("===手工====配置节点,请求发送完毕 !=="+ " /sn=" + jsonParam.getString("plc_sn"));
 		    resultJsonParam.put("msg", "seccess");
		    resultJsonParam.put("data", "配置节点,请求发送完毕 ");
		}catch(Exception e){
			logger.error("===手工====配置节点 ,exception" + " /sn=" + jsonParam.getString("plc_sn") ,e); 
			resultJsonParam.put("msg", "fail");
		    resultJsonParam.put("data", "exception :"+e.getMessage());
		}
	    return resultJsonParam.toJSONString();
    }
	
	
	/**
	 * 根据节点SN生成节点ID
	 * 
	 */
	 @ResponseBody
	 @RequestMapping(value = "/plc/generatorId", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	    public String generatorId(@RequestBody JSONObject jsonParam) {
	    	JSONObject json = new JSONObject();
	    	String flagId=jsonParam.getString("flagId"); //4112
	    	String manufacturer=jsonParam.getString("manufacturer");//12289
	    	String id = DeviceIdGenerator.generatorId(jsonParam.getString("plc_node_sn"),Integer.parseInt(flagId),Integer.parseInt(manufacturer));
	    	json.put("plc_node_id", id);
	    	return json.toJSONString();
	    }
	 
	 
}
