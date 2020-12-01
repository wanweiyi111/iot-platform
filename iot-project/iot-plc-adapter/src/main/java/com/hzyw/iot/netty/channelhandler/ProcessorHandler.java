package com.hzyw.iot.netty.channelhandler;

import com.hzyw.iot.netty.processor.*;
import com.hzyw.iot.netty.processor.Impl.IDataProcessor;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.vo.dc.GlobalInfo;
import com.hzyw.iot.vo.dc.ItemInfo;
import com.hzyw.iot.vo.dc.RTUChannelInfo;
import com.hzyw.iot.vo.dc.RTUInfo;
import com.hzyw.iot.vo.dc.enums.EMqExchange;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 
/**
 * 处理分发
 * @author Administrator
 */
@Component
public class ProcessorHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorHandler.class);
    private final IDataProcessor sysDataProcessor = new WiffDataProcessor(); //wiff  Processor
    private final IDataProcessor plcDataProcessor = new PlcDataProcessor();  //PLC   Processor
    private RedisService redisService;
    boolean flag = false;
    int type;
    
    public ProcessorHandler() {
    }
    
    public ProcessorHandler(int type,RedisService redis) {
    	init(type,redis);
    	plcDataProcessor.setNextProcessor(sysDataProcessor);
    	//sysDataProcessor.setNextProcessor(xxProcessor);
    	//传感器 Processor
    }
    
    public void init(int type,RedisService redisService){
    	this.type = type;
    	this.redisService = redisService;
    	sysDataProcessor.setType(this.type,redisService);
    	plcDataProcessor.setType(this.type,redisService);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            RTUInfo rtuInfo = new RTUInfo(null);
            //rtuInfo {id, sn ,data<T>}   T可以适配任何设备模型，如有新的业务场景 抽象出的设备模型不一样 ，那这里就很有意义了哦
            // -- PLC接入 这里的T可以转化成  他对应的统一的数据模型
            // -- 如：{uuid系统唯一标识, 设备sn ,data<T>}   {msgid, 设备sn ,data<MessageVO>}
            plcDataProcessor.translate(ctx, buf, rtuInfo);
            
            flag = true;
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }

    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
		ctx.flush();
		if (flag) {
			System.out.println("已经读取完毕...");
			// ctx.close();
		} else {
			System.out.println("没读完 继续...");
			ctx.read();
		}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        System.out.println("异常。。关闭连接 "+cause.toString());
        ctx.close();
    } 

    /**
     * rtuinfo{
     *   id
     *   sn
     *   data<item>
     * 统一补全设备的相关属性信息
     * 
     * 
     * @param ctx
     * @param rtuInfo
     */
    private void buildRtuInfo(ChannelHandlerContext ctx, RTUInfo rtuInfo) { //补全rtuInfo信息
        RTUChannelInfo rtuChannelInfo = GlobalInfo.CHANNEL_INFO_MAP.get(ctx.channel().id()); //全局 netty channel 管理器 map{channelId, channelinfo}
        //待
    }
}
