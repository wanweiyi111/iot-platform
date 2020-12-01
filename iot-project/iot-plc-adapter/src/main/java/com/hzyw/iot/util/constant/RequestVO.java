package com.hzyw.iot.util.constant;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.vo.dataaccess.DataType;
import com.hzyw.iot.vo.dataaccess.MessageVO;
import com.hzyw.iot.vo.dataaccess.RequestDataVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestVO {
    public static void main(String[] args) {
        String msgId="31a8c447-5079-4e91-a364-1769ac06fd5c";
        MessageVO<RequestDataVO> mesVO=new MessageVO<RequestDataVO>();
        mesVO.setType(DataType.Request.getMessageType());
        mesVO.setTimestamp(System.nanoTime());
        mesVO.setMsgId(msgId);
        mesVO.setGwId("31a8c447-5079-4e91-a364-1769ac06fd5c"); //000000000100
        mesVO.setData(getDataVO());
        System.out.println("===pdtResposeParser==发送设备状态上报数据 的json结构:"+ JSONObject.toJSONString(mesVO));
    }

    public static JSONObject getReqVO(){
        String msgId="31a8c447-5079-4e91-a364-1769ac06fd5c";
        MessageVO<RequestDataVO> mesVO=new MessageVO<RequestDataVO>();
        mesVO.setType(DataType.Request.getMessageType());
        mesVO.setTimestamp(System.nanoTime());
        mesVO.setMsgId(msgId);
        mesVO.setGwId("000000000100"); //"000000000100": 1010-d8b38d288d431464-3001-ffff-36cf
        mesVO.setData(getDataVO());
        System.out.println("===pdtResposeParser==发送设备状态上报数据 的json结构:"+ JSONObject.toJSONString(mesVO));
        JSONObject jsonObj= (JSONObject) JSONObject.toJSON(mesVO);
        return jsonObj;
    }

    private static RequestDataVO getDataVO() {
        RequestDataVO dataVO=new RequestDataVO();
        List<Map> methods=new ArrayList<Map>();
        List<Map>ins=new ArrayList<Map>();
        Map<String,Object>dataMap=new HashMap<String, Object>();
        Map<String,Object>inMap=new HashMap<String, Object>();

        Map<String,Object> tags =new HashMap<String,Object>();
        tags.put("agreement", "plc");
        inMap.put("code", "01H"); //00H
        inMap.put("level", 100); //00H
        //01H：控制A灯。(控制双灯控制器的A灯) 02H：控制B灯。(控制双灯控制器的B灯)
        //03H：同时控制A灯和B灯。（同时控制双灯控制器的A、B灯；控制LED、HID路灯电源）
        inMap.put("ab", "03H");
        inMap.put("onoff", 0); //0:关灯,1:开灯

        ins.add(inMap);
        //dataMap.put("method", "opera23"); //查询节点详细数据(45H) set_onoff
        dataMap.put("method", "set_onoff"); //节点调光(42H)
        dataMap.put("in", ins);
        methods.add(dataMap);
        dataVO.setId("0000020004ee"); //  1010-1f31e84812046d00-3001-ffff-8ea3
        dataVO.setTags(tags);
        dataVO.setMethods(methods);
        return dataVO;
    }
}
