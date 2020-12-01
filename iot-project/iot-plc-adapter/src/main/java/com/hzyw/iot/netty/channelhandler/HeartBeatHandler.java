package com.hzyw.iot.netty.channelhandler;

 import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.utils.PlcProtocolsUtils;
import com.hzyw.iot.utils.SendKafkaUtils;
import com.hzyw.iot.vo.dataaccess.DevOnOffline;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dc.GlobalInfo;

import cn.hutool.core.convert.Convert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 自定义心跳检测 headler
 *
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);
	
    private int loss_connect_counts = 0;
    
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    	super.userEventTriggered(ctx, evt);  
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE) {
            	loss_connect_counts++;
                if (loss_connect_counts > 2 && ctx != null) {
                	handlerBusiness(ctx);
                }
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {  
            	logger.info("---------心跳----写操作----------" );
            } else if (event.state().equals(IdleState.ALL_IDLE)) {  
            	logger.info("---------心跳----ALL_IDLE操作------" );
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	logger.error("---------心跳HeartBeatHandler,异常 ！" ,cause);
        ctx.close();
    }
    
    /**
     * 1,设置全局里面此设备的在线状态为离线  
     * 2,上报一个 DEV离线的消息类型
     * 
     * @param ctx
     */
    private void handlerBusiness(ChannelHandlerContext ctx){
		if (ctx == null || ctx.channel() == null || GlobalInfo.CHANNEL_INFO_MAP.get(ctx.channel().id()) == null) {
    		logger.error("---------心跳HeartBeatHandler,handlerBusiness处理warning!!! 无法获取到ctx .." );
    		return;
    	}
		if (GlobalInfo.CHANNEL_INFO_MAP.get(ctx.channel().id()) == null) {
    		logger.error("---------心跳HeartBeatHandler,handlerBusiness处理warning!!! CHANNEL_INFO_MAP中没有发现此连接对应的channel .." );
    		return;
    	}
		ChannelId channelId = ctx.channel().id();
    	InetSocketAddress insocket = (InetSocketAddress)ctx.channel().localAddress();
    	String currentPort = insocket.getPort()+"";
    	
    	String plc_sn = GlobalInfo.CHANNEL_INFO_MAP.get(channelId).getSn();
    	try{
    		PlcProtocolsUtils.gloable_dev_status.put(plc_sn + "_login", "0"); // 1 --上线   0--离线
    		if(IotInfoConstant.allDevInfo.get(currentPort).get(plc_sn+"_defAttribute") == null){
    			return;
    		}
    		String gwid = (String)IotInfoConstant.allDevInfo.get(currentPort).get(plc_sn+"_defAttribute").get(IotInfoConstant.dev_plc_plc_id);
    		DevOnOffline devOnline = new DevOnOffline();
			devOnline.setId(gwid); //=plc_Id
			devOnline.setStatus("offline");
			Map<String,String> tags = new HashMap<String,String>();
			tags.put(IotInfoConstant.dev_plc_dataaccess_key, IotInfoConstant.dev_plc_dataaccess_value); //指定接入类型是PLC接入类型
			devOnline.setTags(tags); 
			//消息结构
			MessageVO messageVo = getMessageVO(devOnline,"devOffline",System.currentTimeMillis(),UUID.randomUUID().toString(),gwid);
			SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
			//获取PLC下的灯具节点，并上报各个灯具的在线和离线状态
			List<Map<String, String>> nodelist = IotInfoConstant.plc_relation_plcsnToNodelist.get(currentPort).get(plc_sn);
			for (int i = 0; i < nodelist.size(); i++) {
				Map<String, String> item = nodelist.get(i);
				String dev_plc_node_id = item.get(IotInfoConstant.dev_plc_node_id); // 节点deviceid
				devOnline = new DevOnOffline();
				devOnline.setId(dev_plc_node_id); //=plc_node_Id
				devOnline.setStatus("offline");
				tags = new HashMap<String,String>();
				tags.put(IotInfoConstant.dev_plc_dataaccess_key, IotInfoConstant.dev_plc_dataaccess_value); 
				devOnline.setTags(tags); 
				//消息结构
				messageVo = getMessageVO(devOnline,"devOffline",System.currentTimeMillis(),UUID.randomUUID().toString(),gwid);
				SendKafkaUtils.sendKafka("iot_topic_dataAcess", JSON.toJSONString(messageVo));
			}
			logger.info(">>>>>心跳HeartBeatHandler,handlerBusiness:: 离线并上报离线消息成功!!!" );
    	}catch(Exception e){
    		logger.error("---------心跳HeartBeatHandler,handlerBusiness处理异常 !!!" ,e);
    	}
    	//超过10*2秒没有心跳则会被认为客户端不在线
    	ctx.channel().close();
        logger.info(">>>>>>heartbeat--检测到心跳超时,主机强迫关闭了一个现有的连接(plc_sn="+plc_sn+"/channelid="+channelId+") ! >>>>>>>>"  );
        logger.info(">>>>>>heartbeat--设置登陆状态为未登陆......(plc_sn="+plc_sn+"/channelid="+channelId+") ! >>>>>>>>"  );
        PlcProtocolsUtils.setLoginStatus(plc_sn + PlcProtocolsUtils._login, "0");  // 1 --上线   0--离线
    }
     
    
	public <T> MessageVO<T>  getMessageVO(T data,String type,Object timestamp,String msgId,String Plcid) {
		//消息结构
		MessageVO<T> messageVo = new MessageVO<T>();
		//消息结构
		messageVo.setType(type);
		messageVo.setTimestamp(timestamp);//取当前时间戳即可
		messageVo.setMsgId(msgId);
		messageVo.setData(data);
		messageVo.setGwId(Plcid);
		return messageVo;
	}

}
