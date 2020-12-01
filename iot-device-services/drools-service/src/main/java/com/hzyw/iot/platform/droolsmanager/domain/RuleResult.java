package com.hzyw.iot.platform.droolsmanager.domain;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class RuleResult implements Serializable {

    //来源设备ID
    private String sourceDeviceId;
    //目标设备ID
    private String targetDeviceId;
    //触发动作
    private String triggerActionName;
   //执行动作
    private List<String> executeActionList = new ArrayList<String>();
    //输入参数
    //private InputParam inputParam;
    private int onoff = -1; //默认值，表示不设置状态
    private int level = -1; //默认值，表示不设置状态
    //匹配总数
    private int count = 0;
    public void addCount() {
        this.count += 1;
    }
    //规则执行结果
    private String msg;
    //private String condition;
    //输出参数
    //private int outonoff;
    //private int outlevel;


    //private String outputParam;

}
