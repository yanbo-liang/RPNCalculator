package com.liangyanbo.airwallex;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RPNApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RPNApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run();
    }
}
