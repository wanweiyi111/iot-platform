package com.hzyw.iot.platform.devicemanager.mqtt;

import com.alibaba.fastjson.JSON;
import com.hzyw.iot.platform.devicemanager.caches.EquipmentCacheService;
import com.hzyw.iot.platform.devicemanager.caches.StatusHistoryCacheService;
import com.hzyw.iot.platform.models.equip.Equipment;
import com.hzyw.iot.platform.models.equip.StatusHistory;
import com.hzyw.iot.platform.util.json.MetrxJsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/23.
 */
@Service
@Slf4j
public class MetricSender {

    @Autowired
    EquipmentCacheService equipmentCache;
    @Autowired
    MqttGateway mqttGateway;
    @Autowired
    StatusHistoryCacheService statusHistoryCache;

    public void sendMetricToMqtt(String deviceId, List<MetrxJsonData> delta) {
        //TODO： 全处理为字符串了，没有传单位，等应用平台的设计跟上后再优化。
        //封装上报应用平台数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("deviceId", deviceId);
        map.put("timeStamp",System.currentTimeMillis());
        for (MetrxJsonData e : delta) {
            map.put(MappingStandardKey(e.getType()), MappingFixedValue(e.getValue()));
        }
        //查询获取设备信息
        Equipment deviceInfoDO = equipmentCache.get(deviceId);
        if(deviceInfoDO == null){
            log.warn("No such information and ignore metrics for this deviceId: {}. ",deviceId);
            return;
        }
        map.put("type", String.valueOf(deviceInfoDO.getEquipmentType().getDomainFlag()));
        String jsonString = JSON.toJSONString(map);
        //组装主题
        String topicName = "Runtime/" + deviceInfoDO.getEquipmentType().getDomainFlag();
        //发送MQTT消息给应用平台
        mqttGateway.sendToMqtt(topicName, jsonString);
        log.info("sendMetricToMqtt(delta), topic = {}, msg = {}", topicName, jsonString);
    }

    private String MappingStandardKey(String type) {
        //TODO: hardCode，以后在网关做，对北向平台统一，数据网关是定制开发，做起来更简单。
        if ("plc_node_electri_out".equals(type)) {
            return "current";
        } else if ("plc_node_electri_in".equals(type)) {
            return "current";
        } else if ("plc_node_voltage_out".equals(type)) {
                return "voltage";
        } else if ("plc_node_voltage_in".equals(type)) {
            return "voltage";
        } else if ("plc_node_power_in".equals(type)) {
            return "capacity";
        } else if ("plc_node_a_onoff".equals(type)) {
            return "on_off";
        } else if ("plc_node_a_brightness".equals(type)) {
            return "brightness";
        }else {
            return type;
        }
    }

    private Object MappingFixedValue(Object value) {
        //TODO: 类型转换，单位转换。
        return value;
    }

    public void sendAllMetricToMqtt(String deviceId,List<MetrxJsonData> delta) {
        //上报所有数据
        StatusHistory history = statusHistoryCache.get(deviceId);
        if (history != null) {
            Map map = history.getLastAttributesString();
            for(MetrxJsonData metrxJsonData : delta){
                map.put(metrxJsonData.getType(),metrxJsonData.getValue());
            }

            //查询获取设备信息
            Equipment deviceInfoDO = equipmentCache.get(deviceId);
            map.put("deviceId", deviceId);
            map.put("timeStamp",System.currentTimeMillis());
            map.put("type", String.valueOf(deviceInfoDO.getEquipmentType().getDomainFlag()));
            String jsonString = JSON.toJSONString(map);
            //组装主题
            String topicName = "Runtime/" + deviceInfoDO.getEquipmentType().getDomainFlag();
            //发送MQTT消息给应用平台
            mqttGateway.sendToMqtt(topicName, jsonString);
            log.info("sendMetricToMqtt(delta), topic = {}, msg = {}", topicName, jsonString);
        }
    }

    public void sendToMqtt(String topicName, String json) {
        mqttGateway.sendToMqtt(topicName, json);
        log.info("signal msg have send to mqtt: topic = {}, msg = {}", topicName, json);
    }
}
