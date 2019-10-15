package com.lfm.rpc.server;

import com.lfm.rpc.core.annotation.LfmRpcService;

/**
 * @author lifengming
 * @since 16.09.2019
 */
@LfmRpcService(HelloService.class)
public interface HelloService   {

    String saveUser(User user);

    String sayHello(String content);
}
