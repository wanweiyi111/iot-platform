package com.hzyw.iot.vo.dc;

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
     * map{channelId, channelinfo}
     */
    public static final Map<ChannelId, RTUChannelInfo> CHANNEL_INFO_MAP = new ConcurrentHashMap<>();

    /**
     * 全局 netty channel 管理器
     * map{port+sn, channelinfo}
     * sn可能会存在不唯一情况 ，这里的KEY约定为= port+sn
     * 接入一种新产品的时候尽量指定一个新的端口，一般来说相同的产品的SN码应该会不一样
     */
    public static final Map<String, RTUChannelInfo> SN_CHANNEL_INFO_MAP = new ConcurrentHashMap<>();
}
 