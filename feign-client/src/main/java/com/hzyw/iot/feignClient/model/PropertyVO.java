package com.hzyw.iot.feignClient.model;

import java.util.Map;

public class PropertyVO {
    private String propertyKey;    //属性
    private String propertyValue;       //属性值
    private String unit;        //单位
    private Map<Object,Object> metaData;        //元数据
    private String type;    // 灯

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Map<Object, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<Object, Object> metaData) {
        this.metaData = metaData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "PropertyVO{" +
                "\"propertyKey\":\"" + propertyKey +"\""+
                ", \"propertyValue\":\"" + propertyValue +"\""+
                ", \"unit\":\"" + unit +"\""+
                ", \"metaData\":\"" + metaData +
                ", \"type\":\"" + type +"\""+
                '}';

    }
}
