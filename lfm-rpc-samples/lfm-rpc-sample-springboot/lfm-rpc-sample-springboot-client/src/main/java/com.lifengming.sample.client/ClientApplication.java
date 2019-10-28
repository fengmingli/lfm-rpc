package com.lifengming.sample.client;

import com.lifengming.rpc.client.annotation.EnableLfmRpcClient;
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
@EnableLfmRpcClient(basePackages = "com.lifengming.rpcsample.api")
@ComponentScan(value = "com.lifengming")
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
