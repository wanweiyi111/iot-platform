package com.hzyw.iot.platform.devicemanager.caches;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceInfoDO;
import com.hzyw.iot.platform.devicemanager.domain.device.ManufacturerDO;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceInfoService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceManufacturerService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceTypeService;
import com.hzyw.iot.platform.models.equip.DeviceType;
import com.hzyw.iot.platform.models.equip.Equipment;
import com.hzyw.iot.platform.models.equip.Manufacturer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/4.
 */
@Slf4j
@Service
public class EquipmentCacheService {

    private static final String CACHE_NAME = "IotEquipment";
    @Autowired
    DeviceInfoService deviceInfoService;
    @Autowired
    DeviceManufacturerService manufacturerService;

    @Autowired
    DeviceTypeCacheService typeCacheService;
//    @Resource(type = DeviceTypeCacheService.class)
//    ICacheRead<DeviceType> deviceTypeCacheService;

//    @Override
    @Cacheable(cacheManager = "EquipmentCache", cacheNames = CACHE_NAME, key = "#key", unless = "#result==null||#result.deviceId==null")
    public Equipment get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        log.debug("Cache--> not found {} in {},will search from DB", key, CACHE_NAME);
        DeviceInfoDO deviceInfoDO = deviceInfoService.searchEquipmentById(key);
        if(deviceInfoDO !=null){
            return buildEquipment(deviceInfoDO);
        }else {
            return null;
        }

    }

    private Equipment buildEquipment(DeviceInfoDO deviceInfoDO) {
        Equipment equipment = new Equipment(deviceInfoDO.getSerialNumber(), deviceInfoDO.getDeviceId());
        equipment.setEquipmentType(typeCacheService.get(deviceInfoDO.getDeviceType()));
        equipment.setBatchNumber(deviceInfoDO.getBatchNumber());
        equipment.setDeviceAlias(deviceInfoDO.getDeviceName());
        equipment.setExpireDate(deviceInfoDO.getExpireDate());
        equipment.setProductionDate(deviceInfoDO.getProductDate());
        ManufacturerDO manufacturerDO = manufacturerService.getManufacturer(deviceInfoDO.getManufacturerCode());
        equipment.setManufacturer(buildManufacturer(manufacturerDO));
        return equipment;
    }

    private Manufacturer buildManufacturer(ManufacturerDO manufacturerDO) {
        Manufacturer manufacturer = new Manufacturer(manufacturerDO.getManufacturerCode());
        manufacturer.setAddress(manufacturerDO.getAddress());
        manufacturer.setManufacturerName(manufacturerDO.getManufacturerName());
        manufacturer.setContactInfo(manufacturerDO.getContactInfo());
        return manufacturer;
    }

//    @Override
    public Equipment getNonNull(String key, Equipment defaultValue) {
        Equipment equipment = get(key);
        if (Objects.isNull(equipment)) {
            return defaultValue;
        }
        return equipment;
    }

//    @Override
    @CacheEvict(cacheManager = "EquipmentCache",cacheNames = CACHE_NAME, key = "#key")
    public void evict(String key) {
        log.debug("Cache--> evict {} from {}.", key, CACHE_NAME);
    }

//    @Override
    @CacheEvict(cacheManager = "EquipmentCache",cacheNames = CACHE_NAME, key = "#key")
    public Equipment remove(String key) {
        log.debug("Cache--> evict {} from {}.", key, CACHE_NAME);
        DeviceInfoDO infoDO = deviceInfoService.searchEquipmentById(key);
        if(infoDO != null){
            deviceInfoService.deleteById(key);
        }
        return buildEquipment(infoDO);
    }
}


