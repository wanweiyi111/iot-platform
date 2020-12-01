package com.hzyw.iot.netty.channelhandler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.vo.dc.GlobalInfo;
import com.hzyw.iot.vo.dc.RTUChannelInfo;

/**
 * 链路管理 handler
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-25 15:18
 */
@Sharable
public class ChannelManagerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelManagerHandler.class);
    
    public ChannelManagerHandler(){}

    /* 
     * 设备建立连接进入
     * (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRegistered(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("new channel coming! --注册--> {}", ctx.channel());
        ChannelId channelId = ctx.channel().id();
        RTUChannelInfo channelInfo = GlobalInfo.CHANNEL_INFO_MAP.getOrDefault(channelId, RTUChannelInfo.build("unknownSN", channelId));
        GlobalInfo.CHANNEL_INFO_MAP.put(channelId, channelInfo);//设备建立连接的时候，实际上是没有交互过消息的，所以不可能知道当前是什么设备ID建立的连接
        ctx.fireChannelRegistered();
        System.out.println(GlobalInfo.CHANNEL_INFO_MAP.get(channelId));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("channel out! ---离线拉-> {}", ctx.channel());
        ChannelId channelId = ctx.channel().id();
        InetSocketAddress insocket = (InetSocketAddress)ctx.channel().localAddress();
        RTUChannelInfo channelInfo = GlobalInfo.CHANNEL_INFO_MAP.remove(channelId);
        GlobalInfo.SN_CHANNEL_INFO_MAP.remove(insocket.getPort()+ channelInfo.getSn());
       // PlcProtocolsUtils.setLoginStatus(channelInfo.getSn(), "0"); // 设置设备上线状态 1-上线 0-下线
        LOGGER.info("remove channel: {}", channelInfo);
        ctx.fireChannelUnregistered();
    }

    /**
     * 
     * 设备发送第一次消息进入 （一般是登陆）
     * 登陆成功 --》把设备信息挂在 全局的 CHANNEL_INFO_MAP 和 SN_CHANNEL_INFO_MAP里面
     *
     * @param ctx
     * @param sn  plc_sn
     * @return
     *//*
    public static boolean setRTUChannelInfo(ChannelHandlerContext ctx, String plc_sn) {
    	boolean seccess = false;
    	ChannelId channelId = ctx.channel().id();
    	InetSocketAddress insocket = (InetSocketAddress)ctx.channel().localAddress();
    	try{
            Map<String, Map<String, Object>> Alldevinfo = IotInfoConstant.allDevInfo.get(insocket.getPort()+"");
            if(Alldevinfo.get(plc_sn+"_attribute") == null){
            	LOGGER.error("--------- channelId ="+channelId+"---/Port= "+insocket.getPort() + " /plc_sn="+plc_sn + "---初始化RTUChannelInfo ,没有找到此设备的设备信息---");
            	return seccess;
            }
            
            //获取设备信息
            Map<String, Map<String,Object>> devInfo_new = getGloableDevInfo(Alldevinfo,plc_sn);
            GlobalInfo.CHANNEL_INFO_MAP.get(channelId).setSn(plc_sn).setChannel(ctx.channel());
            GlobalInfo.CHANNEL_INFO_MAP.get(channelId).setDevInfo(devInfo_new);//设备信息挂到CHANNEL_INFO_MAP   后面用不到可以考虑取消这个全局的变量

            RTUChannelInfo rTUChannelInfo = GlobalInfo.SN_CHANNEL_INFO_MAP.getOrDefault(insocket.getPort()+plc_sn, RTUChannelInfo.build(plc_sn, channelId));
            rTUChannelInfo.setChannel(ctx.channel());
            rTUChannelInfo.setDevInfo(devInfo_new);//设备信息挂到SN_CHANNEL_INFO_MAP
            rTUChannelInfo.setCtx(ctx);
            GlobalInfo.SN_CHANNEL_INFO_MAP.put(insocket.getPort() + plc_sn, rTUChannelInfo); 
            System.out.println(insocket.getPort()+plc_sn);
            LOGGER.info(">>> sn: {} in the house.", plc_sn);
            seccess = true;
    	}catch(Exception e){
    		seccess = false;
        	LOGGER.error("--------- channelId ="+channelId+"---/Port= "+insocket.getPort() + " /sn="+plc_sn + "---初始化RTUChannelInfo ,异常---",e);
    	}

    	return seccess;
    }
    
    private static Map<String, Map<String,Object>> getGloableDevInfo(Map<String, Map<String, Object>> devinfos,String plc_sn){
    	Map<String, Map<String,Object>> devInfo_new = new HashMap<String, Map<String,Object>>();
        devInfo_new.put(plc_sn+"_attribute", devinfos.get(plc_sn+"_attribute"));
        devInfo_new.put(plc_sn+"_defAttribute", devinfos.get(plc_sn+"_defAttribute"));
        devInfo_new.put(plc_sn+"_method", devinfos.get(plc_sn+"_method"));
        devInfo_new.put(plc_sn+"_cmd", devinfos.get(plc_sn+"_cmd"));
        devInfo_new.put(plc_sn+"_signl", devinfos.get(plc_sn+"_signl"));
        return devInfo_new;
    }

    *//**
     * 刷新 链路信息
     * 如果全局的IotInfoConstant.allDevInfo设备信息有更新，这里可手动的调用此方法来刷新
     * 暂时用不到，未来做设备信息管理的改造比如从数据库查询，然后定时调度任务来刷新设备信息的场景或有变化的设备仍到KAFKA，然后通过消费KAFKA来刷新这里
     * 
     *//*
    public static void refreshRTUChannelInfo() {
        LOGGER.info("refresh GlobalInfo...");
        GlobalInfo.CHANNEL_INFO_MAP.forEach((channelId, rTUChannelInfo) -> {
            String plc_sn = rTUChannelInfo.getSn();  
            //channelInfo.setIotInfo(GlobalInfo.iotMapper.get(sn));
            InetSocketAddress insocket = (InetSocketAddress)rTUChannelInfo.getChannel().localAddress();
            Map<String, Map<String, Object>> Alldevinfo = IotInfoConstant.allDevInfo.get(insocket.getPort()+"");
            rTUChannelInfo.setDevInfo(getGloableDevInfo(Alldevinfo,plc_sn));//设置设备信息
        });
        
        GlobalInfo.SN_CHANNEL_INFO_MAP.forEach((sn, rTUChannelInfo) ->{ 
        	// 这里sn=port+plc_sn
        	InetSocketAddress insocket = (InetSocketAddress)rTUChannelInfo.getChannel().localAddress();
        	Map<String, Map<String, Object>> Alldevinfo = IotInfoConstant.allDevInfo.get(insocket.getPort()+"");
        	rTUChannelInfo.setDevInfo(getGloableDevInfo(Alldevinfo,rTUChannelInfo.getSn()));//设置设备信息
        });
    }*/
}
