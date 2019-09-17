package com.lfm.rpc.server;

/**
 * @author lifengming
 * @since 16.09.2019
 */
public interface HelloService   {

    String saveUser(User user);

    String sayHello(String content);
}
