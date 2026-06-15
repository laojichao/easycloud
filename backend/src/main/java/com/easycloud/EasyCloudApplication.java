package com.easycloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * EasyCloud 应用启动类
 * <p>
 * Spring Boot 应用入口，配置以下关键注解：
 * <ul>
 *   <li>@SpringBootApplication - 组件扫描、自动配置</li>
 *   <li>@MapperScan("com.easycloud.mapper") - MyBatis-Plus Mapper 接口扫描</li>
 *   <li>@EnableAsync - 启用异步方法支持（邮件、短信等异步发送）</li>
 * </ul>
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.easycloud.mapper")
@EnableAsync
public class EasyCloudApplication {

    /**
     * 应用主入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(EasyCloudApplication.class, args);
    }
}