package com.hzyw.iot.platform.devicemanager.service.device.impl;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAccessDO;
import com.hzyw.iot.platform.devicemanager.mapper.device.DeviceAccessDao;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/2.
 */
@Slf4j
@Service
public class DeviceAccessServiceImpl implements DeviceAccessService {

    @Resource
    DeviceAccessDao dao;

    @Override
    public void insertDeviceAccess(DeviceAccessDO deviceAccessDO) {
        dao.insertDeviceAccess(deviceAccessDO);
    }

    @Override
    public void deleteDeviceAccess(String deviceId) {
        dao.deleteDeviceAccess(deviceId);
    }

    @Override
    public DeviceAccessDO getDeviceAccess(String deviceId) {
        return dao.getDeviceAccess(deviceId);
    }

    @Override
    public void updateDeviceAccess(DeviceAccessDO deviceAccessDO) {
        dao.updateDeviceAccess(deviceAccessDO);
    }

    @Override
    public List<DeviceAccessDO> findDeviceAccessInfoList() {
        return dao.findDeviceAccessInfoList();
    }
}
