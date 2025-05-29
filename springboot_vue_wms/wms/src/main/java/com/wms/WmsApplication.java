package com.wms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.wms", "com.wms.service"})
public class WmsApplication {

    public static void main(String[] args) {

        SpringApplication.run(WmsApplication.class, args);
    }

}
