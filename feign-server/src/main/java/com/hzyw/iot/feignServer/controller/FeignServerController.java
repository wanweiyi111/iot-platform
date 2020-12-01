package com.hzyw.iot.feignServer.controller;


import com.hzyw.iot.feignServer.service.IFeignService;
import com.hzyw.iot.feignServer.util.TransmitDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.FutureTask;


@RestController
@EnableDiscoveryClient
@RequestMapping("/feignService")
public class FeignServerController {

    @Autowired
    private IFeignService feignService;

    @GetMapping("/test")
    public TransmitDataVO getModelInfo(String jsonString) {
        return feignService.getModelInfo(jsonString);
    }

    @PostMapping("/hello")
    public String testHello(String jsonString) {
         System.out.println("ing... testHello");
         FutureTask task = feignService.doAction();

         return "testHello() now doing...";
//        return feignService.testHello(jsonString);
    }

}
