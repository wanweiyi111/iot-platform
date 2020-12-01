package com.hzyw.iot.feignClient.controller;


import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.*;




@RestController
@EnableDiscoveryClient
@RequestMapping("/on")
public class BackController {


    @RequestMapping(value="/success", method= RequestMethod.GET)
    public String successArrived(String res) {
         System.out.println("log success,I want cost xxx, result is"+ res );
         return "success!!";
//        return feignService.testHello(jsonString);
    }

}
