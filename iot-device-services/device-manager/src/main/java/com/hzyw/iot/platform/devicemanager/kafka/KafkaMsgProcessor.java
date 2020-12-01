package com.hzyw.iot.platform.devicemanager.kafka;

import com.alibaba.fastjson.JSONObject;
import com.hzyw.iot.platform.devicemanager.caches.*;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAccessDO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAttributeDO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceInfoDO;
import com.hzyw.iot.platform.devicemanager.mqtt.MetricSender;
import com.hzyw.iot.platform.devicemanager.service.alarm.SignalAlarmMsgService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAccessService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAttrService;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceInfoService;
import com.hzyw.iot.platform.models.alarm.SignalAlarmMsg;
import com.hzyw.iot.platform.models.equip.DeviceAttribute;
import com.hzyw.iot.platform.models.equip.DeviceType;
import com.hzyw.iot.platform.models.equip.Equipment;
import com.hzyw.iot.platform.models.transfer.*;
import com.hzyw.iot.platform.util.json.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author by early
 * @blame IOT Team
 * @date 2019/9/23.
 */

@Slf4j
public class KafkaMsgProcessor {

    @Autowired
    Environment environment;
    @Autowired
    AttributeCacheService attributeCacheService;
    @Autowired
    DeviceAttrService attributeService;
    @Autowired
    private DeviceTypeCacheService typeCacheService;
    @Autowired
    private EquipmentCacheService equipmentCacheService;
    @Autowired
    private TransactionSessionCacheService transactionSessionCache;
    @Autowired
    private StatusHistoryCacheService statusHistoryCacheService;
    @Autowired
    private DeviceAccessService accessService;
    //    @Autowired
//    private DeviceTypeService typeService;
//    @Autowired
//    private DeviceRelationService relationService;
    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private SignalAlarmMsgService signalAlarmMsgService;
    @Autowired
    private MetricSender metricSender;

    /**
     * 处理Access主题的上报数据，第一次报一次，仅一次。
     *
     * @param msg 报文
     */
    public void processAccess(String msg) throws DefaultDeviceException {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        if (!MessageJsonUtil.validMsg(jsonObject)) {
            log.warn("message is not valid!");
        }
        String gwId = jsonObject.getString(R.GW_ID);
        List<DevInfoJsonData> list = jsonObject.getJSONObject(R.DATA).getJSONArray(R.DATA_ATTRIBUTES).toJavaList(DevInfoJsonData.class);
        for (DevInfoJsonData jsonData : list) {
            //保存设备基础数据和接入数据
            saveDevice(jsonData, gwId);
        }
        //修正属性与方法关联
        updateRefData(jsonObject.getJSONObject(R.DATA), !environment.getProperty("auto-revise",Boolean.class,false));
        //记录初始状态数据
        List<MetrxJsonData> metrx = jsonObject.getJSONObject(R.DATA).getJSONArray(R.DATA_DEFINED_ATTRIBUTES).toJavaList(MetrxJsonData.class);
        List<MetrxJsonData> delta = statusHistoryCacheService.saveMetrics(list.get(0).getUuid(), metrx);
        //发送MQTT消息
        metricSender.sendAllMetricToMqtt(jsonObject.getJSONObject(R.DATA).getString(R.DATA_ID),delta);
    }

    /**
     * 修正设备方法和属性，只加不减，（此方法选择开启，在数据模型稳定后，关闭此方法）
     * 只能作为辅助录入手段，仍需要人工修正。
     *
     * @param data
     * @param ignore
     */
    private void updateRefData(JSONObject data, boolean ignore) {
        if (ignore) {
            return;
        }
        DeviceType type = equipmentCacheService.get(data.getString(R.DATA_ID)).getEquipmentType(DeviceType.class);
        List<MetrxJsonData> metrics = data.getJSONArray(R.DATA_DEFINED_ATTRIBUTES).toJavaList(MetrxJsonData.class);
        for (MetrxJsonData metrx : metrics) {
            if (attributeCacheService.get(metrx.getType()) != null) {
                //TODO：数据库里有原始定义的才修正，暂不考虑自动新增，后面等元数据齐备了再说。
                if (metrx.getValue() != null && metrx.getType() != null) {
                    DeviceAttributeDO attribute = new DeviceAttributeDO();
                    attribute.setAttrKey(metrx.getType());
                    attribute.setAttrName(metrx.getType());
                    attribute.setUnit(metrx.getCompany());
                    if (metrx.getValue() instanceof BigDecimal) {
                        attribute.setValueType(DeviceAttribute.NUMBER);
                    } else if (metrx.getValue() instanceof Integer) {
                        attribute.setValueType(DeviceAttribute.INT);
                    } else if (metrx.getValue() instanceof Boolean) {
                        attribute.setValueType(DeviceAttribute.BOOLEAN);
                    } else if (metrx.getValue() instanceof String) {
                        attribute.setValueType(DeviceAttribute.STRING);
                    } else if (metrx.getValue() instanceof Arrays) {
                        attribute.setValueType(DeviceAttribute.ARRAY);
                    }else {
                        //以后考虑统一归为String，用的时候再转
                        attribute.setValueType(DeviceAttribute.STRING);
                    }
                    attributeService.saveDeviceAttr(attribute);
                    attributeService.saveAttrTypeRelation(type.getTypeId(), attribute.getAttrKey());
                }
            }
        }
        //处理方法，屏蔽没有的方法。
        List method = data.getJSONArray(R.DATA_METHODS);
//        type.getMethods()


    }

    /**
     * 保存设备基础数据
     *
     * @param jsonData
     * @param gwId
     */
    private void saveDevice(DevInfoJsonData jsonData, String gwId) throws DefaultDeviceException {
        String typeId = jsonData.getModel() + DeviceType.TYPE_HYPHEN + jsonData.getDevice_type_code() + DeviceType.TYPE_HYPHEN + jsonData.getVendor_code();
        DeviceType type = typeCacheService.get(typeId);
        if (type == null) {
            log.warn("No Support this deviceType type: {}, id: {}", typeId, jsonData.getUuid());
            throw new MessageDataProcessException(String.format("No Support this deviceType type: {%s}, id: {%s}", typeId, jsonData.getUuid()));
        }
        //保存DeviceInfo进数据库
        DeviceInfoDO deviceInfoDO = new DeviceInfoDO();
        deviceInfoDO.setDeviceId(jsonData.getUuid());
        deviceInfoDO.setSerialNumber(jsonData.getSn());
        deviceInfoDO.setDeviceName(jsonData.getDevice_type_name());
        deviceInfoDO.setDeviceDomain(jsonData.getDevice_type_code());
        deviceInfoDO.setDeviceType(typeId);
        deviceInfoDO.setManufacturerCode(jsonData.getVendor_code());
        try {
            deviceInfoDO.setProductDate(new SimpleDateFormat("yyyy-MM-dd").parse(jsonData.getDate_of_production()));
        } catch (ParseException e) {
            log.warn("Json: productionDate={}, is not a date format!", jsonData.getDate_of_production());
            deviceInfoDO.setProductDate(null);
        }
        deviceInfoDO.setMacAddr(jsonData.getMac_addr());
//        deviceInfoDO.setExpireDate(new SimpleDateFormat());
        try {
            deviceInfoService.saveDevice(deviceInfoDO);
        } catch (IllegalParameterException e) {
            log.error("deviceInfo Data was valid!", e);
            throw new MessageDataProcessException("deviceInfo Data was valid!", e);
        }
        //保存DeviceAccess进数据库
        DeviceAccessDO deviceAccessDO = new DeviceAccessDO();
        deviceAccessDO.setDeviceId(jsonData.getUuid());
        deviceAccessDO.setDeviceDomain(jsonData.getDevice_type_code());
        deviceAccessDO.setAccessTime(new Date());
        deviceAccessDO.setRegistration(DeviceAccessDO.ONLINE);
        deviceAccessDO.setDeviceIPv4(jsonData.getIpaddr_v4());
        deviceAccessDO.setDeviceIPv6(jsonData.getIpaddr_v6());
        deviceAccessDO.setGatewayId(gwId);
        if (accessService.getDeviceAccess(jsonData.getUuid()) == null) {
            accessService.insertDeviceAccess(deviceAccessDO);
        } else {
            accessService.updateDeviceAccess(deviceAccessDO);
        }
//        //保存DeviceRelation进数据库
//        relationService.setEdgeGateWay(gwId);
        //移除缓存
        equipmentCacheService.evict(jsonData.getUuid());
    }

    /**
     * 处理metric主题的上报数据，每分钟上报
     *
     * @param msg 报文
     */
    public void processMetrx(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        if (!MessageJsonUtil.validMsg(jsonObject)) {
            log.warn("message is not valid!");
        }
        String type = jsonObject.getString(R.TYPE);
        long timeStamp = jsonObject.getLongValue(R.TIMESTAMP_LONG);
        JSONObject data = jsonObject.getJSONObject(R.DATA);
        if (R.TYPE_RESPONSE.equals(type)) {
            //上报下发请求结果
            log.info("processMessage: type = {}", type);
            String msgId = jsonObject.get(R.MSG_ID).toString();
            Integer msgCode = (Integer) jsonObject.get(R.MESSAGE_CODE_INT);
            doOptCallBack(msgId, msgCode,timeStamp);

        } else if (R.TYPE_METRIC_INFO_RESPONSE.equals(type)) {
            //设备状态数据上报（增量）
            log.info("processMessage: type = {}", type);
            List<MetrxJsonData> list = data.getJSONArray(R.DATA_DEFINED_ATTRIBUTES).toJavaList(MetrxJsonData.class);
            // 存入状态缓存
            List<MetrxJsonData> delta = statusHistoryCacheService.saveMetrics(data.get(R.DATA_ID).toString(), list);
            //发送MQTT消息
            metricSender.sendMetricToMqtt(data.getString(R.DATA_ID), delta);

        } else if (R.TYPE_DEV_SIGNAL_RESPONSE.equals(type)) {
            //信号上报
            log.info("processMessage: type = {}", type);
            Equipment deviceInfoDO = equipmentCacheService.get(data.get(R.DATA_ID).toString());
            if (deviceInfoDO != null) {
                //组装主题
                int domain = deviceInfoDO.getEquipmentType().getDomainFlag();
                String topicName = "Exception/" + domain;
                List<SignalJsonData> signals = data.getJSONArray(R.DATA_SIGNALS).toJavaList(SignalJsonData.class);
                for (SignalJsonData jsonData : signals) {
                    //封装消息mqtt发送消息，一个信号一条消息
                    SignalAlarmMsg sam = signalAlarmMsgService.getAlarm(jsonData.getSignalCode());
                    if (sam != null) {
                        jsonData.setSignalName(sam.getName());
                        jsonData.setSignalMsg(sam.getMsg());
                        jsonData.setSignalLevel(sam.getLevel());
                    }
                    if(jsonData.getSignalLevel()<=15){ // 15以下的级别才是前台级别，详见数据库字段备注
                        String json = MessageJsonUtil.signalData2Json(deviceInfoDO.getDeviceId(), domain, jsonData, timeStamp);
                        metricSender.sendToMqtt(topicName, json);
                    }
                }
            }
        }
    }

    private void doOptCallBack(String msgId, int msgCode,long timeStamp) {
        //查找事务上下文
        DeviceServiceContext deviceServiceContext = transactionSessionCache.get(msgId);
        if (deviceServiceContext != null) {
            //清除TransactionLock;
            transactionSessionCache.release(msgId);
            //封装回调数据
            String prefixUrl = deviceServiceContext.getDeviceRequest().getCallbackUrl();
            String url = prefixUrl + R.SDK_CALLBACK_URI;
            String customMsgId = deviceServiceContext.getDeviceRequest().getCustomMsgId();
            DeviceOptResponse response = new DeviceOptResponse();
            if (msgCode == 0) {
                response.setSuccess(true).setDown(true).setRespObj(customMsgId);
            } else {
                SignalAlarmMsg sam = signalAlarmMsgService.getAlarm(msgCode, SignalAlarmMsgService.ALARM_TYPE.ERROR);
                TransmitOptException exp;
                if(sam == null){
                    exp = TransmitOptException.UNDEFINED;
                }else {
                     exp = new TransmitOptException(sam);
                }
                exp.setTransactionId(customMsgId);
                response.setDown(false).setSuccess(false).setRespObj(customMsgId).setError(exp);
            }
            //回调接口
            RestTemplate restTemplate = new RestTemplate();
            try {
                restTemplate.postForObject(url, response, void.class);
                log.info("doOptCallBack() has down: post URL:{}", url);
            } catch (RestClientException e) {
                log.warn("doOptCallBack(): post URL:{} error!, msg: {}", url, e.getMessage());
            }
            //MQTT广播回调数据，两种方式的回调
//            deviceServiceContext.getDeviceRequest()
            String json = MessageJsonUtil.callBackData2Json(response, timeStamp);
            metricSender.sendToMqtt("Operation/Callback", json);
        } else {
            log.error("###No Transaction Found! msgId： {} was Lost!", msgId);
        }
    }
}
