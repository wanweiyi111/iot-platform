package com.hzyw.iot.test;



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
public class ClientChannelInitializer extends ChannelInitializer<NioDatagramChannel>{
	@Autowired
    ClientChannelHandler clientChannelHandler;
 
 
    @Override
    protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
        ChannelPipeline pipeline = nioDatagramChannel.pipeline();
        //数据的解码
//        pipeline.addLast(new StringDecoder());
        //自定义Handler
        pipeline.addLast("clientChannelHandler", clientChannelHandler);
        //数据的编码
//        pipeline.addLast(new StringEncoder());
 
    }
}
