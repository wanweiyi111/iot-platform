package com.hzyw.iot.util.constant;

import com.hzyw.iot.vo.dataaccess.DataType;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.ResponseDataVO;

import java.util.*;

/**
 * PLC协议返回 VO 消息头
 */
public class T_MessageResult {

    public static MessageVO<ResponseDataVO> getResponseVO(String uuid, String code, String cmd, LinkedHashMap<String,Object> pdt){
        String msgId="31a8c447-5079-4e91-a364-1769ac06fd5c-response";  //暂时固定值，后面考虑怎么生成获取消息ID
        MessageVO<ResponseDataVO> mesVO=new MessageVO<ResponseDataVO>();
        mesVO.setType(DataType.Response.getMessageType());
        mesVO.setTimestamp(System.nanoTime());
        mesVO.setData(getDataVO(code,cmd,pdt));
        mesVO.setMsgId(msgId);
        mesVO.setGwId(uuid);
        return mesVO;
    }

    private static ResponseDataVO getDataVO(String code,String cmd,LinkedHashMap<String,Object> pdtMap) {
        ResponseDataVO dataVO=new ResponseDataVO();
        List<Map> methods=new ArrayList<Map>();
        //List<Map>ins=new ArrayList<Map>();
        Map<String,Object>dataMap=new HashMap<String, Object>();
        Map<String,Object>inMap=new HashMap<String, Object>();

        inMap.put("code", code); //00H
        inMap.put("pdtData",pdtMap);
        //ins.add(inMap);
        dataMap.put("method", cmd); //82H
        dataMap.put("out", inMap);
        methods.add(dataMap);

        //dataVO.setId("");
        dataVO.setMethods(methods);
        return dataVO;
    }

}
