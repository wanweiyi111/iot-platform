package com.hzyw.iot.feignServer.util;



/**
 * 设备传入数据模型
 * 2019-08-08
 */

public class TransmitDataVO {


    private String type;     //请求类型
    private String deviceId;
    private MessageVO data=new MessageVO();
    private  long seq;      //序号
    private  long timestamp;    //时间错

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public MessageVO getData() {
        return data;
    }

    public void setData(MessageVO data) {
        this.data = data;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TransmitDataVO{" +
                "type='" + type + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", data=" + data +
                ", seq=" + seq +
                ", timestamp=" + timestamp +
                '}';
    }
}
