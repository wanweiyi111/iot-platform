package com.hzyw.iot.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-11-08 14:19
 */
public class GlobalInfo {

    public static String Global_Iot_Redis_Key;
 
    /**
     * 全局 netty channel 管理器
     * 
     */
    public static final Map<String, Channel> CHANNEL_INFO_MAP = new ConcurrentHashMap<>();

}
 