package com.hzyw.iot.util.constant;

/**
 * 方法名 与 指令码 映射配置（处理适用 下发请求的入参）
 */
public enum PLC_METHOD_CMD_CONFIG {
    opera1("70H"), //集中器继电器开
    opera2("71H"), //集中器继电器关
    sel_plc_state("73H"), //查询集中器状态
    opera4("82H"), //下发定时任务
    opera5("83H"), //查询定时任务
    opera6("84H"), //清除定时任务
    opera7("8CH"), //设置集中器时间
    opera8("8EH"), //设置集中器参数
    opera9("8FH"), //查询集中器参数
    put_node_list("96H"), //下发节点
    read_node_list("97H"), //读取节点
    save_node("98H"), //配置节点
    del_node("99H"), //删除节点
    opera14("F0H"), //集中器登录
    opera15("F1H"), //集中器与主机保持连接心跳
    opera16("F2H"), //系统控制
    opera17("F3H"), //集中器报警
    opera18("F4H"), //执行失败返回
    opera19("F5H"), //报警能使设置
    opera20("F6H"), //报警能使查询
    set_onoff("42H"), //开关灯
    set_brightness("42H"), //节点调光
    auto_report_node("F7H"), //主动上报节点数据
    sel_detail_node("45H"), //查询节点详细数据
    opera24("FBH"), //查询和上传历史数据
    opera25("FCH"), //设置集中器远程更新IP和端口
    opera26("FDH"), //查询集中器远程更新IP和端口
    opera27("9AH"), //查询集中器组网情况
    opera28("9BH"), //查询集中器版本信息
    opera29("9CH"), //PLC软件复位
    opera30("60H"), //设置集中器继电器必须开启时间
    opera31("61H"), //查询集中器继电器必须开启时间
    opera32("46H"), //查询节点传感器信息
    opera33("FEH"), //节点传感器主动上报信息
    opera34("62H"), //2480开始组网
    opera35("63H"), //2480停止组网
    opera36("66H"), //2480存储节点列表
    opera37("67H"), //读取2480FLAH节点列表
    opera38("9EH"), //增加单个节点
    opera39("9DH"), //删除单个节点
    opera40("69H"), //2480删除节点FLSH存储列表
    opera41("4AH"), //查询集中器硬件信息
    opera42("F8H"), //设置集中器服务器IP和端口
    opera43("F9H"), //查询集中器服务器IP和端口
    opera44("6AH"), //设定电源最大功率
    opera45("6BH"), //查询电源最大功率
    opera46("6CH"), //设定电源报警阀值
    opera47("6DH"), //查询电源报警阀值
    opera48("6FH"), //查询电源任务编号
    opera49("47H"), //删除电源任务编号
    opera50("48H"), //查询电源一条定时任务
    opera51("49H"), //设定电源时间
    opera52("4BH"), //查询电源时间
    opera53("4CH"); //设定电源初始化值


    private String code;
    PLC_METHOD_CMD_CONFIG(String code) {
        this.code = code;
    }

    public static String Method2CMD(String operaName){
        operaName=operaName==null?"NONE":operaName;
        String resCode="";
        for (PLC_METHOD_CMD_CONFIG cf : PLC_METHOD_CMD_CONFIG.values()) {
            if (cf.name().equals(operaName)) {
                resCode=cf.code;
                break;
            }
        }
        return resCode;
    }

    public static String CMD2Method(String cmdCode){
        String resOperaName="";
        for (PLC_METHOD_CMD_CONFIG cf : PLC_METHOD_CMD_CONFIG.values()) {
            cmdCode=cmdCode==null?"NONE":cmdCode;
            if (cf.code.equals(cmdCode)) {
                resOperaName=cf.name();
                break;
            }
        }
        return resOperaName;
    }
}
