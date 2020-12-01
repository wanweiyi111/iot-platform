package com.hzyw.iot.platform.devicemanager.service.device.impl;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceInfoDO;
import com.hzyw.iot.platform.devicemanager.domain.vo.DeviceListVO;
import com.hzyw.iot.platform.devicemanager.mapper.device.DeviceInfoDao;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceInfoService;
import com.hzyw.iot.platform.models.transfer.IllegalParameterException;
import com.hzyw.iot.platform.util.math.DeviceIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DeviceInfoServiceImpl
 *
 * @blame Android Team
 */
@Slf4j
@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    @Resource
    DeviceInfoDao deviceInfoDao;

    @Override
    public DeviceInfoDO searchEquipmentById(String deviceId) {
        return deviceInfoDao.getDeviceInfoByID(deviceId);
    }

    @Override
    public List<DeviceInfoDO> searchEquipmentBySN(String serialNumber, Integer deviceDomain) {
        if (serialNumber == null) {
            return null;
        }
        List<DeviceInfoDO> list = deviceInfoDao.getDeviceInfoBySN(serialNumber);
        if (deviceDomain != null) {
            List fl = list.stream().filter(e -> deviceDomain.equals(e.getDeviceDomain())).collect(Collectors.toList());
            return fl;
        }
        return list;
    }

    @Override
    public DeviceInfoDO searchEquipmentBySN(String serialNumber, String deviceType) {
        List<DeviceInfoDO> list = deviceInfoDao.getDeviceInfoByType(deviceType, serialNumber);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<DeviceInfoDO> searchEquipmentByType(String deviceType) {
        List<DeviceInfoDO> list = deviceInfoDao.getDeviceInfoByType(deviceType, null);
        return list;
    }

    @Override
    public void createDevice(DeviceInfoDO deviceInfoDO) {
        deviceInfoDao.insertDeviceInfo(deviceInfoDO);
    }

    @Override
    public void deleteById(String deviceId) {
        deviceInfoDao.deleteDeviceInfo(deviceId);
    }

    @Override
//    @Transactional
    public void saveDevice(DeviceInfoDO deviceInfoDO) throws IllegalParameterException {
        if (deviceInfoDO != null) {
            if (StringUtils.isEmpty(deviceInfoDO.getSerialNumber())) {
//           SN不会没有值，默认为字符串NULL
                deviceInfoDO.setSerialNumber("NULL");
            }
            if (StringUtils.isEmpty(deviceInfoDO.getDeviceId())) {
//               没有DeviceId,则生成Id,有DeviceId,则验证Id,对于SN==NULL的，目前暂时是要验证Id合规的。
                String id = DeviceIdGenerator.generatorId(deviceInfoDO.getSerialNumber(),
                        deviceInfoDO.getDeviceDomain(), Integer.valueOf(deviceInfoDO.getManufacturerCode()));
                deviceInfoDO.setDeviceId(id);
            }else{
                if (!DeviceIdGenerator.validateDeviceId(deviceInfoDO.getDeviceId(), deviceInfoDO.getSerialNumber())) {
//               没有验证通过，抛出异常
                    throw new IllegalParameterException(String.format("DeviceInfo.deviceId: {%s} is invalid!", deviceInfoDO.getDeviceId()));
                }
            }
//            执行更新或插入逻辑
            DeviceInfoDO infoDO = deviceInfoDao.getDeviceInfoByID(deviceInfoDO.getDeviceId());
            if (infoDO != null) {
                deviceInfoDao.updateDeviceInfo(deviceInfoDO);
            } else {
                deviceInfoDao.insertDeviceInfo(deviceInfoDO);
            }
        }
    }

    @Override
    public boolean changeSerialNumber(String deviceId, String oldSN, String newSN) {
        if(deviceId==null||oldSN==null||newSN==null){
            return false;
        }
        DeviceInfoDO  info = deviceInfoDao.getDeviceInfoByID(deviceId);
        if(info == null|| !oldSN.equals(info.getSerialNumber())){
            return false;
        }
        if(DeviceIdGenerator.validateDeviceId(deviceId,newSN)){
            deviceInfoDao.updateDeviceSN(deviceId,newSN);
            return true;
        }else {
            log.warn("deviceId Validate Failed!, id ={}, sn={}",deviceId,newSN);
            return false;
        }
    }

    @Override
    public List<DeviceListVO> getAllDeviceInfo(String deviceId, String deviceType, String gatewayId) {
        return deviceInfoDao.getAllDeviceInfo(deviceId,deviceType,gatewayId);
    }

//    public static Equipment copyToEquipment(DeviceInfoDO deviceInfoDO, DeviceTypeDO deviceTypeDO, ManufacturerDO manufacturerDO) {
//        Equipment equipment = null;
//        PoleType poleType;
//        EquipmentType equipmentType;
//        Manufacturer manufacturer;
//        if (deviceInfoDO != null) {
//            equipment = new Equipment(deviceInfoDO.getSerialNumber());
//            equipment.setDeviceId(deviceInfoDO.getDeviceId());
//            equipment.setDeviceAlias(deviceInfoDO.getDeviceName());
//            equipment.setBatchNumber(deviceInfoDO.getBatchNumber());
//            equipment.setProductionDate(deviceInfoDO.getProductDate());
//            equipment.setExpireDate(deviceInfoDO.getExpireDate());
//            if (deviceTypeDO != null) {
//                poleType = new PoleType();
//                poleType.setTypeCode(deviceTypeDO.getTypeCode());
//                poleType.setTypeName(deviceTypeDO.getTypeName());
//                poleType.setDomainFlag(EquipmentFlag.getEquipmentFlagByIndex(deviceTypeDO.getDeviceDomain()));
//                equipment.setEquipmentType(poleType);
//            }
//            if (manufacturerDO != null) {
//                manufacturer = new Manufacturer();
//                manufacturer.setManufacturerCode(manufacturerDO.getManufacturerCode());
//                manufacturer.setManufacturerName(manufacturerDO.getManufacturerName());
//                manufacturer.setAddress(manufacturerDO.getAddress());
//                manufacturer.setContactInfo(manufacturerDO.getContactInfo());
//                equipment.setManufacturer(manufacturer);
//            }
//            return equipment;
//        }
//        return equipment;
//    }


}
