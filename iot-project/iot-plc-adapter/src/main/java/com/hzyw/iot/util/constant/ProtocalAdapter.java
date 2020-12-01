package com.hzyw.iot.util.constant;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.utils.PlcProtocolsBusiness;
import com.hzyw.iot.utils.PlcProtocolsUtils;
import com.hzyw.iot.vo.dataaccess.DataType;
import com.hzyw.iot.vo.dataaccess.RequestDataVO;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PLC 协议适配置器
 * 上行通信规约
 */
public class ProtocalAdapter{
    private static final Logger log = LoggerFactory.getLogger(ProtocalAdapter.class);
    //缓存 请求消息ID(实现PLC 一应一答)
    private static ConcurrentHashMap<String,String[]> CacheReqID=new ConcurrentHashMap<String,String[]>();
    /**
     * 按指令生成对应的协议码报文
     * @param uuid 设备ID(必填)
     * @param code 指定控制码  (格式 如：00H) 动态值时(必传) 非动态值(不传)
     *             00H:对集中器控制或参数配置/集中器控制或配置的结果返回
     *             01H:对集中器下属某终端单点控制/单点控制的结果返回
     *             02H:对集中器下属某类终端组控制/组控制的结果返回
     *             03H:对集中器下属某类终端广播控制/广播控制的结果返回
     *             04H:集中器主动上报数据
     *             80H:某种操作或请求的应答
     * 集中器控制或配置的结果返回
     * @param cmd 命令码(必填) (格式 如：F0H)
     * @param paramBody 命令参数
     * @return  synchronized
     */
    public static String generaMessage(String uuid,String code, String cmd, String paramBody,ChannelHandlerContext ctx) throws Exception {
        String haveCode=O_CODE_VAL.CodeNameMethod(cmd);
        String codes="";
        if(!"".equals(haveCode) && haveCode!=null && !"80H".equals(code)){ //如果 指令码有固定的控制码, code强制传空
            code="";
            codes=haveCode;
        } else {
            codes=code;
        }
        if(StringUtils.trimToNull(codes)==null)
            throw new Exception("=====generaMessage方法===按指令生成对应的协议码报文时,该指令:"+cmd+"对应 控制码 入参为空!"+codes);
        //设备ID (校验16进制的长度6个字节)
        HEAD_TEMPLATE.setUID(checkDeviceUID(uuid));

        //生成"指令参数"的十六进制值
        log.info("================待生成[指令参数]十六进制值的原值入参:"+paramBody);
        //根据指令调PDT模板，生成相应指令参数 (codes入参形式 后缀要带H)
        paramBody=O_CODE_VAL.PDTTemplate(codes,cmd,paramBody,ctx);
        log.info("=======cmd:"+cmd+",===code:"+codes+"====根据指令调PDT模板 生成[指令参数]的十六进制值:"+paramBody);
        if(("80H".equals(codes) || "04H".equals(codes))){
            //上报或响应过来的，若无需返回的直接返回空串(paramBody为空:表示不需响应)
            if("".equals(paramBody)) return ""; else code=codes="80H";
    }
        HEAD_TEMPLATE.setPDT(paramBody);

        String L_SIZE=CLAC_L_SIZE(O_CODE_VAL.CmdValueMethod(cmd),paramBody); //统计后字节数 是16进制形式
        //计算“数据域”中所有数据的字节数；L=0 表示无数据域
        HEAD_TEMPLATE.setL(L_SIZE);

        log.trace("====计算校验码(CS)时 传参的控制码: "+codes);
        //System.out.println("====计算校验码(CS)时 传参的控制码: "+codes);
        //计算校验码(CS): 从“帧起始符”到校验码之前的所有字节的模256的和，即各字节二进制算术和，不计超过256 的溢出值
        HEAD_TEMPLATE.setCS(CLAC_CS_SUM(C_CODE_VAL.CValueMethod(codes),L_SIZE,O_CODE_VAL.CmdValueMethod(cmd),paramBody));
        //根据指令获取相应请求或响应报文
        String reqMessage=O_CODE_VAL.OrderMethod(cmd,code);
        log.info("====指令码:"+cmd+",===根据指令获取相应 组装后的请求协议报文(reqMessage):"+reqMessage);
        log.info("====PLC地址(uid):"+HEAD_TEMPLATE.getUID());

        //校验生成的协议报文是否有错误
        byte[] resp=ConverUtil.hexStrToByteArr(reqMessage);
        boolean validateRes=ValidateProtocalMessage(resp);
        if (!validateRes) log.error("主机->设备 的 协议报文的格式或校验有错误! 无法生成报文!");
        return reqMessage;
    }

    /**
     * 按指令生成对应请求码报文 (上游调用：KAFKA 或其它)
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public static String  messageRequest(JSONObject jsonObject){
        List<Map<String,Object>>outList=new ArrayList<Map<String,Object>>();
        log.info("=============按指令生成对应请求码报文(messageRequest)方法 入参:"+jsonObject.toJSONString());
        try {
            String algorithm_uuid=jsonObject.get("gwId").toString(); //算法后 PLC设备ID
            //与上游对接 入参格式的适配
            jsonObject=RequestFormatAdapter(jsonObject,outList);
            log.info("=============按指令生成对应请求码报文(messageRequest)方法 入参格式适配转换后结构:"+jsonObject.toJSONString());
            //outList.add(outMap);
            String requestType= jsonObject.get("type").toString();
            if(!requestType.equals(DataType.Request.getMessageType())){
                log.error("PLC 调请求协义模板的方法(messageRequest)时, 入参:消息类型(type:request) 错误! "+requestType);
                return "";
            }
            String jsonStr=((JSONObject) jsonObject.get("data")).toJSONString();
            RequestDataVO requestVO=JSONObject.parseObject(jsonStr,RequestDataVO.class);
            Map<String,Object> methodMap=(Map<String, Object>)requestVO.getMethods().get(0);  //自定义扩展属性集合
            List<Map<String,Object>> inList=(List<Map<String,Object>>) methodMap.get("in"); //上报参数属性集合
            String uuid=jsonObject.getString("gwId"); //集中器ID
            String nodeID=requestVO.getId(); //节点ID  或 组ID
            String code=inList.get(0).get("code").toString(); //控制码
            String cmd=methodMap.get("method").toString(); //指令码
            List<String[]> pdtParams=(List<String[]>) inList.get(0).get("pdt"); //指令参数
            List<String[]> nodeArr=new ArrayList<String[]>();
            nodeArr.add(new String[]{nodeID});

            pdtParams=(nodeID!=null && !"".equals(nodeID))? nodeArr:pdtParams;
            String pdtParamsStr = JSONObject.toJSONString(pdtParams);

            log.trace("=========按指令生成对应请求码报文KAFKA 集中器ID:"+uuid);
            //System.out.println("=========按指令生成对应请求码报文KAFKA 集中器ID:"+uuid);
            log.trace("=========按指令生成对应请求码报文KAFKA 算法后的集中器ID:"+algorithm_uuid);
            //System.out.println("=========按指令生成对应请求码报文KAFKA 算法后的集中器ID:"+algorithm_uuid);
            log.trace("=========按指令生成对应请求码报文KAFKA 节点ID:"+nodeID);
            //System.out.println("=========按指令生成对应请求码报文KAFKA 节点ID:"+nodeID);
            log.trace("=========按指令生成对应请求码报文KAFKA 控制码:"+code);
            //System.out.println("=========按指令生成对应请求码报文KAFKA 控制码:"+code);
            System.out.println("=========按指令生成对应请求码报文KAFKA 指令码:"+cmd);
            log.trace("=========按指令生成对应请求码报文KAFKA 指令参数:"+pdtParams);
            //System.out.println("=========按指令生成对应请求码报文KAFKA 指令参数:"+pdtParams);

            //请求时，设置消息ID 缓存 处理逻辑 实现(一应一答)功能
            String  mesgID=jsonObject.getString("msgId");
            if(!isReqBlock(outList,algorithm_uuid,cmd,nodeID,mesgID,uuid)){
                return generaMessage(uuid,code,cmd,pdtParamsStr,null);
            }
        } catch (Exception e) {
            log.error("ProtocalAdapter # messageRequest方法里 生成请求指令时,异常!"+e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 是否请求 阻塞，如果阻塞不发PLC
     * 直接响应 "忙" 状态码 给请求端
     * 则否发PLC, 缓存 请求消息ID
     * 忙: 20324(（冲突）服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息
     * @param outList
     * @param params (plc_sn:plc设备ID、cmd:指令码、nodeID:节点ID)
     * @return true:忙
     */
    private static boolean isReqBlock(List<Map<String,Object>> outList, String... params){
        String msgID_NEW=params[3]; //新的请求消息ID
        //请求消息ID为空，默认为 单向请求无应答（不做一应一答的指令 没有请求消息ID）
        if(StringUtils.isEmpty(msgID_NEW) || StringUtils.isBlank(msgID_NEW)) return false;
        String sn =StringUtils.trimToNull(params[4]),cmd=StringUtils.trimToNull(params[1]);
        //String nodeIds=cmd=StringUtils.trimToNull(params[2]);
        //nodeIds=nodeIds==null? "":"_".concat(nodeIds);
        if(sn==null || cmd==null) return true;
        try {
            //集中器SN+指令码+节点ID 做为缓存KEY(注：key细粒到 节点ID 不行,因为响应报文里没有节点ID)
            //String key=sn.concat("_").concat(cmd).concat(nodeIds);
            String key=sn.concat("_").concat(cmd); //集中器SN+指令码 做为缓存KEY
            if(CacheReqID.containsKey(key)){
                long init_time=Long.parseLong(CacheReqID.get(key)[1]);
                Long time_out=(System.currentTimeMillis()-init_time)/1000;
                log.info("=======缓存中 请求消息ID:"+CacheReqID.get(key)[0]);
                log.info("=======缓存中 请求消息KEY:"+key+",未响应超时 时间(限3秒内):"+time_out);
                if(time_out<=3 && time_out>0) {//调置 超时3秒, 等待3秒响应时间(一秒内让过，三秒后让过)
                    //20324:请求冲突, 响应后面要做错误码 映射配置
                	log.warn("=======this command by Filter ....plcSn_cmdCode/messageId  ," + key + "/" + msgID_NEW);
                    PlcProtocolsUtils.plcACKResponseSend(params[0],params[1],params[2],20324,msgID_NEW,null,null);
                    return true;
                }
                log.info("====key:"+key+",====清理 缓存里设备未响应,超时3秒以上的消息ID:"+CacheReqID.get(key)[0]);
                CacheReqID.remove(key);
                log.info("====key:"+key+",====新的请求消息ID 替换缓存里超时的消息ID:"+msgID_NEW);
                CacheReqID.put(key,new String[]{msgID_NEW,String.valueOf(System.currentTimeMillis())});
            }else{
                CacheReqID.put(key,new String[]{msgID_NEW,String.valueOf(System.currentTimeMillis())});
            }
            log.info("=====isReqBlock===缓存里 剩余未响应的请求消息ID 数量:"+CacheReqID.size());
        } catch (Exception e) {
            log.error("=====请求 阻塞, 发送 响应状态 异常！ Exception:"+e.getMessage());
        }
        return false;
    }

    /**
     * 响应端 消费 缓存中 对应请求ID
     * 实现(一应一答)绑定
     * @param key
     * @return
     */
    public static String consumeRequestID(String key){
        String resMesgID="";
        if(CacheReqID.containsKey(key)){
            resMesgID=CacheReqID.get(key)[0];
            log.info("========key:"+key+",=响应 正在 消费缓存中的请求 消息ID:"+resMesgID);
            CacheReqID.remove(key);
        }
        log.info("=====consumeRequestID====缓存里 剩余未响应的 请求消息ID 数量:"+CacheReqID.size());
        return resMesgID;
    }

    public static void setCacheReqID(String key,String msgID_NEW){
        CacheReqID.put(key,new String[]{msgID_NEW,String.valueOf(System.currentTimeMillis())});
    }


    /**
     * 按指令生成对应响应码报文
     * 集中器->主机
     * @param resp
     * @return
     * @throws Exception
     */
    public static String  messageRespose(byte[] resp, ChannelHandlerContext ctx){
        log.info("=============按指令生成对应响应码报文(messageResposen)方法 入参:"+ConverUtil.convertByteToHexString(resp));
        try {
            if(resp.length<13){
                log.error("协议报文长度有错误！总字节长度范围：13~267!");
                return "";
            }
            String uuid=ConverUtil.unpackContent(resp,1,7);
            //System.out.println("=====提取的入参设备UUID:"+uuid);
            log.trace("=====提取的入参设备UUID:"+uuid);
            HEAD_TEMPLATE.setUID(uuid); //设备ID
            //校验集中器- 校验返回的协议报文 (01H: 成功 02H: 失败, 03H：主机忙  或 协议报文有错误)
            String paramBody=ValidateResponseMessage(resp);
            //从 集中器 返回的报文 中 提取入参
            String code=ConverUtil.convertByteToHexStr(resp[8])+"H"; //控制码
            String cmd=ConverUtil.convertByteToHexStr(resp[10])+"H";  //指令码
            log.trace("=====提取的入参设备UUID:"+uuid);
            log.info("======messageRespose方法==提取的入参控制码:"+code);
            log.info("======messageRespose方法==提取的入参指令码:"+cmd);
            log.info("======messageRespose方法==提取的入参指令参数:"+paramBody);
            return generaMessage(uuid,code,cmd,paramBody,ctx);
        } catch (Exception e) {
            log.error("ProtocalAdapter # messageRespose方法里 响应请求时,异常!"+e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    /**
     * PLC 设备->主机(响应) 上报文校验
     * @param resp
     * @return
     */
    public static String ValidateResponseMessage(byte[] resp){
        String paramBody="";
        //校验集中器- 返回的协议报文 (01H: 成功 02H: 失败, 03H：主机忙  或 协议报文有错误)
        try {
            boolean validateRes = ValidateProtocalMessage(resp);
            //协议报文 校验是否成功? 01H: 成功 02H: 失败
            paramBody=validateRes? "01":"02";
        } catch (Exception e) {
            log.error("=====集中器->主机 的 协议报文的格式或校验有错误! 无法生成报文!"+e.getMessage());
            paramBody="03"; //主机忙 或异常错误
        }
        //提取指令入参
        ByteBuffer buffer = ByteBuffer.allocate(resp.length-13);
        for(int i=11;i<resp.length-2; i++) {
            buffer.put(resp[i]);
        }
        String pdtContent=ConverUtil.convertByteToHexString(buffer.array()); //指令入参
        paramBody="".equals(pdtContent)?paramBody:pdtContent; //指令参数为空时,设返回状态码
        return paramBody;
    }

    /**
     * 校验响应协议报文的准确性
     * @param message 报文
     * @tip 异常提示信息
     * @return
     */
    public static boolean ValidateProtocalMessage(byte[] message) throws Exception {
        String byteToHexStr =ConverUtil.convertByteToHexString(message); //字节数组转16进制字符串
        message=ConverUtil.hexStrToByteArr(byteToHexStr); //16进制字符串转字节数组
        log.info("======协议报文总字节长度:"+message.length);
        //System.out.println("======协议报文总字节长度:"+message.length);
        if(!rangeInDefined(message.length,13,267)) {
            log.error("协议报文长度有错误！总字节长度范围：13~267!");
            return false;
        }
        if(!checkDataArea(message)) {
            log.error("协议报文模板的L(数据长度),CS(校验码) 与实际数据域长度不匹配!");
            return false;
        }
        if(!checkFixedValue(message)){
            log.error("协议报文模板的H,C,T 固定值有错误!");
            return false;
        }
        return true;
    }

    /**
     * 计算 数据域 字节数长度
     * @param params
     * @return
     */
    private static String CLAC_L_SIZE(String... params){
        byte[] byteArry=null;
        String sizeVal="00";
        Integer calcSum=0;  //累加字节统计后 是10进制值的
        try {
            if(params.length>0){
                for(int i=0;i<params.length;i++){
                    log.trace("第"+(i+1)+"个数据域参数是"+params[i]+";");
                    //System.out.println("第"+(i+1)+"个数据域参数是"+params[i]+";");
                    byteArry= ConverUtil.hexStrToByteArr(params[i]);
                    log.trace("第"+(i+1)+"个数据域字节长度是"+byteArry.length);
                    //System.out.println("第"+(i+1)+"个数据域字节长度是"+byteArry.length);
                    calcSum=byteArry.length+calcSum;
                }
                System.out.println("数据域 字节数计算和(L)："+calcSum);
                sizeVal=DecimalTransforUtil.toHexStr(String.valueOf(calcSum),1); //10进制转16进制
                if(StringUtils.isNumeric(sizeVal)){
                    sizeVal=new DecimalFormat("00").format(Integer.parseInt(sizeVal));
                }
                log.info("数据域 字节数计算和(进制转换处理后)："+sizeVal);
            }
        } catch (Exception e) {
            log.error("======CLAC_L_SIZE方法里,计算 数据域 字节数长度 异常!"+e.getMessage());
            e.printStackTrace();
        }
        return sizeVal;
    }

    /**
     * 计算 校验码和
     * @param params
     * @return
     */
    private static String CLAC_CS_SUM(String... params){
        String csStr=HEAD_TEMPLATE.H.getValue().concat(HEAD_TEMPLATE.UID.getValue().concat(HEAD_TEMPLATE.H.getValue().concat("%s%s%s%s")));
        //System.out.println("计算 校验码和 模板："+csStr);
        log.trace("计算 校验码和 模板："+csStr);
        if(params.length==4){
            //String L_SIZE=String.valueOf(DecimalTransforUtil.toHexStr(params[1],1)); //10进制转16进制
            csStr=String.format(csStr,params[0],params[1],params[2],params[3]);
            log.info("计算 校验码和 原值："+csStr);
            //System.out.println("计算 校验码和 原值："+csStr);
            log.info("计算 校验码和 CS："+ConverUtil.makeChecksum(csStr));
            //System.out.println("计算 校验码和 CS："+ConverUtil.makeChecksum(csStr));
            return ConverUtil.makeChecksum(csStr);
        }
        return "00";
    }

    /**
     * 数值区间比较
     * @param current
     * @param min
     * @param max
     * @return
     */
    public static boolean rangeInDefined(int current, int min, int max)
    {
        return Math.max(min, current) == Math.min(current, max);
    }

    /**
     * 检查协议报文的数据长度，校验码
     * @param message
     * @return
     */
    public static boolean checkDataArea(byte[] message){
        String L=StringUtils.lowerCase(ConverUtil.convertByteToHexStr(message[9])); //L 16进制的
        //L=String.valueOf(DecimalTransforUtil.hexToLong(L,true)); //16进制转10进制
        //System.out.println("====检查协议报文的数据长度，校验码=L:"+L);
        log.trace("====检查协议报文的数据长度，校验码=L:"+L);
        String CS=StringUtils.lowerCase(ConverUtil.convertByteToHexStr(message[message.length-2]));  //CS
        //System.out.println("====检查协议报文的数据长度，校验码=CS:"+CS);
        log.trace("====检查协议报文的数据长度，校验码=CS:"+CS);

        String code=ConverUtil.convertByteToHexStr(message[8])+"H"; //控制码
        //System.out.println("====检查协议报文的数据长度，校验码=控制码:"+code);
        log.trace("====检查协议报文的数据长度，校验码=控制码:"+code);
        String cmd=ConverUtil.convertByteToHexStr(message[10])+"H"; //指令码
        log.trace("");
        //System.out.println("====检查协议报文的数据长度，校验码=指令码:"+ cmd);
        log.trace("====检查协议报文的数据长度，校验码=指令码:"+ cmd);
        String paramBody=""; //指令参数
        for(int i=11;i<message.length-2; i++) {
            paramBody=paramBody+ConverUtil.convertByteToHexStr(message[i]);
        }
        //System.out.println("====检查协议报文的数据长度，校验码=paramBody:"+paramBody);
        log.trace("====检查协议报文的数据长度，校验码=paramBody:"+paramBody);
        //计算“数据域”中所有数据的字节数；L=0 表示无数据域
        String L_SIZE=StringUtils.lowerCase(CLAC_L_SIZE(O_CODE_VAL.CmdValueMethod(cmd),paramBody)); //统计后 返回的是 16进制的
        //if(!StringUtils.isNumeric(L_SIZE))L_SIZE=String.valueOf(DecimalTransforUtil.hexToLong(L_SIZE,true)); //16进制转10进制
        //System.out.println("====检查协议报文的数据长度，校验码=L_SIZE:"+L_SIZE);
        log.trace("====检查协议报文的数据长度，校验码=L_SIZE:"+L_SIZE);
        //计算校验码(CS): 从“帧起始符”到校验码之前的所有字节的模256的和，即各字节二进制算术和，不计超过256 的溢出值
        String CS_SIZE=StringUtils.lowerCase(CLAC_CS_SUM(C_CODE_VAL.CValueMethod(code),L_SIZE,O_CODE_VAL.CmdValueMethod(cmd),paramBody));
        log.debug("====检查协议报文的数据长度CS 比较结果: CS:"+CS+"和 CS_SIZE:"+CS_SIZE);
        if(StringUtils.isNumeric(L)){
            L=new DecimalFormat("00").format(Integer.parseInt(L));
        }else if(L.length()<2){
            L+="0".concat(L);
        }
        log.debug("====检查协议报文的数据长度L 比较结果: L:"+L+"和 L_SIZE:"+L_SIZE);
        log.debug("====检查协议报文的数据长度L 最大字节数范围不能超过10进制的255: "+Integer.parseInt(L,16));
        return L.equals(L_SIZE) && Integer.parseInt(L,16)<=255 && CS.equals(CS_SIZE);
    }

    /**
     * 检查协议模板固定值 H,C,T
     * @param message
     * @return
     */
    public static boolean checkFixedValue(byte[] message){
        String H= ConverUtil.convertByteToHexStr(message[0]);
        String H1= ConverUtil.convertByteToHexStr(message[7]);
        String C= ConverUtil.convertByteToHexStr(message[8])+"H";
        String CMD= ConverUtil.convertByteToHexStr(message[10])+"H";
        String T= ConverUtil.convertByteToHexStr(message[message.length-1]);
        //System.out.println(String.format("====检查协议模板固定值 H:%s, H1:%s, C:%s, CMD:%s ",H,H1,C,CMD));
        log.trace("====检查协议模板固定值 H:%s, H1:%s, C:%s, CMD:%s ",H,H1,C,CMD);
        C=C_CODE_VAL.CValueMethod(C);
        CMD=O_CODE_VAL.CmdValueMethod(CMD);
        return "68".equals(H) && "68".equals(H1) && "16".equals(T) && !"".equals(C) && !"".equals(CMD);
    }

    /**
     * 检查设备ID的16进制长度是否6个字节，不满足高位补0,超过报异常
     * @param uid
     * @return
     * @throws Exception
     */
    public static String checkDeviceUID(String uid) throws Exception{
        byte[]uidByte=ConverUtil.hexStrToByteArr(uid);
        if(uidByte.length>6) throw new Exception("PLC 设备UUID的16进制长度超过了6个字节!");
        String UUID=ConverUtil.convertUUIDByteToHexString(uidByte);
        //System.out.println("========checkDeviceUID===UUID:"+UUID);
        log.trace("========checkDeviceUID===UUID:"+UUID);
        return UUID;
    }

    /**
     * 请求 指令(适用批处理的指令调用)
     * 入参：指令，控制码，简单指令参数
     * @param code
     * @param cmd
     * @param pdt
     * @return
     * @throws Exception
     */
    public static String batchRequestCode(String code, String cmd, String pdt)throws Exception{
        //JSONObject jsonObj=T_RequestVO.getRequestVO("000000000100",code,cmd);
        //HEAD_TEMPLATE.setUID("000000000100");
        return generaMessage(HEAD_TEMPLATE.getUID(),code,cmd,pdt,null);
    }

    /**
     * 响应 测试入口
     * @param resp
     * @return
     * @throws Exception
     */
    public static String batchResponseCode(String resp) throws Exception {
        byte[] byteArrs=ConverUtil.hexStrToByteArr(resp);
        return messageRespose(byteArrs,null);
    }

    /**
     * 与上游对接入参格式的适配
     * @param jsonObj
     * @return
     */
    private static JSONObject RequestFormatAdapter(JSONObject jsonObj,List<Map<String,Object>> outList)throws Exception{
        String requestType= jsonObj.get("type").toString(); //消息类型
        String uuid=jsonObj.get("gwId")!=null?jsonObj.get("gwId").toString():""; //PLC设备ID
        if("".equals(uuid)) throw new Exception("===请求报文模板方法, 入参 PLC集中器地址(gwId) 不能为空！");
        String msgID=jsonObj.get("msgId").toString(); //消息ID

        String jsonData=((JSONObject) jsonObj.get("data")).toJSONString();
        RequestDataVO requestVO=JSONObject.parseObject(jsonData,RequestDataVO.class);
        String nodeID=requestVO.getId(); //节点ID  或 组ID

         //--------------取节点
        if(StringUtils.isNumeric(nodeID) && nodeID.length()<=3){  //判断 ID 是否‘组号’ 类型
            if(ConverUtil.rangeInDefined(Integer.parseInt(nodeID),1,255)){ //组号范围:1~255
                nodeID=DecimalTransforUtil.toHexStr(nodeID,6);//10进制->16进制串 拼装6字节的组号
                log.info("===拼装6字节后的组号:"+nodeID);
            }else{
                throw new Exception("=======下发指令 入参 的 组号超过了范围(1~255)! "+nodeID);
            }
        }else{
            nodeID = PlcProtocolsBusiness.getPlcNodeSnByPlcNodeID(nodeID); //00000200053a
        }
        //System.out.println("====================应用平台 下发过来的 (转换后结果)节点ID:"+nodeID);
        log.trace("====================应用平台 下发过来的 (转换后结果)节点ID:"+nodeID);
         //----------------取集中器SN
        uuid =PlcProtocolsBusiness.getPlcSnByPlcID(uuid); //000000000100
        if(StringUtils.trimToNull(uuid)==null) throw new Exception(String.format("=====从配置文件获取'集中器ID':%s 对应的算法ID为空！",uuid));
        log.trace("====================应用平台 下发过来的 (转换后结果)集中器SN:"+uuid);

        Map<String,Object> methodMap=(Map<String, Object>)requestVO.getMethods().get(0);  //自定义扩展属性集合
        List<Map<String,Object>> inList=(List<Map<String,Object>>) methodMap.get("in"); //上报参数属性集合
        String cmd=methodMap.get("method").toString(); //指令码
        Map<String,Object>pdtMap=inList.get(0);
        if(StringUtils.isNotBlank(nodeID) && StringUtils.isNotEmpty(nodeID)){
            pdtMap.put("ID",nodeID);
        }
        cmd=PLC_METHOD_CMD_CONFIG.Method2CMD(cmd); //方法名 映射 指令码
        String code=pdtMap.get("code")!=null?pdtMap.get("code").toString():""; //"03H";//控制码  上游没有 控制码 入参，暂定值:03H 广播类型
        //if("".equals(code) && code==null) code="03H";
        //if("".equals(uuid) || uuid==null)uuid="000000000100";
        outList=inList;
        JSONObject tragetObj=T_RequestVO.getRequestVO(uuid,code,cmd,msgID,inList);
        return tragetObj;
    }
}