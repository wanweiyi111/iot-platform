package com.hzyw.iot.feignServer.util;

import java.util.Map;

public class MethodVO {
    private String type;
    private String method;      //方法名
    private Map<Object,Object> in;      //输入数据
    private Map<Object,Object> out;     //输出数据
    private Map<Object,Object> metaData;        //元数据

    public Map<Object, Object> getIn() {
        return in;
    }

    public void setIn(Map<Object, Object> in) {
        this.in = in;
    }

    public Map<Object, Object> getOut() {
        return out;
    }

    public void setOut(Map<Object, Object> out) {
        this.out = out;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<Object, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<Object, Object> metaData) {
        this.metaData = metaData;
    }
}
