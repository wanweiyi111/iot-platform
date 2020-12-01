package com.hzyw.iot.platform.devicemanager.caches;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAttributeDO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceMethodDO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceTypeDO;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAttrService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceMethodService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceTypeService;
import com.hzyw.iot.platform.models.equip.DeviceAttribute;
import com.hzyw.iot.platform.models.equip.DeviceMethod;
import com.hzyw.iot.platform.models.equip.DeviceType;
import com.hzyw.iot.platform.models.equip.EquipmentFlag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/2.
 */
@Slf4j
@Service
public class DeviceTypeCacheService {

    private static final String CACHE_NAME = "DeviceType";
    final DeviceMethodService methodService;
    final DeviceTypeService typeService;
    final DeviceAttrService attrService;

    public DeviceTypeCacheService(DeviceMethodService methodService, DeviceTypeService typeService, DeviceAttrService attrService) {
        this.methodService = methodService;
        this.typeService = typeService;
        this.attrService = attrService;
    }

//    @Override
    @Cacheable(cacheManager = "DeviceTypeCache",cacheNames = CACHE_NAME, key = "#typeId", unless = "#result==null&&#result.typeId!=null")
    public DeviceType get(String typeId) {
        if (StringUtils.isEmpty(typeId)) {
            return null;
        }
        log.debug("Cache--> not found {} in {},will search from DB", typeId, CACHE_NAME);
        String[] pks = typeId.split(DeviceType.TYPE_HYPHEN);
        if (pks.length != 3) {
            log.error("typeId input illegal! expect:typeCode-deviceDomain-manufacturer, actual:{}", typeId);
            return null;
        }
        DeviceTypeDO typeDO = typeService.selectDeviceType(pks[0], java.lang.Integer.parseInt(pks[1]), java.lang.Integer.parseInt(pks[2]));
        List<DeviceMethodDO> methodList = methodService.getMethodListByType(typeId);
        List<DeviceAttributeDO> attrList = attrService.searchDeviceAttrByType(typeId);
        if (typeDO == null) {
            return null;
        }
        return buildDeviceType(typeDO, methodList, attrList);
    }

//    @Override
    @CacheEvict(cacheManager = "DeviceTypeCache",cacheNames = CACHE_NAME, key = "#typeId")
    public Boolean evict(String typeId) {
        log.debug("Cache--> evict {} from {}", typeId, CACHE_NAME);
        return true;
    }

    private DeviceType buildDeviceType(DeviceTypeDO typeDO, List<DeviceMethodDO> methodList, List<DeviceAttributeDO> attrList) {
        EquipmentFlag flag = EquipmentFlag.getEquipmentFlagByIndex(typeDO.getDeviceDomain());
        DeviceType deviceType = new DeviceType(typeDO.getTypeCode(), flag, typeDO.getManufacturerCode());
        deviceType.setTypeName(typeDO.getTypeName());
//        TODO: deviceType.setNamedMapping(); and set Other things
        for (DeviceMethodDO methodDO : methodList) {
            if (methodDO != null) {
                DeviceMethod method = buildDeviceMethod(methodDO);
                deviceType.addMethod(method);
            }
        }
        for (DeviceAttributeDO attributeDO : attrList) {
            if (attributeDO != null) {
                DeviceAttribute attribute = buildDeviceAttribute(attributeDO);
                deviceType.addAttribute(attribute);
            }
        }
        return deviceType;
    }

    private DeviceAttribute buildDeviceAttribute(DeviceAttributeDO attributeDO) {
        if (StringUtils.isEmpty(attributeDO.getAttrKey())) {
            return null;
        }
        DeviceAttribute attribute = new DeviceAttribute(attributeDO.getAttrKey(), attributeDO.getValueType());
        attribute.setAttributeName(attributeDO.getAttrName());
        attribute.setUnit(attributeDO.getUnit());
        if (!StringUtils.isEmpty(attributeDO.getMetaData())) {
            Map m = JSONObject.parseObject(attributeDO.getMetaData()).getInnerMap();
            attribute.setAttributeMeta(m);
        }
        return attribute;
    }

    private DeviceMethod buildDeviceMethod(DeviceMethodDO methodDO) {
        DeviceMethod deviceMethod = new DeviceMethod();
        deviceMethod.setMethodName(methodDO.getMethodName());
        if (!StringUtils.isEmpty(methodDO.getMethodIn())) {
            deviceMethod.setInput(methodDO.getMethodIn().split(","));
        }
        if (!StringUtils.isEmpty(methodDO.getMethodOut())) {
            deviceMethod.setOutput(methodDO.getMethodOut().split(","));
        }
        return deviceMethod;
    }

}
