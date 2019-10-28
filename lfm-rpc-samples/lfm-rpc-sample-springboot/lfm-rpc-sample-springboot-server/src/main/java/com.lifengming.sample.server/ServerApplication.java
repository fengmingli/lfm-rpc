package com.lifengming.sample.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author lifengming
 * @since 2019.10.16
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(value = "com.lifengming")
@Slf4j
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class,args);
        log.info("测试服务端启动成功！");
    }

}
