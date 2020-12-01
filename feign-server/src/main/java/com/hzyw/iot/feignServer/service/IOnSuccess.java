package com.hzyw.iot.feignServer.service;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;


@EnableFeignClients
@FeignClient(value ="FeignClient")
public interface IOnSuccess {

    @RequestMapping(value="/on/success", method= RequestMethod.GET)
    public String successArrived(@RequestParam("res") String res);
}