package com.hzyw.iot.feignClient.service;

import com.hzyw.iot.feignClient.model.TransmitDataVO;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@EnableFeignClients
@FeignClient(value = "FeignServer")
public interface IFeingClientService {

    @GetMapping("/feignService/test")
    public TransmitDataVO getModelInfo(@RequestParam("jsonString") String string);

    @PostMapping("/feignService/hello")
    public String testHello(@RequestParam("jsonString") String string);
}
