package com.hzyw.iot.platform.models.equip;

import java.io.Serializable;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/9.
 */

public abstract class EquipmentType implements Serializable {

    /**
     * 设备ID 主键编号
     */
    private String typeId;
    /**
     * 厂商的设备型号编码
     */
    private String typeCode;
    /**
     * 常用名或通俗用语
     */
    private String typeName;

    /**
     * 设备大类
     */
    private int domainFlag;
    /**
     * 生产商
     */
    private int manufacturerCode;

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getDomainFlag() {
        return domainFlag;
    }

    public void setDomainFlag(int domainFlag) {
        this.domainFlag = domainFlag;
    }

    public java.lang.Integer getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(int manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }
}
