package com.hzyw.iot.netty;

import com.hzyw.iot.service.RedisService;

import com.hzyw.iot.netty.channelhandler.ProcessorHandler;
 
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * RTU 接口监听器
 */
public class RTUPortListener extends PortListenerAbstract {
	//int port;
    public RTUPortListener(int port, EventLoopGroup bossGroup, EventLoopGroup workerGroup,RedisService redisService) {
    	
        super(port, bossGroup, workerGroup,redisService);
        //this.port = port;
        super.bind();
        
    }
 
    @Override
    ChannelHandler settingChannelInitializerHandler() {
        return new ChildChannelHandler(this.getPort(),this.getRedisService());  
    }

    private class ChildChannelHandler extends BaseHandler {
    	RedisService _redisService;
    	int _port;
    	ChildChannelHandler(){}
    	ChildChannelHandler(int port,RedisService redisService){
    		this._port = port;
    		this._redisService = redisService;
    	}
    	
        @Override
        ChannelPipeline extHandler(ChannelPipeline pipeline) {
        	//客户端每次连接都会进入注册handler
        	//获取接入类型,根据具体的协议规范来定义Decoder
        	if(_port == 12345){
        		System.out.println("---PLC("+_port+")集中器---建立连接--------------" + pipeline.channel().id());
        		pipeline.addLast(new LengthFieldBasedFrameDecoder(100000, 9, 1, 2, 0));//根据数据帧的格式做拆包粘包解码
        		// 数据封装
                pipeline.addLast(new ProcessorHandler(_port,_redisService));
        	}
        	if(_port == 5678){
        		System.out.println("---其他类型("+_port+")集中器---建立连接--------------" + pipeline.channel().id());
        		pipeline.addLast(new LengthFieldBasedFrameDecoder(100, 9, 1, 2, 0));//根据数据帧的格式做拆包粘包解码
        		pipeline.addLast(new ProcessorHandler(_port,_redisService));
        	}
            return pipeline;
        }
    }

}
