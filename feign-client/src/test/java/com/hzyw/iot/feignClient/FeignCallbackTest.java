package com.hzyw.iot.feignClient;

import com.hzyw.iot.feignClient.service.IFeingClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignCallbackTest {

    @Autowired
    private IFeingClientService feingClientService;

    @Test
    public void contextLoads() {
        System.out.println("Feign测试开始");
        String res=feingClientService.testHello("1");
        System.out.println("服務端收到:" + res);
        System.out.println("testHello执行完毕");
    }

}
