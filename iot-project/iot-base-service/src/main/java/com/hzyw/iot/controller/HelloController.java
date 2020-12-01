package com.hzyw.iot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@RequestMapping("/hello")
    public String index(@RequestParam String name) {
        return "hello "+name+"，负载均衡，路由到device service ----------------2   了";
    }
	
}
