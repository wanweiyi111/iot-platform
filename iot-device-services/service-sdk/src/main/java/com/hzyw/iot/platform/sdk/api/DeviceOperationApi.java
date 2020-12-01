package com.hzyw.iot.platform.sdk.api;

import com.hzyw.iot.platform.models.transfer.DeviceOptRequest;
import com.hzyw.iot.platform.models.transfer.DeviceOptResponse;
import com.hzyw.iot.platform.sdk.config.PlatformSdkProperties;
import com.hzyw.iot.platform.sdk.rest.DeviceMgrFeignClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/8/16.
 */

public class DeviceOperationApi {

    @Autowired
    PlatformSdkProperties sdkConnectServerProperties;
    @Autowired
    DeviceMgrFeignClient deviceMgrFeignClient;

    public Object doOption(String deviceId, MethodVO method) throws Throwable {
        return doOption(deviceId, method, null, null);
    }

    public Object doOption(String deviceId, MethodVO method, String optId, Map audit) throws Throwable {
        List<String> ids = new ArrayList<>();
        ids.add(deviceId);
        return doOption(ids, method, optId, audit);
    }

    public Object doOption(List<String> deviceIds, MethodVO method, String optId, Map audit) throws Throwable {
        DeviceOptRequest request = new DeviceOptRequest();
        Map<String, Map<String, Object>> map = new HashMap();
        map.put(method.getName(), method.getParas());
        request.setMethod(map);
        request.setDeviceIds(deviceIds);
        request.setCallbackUrl(sdkConnectServerProperties.getCallBack().getUrl());
        request.setCallbackName(sdkConnectServerProperties.getCallBack().getName());
        request.setCustomMsgId(optId);
        //审计日志
        request.setAudit(audit);

//        调用FeignRestClient
        DeviceOptResponse response = deviceMgrFeignClient.deviceOpt(request);

        if (response.success()) {
            return response.getResponse();
        } else {
            if (response.getError() != null) {
                throw response.getError().getRootCause();
            }
            return null;
        }
    }

}
