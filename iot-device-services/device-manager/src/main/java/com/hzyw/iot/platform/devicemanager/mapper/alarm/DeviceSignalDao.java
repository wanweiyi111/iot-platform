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
public interface DeviceSignalDao {

    @Select("select * from DEVICE_SIGNAL_T where code=#{code}")
    SignalAlarmMsg getSignal(int code);

    @Insert("replace into DEVICE_SIGNAL_T(code, name, msg,level) VALUES (#{code},#{name},#{msg},#{level})")
    void saveSignal(int code, String name, String msg);

    @Select("select * from DEVICE_SIGNAL_T")
    List<SignalAlarmMsg> listSignal();

    @Select("select * from DEVICE_SIGNAL_T where code=#{code} union select * from DEVICE_ERROR_T where code=#{code}")
    SignalAlarmMsg selectCode(int code);
}
