package com.hzyw.iot.platform.models.alarm;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/17.
 */

public class SignalAlarmMsg {
    private Integer code;
    private String name;
    private String msg;
    private Integer level;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}

