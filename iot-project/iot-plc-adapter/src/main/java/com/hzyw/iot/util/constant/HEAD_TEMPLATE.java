package com.hzyw.iot.util.constant;

/**
 * 帧消息头模板
 */
public enum HEAD_TEMPLATE {
    /* 帧起始符 */
    H("68"),
    /* 帧结束符 */
    T("16"),
    /* 数据长度 */
    L(null),
    /* 校验码 */
    CS(null),
    /* 帧数据域 */
    PDT(null),
    /* 地址域 暂固定值,测试用*/
    UID(null);
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value=value;
    }

    public static String getUID() {
        return HEAD_TEMPLATE.UID.value;
    }

    public static void setUID(String uid) {
        HEAD_TEMPLATE.UID.value = uid;
    }

    public static String getL() {
        return HEAD_TEMPLATE.L.value;
    }

    public static void setL(String l) {
        HEAD_TEMPLATE.L.value=l;
    }

    public static String getPDT() {
        return HEAD_TEMPLATE.PDT.value;
    }

    public static void setPDT(String pdt) {
        HEAD_TEMPLATE.PDT.value=pdt;
    }

    public static String getCS() {
        return HEAD_TEMPLATE.CS.value;
    }

    public static void setCS(String cs) {
        HEAD_TEMPLATE.CS.value=cs;
    }

    /**
     * 协议格式模板
     * H(帧起始符)+UID(地址域)+H(帧起始符)+C(控制码)+L(数据长度)+DT(CMD(命令码)+PDT(参数数据))+CS(校验码)+T(帧结束符)
     * @deprecated:
     * 帧起始符：用H表示，标识一帧数据的开始，其值为68H
     * 地址域：用UID表示，是固定长度为6个字节的设备标识，具有唯一性；当使用的地址码长度不足6字节时用00H补高字节补足6字节。高字节在先，低字节在后。
     * 控制码：用C表示，是控制终端方式的指令。
     * 数据长度：用L表示，指“数据域”中所有数据的字节数；L=0 表示无数据域。
     * 帧数据：用DT表示，各种控制传输的数据，包括命令CMD，参数数据PDT等，DT结构随控制码的功能而改变。主机与集中器帧数据最大255Bs。
     * 命令码:用CMD表示，对终端设备控制的指令或应答命令，视具体控制码而定。
     * 参数数据:用PDT表示，对终端设备控制的命令参数或命令执行后的结果返回。视具体控制码和命令而定，可没有此项。
     * 校验码：用CS表示，从“帧起始符”到校验码之前的所有字节的模256的和，即各字节二进制算术和，不计超过256 的溢出值。
     * 帧结束符：用T表示，标识一帧信息的结束，其值为16H
     * @return
     */
    @Override
    public String toString() {
        return HEAD_TEMPLATE.H.value+"%s"+HEAD_TEMPLATE.H.value+"%s%s%s%s%s"+HEAD_TEMPLATE.T.value;
}


    HEAD_TEMPLATE(String value) {
        this.value = value;
    }
    public static void main(String[] args){
        System.out.println(HEAD_TEMPLATE.T.toString());
    }
}
