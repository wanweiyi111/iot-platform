package com.hzyw.iot.util.constant;

/**
 * PLC 属性名，code 配置枚举类
 */
public enum PLC_CONFIG {
    //路灯电源（设备码：00H~6FH） 信号
    电源状态位(109001,"current_source_bit0_code"),
    输入电压过低报警位(109002,"current_source_bit1_code"),
    输入电压过高报警位(109003,"current_source_bit2_code"),
    功率过高报警位(109004,"current_source_bit3_code"),
    输出开路报警位(109005,"current_source_bit4_code"),
    输出短路报警位(109006,"current_source_bit5_code"),
    无法启动报警位(109007,"current_source_bit6_code"),
    温度过低报警位(109008,"current_source_bit7_code"),
    温度过高报警位(109009,"current_source_bit8_code"),
    //路灯控制器（设备码：70H~7FH） 信号
    A灯继电器状态位(109010,"controller_bit0_code"),
    A灯欠流报警位(109011,"controller_bit1_code"),
    A灯过流报警位(109012,"controller_bit2_code"),
    A灯欠压报警位(109013,"controller_bit3_code"),
    A灯过压警位(109014,"controller_bit4_code"),
    A灯欠载报警位(109015,"controller_bit5_code"),
    A灯过载报警位(109016,"controller_bit6_code"),
    A灯继电器失效报警位(109017,"controller_bit7_code"),
    B灯继电器状态位(109018,"controller_bit8_code"),
    B灯欠流报警位(109019,"controller_bit9_code"),
    B灯过流报警位(109020,"controller_bit10_code"),
    B灯欠压报警位(109021,"controller_bit11_code"),
    B灯过压警位(109022,"controller_bit12_code"),
    B灯欠载报警位(109023,"controller_bit13_code"),
    B灯过载报警位(109024,"controller_bit14_code"),
    B灯继电器失效报警位(109025,"controller_bit15_code"),

    帧总数(0,"plc_plain_count"),
    帧数(0,"plc_plain_num"),
    //上报节点相关属性
    节点总数(0,"plc_node_count"),
    节点ID(0,"plc_node_id"),
    设备码(0,"plc_node_devCode"),
    在线状态(0,"plc_node_a_status"),
    温度(0,"plc_node_temperature"),
    输入电压(0,"plc_node_voltage_in"),
    输出电压(0,"plc_node_voltage_out"), //voltage 电压
    A路输入电流(0,"plc_node_a_electri_in"),
    B路输入电流(0,"plc_node_b_electri_in"),
    输入电流(0,"plc_node_electri_in"),
    输出电流(0,"plc_node_electri_out"),  //电流
    输入功率(0,"plc_node_power_in"),
    A路有功功率(0,"capacity"), //plc_node_a_power  功率
    B路有功功率(0,"plc_node_b_power"),
    A路功率因数(0,"plc_node_a_pf"),
    B路功率因数(0,"plc_node_b_pf"),
    功率因数(0,"plc_node_pf"),
    A路亮度(0,"plc_node_a_brightness"),
    B路亮度(0,"plc_node_b_brightness"),
    调光亮度(0,"plc_node_brightness"),
    异常状态(0,"plc_node_exception_state"),
    状态(0,"plc_node_state"),

    //新电源设备扩展属性
    灯具温度(0,"plc_node_light_temperature"),
    输出功率(0,"plc_node_power_out"),
    灯具运行时长(0,"plc_node_light_runtime"),
    电能(0,"plc_elect_energy"),
    故障时长(0,"plc_node_trouble_time"),

    时间(0,"plc_node_time"),

    //读取节点 属性
    节点所属组号(0,"plc_node_group");


    private Integer code;
    private String key;
    PLC_CONFIG(Integer code,String key) {
        this.code = code;
        this.key=key;
    }

    public Integer getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }

    //查询节点详细数据 【预留的 0标识】
    public static  Object[][] StateBitTemp(){
        Object[][] stateBitTemp=new Object[][]{{PLC_CONFIG.电源状态位.code,PLC_CONFIG.A灯继电器状态位.code},{PLC_CONFIG.输入电压过低报警位.code,PLC_CONFIG.A灯欠流报警位.code},
                                               {PLC_CONFIG.输入电压过高报警位.code,PLC_CONFIG.A灯过流报警位.code},{PLC_CONFIG.功率过高报警位.code,PLC_CONFIG.A灯欠压报警位.code},
                                               {PLC_CONFIG.输出开路报警位.code,PLC_CONFIG.A灯过压警位.code},{PLC_CONFIG.输出短路报警位.code,PLC_CONFIG.A灯欠载报警位.code},
                                               {PLC_CONFIG.无法启动报警位.code,PLC_CONFIG.A灯过载报警位.code},{PLC_CONFIG.温度过低报警位.code,PLC_CONFIG.A灯继电器失效报警位.code},
                                               {PLC_CONFIG.温度过高报警位.code,PLC_CONFIG.B灯继电器状态位.code},{0,PLC_CONFIG.B灯欠流报警位.code},
                                               {0,PLC_CONFIG.B灯过流报警位.code},{0,PLC_CONFIG.B灯欠压报警位.code},
                                               {0,PLC_CONFIG.B灯过压警位.code},{0,PLC_CONFIG.B灯欠载报警位.code},
                                               {0,PLC_CONFIG.B灯过载报警位.code},{0,PLC_CONFIG.B灯继电器失效报警位.code}};
        return stateBitTemp;
    }
    /***********************查询节点详细数据(45H) 集中器->主机***BEGIN***************************/
    //参数属性名模板(非路灯控制器设备【路灯电源】 老程序) bit15~bit9:0x00,为老程序
    static String[] paramNameTemp_OLD=new String[] {PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,PLC_CONFIG.温度.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.输出电压.key,PLC_CONFIG.A路输入电流.key,PLC_CONFIG.B路输入电流.key,PLC_CONFIG.输出电流.key,
            PLC_CONFIG.A路有功功率.key,PLC_CONFIG.B路有功功率.key,PLC_CONFIG.A路功率因数.key,PLC_CONFIG.B路功率因数.key,PLC_CONFIG.A路亮度.key,
            PLC_CONFIG.B路亮度.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key};
    /*static String[] paramNameTemp_OLD=new String[] {PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,PLC_CONFIG.温度.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.输出电压.key,PLC_CONFIG.输入电流.key,PLC_CONFIG.输出电流.key,
            PLC_CONFIG.输入功率.key,PLC_CONFIG.功率因数.key,PLC_CONFIG.调光亮度.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key};
    static String[] paramNameTemp_OLD=new String[] {PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,PLC_CONFIG.温度.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.输出电压.key,PLC_CONFIG.输入电流.key,PLC_CONFIG.输出电流.key,
            PLC_CONFIG.功率因数.key,PLC_CONFIG.调光亮度.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key};*/
    //参数属性名模板(非路灯控制器设备【路灯电源】 新程序) bit15~bit9:0Xf0，则为新程序
    static String[] paramNameTemp_NEW=new String[] {PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,PLC_CONFIG.温度.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.输出电压.key,PLC_CONFIG.输入电流.key,PLC_CONFIG.输出电流.key,PLC_CONFIG.输入功率.key,
            PLC_CONFIG.功率因数.key,PLC_CONFIG.调光亮度.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key,PLC_CONFIG.灯具温度.key,PLC_CONFIG.输出功率.key,
            PLC_CONFIG.灯具运行时长.key,PLC_CONFIG.电能.key,PLC_CONFIG.故障时长.key};
    //参数属性名模板(单灯控制器设备)
    static String[] paramNameTemp_SINGLE=new String[] {PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,PLC_CONFIG.温度.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.A路输入电流.key,PLC_CONFIG.A路有功功率.key,
            PLC_CONFIG.A路功率因数.key,PLC_CONFIG.A路亮度.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key};
    //参数属性名模板(双灯控制器设备)
    static String[] paramNameTemp_DOUBLE=new String[] {PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,PLC_CONFIG.温度.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.A路输入电流.key,PLC_CONFIG.B路输入电流.key,
            PLC_CONFIG.A路功率因数.key,PLC_CONFIG.B路功率因数.key,PLC_CONFIG.A路亮度.key,PLC_CONFIG.B路亮度.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key};

   public static Object[][] paramNameT45HTemp(int[][] byteLenResTemp_OLD,int[][] byteLenResTemp_NEW,int[][] byteLenResTemp_SINGLE,int[][] byteLenResTemp_DOUBLE){
       Object[][] paramNameTemps=new Object[][] {{paramNameTemp_OLD,byteLenResTemp_OLD},{paramNameTemp_NEW,byteLenResTemp_NEW},
               {paramNameTemp_SINGLE,byteLenResTemp_SINGLE},{paramNameTemp_DOUBLE,byteLenResTemp_DOUBLE}};
       return paramNameTemps;
   }
    /***********************主动上报节点数据(f7H) 集中器->主机***BEGIN***************************/
    //参数属性名模板(非路灯控制器设备【路灯电源】 老程序) bit15~bit9:0x00,为老程序
    static String[] paramNameTempF7H_OLD=new String[] {PLC_CONFIG.节点总数.key,PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.输入电流.key,PLC_CONFIG.输入功率.key,
            PLC_CONFIG.功率因数.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key};
    //参数属性名模板(非路灯控制器设备【路灯电源】 新程序) bit15~bit9:0X88，则为新程序
    static String[] paramNameTempF7H_NEW=new String[] {PLC_CONFIG.节点总数.key,PLC_CONFIG.节点ID.key,PLC_CONFIG.设备码.key,PLC_CONFIG.在线状态.key,
            PLC_CONFIG.输入电压.key,PLC_CONFIG.输入电流.key,PLC_CONFIG.输入功率.key,
            PLC_CONFIG.功率因数.key,PLC_CONFIG.状态.key,PLC_CONFIG.异常状态.key,PLC_CONFIG.灯具温度.key,PLC_CONFIG.输出功率.key,PLC_CONFIG.灯具运行时长.key,
            PLC_CONFIG.电能.key,PLC_CONFIG.故障时长.key};
    public static Object[][] paramNameTF7HTemp(int[][] byteLenResTemp_OLD,int[][] byteLenResTemp_NEW){
        Object[][] paramNameTemps=new Object[][] {{paramNameTempF7H_OLD,byteLenResTemp_OLD},{paramNameTempF7H_NEW,byteLenResTemp_NEW}};
        return paramNameTemps;
    }
    /***********************主动上报节点数据(f7H) 集中器->主机***END***************************/
    /***********************读取节点(97H) 集中器->主机******************************/
    static String[] paramNameTemp97H=new String[] {PLC_CONFIG.帧总数.key,PLC_CONFIG.帧数.key,PLC_CONFIG.节点ID.key,PLC_CONFIG.节点所属组号.key,PLC_CONFIG.设备码.key};
    public static String[] paramNameT97HTemp(){
        return paramNameTemp97H;
    }
    /***********************读取节点(97H) 集中器->主机**END****************************/
}
