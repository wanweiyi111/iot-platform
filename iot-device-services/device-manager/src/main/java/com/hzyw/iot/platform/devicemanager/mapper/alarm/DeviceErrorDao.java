package com.hzyw.iot.platform.devicemanager.mapper.alarm;

import com.hzyw.iot.platform.models.alarm.SignalAlarmMsg;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/17.
 */
public interface DeviceErrorDao {

    @Select("select * from DEVICE_ERROR_T where  code= #{code}")
    SignalAlarmMsg getError(int code);

    @Insert("replace into DEVICE_ERROR_T(code, name, msg, level) VALUES (#{code},#{name},#{msg},#{level})")
    void saveError(int code,String name, String msg);

    @Select("select * from DEVICE_ERROR_T")
    List<SignalAlarmMsg> listError();
}
