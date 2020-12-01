package com.hzyw.iot.feignServer.util;


import java.util.ArrayList;
import java.util.List;

/**
 * 消息体
 */

public class MessageVO {
    private String deviceId;        //设备编码
    private String status;          //状态
    private List<PropertyVO> attributer=new  ArrayList<PropertyVO>();  //LED灯
    private List<MethodVO> methods=new  ArrayList<MethodVO>();
    private List<PropertyVO> definedAttributer=new  ArrayList<PropertyVO>() ;//自定义方法 ,美的L-45eT型号的LED灯
    private List<MethodVO> definedMethod=new  ArrayList<MethodVO>();      //自定义方法
    private String signal;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<MethodVO> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodVO> methods) {
        this.methods = methods;
    }

    public List<PropertyVO> getAttributer() {
        return attributer;
    }

    public void setAttributer(List<PropertyVO> attributer) {
        this.attributer = attributer;
    }

    public List<PropertyVO> getDefinedAttributer() {
        return definedAttributer;
    }

    public void setDefinedAttributer(List<PropertyVO> definedAttributer) {
        this.definedAttributer = definedAttributer;
    }

    public List<MethodVO> getDefinedMethod() {
        return definedMethod;
    }

    public void setDefinedMethod(List<MethodVO> definedMethod) {
        this.definedMethod = definedMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    @Override
    public String toString() {
        return "MessageVO{" +
                "deviceId='" + deviceId + '\'' +
                ", status='" + status + '\'' +
                ", attributer=" + attributer +
                ", methods=" + methods +
                ", definedAttributer=" + definedAttributer +
                ", definedMethod=" + definedMethod +
                ", signal='" + signal + '\'' +
                '}';
    }
}
