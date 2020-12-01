package com.hzyw.iot.util.constant;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.utils.PlcProtocolsUtils;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.ResponseDataVO;
import org.apache.commons.lang3.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 指令码值 枚举集合
 */
public enum O_CODE_VAL{
    /* 集中器继电器开 */
    T70H("70",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception {
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 集中器继电器开(70H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),1);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length>1)throw new Exception("pdt param length is 1B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                Integer mesgCode=0; //0: 默认成功
                /*01H：集中器成功受理;02H：命令或数据格式无效;03H：集中器忙*/
                String mesageID=ProtocalAdapter.consumeRequestID(HEAD_TEMPLATE.getUID().concat("_70H")); //响应 消费对应 请求消息ID
                if(StringUtils.trimToNull(mesageID)==null)
                    throw new Exception("=======集中器继电器开(70H)指令, 应答时没有消费到缓存里对应的请求消息ID! 请检查该指令下发时是否设置了消息ID?");
                if (!PDTValidateUtil.validateResComPdt(cmdParam)){
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1);
                    mesgCode="2".equals(pdtMesg)?10005:20324;//02：失败 (命令或数据格式无效); 03：忙 (集中器忙)
                }
                System.out.println("==========集中器继电器开(70H) 响应结果 发KAFKA 操作......");
                PlcProtocolsUtils.plcACKResponseSend(HEAD_TEMPLATE.getUID(),"70H",null,mesgCode,mesageID,null,ctx);
                pdtMesg=""; //无需向设备响应结果
            }
            System.out.println("*************集中器继电器开(70H) 响应的PDT解析 结果:" +pdtMesg);
            return pdtMesg;
        }
    },
    /* 集中器继电器关 */
    T71H("71",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue())
            {
                @Override
                public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx) throws Exception{
                    if("".equals(cmdParam) || cmdParam==null) return "";
                    if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                        throw new Exception("====O_CODE_VAL 集中器继电器关(71H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
                    }
                    String pdtMesg="";
                    //区分 请求:true、响应:false 类型(80:响应;非80:请求)
                    boolean isRequest=!"80H".equals(c);
                    System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
                    if(isRequest){ //pdt 请求报文解析处理
                        // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                        List<Object> paramList= JSONArray.parseArray(cmdParam);
                        if(paramList.size()>0){
                            JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                            String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),1);
                            System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                            byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                            if(codeByte.length>1)throw new Exception("pdt param length is 1B!");
                            pdtMesg=hexStr;
                        }
                    }else { //pdt 响应报文解析处理
                        if (PDTValidateUtil.validateResComPdt(cmdParam)){
                            //对响应状态码，发kafka
                            System.out.println("==========集中器继电器关(71H) 响应结果 发KAFKA 操作......");
                            pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                        }else{
                            pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                        }
                    }
                    return pdtMesg;
                }
            },
    /* 查询集中器状态 */
    T73H("73",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx) throws Exception{
            /*
             * 参数 字节长度的二维数组模板（响应类型）
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
             * 三维：属性值单位【十进制转时:1、相电压(V)【11:毫伏电压(mV);12:AD路输入电压((mV))】,2、相电流(mA),3、相功率(W),4、相功率因数(%),5、电能(kWh)】;
             *                 二进制转时【转二进制保留位数,为0时 默认字节8位倍数 保留位数】
             * 注：请求模板 不考虑二进制位的转换
             */
            int[][] byteLenResTemp=new int[][]{{2,1,1,0},{2,1,1,0},{2,1,1,0},{2,1,2,0},{2,1,2,0},{2,1,2,0},{2,1,3,0},{2,1,3,0},
                    {2,1,3,0},{2,1,3,0},{1,1,4,0},{1,1,4,0},{1,1,4,0},{1,1,4,0},{3,1,5,0},
                    {1,2,3,0},{2,1,12,0},{2,1,12,0},{1,2,2,0}};

            //参数属性名模板
            String[] paramNameTemp=new String[] {"A相电压","B相电压","C相电压","A相电流","B相电流","C相电流","A相功率","B相功率","C相功率","总功率","PFa","PFb","PFc",
                    "PFs","电能","继电器状态","AD1路输入电压","AD2路输入电压","光耦输入电平"};

            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询集中器状态(73H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询集中器状态(73H),不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateResT73HPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"73H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======解析 查询集中器状态(73H) 响应PDT协议报文的 结果:" +pdtMesg);
                    //String mesageID=ProtocalAdapter.consumeRequestID(HEAD_TEMPLATE.getUID().concat("_73H")); //响应 消费对应 请求消息ID
                    //PlcProtocolsUtils.plcACKResponseSend(null,null,null,20324,null);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 下发定时任务 */
    T82H("82",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx) throws Exception{
            /*
             * 参数 字节长度的二维数组模板(请求类型)
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenReqTemp=new int[][]{{1,1},{3,1},{3,1},{1,2},{1,1},{1,2},{2,1},{1,1},{1,2},{1,1},{1,2},{1,2},{1,2}};
            /*
             * 参数 字节长度的二维数组模板（响应类型）
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenResTemp=new int[][]{{1,3,0,0},{250,1,0,0}};
            //参数属性名模板
            String[] paramNameTemp=new String[] {"ACK","TN"};

            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 下发定时任务(82H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT82HPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenReqTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateResT82HPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"82H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======解析 下发定时任务(82H) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                }else{
                    pdtMesg=ConverUtil.MappCODE(cmdParam);
                }
            }
            return pdtMesg;
        }
    },
    /* 查询定时任务 */
    T83H("83",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询定时任务(83H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询定时任务(83H),不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 清除定时任务 */
    T84H("84",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 清除定时任务(84H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 设置集中器时间 */
    T8CH("8c",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 设置集中器时间(8cH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 设置集中器参数 */
    T8EH("8e",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 设置集中器参数(8eH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 查询集中器参数 */
    T8FH("8f",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询集中器参数(8fH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 下发节点 */
    T96H("96",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            /*
             * 参数 字节长度的二维数组模板(请求类型)
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:进制转【1:默认为十六制不转换;11:十进制转十六进制】, 2:码映射，3：直接赋值】）
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenReqTemp=new int[][]{{6,1},{1,11},{1,2}};

            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 下发节点(96H)对应控制码指令是(80H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                StringBuffer paramBuffer=new StringBuffer(cmdParam);
                if (PDTValidateUtil.validateReqT96HPdt(c,paramBuffer)) {
                    cmdParam=paramBuffer.toString();
                    pdtMesg=PDTAdapter.pdtRequstParser(byteLenReqTemp,cmdParam);
                }
            }else { //pdt 响应报文解析处理
                Integer mesgCode=0; //0: 默认成功
                /*01H：集中器成功受理;02H：命令或数据格式无效;03H：集中器忙*/
                String mesageID=ProtocalAdapter.consumeRequestID(HEAD_TEMPLATE.getUID().concat("_96H")); //响应 消费对应 请求消息ID
                if (!PDTValidateUtil.validateResComPdt(cmdParam)){
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1);
                    mesgCode="2".equals(pdtMesg)?10005:20324;//02：失败 (命令或数据格式无效); 03：忙 (集中器忙)
                }
                if(mesgCode==0){ //批处理指令: 1、下发组节点(96H)成功后 >> 2、配置节点(98H)
                    String batchReqMessage=ProtocalAdapter.batchRequestCode("03H","98H","");
                    System.out.println("=====指令 批处理====下发组节点(96H)成功后>>配置节点(98H) 下发的报文:"+batchReqMessage);
                    PlcProtocolsUtils.plcRequestCMDSend(batchReqMessage,HEAD_TEMPLATE.getUID(),"98H",mesageID,ctx); //配置节点(98H)
                }else{
                    System.out.println("==========下发节点(96H) 响应结果(1:失败,2:忙):"+mesgCode+", 发KAFKA 操作......");
                    PlcProtocolsUtils.plcACKResponseSend(HEAD_TEMPLATE.getUID(),"96H",null,mesgCode,mesageID,null,ctx);
                }
                pdtMesg=""; //无需向设备响应结果
            }
            return pdtMesg;
        }
    },
    /* 读取节点 */
    T97H("97",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T03H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            /*
             * 参数 字节长度的多维数组模板（响应类型）
             * 一维：第个参数值的固定字节长度；
             * 二维：解析参数值方法【1:十进制转(11:有符号整型进制转; 12:浮点型进制转),2:二进制转 3:码映射，4：直接赋值】）
             * 三维：属性值单位【十进制转时:1、相电压(V)【11:毫伏电压(mV);12:AD路输入电压((mV))】,2、相电流(mA),3、相功率(W)【31:输入/输出功率(0.1W)】,4、相功率因数(%),5、电能(kWh),8、温度单位(.C),9、小时(h)】;
             *                 二进制转时【转二进制保留位数,为0时 默认字节8位倍数 保留位数】
             * 四维：统计属性字段【如： 节点总数、当前帧数、总帧数】0：默认不是，1：是
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenResTemp=new int[][]{{1,1,0,1},{1,1,0,1},{6,4,0,0},{1,1,0,0},{1,3,0,0}};

            //参数属性名模板
            String[] paramNameTemp=PLC_CONFIG.paramNameT97HTemp();

            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T03H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 读取节点(97H)对应控制码指令是(80H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                pdtMesg=""; //没有指令入参
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResT97HPdt(cmdParam)) {
                    //响应报文 调KAFKA发送(状态上报) 读取结果存数据库
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);

                    //处理 二进制位 属性名映射 解析
                    List<Map<String,Object>>pdtList= (List<Map<String, Object>>) pdtResposeVO.get("pdtList");
                    pdtResposeVO.put("pdtList",pdtList);

                    //响应报文解析后的结构
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"97H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======解析 主动上报节点数据(97H) 响应PDT协议报文的 结果:" +pdtMesg);
                    pdtMesg="";  //无需向设备响应结果
                }else{
                    pdtMesg=""; //无需向设备响应结果
                    System.out.println("======'主动上报节点数据'(97H)响应的PDT 校验 报文错误！02表示报文校验有错误; 03:系统忙:"+cmdParam);
                }
            }
            return pdtMesg;
        }
    },
    /* 配置节点 */
    T98H("98",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T03H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T03H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  配置节点(98H)对应控制码指令是(80H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                pdtMesg=""; //文档的定义 没有指令入参
            }else { //pdt 响应报文解析处理
                Integer mesgCode=0; //0: 默认成功
                /*01H：集中器成功受理;02H：命令或数据格式无效;03H：集中器忙*/
                String mesageID=ProtocalAdapter.consumeRequestID(HEAD_TEMPLATE.getUID().concat("_98H")); //响应 消费对应 请求消息ID
                if (!PDTValidateUtil.validateResComPdt(cmdParam)){
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1);
                    mesgCode="2".equals(pdtMesg)?10005:20324;//02：失败 (命令或数据格式无效); 03：忙 (集中器忙)
                }

                System.out.println("==========下发节点(98H) 响应结果(1:失败,2:忙):"+mesgCode+", 发KAFKA 操作......");
                PlcProtocolsUtils.plcACKResponseSend(HEAD_TEMPLATE.getUID(),"98H",null,mesgCode,mesageID,null,ctx);

                if(mesgCode==0){ //批处理指令: 2、配置节点(98H)成功后 >> 3、读取节点(97H) 存至数据库
                    String batchReqMessage=ProtocalAdapter.batchRequestCode("03H","97H","");
                    System.out.println("=====指令 批处理====配置节点(98H)成功后 >> 读取节点(97H) 下发的报文:"+batchReqMessage);
                    PlcProtocolsUtils.plcRequestCMDSend(batchReqMessage,HEAD_TEMPLATE.getUID(),"97H",null,ctx); //读取节点(97H)
                }
                pdtMesg=""; //无需向设备响应结果
            }
            return pdtMesg;
        }
    },
    /* 删除节点 */
    T99H("99",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            /*
             * 参数 字节长度的二维数组模板(请求类型)
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenReqTemp=new int[][]{{6,1}};

            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 删除节点(99H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                StringBuffer paramBuffer=new StringBuffer(cmdParam);
                if (PDTValidateUtil.validateReqT99HPdt(c,paramBuffer)) {
                    cmdParam=paramBuffer.toString();
                    pdtMesg=PDTAdapter.pdtRequstParser(byteLenReqTemp,cmdParam);
                }
            }else { //pdt 响应报文解析处理
                Integer mesgCode=0; //0: 默认成功
                /*01H：集中器成功受理;02H：命令或数据格式无效;03H：集中器忙*/
                String mesageID=ProtocalAdapter.consumeRequestID(HEAD_TEMPLATE.getUID().concat("_99H")); //响应 消费对应 请求消息ID
                if(StringUtils.trimToNull(mesageID)==null)
                    throw new Exception("=======删除节点(99H)指令, 应答时没有消费到缓存里对应的请求消息ID! 请检查该指令下发时是否设置了消息ID?");
                if (!PDTValidateUtil.validateResComPdt(cmdParam)){
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1);
                    mesgCode="2".equals(pdtMesg)?10005:20324;//02：失败 (命令或数据格式无效); 03：忙 (集中器忙)
                }
                System.out.println("==========删除节点(99H) 响应结果 发KAFKA 操作......");
                PlcProtocolsUtils.plcACKResponseSend(HEAD_TEMPLATE.getUID(),"99H",null,mesgCode,mesageID,null,ctx);
                pdtMesg=""; //无需向设备响应结果
            }
            return pdtMesg;
        }
    },
    /* 集中器登录 集中器->主机 */
    F0H("f0",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T04H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T04H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  集中器登录(f0H)对应控制码指令是(80H或04H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T04H.getValue())?false:true;  //04:集中器登录
            System.out.println("======goin ==pdt pdtData 方法,控制码:("+c+") 集中器登录数据类型(04码:true、非04码:false):"+isRequest);
            if(isRequest){ //pdt 集中器登陆主机
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("集中器登录(F0H),不用传 请求指令参数入参!");
            }else { //pdt 主机响应登陆
                if (PDTValidateUtil.validateResF0HPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========集中器登录(F0H) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            System.out.println("========pdt模析解析【集中器登录】 返回出参："+cmdParam);
            //
            return pdtMesg;
        }
    },
    /* 集中器与主机保持连接心跳 集中器->主机 */
    F1H("f1",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 集中器与主机保持连接心跳(f1H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!"80H".equals(c);  //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 系统控制 */
    F2H("f2",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  系统控制(f2H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!"80H".equals(c);  //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 集中器报警 集中器->主机 */
    F3H("f3",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 集中器报警(f3H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!"80H".equals(c);  //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 执行失败返回 */
    F4H("f4",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T04H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T04H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  执行失败返回(f4H)对应控制码指令是(80H或04H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T04H.getValue())?false:true;
            System.out.println("======goin ==pdt pdtData 方法,控制码:("+c+") 执行失败返回数据类型(04码:true、非04码:false):"+isRequest);
            if(isRequest){
                //todo pdt 执行失败返回报文解析处理
            }
            return pdtMesg;
        }
    },
    /* 报警能使设置 */
    F5H("f5",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  报警能使设置(f5H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!"80H".equals(c);  //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /*报警使能查询(F6H) */
    F6H("f6",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  报警使能查询(F6H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            boolean isRequest=!"80H".equals(c);  //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 节点调光 */
    T42H("42",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            /*
             * 参数 字节长度的二维数组模板(请求类型)
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenReqTemp=new int[][]{{6,1},{1,2},{1,2}};

            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 节点调光(42H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                StringBuffer paramBuffer=new StringBuffer(cmdParam);
                if (PDTValidateUtil.validateReqT42HPdt(c,paramBuffer)) {
                    cmdParam=paramBuffer.toString();
                    pdtMesg=PDTAdapter.pdtRequstParser(byteLenReqTemp,cmdParam);
                }
            }else { //pdt 响应报文解析处理
                Integer mesgCode=0; //0: 默认成功
                /*01H：集中器成功受理;02H：命令或数据格式无效;03H：集中器忙*/
                String mesageID=ProtocalAdapter.consumeRequestID(HEAD_TEMPLATE.getUID().concat("_42H")); //响应 消费对应 请求消息ID
                if(StringUtils.trimToNull(mesageID)==null)
                    throw new Exception("=======节点调光(42H)指令, 应答时没有消费到缓存里对应的请求消息ID! 请检查该指令下发时是否设置了消息ID?");
                if (!PDTValidateUtil.validateResComPdt(cmdParam)){
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1);
                    mesgCode="2".equals(pdtMesg)?10005:20324;//02：失败 (命令或数据格式无效); 03：忙 (集中器忙)
                }
                System.out.println("==========节点调光(42H) 响应结果 发KAFKA 操作......");
                PlcProtocolsUtils.plcACKResponseSend(HEAD_TEMPLATE.getUID(),"42H",null,mesgCode,mesageID,null,ctx);
                pdtMesg=""; //无需向设备响应结果
            }
            return pdtMesg;
        }
    },
    /* 主动上报节点数据 集中器->主机 */
    F7H("f7",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T04H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            /*
             * 参数 字节长度的多维数组模板（响应类型）
             * 一维：第个参数值的固定字节长度；
             * 二维：解析参数值方法【1:十进制转(11:有符号整型进制转; 12:浮点型进制转),2:二进制转 3:码映射，4：直接赋值】）
             * 三维：属性值单位【十进制转时:1、相电压(V)【11:毫伏电压(mV);12:AD路输入电压((mV))】,2、相电流(mA),3、相功率(W)【31:输入/输出功率(0.1W)】,4、相功率因数(%),5、电能(kWh),8、温度单位(.C),9、小时(h)】;
             *                 二进制转时【转二进制保留位数,为0时 默认字节8位倍数 保留位数】
             * 四维：统计属性字段【如： 节点总数、当前帧数、总帧数】0：默认不是，1：是
             * 请求模板 不考虑二进制位的转换
             */
            //非路灯控制器设备【路灯电源】 老程序
            int[][] byteLenResTemp_OLD=new int[][]{{1,1,0,1},{6,4,0,0},{1,3,0,0},{1,3,0,0},{2,1,11,0},{2,1,21,0},{2,1,31,0},{1,1,4,0},
                    {2,2,16,0},{1,1,0,0}};
            //非路灯控制器设备【路灯电源】 新程序
            int[][] byteLenResTemp_NEW=new int[][]{{1,1,0,1},{6,4,0,0},{1,3,0,0},{1,3,0,0},{2,1,11,0},{2,1,21,0},{2,1,31,0},{1,1,4,0},
                    {2,2,16,0},{1,1,0,0},{1,11,8,0},{2,1,31,0},{2,1,9,0},{2,1,5,0},{2,1,9,0}};

            //状态: 位(bit)属性名模板
            Object[][] stateBitTemp=PLC_CONFIG.StateBitTemp();
            //参数属性名模板
            Object[][] paramNameTemp=PLC_CONFIG.paramNameTF7HTemp(byteLenResTemp_OLD,byteLenResTemp_NEW);

            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T04H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  主动上报节点数据(f7H)对应控制码指令是(80H或04H), 传入的控制码错误!:"+c);
            }

            String pdtMesg="";
            boolean isRequest=!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T04H.getValue())?false:true; //04:集中器主动上报数据
            System.out.println("======goin ==pdt pdtData 方法,控制码:("+c+") 是否集中器主动上报数据类型(04码:true、非04码:false):"+isRequest);
            if(isRequest){ //pdt 集中器主动上报解析处理
                AtomicInteger indexNum=new AtomicInteger(0);
                if (PDTValidateUtil.validateResF7HPdt(cmdParam,indexNum)) {
                    String[] paramNamesTemp= (String[]) paramNameTemp[indexNum.intValue()][0];
                    int[][] byteLenResTemp= (int[][]) paramNameTemp[indexNum.intValue()][1];
                    //响应报文 调KAFKA发送(状态上报)
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNamesTemp,cmdParam,ctx);

                    //处理 二进制位 属性名映射 解析
                    List<Map<String,Object>>pdtList= (List<Map<String, Object>>) pdtResposeVO.get("pdtList");
                    pdtResposeVO.put("pdtList",pdtList);
                    //响应报文 调KAFKA发送(信号上报)
                    BitResposeParser(pdtList,stateBitTemp,ctx);

                    //响应报文解析后的结构
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"F7H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======解析 主动上报节点数据(f7H) 响应PDT协议报文的 结果:" +pdtMesg);
                    pdtMesg="";  //无需向设备响应结果
                }else{
                    pdtMesg=""; //无需向设备响应结果
                    System.out.println("======'主动上报节点数据'(F7H)响应的PDT 校验 报文错误！02表示报文校验有错误; 03:系统忙:"+cmdParam);
                }
            }
            return pdtMesg;
        }
    },
    /* 查询节点详细数据 【注： 这个指令 属性名映射需求 复杂 代码有点问题， 后面待细化处理】*/
    T45H("45",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            /*
             * 参数 字节长度的二维数组模板(请求类型)
             * 一维：第个参数值的固定字节长度；
             * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenReqTemp=new int[][]{{6,1}};

            /*
             * 参数 字节长度的多维数组模板（响应类型）
             * 一维：第个参数值的固定字节长度；
             * 二维：解析参数值方法【1:十进制转(11:有符号整型进制转; 12:浮点型进制转),2:二进制转 3:码映射，4：直接赋值】）
             * 三维：属性值单位【十进制转时:1、相电压(V)【11:毫伏电压(mV)要换算的;12:AD路输入电压((mV)】,2、相电流(mA)【21:输入/输出毫安电流 不要换算】,3、相功率(W)【31:输入/输出功率(0.1W)】,4、相功率因数(%)【41:AB路亮度(%) 10进制除2换算】,5、电能(kWh),8、温度单位(.C),9、小时(h)】;
             *                 二进制转时【转二进制保留位数,为0时 默认字节8位倍数 保留位数】
             * 四维：统计属性字段【如： 节点总数、当前帧数、总帧数】0：默认不是，1：是
             * 请求模板 不考虑二进制位的转换
             */
            int[][] byteLenResTemp_OLD=new int[][]{{6,4,0,0},{1,3,0,0},{1,3,0,0},{1,1,8,0},{2,1,11,0},{2,1,11,0},{2,1,21,0},{2,1,21,0},{2,1,21,0},{2,1,31,0},
                    {2,1,31,0},{1,1,4,0},{1,1,4,0},{1,1,41,0},{1,1,41,0},{2,2,16,0},{1,1,0,0}};

            /*int[][] byteLenResTemp_OLD=new int[][]{{6,1,0,0},{1,3,0,0},{1,3,0,0},{1,1,8,0},{2,1,11,0},{2,1,12,0},{2,1,21,0},{2,1,21,0},
                                                   {2,1,31,0},{1,1,4,0},{1,1,41,0},{2,2,16,0},{1,1,0,0}};
            int[][] byteLenResTemp_OLD=new int[][]{{6,4,0,0},{1,3,0,0},{1,3,0,0},{1,1,8,0},{2,1,11,0},{2,1,11,0},{2,1,21,0},{2,1,21,0},
                    {1,1,4,0},{1,1,41,0},{2,2,16,0},{1,1,0,0}}; //{2,1,31,0}, */
            int[][] byteLenResTemp_NEW=new int[][]{{6,4,0,0},{1,3,0,0},{1,3,0,0},{1,1,8,0},{2,1,11,0},{2,1,11,0},{2,1,21,0},{2,1,21,0},
                    {2,1,31,0},{1,1,4,0},{1,1,41,0},{2,2,16,0},{1,1,0,0},
                    {1,11,8,0},{2,1,31,0},{2,1,9,0},{2,1,5,0},{2,1,9,0}};
            int[][] byteLenResTemp_SINGLE=new int[][]{{6,4,0,0},{1,3,0,0},{1,3,0,0},{1,1,8,0},{2,1,11,0},{2,1,21,0},{2,1,31,0},{1,1,4,0},
                    {1,1,41,0},{2,2,16,0},{1,1,0,0}};
            int[][] byteLenResTemp_DOUBLE=new int[][]{{6,4,0,0},{1,3,0,0},{1,3,0,0},{1,1,8,0},{2,1,11,0},{2,1,21,0},{2,1,21,0},
                    {2,1,4,0},{2,1,4,0},{1,1,41,0},{1,1,41,0},{2,2,16,0},{1,1,0,0}};
            //状态: 位(bit)属性名模板88
            Object[][] stateBitTemp=PLC_CONFIG.StateBitTemp();
            //参数属性名模板
            Object[][] paramNameTemp=PLC_CONFIG.paramNameT45HTemp(byteLenResTemp_OLD,byteLenResTemp_NEW,byteLenResTemp_SINGLE,byteLenResTemp_DOUBLE);

            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 查询节点详细数据(45H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                StringBuffer paramBuffer=new StringBuffer(cmdParam);
                if (PDTValidateUtil.validateReqT45HPdt(c,paramBuffer)) {
                    cmdParam=paramBuffer.toString();
                    pdtMesg=PDTAdapter.pdtRequstParser(byteLenReqTemp,cmdParam);
                }
            }else { //pdt 响应报文解析处理
                AtomicInteger indexNum=new AtomicInteger(0);
                if (PDTValidateUtil.validateResT45HPdt(cmdParam,indexNum)) {
                    String[] paramNamesTemp= (String[]) paramNameTemp[indexNum.intValue()][0];
                    int[][] byteLenResTemp= (int[][]) paramNameTemp[indexNum.intValue()][1];
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNamesTemp,cmdParam,ctx);

                    //处理 二进制位 属性名映射 解析
                    List<Map<String,Object>>pdtList= (List<Map<String, Object>>) pdtResposeVO.get("pdtList");
                    pdtResposeVO.put("pdtList",pdtList);
                    BitResposeParser(pdtList,stateBitTemp,ctx);

                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"45H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======解析 查询节点详细数据(45H) 响应PDT协议报文的 结果:" +pdtMesg);
                    //调KAFKA发送 json报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：失败; 03H：主机忙
                }
                pdtMesg=""; //无需向设备响应结果
            }
            return pdtMesg;
        }
    },
    /* 查询和上传历史数据 */
    FBH("fb",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询和上传历史数据(fbH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),1);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 1)throw new Exception("pdt param length is 1B!");
                    pdtMesg=hexStr;
                }
            }
            return pdtMesg;
        }
    },
    /* 设置集中器远程更新IP和端口 */
    FCH("fc",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  设置集中器远程更新IP和端口(fcH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设置集中器远程更新IP和端口（FCH）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询集中器远程更新IP和端口 */
    FDH("fd",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询集中器远程更新IP和端口(fdH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询集中器远程更新IP和端口（FDH）,不用传 请求指令参数入参!");

            }else { //pdt 响应报文解析处理
            }
            return pdtMesg;
        }
    },
    /* 查询集中器组网情况 */
    T9AH("9a",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询集中器组网情况(9aH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询集中器组网情况（9AH）,不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
            }
            return pdtMesg;
        }
    },
    /* 查询集中器版本信息 */
    T9BH("9b",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询集中器版本信息(9bH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询集中器版本信息（9BH）,不用传 请求指令参数入参!");
            }else {
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压】; 二进制转时:转二进制保留位数
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{4,1,0,0},{2,1,0,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"PLC_ID","PLC版本"};
                if (PDTValidateUtil.validateResT9BHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"9BH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询集中器版本信息  (9BH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* PLC软件复位 */
    T9CH("9c",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  PLC软件复位(9cH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("PLC软件复位（9CH）,不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========PLC软件复位（9CH）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 设置集中器继电器必须开启时间 */
    T60H("60",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  PLC软件复位(60H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenTemp=new int[][]{{1,1},{2,1},{2,1},{2,1},{2,1},{1,1},{2,1},{2,1},{2,1},{2,1},{1,1},{2,1},{2,1},{2,1},{2,1}};
                if (PDTValidateUtil.validateReqT82HPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenTemp,cmdParam);
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设置集中器继电器必须开启时间（60H）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询集中器继电器必须开启时间 */
    T61H("61",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询集中器继电器必须开启时间(61H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询集中器继电器必须开启时间（61H）,不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压】; 二进制转时:转二进制保留位数
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{1,1,0,0},{2,1,9,0},{2,1,9,0},{2,1,9,0},{2,1,9,0},
                        {1,1,0,0},{2,1,9,0},{2,1,9,0},{2,1,9,0},{2,1,9,0},{1,1,0,0},{2,1,9,0},{2,1,9,0},{2,1,9,0},{2,1,9,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"继电器1使能位","继电器1必须打开开始时间","继电器1必须打开结束时间","继电器1必须关闭开始时间","继电器1必须关闭结束时间",
                        "继电器2使能位","继电器2必须打开开始时间","继电器2必须打开结束时间","继电器2必须关闭开始时间","继电器2必须关闭结束时间","继电器3使能位","继电器3必须打开开始时间",
                        "继电器3必须打开结束时间","继电器3必须关闭开始时间","继电器3必须关闭结束时间"};
                if (PDTValidateUtil.validateResTFEHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"61H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询集中器继电器必须开启时间 (61H) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询节点传感器信息 */
    T46H("46",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if(!"80H".equals(c) && !"01H".equals(c) && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 查询节点传感器信息(46H)对应控制码指令是(80H或01H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),6);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 6)throw new Exception("pdt param length is 6B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压】; 二进制转时:转二进制保留位数
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{6,1,0,0},{1,3,0,0},{2,1,0,0},{4,1,0,0},{2,1,0,0},{4,1,0,0},{2,1,0,0},{4,1,0,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"节点ID","设备码","节点传感器A编码","传感器A数据","节点传感器B编码","传感器B数据","节点传感器C编码","传感器C数据"};
                if (PDTValidateUtil.validateResTFEHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"46H", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询节点传感器信息(46H) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 节点传感器主动上报信息 集中器->主机 */
    FEH("FE",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"01H".equals(c) && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 查询节点传感器信息(feH)对应控制码指令是(80H或01H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压】; 二进制转时:转二进制保留位数
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{6,1,0,0},{1,3,0,0},{2,1,0,0},{4,1,0,0},{2,1,0,0},{4,1,0,0},{2,1,0,0},{4,1,0,0}};
                //参数属性名模板
                String[] paramNameTemp=new String[] {"节点ID","设备码","节点传感器A编码","传感器A数据","节点传感器B编码","传感器B数据","节点传感器C编码","传感器C数据"};

                if (PDTValidateUtil.validateResTFEHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"FEH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======节点传感器主动上报信息 (FEH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 2480开始组网 */
    T62H("62",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  2480开始组网(62H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);

            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),1);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 1)throw new Exception("pdt param length is 1B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========2480开始组网（62H）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 2480停止组网 */
    T63H("63",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  2480停止组网(63H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception(" 2480停止组网（63H）,不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========2480停止组网（63H）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 2480存储节点列表 */
    T66H("66",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  2480存储节点列表(66H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception(" 2480存储节点列表 （66H）,不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========2480存储节点列表 （66H）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 读取2480FLAH节点列表 */
    T67H("67",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  读取2480FLAH节点列表(67H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception(" 读取2480FLAH节点列表（67H）,不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========读取2480FLAH节点列表（67H）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 增加单个节点 */
    T9EH("9e",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  增加单个节点(9eH)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
                /*
                 * 参数 字节长度的二维数组模板
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenTemp=new int[][]{{6,1},{1,2},{1,2},{6,1}};
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT9EHPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========增加单个节点(9EH）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 删除单个节点 */
    T9DH("9d",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 删除单个节点(9dH)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),6);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 6)throw new Exception("pdt param length is 6B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========删除单个节点(9DH）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 2480删除节点FLSH存储列表 */
    T69H("69",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 2480删除节点FLSH存储列表(69H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception(" 设置PLC-2480删除存储节点列表指令（69H）,不用传 请求指令参数入参!");

            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设置PLC-2480删除存储节点列表指令（69H）响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询集中器硬件信息 */
    T4AH("4a",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询集中器硬件信息(4aH)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception(" 查询集中器硬件信息(4AH),不用传 请求指令参数入参!");
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板（响应类型）
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压,9、时分】; 二进制转时【转二进制保留位数】
                 * 注：请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{1,1,0,0},{1,1,0,0},{8,1,0,0},{1,1,0,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"通信方式","联网方式","软件版本号","电表型号与接线方式"};
                if (PDTValidateUtil.validateResT4AHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"4AH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询集中器硬件信息 (4AH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 设置集中器服务器IP和端口 */
    F8H("f8",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 设置集中器服务器IP和端口(f8H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理

            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设置集中器服务器IP和端口(F8H) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询集中器服务器IP和端口 */
    F9H("f9",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T00H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T00H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询集中器服务器IP和端口(f9H)对应控制码指令是(80H或00H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){
                //该指令 文档没有定义指令参数 ,此处无代码逻辑处理
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0) throw new Exception("查询集中器服务器IP和端口(F9H),不用传 请求指令参数入参!");

            }else { //pdt 响应报文解析处理


            }
            return pdtMesg;
        }
    },
    /* 设定电源最大输出功率 */
    T6AH("6a",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 设定电源最大输出功率(6aH)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                /*
                 * 参数 字节长度的二维数组模板(请求类型)
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenReqTemp=new int[][]{{6,1},{2,1}};
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT6CHPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenReqTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设定电源最大功率(6AH) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询电源最大功率 */
    T6BH("6b",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询电源最大功率(6bH)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),6);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 6)throw new Exception("pdt param length is 6B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板（响应类型）
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压,9、时分】; 二进制转时【转二进制保留位数】
                 * 注：请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{6,1,0,0},{2,1,3,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"ID","电源最大功率"};
                if (PDTValidateUtil.validateResT6DHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"6BH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询电源报警阀值(6DH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 设定电源报警阀值 */
    T6CH("6c",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 设定电源报警阀值(6cH)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                /*
                 * 参数 字节长度的二维数组模板(请求类型)
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenReqTemp=new int[][]{{6,1},{2,1},{2,1},{2,1}};
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT6CHPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenReqTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设定电源报警阀值(6CH) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            System.out.println("************设定电源报警阀值(6CH) 响应的PDT解析 结果:" +pdtMesg);
            return pdtMesg;
        }
    },
    /* 查询电源报警阀值 */
    T6DH("6d",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询电源报警阀值(6dH)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),6);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 6)throw new Exception("pdt param length is 6B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板（响应类型）
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压,9、时分】; 二进制转时【转二进制保留位数】
                 * 注：请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{6,1,0,0},{2,1,1,0},{2,1,1,0},{2,1,1,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"ID","输入电压最小值","输入电压最大值","输出电压最大值"};
                if (PDTValidateUtil.validateResT6DHPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"6DH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询电源任务编号(6FH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 查询电源任务编号 */
    T6FH("6f",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL 查询电源任务编号(6fH)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),6);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 6)throw new Exception("pdt param length is 6B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板（响应类型）
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压,9、时分】; 二进制转时【转二进制保留位数】
                 * 注：请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{6,1,0,0},{1,1,0,0},{1,1,0,0},{1,1,0,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"ID","任务总数","任务编号1","任务编号2"};
                if (PDTValidateUtil.validateResT73HPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"6FH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询电源任务编号(6FH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 删除电源任务编号 */
    T47H("47",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 删除电源任务编号(47H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),8);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 8)throw new Exception("pdt param length is 8B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========删除电源任务编号(47H) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            System.out.println("*************删除电源任务编号(47H) 响应的PDT解析 结果:" +pdtMesg);
            return pdtMesg;
        }
    },
    /* 查询电源一条定时任务 */
    T48H("48",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询电源一条定时任务(48H)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                /*
                 * 参数 字节长度的二维数组模板(请求类型)
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenReqTemp=new int[][]{{6,1},{1,1}};
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT48HPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenReqTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理

            }
            return pdtMesg;
        }
    },
    /* 设定电源时间 */
    T49H("49",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 设定电源时间(49H)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板(请求类型)
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenReqTemp=new int[][]{{6,1},{7,1}};
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT49HPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenReqTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设定电源时间(49H) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            System.out.println("*************设定电源时间(49H) 响应的PDT解析 结果:" +pdtMesg);
            return pdtMesg;
        }
    },
    /* 查询电源时间 */
    T4BH("4b",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.T01H.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) throw new Exception("入参不能为空！");
            if(!ConverUtil.MappCODEVal(c).equals(C_CODE_VAL.T01H.getValue()) && !"80H".equals(c)){
                throw new Exception("====O_CODE_VAL  查询电源时间(4bH)对应控制码指令是(80H或01H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                // 控制对象	Bit0- Bit2 每一位对应一路继电器。为1有效	1B
                List<Object> paramList= JSONArray.parseArray(cmdParam);
                if(paramList.size()>0){
                    JSONArray paramValArray=JSONArray.parseArray(JSONArray.toJSONString(paramList.get(0)));
                    String hexStr=DecimalTransforUtil.toHexStr(paramValArray.getString(0),6);
                    System.out.println("*************遍历字节长度 二维数组值 转换16进制结果:" +hexStr);
                    byte[] codeByte=ConverUtil.hexStrToByteArr(hexStr); //16进制值的字符串转换为byte数组
                    if(codeByte.length != 6)throw new Exception("pdt param length is 6B!");
                    pdtMesg=hexStr;
                }
            }else { //pdt 响应报文解析处理
                //校验pdt参数（规则依据文档）
                /*
                 * 参数 字节长度的二维数组模板（响应类型）
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:十进制转,2:二进制转 3:码映射，4：直接赋值】）
                 * 三维：属性值单位【十进制转时:1、相电压,2、相电流,3、相功率,4、相功率因数,5、电能,6、AD输入电压,9、时分】; 二进制转时【转二进制保留位数】
                 * 注：请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenResTemp=new int[][]{{6,1,0,0},{7,1,0,0},{2,1,9,0},{2,1,9,0}};

                //参数属性名模板
                String[] paramNameTemp=new String[] {"ID","年月日周时分秒","日出时分","日落时分"};
                if (PDTValidateUtil.validateResT73HPdt(cmdParam)) {
                    LinkedHashMap<String,Object> pdtResposeVO=PDTAdapter.pdtResposeParser(byteLenResTemp,paramNameTemp,cmdParam,ctx);
                    MessageVO<ResponseDataVO> ResultMesssage=T_MessageResult.getResponseVO(HEAD_TEMPLATE.getUID(),c,"4BH", pdtResposeVO);
                    pdtMesg=JSONObject.toJSONString(ResultMesssage);
                    System.out.println("======查询电源时间(4BH) 响应PDT协议报文的 结果:" +pdtMesg);
                    //封装成指定JSON结构返回 或 调KAFKA发送报文
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr("02",1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    },
    /* 设定电源初始化值 */
    T4CH("4c",HEAD_TEMPLATE.T.toString(),C_CODE_VAL.TxxH.getValue()){
        @Override
        public String pdtData(String c,String cmdParam,ChannelHandlerContext ctx)throws Exception{
            if("".equals(cmdParam) || cmdParam==null) return "";
            if(!"80H".equals(c) && !"01H".equals(c) && !"02H".equals(c)  && !"03H".equals(c) ){
                throw new Exception("====O_CODE_VAL 设定电源初始化值(4cH)对应控制码指令是(80H或01H或02H或03H), 传入的控制码错误!:"+c);
            }
            String pdtMesg="";
            //区分 请求:true、响应:false 类型(80:响应;非80:请求)
            boolean isRequest=!"80H".equals(c);
            System.out.println("=========goin ==pdt pdtData 方法 是否请求类型(请求:true、响应:false):"+isRequest);
            if(isRequest){ //pdt 请求报文解析处理
                /*
                 * 参数 字节长度的二维数组模板(请求类型)
                 * 一维：第个参数值的固定字节长度；
                 * 二维： 解析参数值方法【1:进制转, 2:码映射，3：直接赋值】）
                 * 请求模板 不考虑二进制位的转换
                 */
                int[][] byteLenReqTemp=new int[][]{{6,1},{1,1},{2,1},{2,1},{2,1}};
                //校验pdt参数（规则依据文档）
                if (PDTValidateUtil.validateReqT4CHPdt(cmdParam)) {
                    return PDTAdapter.pdtRequstParser(byteLenReqTemp, cmdParam);
                }
            }else { //pdt 响应报文解析处理
                if (PDTValidateUtil.validateResComPdt(cmdParam)){
                    //对响应状态码，发kafka
                    System.out.println("==========设定电源初始化值(4CH) 响应结果 发KAFKA 操作......");
                    pdtMesg=ConverUtil.MappCODEVal("01H"); //01H：登陆成功;
                }else{
                    pdtMesg=DecimalTransforUtil.toHexStr(cmdParam,1); //02H：登陆失败; 03H：主机忙
                }
            }
            return pdtMesg;
        }
    };

    private String value; //指令码值
    private String message; //组装的协义码串
    private String code; //控制码
    O_CODE_VAL(String value,String message,String code) {
        this.value = value;
        this.message=message;
        this.code=code;
    }

    public String getValue() {
        return value;
    }


    public String getMessage() {
        return message;
    }

    /**
     * 根据指令获取相应请求报文
     * @param cmd 指令码 (带H后缀的 如：42H)
     * @param code 控制码 为空时:指令对应固定控制码, 不为空时:指令对应动态的控制码  (带H后缀的 如：03H)
     * @return
     */
    public static String OrderMethod(String cmd,String code){
        String resMessage="";
        try {
            code= StringUtils.lowerCase(ConverUtil.MappCODEVal(code)); //去掉H后缀, 如:00H->00
            cmd= StringUtils.upperCase(ConverUtil.MappCODEVal(cmd));
            if (!"".equals(cmd)) cmd=cmd.concat("H"); else return "";
            for (O_CODE_VAL cf : O_CODE_VAL.values()) {
                if (cf.name().endsWith(cmd)) {
                    code="".equals(code) || code==null? cf.code:code;
                    System.out.println("查询到 "+cf.name()+" 的对应指令码值： " + cf.value);
                    System.out.println("查询到 *******HEAD_TEMPLATE L： " + HEAD_TEMPLATE.getL());
                    System.out.println("查询到 HEAD_TEMPLATE PDT： " + HEAD_TEMPLATE.getPDT());
                    System.out.println("查询到 *******HEAD_TEMPLATE CS： " + HEAD_TEMPLATE.getCS());
                    System.out.println("查询到 C_CODE_VAL C： " +code);
                    System.out.println("查询到 HEAD_TEMPLATE UID： " + HEAD_TEMPLATE.getUID());
                    resMessage=String.format(cf.message,HEAD_TEMPLATE.getUID(),code,HEAD_TEMPLATE.getL(),cf.value,HEAD_TEMPLATE.getPDT(),HEAD_TEMPLATE.getCS());
                    System.out.println("根据指令获取相应组装请求报文:" + resMessage);
                    break;
                }
            }
            System.out.println("查询指令码 end");
        } catch (Exception e) {
            System.out.println("根据指令获取相应请求报文 异常!"+e.getMessage());
            return resMessage;
        }
        return resMessage;
    }

    /**
     * 查询指令码值
     * @param cmd
     * @return
     */
    public static String CmdValueMethod(String cmd){
        String resMessage="";
        try {
            cmd= StringUtils.upperCase(ConverUtil.MappCODEVal(cmd));
            if (!"".equals(cmd)) cmd=cmd.concat("H"); else return resMessage;
            for (O_CODE_VAL cf : O_CODE_VAL.values()) {
                if (cf.name().endsWith(cmd)) {
                    resMessage=cf.value;
                    System.out.println("查询到 "+cf.name()+" 的对应指令码值： " + cf.value);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("查询指令码值 异常!"+e.getMessage());
            return resMessage;
        }
        return resMessage;
    }

    /**
     * 根据指令码查询对应控制码
     * @param cmd 带H后缀的 如：F7H
     * @return
     * @throws Exception
     */
    public static String CodeNameMethod(String cmd)throws Exception{
        String resMessage="";
        cmd= StringUtils.upperCase(ConverUtil.MappCODEVal(cmd));
        if (!"".equals(cmd)) cmd=cmd.concat("H"); else return resMessage;
        for (O_CODE_VAL cf : O_CODE_VAL.values()) {
            if (cf.name().endsWith(cmd)) {
                resMessage=(!"".equals(cf.code) && cf.code!=null)?ConverUtil.MappCODE(cf.code):null;
                System.out.println("查询到指令码 "+cf.name()+" 的对应控制码:" + resMessage);
                break;
            }
        }
        return resMessage;
    }

    /**
     * 生成相应指令参数的16进制值
     * @param ccode
     * @param cmd
     * @param cmdParam
     * @return
     */
    public static String PDTTemplate(String ccode, String cmd, String cmdParam, ChannelHandlerContext ctx)throws Exception{
        String resMessage="";
        cmd= StringUtils.upperCase(ConverUtil.MappCODEVal(cmd));
        if (!"".equals(cmd)) cmd=cmd.concat("H"); else return resMessage;
        for (O_CODE_VAL cf : O_CODE_VAL.values()) {
            if (cf.name().endsWith(cmd)) {
                resMessage=cf.pdtData(ccode,cmdParam,ctx);
                break;
            }
        }
        return resMessage;
    }

    /**
     * 二进制 (位->属性名)模板 映射解析
     * @param pdtList
     * @param bitTemp
     * @return
     * @throws Exception
     */
    private static void BitResposeParser(List<Map<String,Object>> pdtList,Object[][] bitTemp,ChannelHandlerContext ctx)throws Exception{
        List<Map>devSignalList=null; //设备信号上报的数据LIST
        Map<String,Object>devSignalMap=null;  //设备信号上报的数据MAP
        Integer signalCode=0;
        for(int i=0; i<pdtList.size();i++){
            devSignalList=new ArrayList<Map>();  //设备信号上报的数据
            LinkedHashMap<String,Object>pdtMap= (LinkedHashMap<String, Object>) pdtList.get(i);
            String devCode= (String) pdtMap.get(PLC_CONFIG.设备码.getKey());
            String bitCode= (String) pdtMap.get(PLC_CONFIG.状态.getKey());
            String nodeID= (String) pdtMap.get(PLC_CONFIG.节点ID.getKey());
            char[] bitChar=bitCode.toCharArray(); //倒序排列,从低至高位正确映射到属性名模板
            ConverUtil.reverseString(bitChar);
            System.out.println("=========BitResposeParser 方法==设备码:"+devCode);
            Long codeValue=DecimalTransforUtil.hexToLong(D_CODE_VAL.DValueMethod(devCode),true);
            System.out.println("=========BitResposeParser 方法==设备码("+devCode+") 转 10进制值:"+codeValue);

            if(bitTemp.length!=bitChar.length) throw new Exception("PDT '二进制位' 与 '位属性名模板'的长度不匹配!");
            if(PLCValidateUtil.rangeInDefined(codeValue.intValue(),0,111)){ //路灯电源（设备码：00H~6FH）
                for(int j=0;j<bitTemp.length;j++){
                    signalCode=(Integer) bitTemp[j][0];
                    if(bitChar[j]=='1' &&  signalCode>0){  //获取有报警信息的属性 0:无报警、1:有报警
                        devSignalMap=new HashMap<String,Object>();  //设备信号上报的数据
                        devSignalMap.put("signal_code",signalCode);
                        devSignalList.add(devSignalMap);
                    }
                }
            }else if(PLCValidateUtil.rangeInDefined(codeValue.intValue(),112,127)){//路灯控制器（设备码：70H~7FH）
                for(int j=0;j<bitTemp.length;j++){
                    signalCode=(Integer) bitTemp[j][1];
                    if(bitChar[j]=='1'){  //获取有报警信息的属性 0:无报警、1:有报警
                        devSignalMap=new HashMap<String,Object>();  //设备信号上报的数据
                        devSignalMap.put("signal_code",signalCode);
                        devSignalList.add(devSignalMap);
                    }
                }
            }
            //kafka  发送 设备上报的信号数据
            if(devSignalList.size()>0){
                System.out.println("===============kafka 发送设备信号上报数据 操作...");
                Object resultBitJSON=JSONArray.toJSON(devSignalList);
                System.out.println("===pdtResposeParser==发送设备信号上报数据 的json结构:"+ JSONObject.toJSONString(resultBitJSON));
                PlcProtocolsUtils.plcSignlResponseSend(HEAD_TEMPLATE.getUID(),nodeID,devSignalList,ctx);
            }
        }
    }

    /**
     * 生成相应指令参数格式的16进制值（PDT）
     * @param ccode
     * @param cmdParam
     * @return
     */
    public abstract String pdtData(String ccode,String cmdParam,ChannelHandlerContext ctx)throws Exception;
}