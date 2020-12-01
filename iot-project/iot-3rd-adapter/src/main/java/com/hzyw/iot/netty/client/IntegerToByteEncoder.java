package com.hzyw.iot.netty.client;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
 
//编码器
public class IntegerToByteEncoder extends MessageToByteEncoder<Integer> {
    @Override
    public void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out)
            throws Exception {
        System.err.println("IntegerToByteEncoder encode msg is " + msg);
        out.writeInt(msg);
    }
}