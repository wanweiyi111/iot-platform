package com.hzyw.iot.platform.devicemanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hzyw.iot.platform.devicemanager.domain.comm.PageBean;
import com.hzyw.iot.platform.devicemanager.domain.comm.ResultVO;
import com.hzyw.iot.platform.devicemanager.domain.device.DeviceAccessDO;
import com.hzyw.iot.platform.devicemanager.kafka.DeviceAccessProducer;
import com.hzyw.iot.platform.devicemanager.mqtt.MqttGateway;
import com.hzyw.iot.platform.devicemanager.service.device.DeviceAccessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * DeviceAccessController
 *
 * @blame Android Team
 */
@Slf4j
@RestController
@RequestMapping("/device/access")
@Api(value="设备入网controller",tags={"设备入网接口"})
public class DeviceAccessController {

    @Autowired
    DeviceAccessProducer accessProducer;
    @Autowired
    private MqttGateway mqttGateway;
    //@Autowired
    //private MqttClient mqttClient;
    @Autowired
    private DeviceAccessService deviceAccessService;
    /**
     * 设备入网注册列表
     *
     *
     */
    @ApiOperation(value="查看设备入网注册信息", notes="查看设备入网注册信息")
    @GetMapping("/select/{pageNum}/{pageSize}")
    public ResultVO<List> findDeviceAccessInfoList(@PathVariable("pageNum")  Integer pageNum,@PathVariable("pageSize")  Integer pageSize){

        //ResultVO resultVO = new ResultVO();
        //PageBean pageBean = new PageBean();
        Page<DeviceAccessDO> page = PageHelper.startPage(pageNum,pageSize);
        List<DeviceAccessDO>  list = deviceAccessService.findDeviceAccessInfoList();
        PageInfo<DeviceAccessDO> pageInfo = new PageInfo<DeviceAccessDO>(list);
        return ResultVO.pageSuccess(pageInfo);


    }

    /**
     * 批量设备入网
     *
     *
     */





    /**
     * KAFKA生产消息发送
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("/send")
    @ResponseBody
    public String send(@RequestBody JSONObject jsonObject) {
        String topic = jsonObject.get("topic").toString();
        JSONObject nameJson = (JSONObject) JSONObject.toJSON(jsonObject.get("name"));
        String name = nameJson.toJSONString();
        log.info("======send start: topic = {}, msg = {}", topic, name);
        accessProducer.sendMessage(topic, name);
        return name;
    }

    /**
     * MQTT消息发布
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("/sendMqtt")
    @ResponseBody
    public String sendMqtt(@RequestBody JSONObject jsonObject) {
        String topic = jsonObject.get("topic").toString();
        JSONObject nameJson = (JSONObject) JSONObject.toJSON(jsonObject.get("name"));
        String sendData = nameJson.toJSONString();
        mqttGateway.sendToMqtt(topic, sendData);
        return "SUCCESS";

    }

}
