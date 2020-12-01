package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.hzyw.iot.filter.TokenFilter;

@SpringBootApplication
@EnableZuulProxy
public class IotZuulServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(IotZuulServiceApp.class, args);
	}
	
	@Bean
	public TokenFilter tokenFilter() {
		return new TokenFilter();
	}
	
	
}
