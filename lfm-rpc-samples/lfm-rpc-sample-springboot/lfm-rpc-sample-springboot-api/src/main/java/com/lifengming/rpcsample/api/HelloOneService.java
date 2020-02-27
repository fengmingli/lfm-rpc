package com.lifengming.rpcsample.api;

import com.lifengming.rpc.core.annotation.LfmRpcService;

/**
 * @author lifengming
 * @since 2019.12.05
 */
@LfmRpcService(HelloOneService.class)
public interface HelloOneService {
    String getDockerOne();
}
