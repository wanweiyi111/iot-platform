package com.hzyw.iot.util.constant;

import com.alibaba.fastjson.JSONObject;
/**
 * 控制码值 枚举集合
 */
public enum C_CODE_VAL{
    /* 对集中器控制或参数配置 或 集中器控制或配置的结果返回 */
    T00H("00"),
    /* 对集中器下属某终端单点控制 或 单点控制的结果返回 */
    T01H("01"),
    /* 对集中器下属某类终端组控制 或 组控制的结果返回 */
    T02H("02"),
    /* 对集中器下属某类终端广播控制 或 广播控制的结果返回 */
    T03H("03"),
    /* 集中器主动上报数据 */
    T04H("04"),
    /* 某种操作或请求的应答 */
    T80H("80"),
    /* 指定控制码 */
    TxxH(null);

    private String value;
    C_CODE_VAL(String value) {
        this.value = value;
    }

    public String getValue() {
    	return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 查询指定控制码值
     * @param code
     * @return
     */
    public static String CValueMethod(String code){
        code=code==null?"NONE":code;
        try {
            code=code.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resCode="";
        for (C_CODE_VAL cf : C_CODE_VAL.values()) {
            if (cf.name().endsWith(code)) {
                resCode=cf.value;
                break;
            }
        }
        return resCode;
    }
    private String msg;
    private String status;
    private String code;
    C_CODE_VAL(String msg, String status, String code) {
        this.msg = msg;
        this.status = status;
        this.code = code;
    }
}