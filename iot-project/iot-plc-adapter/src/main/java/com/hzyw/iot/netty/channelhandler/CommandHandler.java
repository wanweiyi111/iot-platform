package com.hzyw.iot.netty.channelhandler;
import com.hzyw.iot.config.ApplicationConfig;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.vo.dc.GlobalInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 指令下发到設備
 */
@Sharable
public class CommandHandler extends MessageToByteEncoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
    	logger.info(">>>>>>>>CommandHandler::encode -------out.writeBytes(msg)--");
        out.writeBytes(msg);
    }
    
    /**
     * @param key  =当前服务开放给此设备的端口+集中器地址 
     * @param command  下发指令
     * @param messageId  提供消息ID，根据 dataSendDownConsumer_request_+messageId 来跟踪下发结果
     */
    public static void writeCommand(String key, String command,String messageId){
        Channel channel = GlobalInfo.SN_CHANNEL_INFO_MAP.get(key).getChannel();//获取要下发的sn设备对应的channel
        try {
        	ByteBuf byteBuf = Unpooled.buffer();
        	byteBuf.writeBytes(com.hzyw.iot.util.constant.ConverUtil.hexStrToByteArr(command));
        	logger.info("====dataSendDownConsumer_request_"+messageId+"====下发成功!!!!==== ,messageId="+messageId+"/command="+command);
			channel.writeAndFlush(byteBuf).addListener((ChannelFutureListener) future -> { //监听下发的请求执行是否成功！
                if (future.isSuccess()) {
                	logger.info("====dataSendDownConsumer_request_"+messageId+"====下发成功!!!!==== ,messageId="+messageId+"/command="+command);
                	 
                } else {
                	logger.info("====dataSendDownConsumer_request_"+messageId+" ===下发失败:"+future.cause() + ",command="+command);
                }
            });
		}catch (Exception e) {
			logger.error("====dataSendDownConsumer_request_"+messageId+"==下发异常channel.writeAndFlush, command={} ", command,e);
		}
    }
     
    
    /**
     * 
     * request下发
     * @param messageVOjson
     */
    public static void writeCommandByRequestMessageVO(String messageVOjson,ApplicationConfig  applicationConfig) {
    	//根据下发的消息进入协议处理
    	PlcProtocolsBusiness.protocals_process(messageVOjson, applicationConfig);
    }
}
