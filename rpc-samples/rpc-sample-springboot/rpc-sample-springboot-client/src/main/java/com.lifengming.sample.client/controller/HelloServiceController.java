package com.lifengming.sample.client.controller;


import com.lifengming.rpc.common.annotation.RpcReference;
import com.lifengming.rpcsample.api.HelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lifengming
 * @since 2019.10.16
 */
@RestController
@RequestMapping("/rpc")
public class HelloServiceController {

    @RpcReference
    private HelloService helloService;

    @RequestMapping("/demo")
    public String hello() {
        return helloService.getDocker();
    }

}
