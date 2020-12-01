package com.hzyw.iot.platform.models.equip;

import java.io.Serializable;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/8.
 */
public enum EquipmentFlag implements Serializable {
    /**
     * 设备大类
     */
    DEVICE("IoT设备", 0x0010),     //十进制码:16
    POLE("灯杆", 0xff00),         //65,280
    HZYW_BOX("盒子", 0x1000),     //4096
    PLC_BOX("PLC集中控制器", 0x2000), //4101
    LAMP("灯控", 0x1010),          //4112
    SCREEN("大屏", 0x1030),        //4144
    CAMERA("摄像头", 0x1020),       //4128
    WIFI("公共WIFI", 0x1040),      //4160
    ENV_SENSOR("环境传感器", 0x1050),     //4176
    RADAR("雷达", 0x1060),     //4192
    CHARGING_PILE("充电桩", 8),
    LOCK("智能锁", 9),
    SOS_ALARM("一键呼叫", 0x1080),
    WATER_GAUGE("智能水尺", 11),
    FLOOD_SENSOR("水浸传感器", 12),
    SEWER_COVER("智能井盖", 13);

    private String description;
    private int index;

    EquipmentFlag(String description, int index) {
        this.description = description;
        this.index = index;
    }

    public static String getDescription(int index) {
        for (EquipmentFlag flag : EquipmentFlag.values()) {
            if (flag.getIndex() == index) {
                return flag.description;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public static EquipmentFlag getEquipmentFlagByIndex(int index){
        for(EquipmentFlag flag : EquipmentFlag.values()){
            if (flag.getIndex() == index) {
                return flag;
            }
        }
        return null;
    }



    @Override
    public String toString() {
        return this.index+"_"+this.description;
    }
}
