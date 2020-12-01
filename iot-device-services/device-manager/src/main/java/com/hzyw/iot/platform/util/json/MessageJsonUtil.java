package com.hzyw.iot.platform.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hzyw.iot.platform.models.transfer.DeviceOptResponse;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/23.
 */
public class MessageJsonUtil {

    public static String buildOptMsg(String deviceId, String msgId, String gwId, Map<String, Map<String, Object>> methodPara) {
        Map message = new HashMap(6);
        message.put(R.MSG_ID, msgId);
        message.put(R.GW_ID, gwId);
        message.put(R.TYPE, R.TYPE_REQUEST);
        message.put(R.TIMESTAMP_LONG, System.currentTimeMillis() / 1000);
//          build data
        Map data = new HashMap(2);
        data.put(R.DATA_ID, deviceId);
        List<MethodVO> methodVOList = new ArrayList<>();
//         build methodVOList
        for (String key : methodPara.keySet()) {
            MethodVO methodVO = new MethodVO();
            methodVO.setMethod(key);
            List<Map> maps = new ArrayList<>();
            Map map = methodPara.get(key);
            maps.add(map);
            methodVO.setIn(maps);
            methodVOList.add(methodVO);
        }
        //网关是PLC集控器类型，需要额外加tag
        if (gwId.startsWith("2000-")) {
            JSONObject object = JSONObject.parseObject("{\"agreement\":\"plc\"}");
            data.put("tags", object);
        }
        data.put(R.DATA_METHODS, methodVOList);
        message.put(R.DATA, data);
        String jsonData = JSON.toJSONString(message, SerializerFeature.DisableCircularReferenceDetect);
        return jsonData;
    }


    public static boolean validMsg(JSONObject jsonObject) {
        boolean illegal = jsonObject == null || StringUtils.isEmpty(jsonObject.get(R.TYPE)) ||
                jsonObject.get(R.DATA) == null || StringUtils.isEmpty(jsonObject.get(R.GW_ID));
        return !illegal;
    }

    public static String signalData2Json(String deviceId, int domain, SignalJsonData jsonData, long timeStamp) {
        Map<String, Object> devSignInfo = new HashMap<>(7);
        devSignInfo.put("id", deviceId);
        devSignInfo.put("type", domain);
        devSignInfo.put("timeStamp", String.valueOf(timeStamp));
        devSignInfo.put("code", jsonData.getSignalCode());
        devSignInfo.put("name", jsonData.getSignalName());
        devSignInfo.put("msg", jsonData.getSignalMsg());
        devSignInfo.put("level", "error");
        return JSON.toJSONString(devSignInfo);
    }

    public static String callBackData2Json(DeviceOptResponse response, long timeStamp) {
        String customMsgId = response.getResponse().toString();
        Map<String, Object> callBackMsg = new HashMap<>();
        callBackMsg.put("msgId", customMsgId);
        callBackMsg.put("timeStamp",timeStamp);
        if (response.isDown() && response.isSuccess()) {
            callBackMsg.put("isSuccess", true);
        } else {
            callBackMsg.put("isSuccess", false);
            callBackMsg.put("errCode", response.getErrorCode());
            callBackMsg.put("errMsg", response.getErrorMsg());
        }
        return JSON.toJSONString(callBackMsg);
    }

}
