package com.hzyw.iot.platform.models.equip;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/4.
 */
public class DeviceType extends EquipmentType implements Serializable {

    public static final String TYPE_HYPHEN = "::";
    //状态属性集合
    private Map<String, DeviceAttribute> attributes = new HashMap<>();
    //操作方法列表
    private Map<String, DeviceMethod> methods = new HashMap<>();
    //名称映射表
    private Map<String, String> namedMapping = new HashMap<>();

    public DeviceType(String typeCode, EquipmentFlag flag, int manufacturer) {
        setTypeCode(typeCode);
        setDomainFlag(flag.getIndex());
        setManufacturerCode(manufacturer);
        setTypeId(typeCode + TYPE_HYPHEN + flag.getIndex() + TYPE_HYPHEN + manufacturer);
    }

    public DeviceType(String typeCode, EquipmentFlag flag, int manufacturer, String typeName) {
        setTypeCode(typeCode);
        setDomainFlag(flag.getIndex());
        setManufacturerCode(manufacturer);
        setTypeName(typeName);
        setTypeId(typeCode + TYPE_HYPHEN + flag.getIndex() + TYPE_HYPHEN + manufacturer);
    }

    @Override
    public String getTypeId() {
        return this.getTypeCode() + TYPE_HYPHEN + this.getDomainFlag() + TYPE_HYPHEN + this.getManufacturerCode();
    }

//    @Override
//    @Deprecated
//    public void setTypeId(String typeId) {
//        //not support yet
////        if (!StringUtils.isEmpty(typeId) && typeId.split(TYPE_HYPHEN).length == 3) {
////            super.setTypeId();
////        }
//    }

    public Map<String, DeviceAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, DeviceAttribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String, DeviceMethod> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, DeviceMethod> methods) {
        this.methods = methods;
    }

    public DeviceMethod getMethod(String name) throws NoSuchMethodException {
        DeviceMethod method = methods.get(name);
        if (method == null) {
            throw new NoSuchMethodException(name);
        }
        return method;
    }

    public void addMethod(DeviceMethod methodDef) {
        if (Objects.nonNull(methodDef)) {
            methods.put(methodDef.getMethodName(), methodDef);
        }
    }

    public DeviceAttribute getAttribute(String key) {
        return attributes.get(key);
    }

    public void addAttribute(DeviceAttribute AttrDef) {
        if (Objects.nonNull(AttrDef)) {
            attributes.put(AttrDef.getAttributeKey(), AttrDef);
        }
    }

    public Map<String, String> getNamedMapping() {
        return namedMapping;
    }

    public void setNamedMapping(Map<String, String> namedMapping) {
        this.namedMapping = namedMapping;
    }
}
