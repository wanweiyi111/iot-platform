package com.hzyw.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "com.hzyw.iot")
//@RestController
public class IotHkCameraApplication {
    public static void main(String[] args) {
       /* new SpringApplicationBuilder()
               // .banner(new TheEmbersBanner())
               // .bannerMode(Banner.Mode.LOG)
                .sources(IotDCApplication.class)
                .run(args);*/
        
        SpringApplication application = new SpringApplication(IotHkCameraApplication.class);
		//application.addInitializers(new ApplicationStartedListener());
		SpringApplication.run(IotHkCameraApplication.class, args);
		
    }
    
}
