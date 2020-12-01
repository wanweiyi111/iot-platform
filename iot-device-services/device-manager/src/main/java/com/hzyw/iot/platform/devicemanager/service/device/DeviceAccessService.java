package com.hzyw.iot.platform.devicemanager.service.device;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAccessDO;

import java.util.List;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/2.
 */
public interface DeviceAccessService {
    
    void insertDeviceAccess(DeviceAccessDO deviceAccessDO);

    void deleteDeviceAccess(String deviceId);

    DeviceAccessDO getDeviceAccess(String deviceId);

    void updateDeviceAccess(DeviceAccessDO deviceAccessDO);

    List<DeviceAccessDO> findDeviceAccessInfoList();
}
