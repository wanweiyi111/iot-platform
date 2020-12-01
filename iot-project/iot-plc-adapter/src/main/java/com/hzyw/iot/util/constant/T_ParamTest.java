package com.hzyw.iot.util.constant;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.*;

/**
 * 测试类
 * 指令测试参数封装
 */
public class T_ParamTest {
    private static final Logger log = LoggerFactory.getLogger(T_ParamTest.class);
    public static List getPdtParams(String cmd, List<Map<String,Object>> pdtMapList)throws Exception{
        List pdtList=new ArrayList<>();
        switch(cmd){
            case "70H"://集中器继电器开
                pdtList.add(new String[]{"03"});
                break;
            case "71H"://集中器继电器关
                pdtList.add(new String[]{"03"});
                break;
            case "73H": //查询集中器状态
                //pdtList.add(new String[]{"03"}); //不用传入参
                break;
            case "82H": //下发定时任务
                pdtList.add(new String[]{"200","040919","051119","02H","0","00H","5922","80","02H","12","42H","C8H","01H"});
                break;
            case "83H": //查询定时任务
                //pdtList.add(new String[]{"03"}); //不用传入参
                break;
            case "84H": //清除定时任务
                pdtList.add(new String[]{"03"});
                break;
            case "8CH": //设置集中器时间
                pdtList.add(new String[]{"03"});
                break;
            case "8EH": //设置集中器参数
                pdtList.add(new String[]{"03"});
                break;
            case "8FH": //查询集中器参数
                pdtList.add(new String[]{"03"});
                break;
            case "96H": {//下发节点
                pdtList=new ArrayList<>();
                String[] paramTemp = new String[]{"nodeID", "groupID", "devType"};
                try {
                    String[] paramTemps;
                    Map<String, Object> pdtMap;
                    for (int i = 0; i < pdtMapList.size(); i++) {
                        paramTemps=new String[]{};
                        pdtMap=new HashMap<String, Object>();
                        pdtMap = pdtMapList.get(i);
                        pdtMap.put("nodeID",StringUtils.lowerCase(PlcProtocolsBusiness.getPlcNodeSnByPlcNodeID(pdtMap.get("nodeID").toString())));
                        paramTemps = getPdtParamVal(paramTemp, pdtMap);
                        pdtList.add(paramTemps);
                    }
                } catch (Exception e) {
                    log.error("=========请求'下发节点'指令 的入参格式适配转换异常!");
                    throw new Exception("=========请求'下发节点'指令 的入参格式适配转换异常!"+e.getMessage());
                }
                break;
            }case "97H": //读取节点
                pdtList.add(new String[]{"03"});
                break;
            case "98H": //配置节点
                pdtList=new ArrayList<>();
                //pdtList.add(new String[]{"03"});
                break;
            case "99H": {//删除节点
                pdtList = new ArrayList<>();
                try {
                    String[] paramTemp = new String[]{"ID"};
                    String[] paramTemps = new String[]{};
                    Map<String, Object> pdtMap = new HashMap<String, Object>();
                    if (pdtMapList.size() > 0) {
                        pdtMap = pdtMapList.get(0);
                        paramTemps = getPdtParamVal(paramTemp, pdtMap);
                        pdtList.add(paramTemps);
                    }
                } catch (Exception e) {
                    log.error("=========请求'删除节点'指令 的入参格式适配转换异常!");
                    throw new Exception("=========请求'下发节点'指令 的入参格式适配转换异常!" + e.getMessage());
                }
                break;
            }case "F0H": //集中器登录
               // pdtList.add(new String[]{"03"});
                break;
            case "F1H": //集中器与主机保持连接心跳
                pdtList.add(new String[]{"03"});
                break;
            case "F2H": //系统控制
                pdtList.add(new String[]{"03"});
                break;
            case "F3H": //集中器报警
                pdtList.add(new String[]{"03"});
                break;
            case "F4H": //执行失败返回
                pdtList.add(new String[]{"03"});
                break;
            case "F5H": //报警能使设置
                pdtList.add(new String[]{"03"});
                break;
            case "F6H": //报警能使查询
                break;
            case "42H":{//节点调光
                pdtList=new ArrayList<>();
                Integer cmdType=0; //指令参数类型(0:关灯指令;1:开灯指令;2:调光灯指令)
                Map<String,Object> pdtMap=pdtMapList.get(0);
                String onoff= StringUtils.trimToNull(pdtMap.get("onoff")+"");  //0:关灯; 1: 开灯
                if("null".equals(onoff)){ //onoff为空,表示调光灯指令, 否则为 开关灯灯指令
                    cmdType=2;
                }else{
                    cmdType=Integer.parseInt(onoff);
                }
                String dim=StringUtils.trimToNull(pdtMap.get("level")+"");
                dim=cmdType==0?"0":cmdType==1?(ConverUtil.parseNumeric(dim)==0)?"100":dim:
                                              (ConverUtil.parseNumeric(dim)==0)?"0":dim;
                BigDecimal dimNum = new BigDecimal(dim);
                if(dimNum.compareTo(new BigDecimal(100))==1) dimNum=new BigDecimal(100);
                log.info("=================开关灯操作onoff(0:关灯,1:开灯):"+onoff+", 当前调光值:"+dim+",对应(0~100)值:"+dimNum);
                //System.out.println("=================开关灯操作onoff(0:关灯,1:开灯):"+onoff+", 当前调光值:"+dim+",对应(0~100)值:"+dimNum);

                dim=DecimalTransforUtil.toHexStr(String.valueOf(dimNum),1);
                log.info("=================开关灯操作onoff(0:关灯,1:开灯):"+onoff+", 当前调光转换后的16进制值:"+dim);
                //System.out.println("=================开关灯操作onoff(0:关灯,1:开灯):"+onoff+", 当前调光转换后的16进制值:"+dim);
                pdtMap.put("level",dim+"H");

                String[] paramTemp=new String[]{"ID","ab","level"};
                paramTemp=getPdtParamVal(paramTemp,pdtMap);
                pdtList.add(paramTemp);
                //pdtList.add(new String[]{"","03H","8CH"});
                break;
            }case "45H": {//查询节点详细数据
                pdtList=new ArrayList<>();
                String[] paramTemp=new String[]{"ID"};
                Map<String,Object> pdtMap=pdtMapList.get(0);
                paramTemp=getPdtParamVal(paramTemp,pdtMap);
                pdtList.add(paramTemp);
                break;
            }case "F7H": //主动上报节点数据
                pdtList=new ArrayList<>();
                //pdtList.add(new String[]{"03"});
                break;
            case "FBH": //查询和上传历史数据
                pdtList.add(new String[]{"01"});
                break;
            case "FCH": //设置集中器远程更新IP和端口
                pdtList.add(new String[]{"03"});
                break;
            case "FDH": //查询集中器远程更新IP和端口
                break;
            case "9AH": //查询集中器组网情况
                break;
            case "9BH": //查询集中器版本信息
                break;
            case "9CH": //PLC软件复位
                break;
            case "60H": //设置集中器继电器必须开启时间
                pdtList.add(new String[]{"01","2359","1023","2359","1023","01","2359","1023","2359","1023","01","2359","1023","2359","1023"});
                break;
            case "61H": //查询集中器继电器必须开启时间
                break;
            case "46H": //查询节点传感器信息
                pdtList.add(new String[]{"16777558"});
                break;
            case "FEH": //节点传感器主动上报信息
                pdtList.add(new String[]{"000001000156","82H","0101","14811793","0201","356","0301","08"});
                break;
            case "62H": //2480开始组网
                pdtList.add(new String[]{"03"});
                break;
            case "63H": //2480停止组网
                break;
            case "66H": //2480存储节点列表
                break;
            case "67H": //读取2480FLAH节点列表
                break;
            case "9EH": //增加单个节点
                pdtList.add(new String[]{"000001000156","245","82H","000001000157"});
                break;
            case "9DH": //删除单个节点
                pdtList.add(new String[]{"000001000156"});
                break;
            case "69H": //2480删除节点FLSH存储列表
                break;
            case "4AH": //查询集中器硬件信息
                break;
            case "F8H": //设置集中器服务器IP和端口
                pdtList.add(new String[]{"03"});
                break;
            case "F9H": //查询集中器服务器IP和端口
                break;
            case "6AH": //设定电源最大功率
                pdtList.add(new String[]{"000000000002","13"});
                break;
            case "6BH": //查询电源最大功率
                pdtList.add(new String[]{"000000000002"});
                break;
            case "6CH": //设定电源报警阀值
                pdtList.add(new String[]{"000000000112","220","210","215"});
                break;
            case "6DH": //查询电源报警阀值
                pdtList.add(new String[]{"000000000112"});
                break;
            case "6FH": //查询电源任务编号
                pdtList.add(new String[]{"000000000112"});
                break;
            case "47H": //删除电源任务编号
                pdtList.add(new String[]{"0000000001121003"});
                break;
            case "48H": //查询电源一条定时任务
                pdtList.add(new String[]{"000000000112","03"});
                break;
            case "49H": //设定电源时间
                pdtList.add(new String[]{"000000000112","19091731500"});
                break;
            case "4BH": //查询电源时间
                pdtList.add(new String[]{"000000000112"});
                break;
            case "4CH": //设定电源初始化值
                pdtList.add(new String[]{"000000000112","10","1000","200","100"});
                break;
        }
        return pdtList;
    }

    /**
     * 组装 下发请求的 指令入参
     * @param paramTemp
     * @param pdtMap
     * @return
     */
    private static String[] getPdtParamVal(String[] paramTemp,Map<String,Object> pdtMap){
        String[] paramTempResult=Arrays.copyOfRange(paramTemp,0,paramTemp.length);
        for(int i=0;i<paramTemp.length; i++){
            if(pdtMap.containsKey(paramTempResult[i])){
                Arrays.fill(paramTempResult,i,i+1,pdtMap.get(paramTempResult[i]));
            }else{
                continue;
                //Arrays.fill(paramTempResult,i,i+1,"");
            }
        }
        log.debug("==========getPdtParamVal==PLC 下发请求 组装后的 指令入参:"+ JSONObject.toJSONString(paramTemp));
        //System.out.println("==========getPdtParamVal==PLC 下发请求 组装后的 指令入参:"+ JSONObject.toJSONString(paramTemp));
        return paramTempResult;
    }
}
