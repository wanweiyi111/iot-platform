package com.hzyw.iot.vo.dc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hzyw.iot.vo.dc.enums.EMqExchange;

import java.util.Arrays;

/**
 * netty 对象
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-22 10:53
 */
public class RTUInfo<T> {
    /**
     * 物联网id
     */
    private String id;

    @JsonIgnore
    private String sn;
    
    private String dataAccessType; //设备类型 如 PLC ,WIFF ,传感器
    
    /**
     * 指标信息
     */
    private T data;

    /**
     * 是否发布
     */
    @JsonIgnore
    private boolean publish;
    /**
     * 消息队列标示
     */
    @JsonIgnore
    private EMqExchange[] mqExchange;


    public String getDataAccessType() {
		return dataAccessType;
	}

	public void setDataAccessType(String dataAccessType) {
		this.dataAccessType = dataAccessType;
	}

	public RTUInfo(String id) {
        this.id = id;
        this.publish = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public EMqExchange[] getMqExchange() {
        return mqExchange;
    }

    public void setMqExchange(EMqExchange[] mqExchange) {
        this.mqExchange = mqExchange;
    }

    @Override
    public String toString() {
        return "RTUInfo{" +
                "id='" + id + '\'' +
                ", sn='" + sn + '\'' +
                ", data=" + data +
                ", publish=" + publish +
                ", mqExchange=" + Arrays.toString(mqExchange) +
                '}';
    }
}
