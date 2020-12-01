package com.hzyw.iot.util.constant;

import com.hzyw.iot.utils.IotInfoConstant;
import com.hzyw.iot.vo.dataaccess.DataType;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.ResultMessageVO;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * PLC 响应 消息头
 */
public class T_ResponseResult {
    private static final Logger log = LoggerFactory.getLogger(T_ResponseResult.class);
    /**
     * PLC 上报数据 消息头
     * @param ctx Netty上下文
     * @param plc_sn 集中器地址 UID
     * @param type 消息类型【设备状态数据(metricInfoResponse), 设备信号数据(devSignalResponse)】
     * @param data 上报的消息体
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> MessageVO<T> getResponseVO(ChannelHandlerContext ctx, String plc_sn, String type, T data)throws Exception{
        String msgId="31a8c447-5079-4e91-a364-"+ UUID.randomUUID();  // -1769ac06fd5c
        MessageVO<T> mesVO = new MessageVO<T>();
        DataType enumType = DataType.getByValue(type);
        System.out.println("============getResponseVO 方法， 消息type: "+type);
        String typee="";
        switch (enumType) {
            case MetricInfoResponse://设备状态数据上报
                typee=DataType.MetricInfoResponse.getMessageType();
                break;
            case DevSignalResponse://设备信号上报
                typee=DataType.DevSignalResponse.getMessageType();
                break;
        }
        if("".equals(typee)) throw new Exception("PLC响应 上报 消息类型 :"+type+", 没在配置文件中定义!");
        // 获取节点ID(设备ID)
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().localAddress();
        String devcdId = (String)IotInfoConstant.allDevInfo.get((insocket.getPort()) + "")
                .get(plc_sn + "_defAttribute").get(IotInfoConstant.dev_plc_plc_id);

        mesVO.setType(typee);
        mesVO.setTimestamp(System.nanoTime());
        mesVO.setData(data);
        mesVO.setMsgId(msgId);
        mesVO.setGwId(devcdId);
        return mesVO;
    }

    /**
     * PLC ACK 响应消息体 生成
     * @param plc_sn 集中器SN
     * @param resultCode 0:成功，10005:失败，20324:忙
     * @param type
     * @param data
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> ResultMessageVO<T> getACKResponseVO(String plc_sn,String nodeID,Integer resultCode,String messageID, String type,T data)throws Exception{
        //消息结构
        ResultMessageVO<T> messageVo = new ResultMessageVO<T>();
        log.debug("============getResponseVO 方法， 消息type: "+type);
        //消息结构
        messageVo.setType(type);
        messageVo.setTimestamp(System.nanoTime());//消息上报时间
        messageVo.setMsgId(messageID);
        messageVo.setData(data);
        messageVo.setGwId(plc_sn);
        messageVo.setMessageCode(resultCode);
        return messageVo;
    }
}
