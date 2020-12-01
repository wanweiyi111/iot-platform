package com.hzyw.iot.netty.processor.Impl;

import com.hzyw.iot.service.RedisService;
import com.hzyw.iot.vo.dc.RTUInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 数据处理器
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-19 15:45
 */
public interface IDataProcessor {
    IDataProcessor getNextProcessor(); 

    void setNextProcessor(IDataProcessor nextProcessor);
    
    void setType(int type,RedisService redisService);

    void translate(ChannelHandlerContext ctx, ByteBuf source, RTUInfo rtuInfo) throws Exception;
}
