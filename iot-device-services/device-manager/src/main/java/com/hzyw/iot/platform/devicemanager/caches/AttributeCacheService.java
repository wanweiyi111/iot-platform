package com.hzyw.iot.platform.devicemanager.caches;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAttributeDO;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAttrService;
import com.hzyw.iot.platform.models.equip.DeviceAttribute;
import com.hzyw.iot.platform.models.equip.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/2.
 */
@Slf4j
@Service
public class AttributeCacheService {

    private static final String CACHE_NAME = "DeviceAttribute";

    private final DeviceAttrService deviceAttrService;

    public AttributeCacheService(DeviceAttrService deviceAttrService) {
        this.deviceAttrService = deviceAttrService;
    }

    //    @Override
    @Cacheable(cacheManager = "AttributeCache",cacheNames = CACHE_NAME, key = "#key", unless = "#result==null")
    public DeviceAttribute get(String key) {
        if(StringUtils.isEmpty(key)){
            return null;
        }
        log.debug("Cache--> not found {} in {},will search from DB", key, CACHE_NAME);
        DeviceAttributeDO deviceAttributeDO = deviceAttrService.selectDeviceAttr(key);
        return buildDeviceAttribute(deviceAttributeDO);
    }

//    @Override
    @CacheEvict(cacheManager = "AttributeCache",cacheNames = CACHE_NAME, key = "#key")
    public void evict(String key) {
        log.debug("Cache--> evict {} from {}.", key, CACHE_NAME);
    }

    public DeviceAttribute buildDeviceAttribute(DeviceAttributeDO deviceAttributeDO) {
        if(deviceAttributeDO == null){
            return  null;
        }
        String key = deviceAttributeDO.getAttrKey();
        DeviceAttribute attr = new DeviceAttribute(key, deviceAttributeDO.getValueType());
        attr.setAttributeName(deviceAttributeDO.getAttrName());
        attr.setUnit(deviceAttributeDO.getUnit());
        if (!StringUtils.isEmpty(deviceAttributeDO.getMetaData())) {
            Map m = JSONObject.parseObject(deviceAttributeDO.getMetaData()).getInnerMap();
            attr.setAttributeMeta(m);
        }
        return attr;
    }

    private DeviceAttributeDO buildDeviceAttributeDO(DeviceAttribute attribute) {
        DeviceAttributeDO attributeDO = new DeviceAttributeDO();
        attributeDO.setUnit(attribute.getUnit());
        attributeDO.setAttrKey(attribute.getAttributeKey());
        attributeDO.setAttrName(attribute.getAttributeName());
        if (attribute.getValueType() != null) {
            attributeDO.setValueType(attribute.getValueType().getName());
        }
        if (attribute.getAttributeMeta() != null && !attribute.getAttributeMeta().isEmpty()) {
            attributeDO.setMetaData(JSON.toJSONString(attribute.getAttributeMeta()));
        }
        return attributeDO;
    }

}
