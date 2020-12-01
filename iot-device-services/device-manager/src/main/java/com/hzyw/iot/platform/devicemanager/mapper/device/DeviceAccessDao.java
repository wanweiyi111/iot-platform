package com.hzyw.iot.platform.devicemanager.mapper.device;

import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAccessDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/30.
 */
public interface DeviceAccessDao {

    /**
     * 查询单条接入信息
     * @param deviceId
     * @return
     */
    DeviceAccessDO getDeviceAccess(@Param("deviceId") String deviceId);

    /**
     * 删除单条接入信息
     */
    @Delete("delete from DEVICE_ACCESS_T where device_id=#{deviceId}")
    void deleteDeviceAccess(@Param("deviceId") String deviceId);

    /**
     * 新增单条接入信息
     */
    @Insert("insert into DEVICE_ACCESS_T(device_id, device_domain, registration, protocol, protocol_version, device_ipv4, " +
            "device_port, longitude, latitude, location_type, access_time, leave_time, create_time, device_ipv6, gateway_id) " +
            "values (#{deviceId},#{deviceDomain},#{registration},#{protocol},#{protocolVersion},#{deviceIPv4},#{devicePort}," +
            "#{longitude},#{latitude},#{locationType},#{accessTime},#{leaveTime},#{createTime},#{deviceIPv6},#{gatewayId})")
    void insertDeviceAccess(DeviceAccessDO vo);

    /**
     * 修改单条属性
     */
    void updateDeviceAccess(DeviceAccessDO vo);



    /**
     * 查询所有网关设备接入信息
     */
    List<DeviceAccessDO> findDeviceAccessInfoList();
}
