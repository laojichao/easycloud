package com.easycloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.easycloud.mapper")
public class EasyCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyCloudApplication.class, args);
    }
}