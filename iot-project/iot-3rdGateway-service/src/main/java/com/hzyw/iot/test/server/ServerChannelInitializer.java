package com.hzyw.iot.test.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerChannelInitializer extends ChannelInitializer<NioDatagramChannel>{
	@Autowired
    ServerChannelInboundHandler serverChannelHandler;
 
    @Override
    protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
        ChannelPipeline pipeline = nioDatagramChannel.pipeline();
//        pipeline.addLast(new StringDecoder());
        //自定义的InboundHandler输入处理者
        pipeline.addLast("serverChannelHandler", serverChannelHandler);
//        pipeline.addLast(new StringEncoder());
    }
}
