package com.hzyw.iot.netty.processor;

import com.hzyw.iot.netty.channelhandler.ChannelManagerHandler;
import com.hzyw.iot.netty.processor.Impl.IDataProcessor;
import com.hzyw.iot.netty.processor.Impl.ProcessorAbstract;
import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.vo.dc.ItemInfo;
import com.hzyw.iot.vo.dc.RTUInfo;
import com.hzyw.iot.vo.dc.enums.EMqExchange;
import com.hzyw.iot.vo.dc.enums.ERTUChannelFlag;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * wiff接入类型的处理逻辑
 *
 */
public class WiffDataProcessor extends ProcessorAbstract implements IDataProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiffDataProcessor.class);
    private int type;
    private RedisService redisService;
    
    public WiffDataProcessor() {
        super(ERTUChannelFlag.WIFF); //表示接入类型是wiff
    }
    
	public void setType(int type,RedisService redisService){ 
		this.type = type;
		this.redisService = redisService;
	}

    @Override
    public void translate(ChannelHandlerContext ctx, ByteBuf source, RTUInfo rtuInfo) throws Exception {
    	System.out.println("------WiffDataProcessor---------type=----"+this.type);
        if (checkAndToProcess(this.type)) { //读取
        	System.out.println("------WiffDataProcessor-------处理wiff接入类型的消息------");
            byte[] dataBytes = new byte[source.readableBytes()];
            source.readBytes(dataBytes);
            String sourceStr = new String(dataBytes);
            System.out.println("------WiffDataProcessor--------已接收到-----" +sourceStr);
            /*if (sourceStr.contains(SIGNAL)) {
                // 信号强度
                String signal = sourceStr.split(":")[1].trim();
                rtuInfo.setData(new ItemInfo(SIGNAL, signal));
                EMqExchange[] eMqExchanges = {EMqExchange.RTU_SIGNAL, EMqExchange.RTU_HEART};
                rtuInfo.setMqExchange(eMqExchanges);
            } else if (sourceStr.contains(VERSION)) {
                // 版本信息
                String version = sourceStr.split(":")[1].trim();
                rtuInfo.setData(new ItemInfo(VERSION, version));
            } else if (sourceStr.contains(PING)) {
                // ping
                rtuInfo.setData(new ItemInfo(PING, "ok"));
                EMqExchange[] eMqExchanges = {EMqExchange.RTU_HEART};
                rtuInfo.setMqExchange(eMqExchanges);
            } else if (sourceStr.contains(SN)) {
                // sn
                String sn = sourceStr.split(":")[1].trim();
                // 补全 channelInfo信息
                ChannelManagerHandler.setRTUChannelInfo(ctx, sn);
            } else {
                LOGGER.warn("unchecked system source: \"{}\"", sourceStr);
            }*/
        } else {
            if (super.getNextProcessor() != null)
                super.getNextProcessor().translate(ctx, source, rtuInfo);
        }
    }
}
