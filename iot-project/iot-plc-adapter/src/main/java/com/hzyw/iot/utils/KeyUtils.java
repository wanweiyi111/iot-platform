package com.hzyw.iot.utils;

import java.util.Map;

/**
 * key 工具类
 *
 * @author TheEmbers Guo
 * @version 1.0
 * createTime 2018-10-26 17:01
 */
public class KeyUtils {
    public static String buildKey(String ip, Integer port) {
        return buildKey("iot-ms:collect_model_mapping", ip, port);
    }

    public static String buildKey(String platformName, String ip, Integer port) {
        return platformName + ":" + ip + ":" + port;
    }

    public static boolean isKey(Map<String, String> map , String key){
        if(null==map||map.size()==0){
            return false;
        }

        for(Map.Entry<String, String> entry : map.entrySet()){
            if(entry.getKey().equals(key)){
                return true;
            }
        }

        return false;
    }

    public static boolean isValue(Map<String, Object> map ,String value){
        if(null==map||map.size()==0){
            return false;
        }
        for(Map.Entry<String, Object> entry : map.entrySet()){
            if(entry.getValue().equals(value)){
                return true;
            }
        }
        return false;
    }

}
