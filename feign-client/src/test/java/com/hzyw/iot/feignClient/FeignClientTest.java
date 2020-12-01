package com.hzyw.iot.feignClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hzyw.iot.feignClient.model.MessageVO;
import com.hzyw.iot.feignClient.model.PropertyVO;
import com.hzyw.iot.feignClient.model.TransmitDataVO;
import com.hzyw.iot.feignClient.service.IFeingClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignClientTest {

    @Autowired
    private IFeingClientService feingClientService;

    @Test
    public void contextLoads() {
        System.out.println("Feign测试开始");
        TransmitDataVO transmitDataVO =new TransmitDataVO();
        MessageVO messageVO=new MessageVO();
        List<PropertyVO> propertyVOS=new ArrayList<PropertyVO>();
        PropertyVO propertyVO1=new PropertyVO();
        PropertyVO pro=new PropertyVO();
        pro.setPropertyKey("属性一");
        pro.setPropertyValue("属性值一");
        pro.setUnit("单位一");
        propertyVO1.setPropertyKey("属性二");
        propertyVO1.setPropertyValue("属性值二");
        propertyVO1.setUnit("单位二");
        propertyVOS.add(pro);
        propertyVOS.add(propertyVO1);
        messageVO.setDefinedAttributer(propertyVOS);
        messageVO.setAttributer(propertyVOS);
        messageVO.setDeviceId("设备一");
        transmitDataVO.setData(messageVO);
        transmitDataVO.setType("devInfoResponse");
        String fastJson= JSON.toJSONString(transmitDataVO, SerializerFeature.DisableCircularReferenceDetect);
        System.out.println("---------传入的模型数据-----------");
        System.out.println(fastJson);
        System.out.println("---------开始调用feign服务端进行数据转换------------");
        TransmitDataVO transmit=feingClientService.getModelInfo(fastJson);
        System.out.println("---------转换后的模型数据---------");
        System.out.println(transmit);
        System.out.println("---------获取模型数据里的属性值-------");
        for (PropertyVO propertyVO:transmit.getData().getAttributer()){
            System.out.println(propertyVO);
        }
    }
}
