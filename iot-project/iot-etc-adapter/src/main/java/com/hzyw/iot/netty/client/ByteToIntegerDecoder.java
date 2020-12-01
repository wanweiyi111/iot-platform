package com.hzyw.iot.netty.client;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
 
import java.util.List;
 
/**
 * 把字节转换为int
 * 继承抽象类ByteToMessageDecoder实现解码器 解码器  在这里实现的编解码器很简单，实现int->bytes的编码和bytes->int的解码
 */
public class ByteToIntegerDecoder extends ByteToMessageDecoder {
 
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
                       List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {  // Check if there are at least 4 bytes readable
            int n = in.readInt();
            System.err.println("ByteToIntegerDecoder decode msg is " + n);
            out.add(n);      //Read integer from inbound ByteBuf, add to the List of decodec messages
        }
    }
}
