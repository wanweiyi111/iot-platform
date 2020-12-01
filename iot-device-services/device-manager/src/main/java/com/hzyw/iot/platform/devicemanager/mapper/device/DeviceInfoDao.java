package com.hzyw.iot.platform.devicemanager.mapper.device;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceInfoDO;
import com.hzyw.iot.platform.devicemanager.domain.vo.DeviceListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DeviceInfoDao
 *
 * @blame Android Team
 */
@Mapper
public interface DeviceInfoDao {

    /**
     * 查询设备静态信息，根据设备ID
     *
     * @param deviceId
     * @return
     */
    public DeviceInfoDO getDeviceInfoByID(@Param("deviceId") String deviceId);

    /**
     * 查询设备静态信息，根据设备序列号,设备大类
     *
     * @param serialNumber
     * @return
     */
    public List<DeviceInfoDO> getDeviceInfoBySN(String serialNumber);

    /**
     * 查询设备静态信息，根据设备型号(SN 为附加条件)
     * @param deviceType
     * @param serialNumber
     * @return
     */
    public List<DeviceInfoDO> getDeviceInfoByType(String deviceType, String serialNumber);

    /**
     * updateDeviceInfo
     * @param deviceInfoDO
     */
    public void updateDeviceInfo(DeviceInfoDO deviceInfoDO);

    /**
     * insertDeviceInfo
     * @param deviceInfoDO
     */
    public void insertDeviceInfo(DeviceInfoDO deviceInfoDO);

    /**
     * deleteDeviceInfo
     * @param deviceId
     */
    public void deleteDeviceInfo(@Param("deviceId") String deviceId);

    /**
     * 获取设备信息列表
     */
    public List<DeviceListVO> getAllDeviceInfo(@Param("deviceId") String deviceId,
                                               @Param("deviceType") String deviceType,
                                                @Param("gatewayId") String gatewayId);

    /**
     * 更新设备SN
     * @param deviceId
     * @param newSN
     */
    void updateDeviceSN(String deviceId, String newSN);
}
