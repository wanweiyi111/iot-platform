package com.hzyw.iot.feignServer;

import com.alibaba.fastjson.JSON;

import com.hzyw.iot.feignServer.service.IOnSuccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignServerTest {

    @Autowired
    private IOnSuccess onSuccess;

    @Test
    public void contextLoads() {
        System.out.println("Feign测试开始");
        String res=onSuccess.successArrived("123");
        System.out.println("服務端收到:" + res);
        System.out.println("testHello执行完毕");
    }
}
