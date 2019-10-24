package com.lifengming.rpcsample.api;

import com.lfm.rpc.core.annotation.LfmRpcService;

/**
 * @author lifengming
 * @since 2019.10.16
 */
@LfmRpcService(HelloService.class)
public interface HelloService {
    /**
     * 测试接口
     * @return
     */
  String getDocker();
}
