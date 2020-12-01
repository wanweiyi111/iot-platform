package com.hzyw.iot.platform.devicemanager.mapper;

import com.hzyw.iot.platform.devicemanager.domain.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DeviceAttrMapper {
    /**
     * 查询单条设备
     */
    Device getOneDevice(@Param("serialNumber") String  serialNumber)throws Exception;

    /**
     * 查询多条设备
     */
    List<Device> getAllDevice(Device device)throws Exception;

    /**
     * 添加单条设备
     */
    void addOneDevice(Device device)throws Exception;

    /**
     * 批量添加设备
     */
    void addMultiDevice(List<Device> deviceList)throws Exception;

    /**
     * 批量更新设备
     */
    void batchUpdateDevice(List<Device> deviceList)throws Exception;

    /**
     * 更新单条设备
     */
    void updateDeviceById(Device device)throws Exception;

    /**
     * 批量删除设备
     */
    void batchDelete(List<Device> deviceList)throws Exception;

    /**
     * 删除单条设备
     */
    void deleteById(Device device)throws Exception;
}
