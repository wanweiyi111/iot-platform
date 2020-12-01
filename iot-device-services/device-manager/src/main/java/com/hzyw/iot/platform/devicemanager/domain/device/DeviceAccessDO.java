package com.hzyw.iot.platform.devicemanager.domain.device;

import lombok.Data;

import java.util.Date;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/30.
 */
@Data
public class DeviceAccessDO {
    /**
     * 注册激活
     */
    public static final int REGISTER = 0;
    /**
     * 在网运营
     */
    public static final int ONLINE = 1;
    /**
     * 离网注销
     */
    public static final int CANCEL = -1;

    private String deviceId;
    private Integer deviceDomain;
    private Integer registration;
    private String protocol;
    private String protocolVersion;
    private String devicePort;
    private String deviceIPv4;
    private String deviceIPv6;
    private String longitude; //经度
    private String latitude;  //纬度
    private String locationType;
    private Date accessTime;
    private Date leaveTime;
    private Date createTime;
    private String gatewayId;
    private String serialNO;
}
