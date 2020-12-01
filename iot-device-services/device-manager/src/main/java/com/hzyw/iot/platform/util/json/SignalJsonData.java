package com.hzyw.iot.platform.util.json;

import lombok.Data;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/21.
 */
@Data
public class SignalJsonData {
    private int signalCode;
    private String signalName;
    private String signalMsg;
    private int signalLevel;
}
