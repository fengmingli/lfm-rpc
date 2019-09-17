package com.lfm.rpc.server;

/**
 * @author lifengming
 * @since 16.09.2019
 */
public class HelloServiceImpl implements HelloService {
    public String saveUser(User user) {
        System.out.println("user->" + user);
        return "success";
    }

    public String sayHello(String content) {
        return "hello word:" + content;
    }
}
