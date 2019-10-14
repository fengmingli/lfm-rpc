package com.lfm.rpc.client;

/**
 * @author lifengming
 * @since 16.09.2019
 */
public interface HelloService   {

    /**
     * 保存User
     * @param user
     * @return
     */
    String saveUser(User user);

    /**
     * TestHello方法
     * @param content
     * @return
     */
    String sayHello(String content);
}
