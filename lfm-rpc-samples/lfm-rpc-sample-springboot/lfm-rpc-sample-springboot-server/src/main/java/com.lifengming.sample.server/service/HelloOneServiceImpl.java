package com.lifengming.sample.server.service;

import com.lifengming.rpcsample.api.HelloOneService;
import com.lifengming.rpcsample.api.HelloService;
import org.springframework.stereotype.Service;

/**
 * @author lifengming
 * @since 2019.10.16
 */
@Service
public class HelloOneServiceImpl implements HelloOneService {
    @Override
    public String getDockerOne() {
        return "张策就是牛逼！！！";
    }
}
