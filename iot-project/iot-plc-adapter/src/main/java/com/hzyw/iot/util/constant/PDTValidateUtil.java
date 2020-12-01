package com.hzyw.iot.util.constant;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * pdt 指令参数校验
 */
public class PDTValidateUtil {
    private static final Logger log = LoggerFactory.getLogger(PDTValidateUtil.class);
    /**
     * 通用校验 公共  响应类型
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateResComPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        return resBoolean;
    }

    /**
     * 集中器登录(F0H)响应报文校验 (部分指令的验证 通用)
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateResF0HPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        return resBoolean;
    }

    /**
     * 主动上报节点数据(F7H)请求报文校验-----（文档上 没有请求需求）
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqF7HPdt(String cmdParam) throws Exception {
        return true;
    }

    /**
     * 主动上报节点数据(F7H)/查询节点详细数据 响应报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateResF7HPdt(String cmdParam, AtomicInteger indexNum) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>250 || bytePdtArrs.length==0 || bytePdtArrs==null) {
            resBoolean = false;
            log.error("======解析 '主动上报节点数据'(F7H)响应的PDT报文错误！字节长度超过250个字节 或指令参数为空!");
        }
        String code=ConverUtil.convertByteToHexStr(bytePdtArrs[8])+"H"; //设备码
        System.out.println("============主动上报节点数据(F7H)=路灯控制器】(设备码:70H~7FH)==【路灯电源】(设备码:00H~6FH)==新老程序对应=设备码:"+code);
        Long codeValue=DecimalTransforUtil.hexToLong(D_CODE_VAL.DValueMethod(code),true);
        System.out.println("============主动上报节点数据(F7H)=路灯控制器】(设备码:112~127)===【路灯电源】(设备码:0~111)===新老程序对应=10进制设备码值:"+codeValue);
        if(PLCValidateUtil.rangeInDefined(codeValue.intValue(),112,127)) {//路灯控制器（设备码：70H~7FH）
                indexNum.set(0);
        }else{//非路灯控制器设备【路灯电源】 00H~6FH
            byte[] byteState=Arrays.copyOfRange(bytePdtArrs,15,17); //截取 状态位 属性值(2字节)
            String state=ConverUtil.convertByteToHexString(byteState);//状态位
            String sitemVal=DecimalTransforUtil.hexStringToByte(state,16); //16进制转2进制
            char[] bitChar=sitemVal.toCharArray(); //倒序排列
            ConverUtil.reverseString(bitChar);
            if(bitChar.length!=16) throw new Exception("PDT 区分新老设备(路灯电源设备) 截取 bit15~bit9时, 状态位不够16位!");
            if(bitChar.length==16){
                bitChar=Arrays.copyOfRange(bitChar,9,16); //截取 bit15~bit9 转16进制串
            }
            String hh = new String(bitChar); //ISO8859-1
            log.debug("============bit15~bit9新老程序状态位(新程序:10001000):"+hh.concat("0"));
            //System.out.println("============bit15~bit9新老程序状态位(新程序:10001000):"+hh.concat("0"));
            String newDevCode=DecimalTransforUtil.BinaryString2hexString(hh.concat("0"));//二进制数据转化为16进制字符串
            log.debug("========主动上报节点数据(F7H)==区分新老设备(路灯电源设备)截取 bit15~bit9 转化为16进制值(88为新程序):"+newDevCode);
            //System.out.println("========主动上报节点数据(F7H)==区分新老设备(路灯电源设备)截取 bit15~bit9 转化为16进制值(88为新程序):"+newDevCode);
            if(StringUtils.isNumeric(newDevCode)){
                if(Integer.valueOf(newDevCode)==88){//88------10001000
                    indexNum.set(1); //bit15~bit9:0Xf0，则为新程序(1)
                }else{
                    indexNum.set(0); //bit15~bit9:0x00,为老程序(0)
                }
            }
        }
        return resBoolean;
    }

    /**
     * 查询节点详细数据 (45H)响应报文校验
     * @param cmdParam
     * @param indexNum
     * @return
     * @throws Exception
     */
    public static boolean validateResT45HPdt(String cmdParam, AtomicInteger indexNum) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>250 || bytePdtArrs.length==0 || bytePdtArrs==null) {
            resBoolean = false;
            log.error("======解析 '主动上报节点数据'(F7H)响应的PDT报文错误！字节长度超过250个字节!");
            //System.out.println("======解析 '主动上报节点数据'(F7H)响应的PDT报文错误！字节长度超过250个字节!");
        }
        String code=ConverUtil.convertByteToHexStr(bytePdtArrs[7])+"H"; //设备码
        log.debug("============查询节点详细数据(45H)=【路灯控制器】(设备码:70H~7FH)==【路灯电源】(设备码:00H~6FH)==新老程序对应=设备码:"+code);
        //System.out.println("============查询节点详细数据(45H)=【路灯控制器】(设备码:70H~7FH)==【路灯电源】(设备码:00H~6FH)==新老程序对应=设备码:"+code);
        Long codeValue=DecimalTransforUtil.hexToLong(D_CODE_VAL.DValueMethod(code),true);
        log.debug("============查询节点详细数据(45H)=【路灯控制器】(设备码:112~127)===【路灯电源】(设备码:0~111)===新老程序对应=10进制设备码值:"+codeValue);
        //System.out.println("============查询节点详细数据(45H)=【路灯控制器】(设备码:112~127)===【路灯电源】(设备码:0~111)===新老程序对应=10进制设备码值:"+codeValue);
        if(PLCValidateUtil.rangeInDefined(codeValue.intValue(),112,127)) {//路灯控制器（设备码：70H~7FH）
            //单灯控制器设备(2)  73H  ---115; 双灯控制器设备(3) 7AH  ---122
            if(codeValue.intValue()==122){//双灯控制器设备
                indexNum.set(3);
            }else{//其它 默认 单灯控制器设备
                indexNum.set(2);
            }
        }else{//非路灯控制器设备【路灯电源】 00H~6FH
            byte[] byteState=Arrays.copyOfRange(bytePdtArrs,21,23);
            String state=ConverUtil.convertByteToHexString(byteState);//状态位
            String sitemVal=DecimalTransforUtil.hexStringToByte(state,16); //16进制转2进制
            char[] bitChar=sitemVal.toCharArray(); //倒序排列
            ConverUtil.reverseString(bitChar);
            if(bitChar.length!=16) throw new Exception("PDT 区分新老设备(路灯电源设备) 截取 bit15~bit9时, 状态位不够16位!");
            if(bitChar.length==16){
                bitChar=Arrays.copyOfRange(bitChar,9,16); //截取 bit15~bit9 转16进制串
            }
            String hh = new String(bitChar); //ISO8859-1
            log.debug("============bit15~bit9新老程序状态位(新程序:11110000):"+hh.concat("0"));
            //System.out.println("============bit15~bit9新老程序状态位(新程序:11110000):"+hh.concat("0"));
            String newDevCode=DecimalTransforUtil.BinaryString2hexString(hh.concat("0"));//二进制数据转化为16进制字符串
            log.debug("========查询节点详细数据(45H)=区分新老设备(路灯电源设备)截取 bit15~bit9 转化为16进制值(f0为新程序):"+newDevCode);
            //System.out.println("========查询节点详细数据(45H)=区分新老设备(路灯电源设备)截取 bit15~bit9 转化为16进制值(f0为新程序):"+newDevCode);
            if(StringUtils.isNumeric(newDevCode)){
                indexNum.set(0); //bit15~bit9:0x00,为老程序(0)
            }else if("f0".equals(newDevCode.toLowerCase())){//f0-----11110000
                indexNum.set(1); //bit15~bit9:0Xf0，则为新程序(1)
            }
        }
        return resBoolean;
    }


    /**
     * 查询节点详细数据(45H) 请求报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
    /*public static boolean validateReqT45HPdt(String cmdParam) throws Exception {
        return true;
    }*/

    /**
     * 查询节点详细数据(45H) 响应报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
   /* public static boolean validateResT45HPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        return resBoolean;
    }*/

    /**
     * 查询集中器状态 (73H)响应报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateResT73HPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>33) {
            resBoolean = false;
            log.error("======解析 '查询集中器状态'(73H)响应的PDT报文错误！字节长度超过33个字节!");
            //throw new Exception(" 解析 '查询集中器状态'(73H)响应的PDT报文错误！字节长度超过33个字节!");
        }
        return resBoolean;
    }

    /**
     * 读取节点(97H) 响应报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateResT97HPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>242) {
            resBoolean = false;
            log.error("======解析 '查询集中器状态'(97H)响应的PDT报文错误！字节长度超过242个字节!");
        }
        return resBoolean;
    }

    /**
     * 下发定时任务 (82H)请求报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqT82HPdt(String cmdParam) throws Exception{
        return true;
    }

    /**
     * 下发定时任务(82H)响应报文校验
     * @return
     * @param cmdParam
     */
    public static boolean validateResT82HPdt(String cmdParam)throws Exception{
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>250) {
            resBoolean = false;
            log.error("======解析 '下发定时任务'(82H)响应的PDT报文错误！字节长度超过250个字节!");
        }
        return resBoolean;
    }

    public static boolean validateResTFEHPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>=250) throw new Exception(" 解析 '查询集中器状态'(73H)响应的PDT报文错误！字节长度必须小于250个字节!");
        return resBoolean;
    }

    public static boolean validateResT9BHPdt(String cmdParam) throws Exception {
        boolean resBoolean=true;
        if ("02".equals(cmdParam) || "03".equals(cmdParam)) return false; //02 (02H) 表示报文校验有错误，返回错误码:02; 03: 系统忙
        byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>242) throw new Exception(" 解析 '查询集中器下PLC版本信息（9BH）响应的PDT报文错误！字节长度超过242个字节!");
        return resBoolean;
    }

    /**
     * 节点调光(42H)请求报文校验
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqT42HPdt(String c,StringBuffer cmdParam)throws Exception{
        if("".equals(c)) return false;
        List<Object> paramList= JSONArray.parseArray(cmdParam.toString());
        String pdtParam="";
        if(paramList.size()>0){
            JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
            for (int h = 0; h < paramValArray.size(); h++) {
                System.out.println("======解析'节点调光(42H)===Array:" + paramValArray.getString(h));
                pdtParam+=ConverUtil.MappCODEVal(paramValArray.getString(h),true);
                System.out.println("=====解析'节点调光(42H)==11111====cmdParam:" +pdtParam);
            }
            byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(pdtParam);
            int pdtLen=bytePdtArrs.length;
            if(!PLCValidateUtil.rangeInDefined(pdtLen,2,8)) {
                log.error("======解析'节点调光(42H)'请求的PDT报文长度有错误！字节长度范围：2~8字节!");
                return false;
            }
            byte[] byteNodeID=Arrays.copyOfRange(bytePdtArrs,0,pdtLen-2); //截取节点ID 参数值
            paramValArray.set(0,validateNodeID(c,byteNodeID));
            paramList=new JSONArray();
            paramList.add(paramValArray);
            cmdParam.replace(0,cmdParam.length(),JSONArray.toJSONString(paramList));
            return true;
        }
        return false;
    }

    /**
     * 删除节点(99H)请求报文校验
     * @param c
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqT99HPdt(String c,StringBuffer cmdParam)throws Exception{
        if("".equals(c)) return false;
        List<Object> paramList= JSONArray.parseArray(cmdParam.toString());
        String pdtParam="";
        if(paramList.size()>0){
            JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
            System.out.println("======解析'删除节点(99H)===Array:" + paramValArray.getString(0));
            pdtParam+=paramValArray.getString(0);
            byte[] bytePdtIDArrs=ConverUtil.hexStrToByteArr(pdtParam);
            if(!PLCValidateUtil.rangeInDefined(bytePdtIDArrs.length,0,6)) {
                log.error("======解析'删除节点(99H)'请求的PDT报文 ID 入参长度有错误！字节长度范围：0~6字节!");
                return false;
            }
            paramValArray.set(0,validateNodeID(c,bytePdtIDArrs));
            paramList=new JSONArray();
            paramList.add(paramValArray);
            cmdParam.replace(0,cmdParam.length(),JSONArray.toJSONString(paramList));
            return true;
        }
        return false;
    }

    /**
     * 查询节点详细数据(45H) 请求报文校验
     * @param c
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqT45HPdt(String c,StringBuffer cmdParam)throws Exception{
        if("".equals(c)) return false;
        List<Object> paramList= JSONArray.parseArray(cmdParam.toString());
        String pdtParam="";
        if(paramList.size()>0) {
            JSONArray paramValArray = JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
            for (int h = 0; h < paramValArray.size(); h++) {
                System.out.print("======解析'查询节点详细数据(45H)===Array:" + paramValArray.getString(h));
                pdtParam+=ConverUtil.MappCODEVal(paramValArray.getString(h),true);
                System.out.println("=====解析'查询节点详细数据(45H)==11111====cmdParam:" +pdtParam);
            }
            byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(pdtParam);
            int pdtLen=bytePdtArrs.length;
            if(!PLCValidateUtil.rangeInDefined(pdtLen,0,6)) {
                log.error("======解析'查询节点详细数据(45H)'请求的PDT 指令参数(ID)长度有错误！字节长度范围：0~6字节!");
                return false;
            }
            paramValArray.set(0,validateNodeID(c,bytePdtArrs));
            paramList=new JSONArray();
            paramList.add(paramValArray);
            cmdParam.replace(0,cmdParam.length(),JSONArray.toJSONString(paramList));
            return true;
        }
        return true;
    }

    public static boolean validateReqT96HPdt(String c,StringBuffer cmdParam) throws Exception{
        if("".equals(c)) return false;
        List<Object> paramList= JSONArray.parseArray(cmdParam.toString());
        Integer firstGroup=0;
        if(paramList.size()>0) {
            JSONArray paramValArray= JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
            firstGroup=paramValArray.getInteger(1); //组号
            for(int i=0;i<paramList.size();i++){
                paramValArray = JSONArray.parseArray(JSONArray.toJSONString(paramList.get(i)));
                Integer groupNum=paramValArray.getInteger(1); //组号
                if(!ConverUtil.rangeInDefined(groupNum,1,255)){ //组号范围:1~255
                   log.error("=======指令(96H),下发 节点时,组号超过了范围(1~255)!");
                   return false;
                }
                if("02H".equals(c)) { //下发 一组节点
                    if (firstGroup != groupNum) {
                        log.error("=======指令(96H),下发一组节点时,每个节点设置所属组号必须相同!");
                        return false;
                    }
                }//下发 全部节点(多组) 同上外 无其它校验
            }
            return true;
        }
        return false;
    }

    /**
     * 验证指令参数 节点ID
     * C = 01H:ID = 节点PHYID
     * C = 02H:6个Byte的最低Byte表示组号。(例如，控制第2组节点,ID=000000000002)
     * C = 03H:ID为全0 即 ID = 000000000000）
     * @param c 控制码
     * @param bytePdtArrs 节点ID 的指令参数
     * @return
     * @throws Exception
     */
    private static String validateNodeID(String c,byte[] bytePdtArrs)throws Exception{
        String phyID="";
        int pdtLen=bytePdtArrs.length;
        if("01H".equals(c) || "02H".equals(c)){
            if(pdtLen==0) throw new Exception("节点调光(42H)请求报文校验: 当C=01H或02H时,ID节点入参不能为空！");
            String ID=ConverUtil.convertByteToHexString(bytePdtArrs);
            System.out.println("======节点调光(42H)请求报文校验 ===控制码(C):"+c+", ===获取入参节点或组ID:"+ID);
            phyID=PLCValidateUtil.checkDeviceUID(ID);
            System.out.println("======节点调光(42H)请求报文校验 ====控制码(C):"+c+",===拼6个字节后的入参节点或组ID:"+phyID);
        }else if("03H".equals(c)){ //C=03H: ID为全0（即 ID = 000000000000）
            byte[] itemByteBuf=ConverUtil.hexStrToByteArr("000000000000");
            phyID=ConverUtil.convertByteToHexString(itemByteBuf);
            System.out.println("======节点调光(42H)请求报文校验 ==C=03H: ID为全0====控制码(C):"+c+",===:"+phyID);
        }
        System.out.println("======节点调光(42H)请求报文校验 ==phyID:"+phyID);
        return phyID;
    }
    
    /**
     * 设定电源时间(49H)
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqT49HPdt(String cmdParam) throws Exception {
        return true;
    }
    
    /**
     * 校验设定电源初始化值(4CH)
     * @param cmdParam
     * @return
     * @throws Exception
     */
    public static boolean validateReqT4CHPdt(String cmdParam) throws Exception {
        return true;
    }

	public static boolean validateReqT48HPdt(String cmdParam) {
		// TODO Auto-generated method stub
		return true;
	}

	public static boolean validateResT6DHPdt(String cmdParam) throws Exception {
		byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>242) throw new Exception(" 解析 '查询电源报警阙值(6DH)响应的PDT报文错误！字节长度超过242个字节!");
        return true;
	}

	public static boolean validateReqT6CHPdt(String cmdParam) {
		// TODO Auto-generated method stub
		return true;
	}

	public static boolean validateResT4AHPdt(String cmdParam) throws Exception {
		byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>242) throw new Exception(" 解析 '查询电源报警阙值(6DH)响应的PDT报文错误！字节长度超过242个字节!");
        return true;
	}

	public static boolean validateReqT9EHPdt(String cmdParam) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public static boolean validateResTF6HPdt(String cmdParam) throws Exception {
		byte[] bytePdtArrs=ConverUtil.hexStrToByteArr(cmdParam);
        if(bytePdtArrs.length>29) throw new Exception(" 解析 '报警使能查询(F6H)响应的PDT报文错误！字节长度超过29个字节!");
        return true;
	}
}
