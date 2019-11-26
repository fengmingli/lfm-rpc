package com.lifengming.rpc.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

/**
 * @author lifengming
 * @since 2019.11.25
 */
@Slf4j
public class RpcLifecycle implements SmartLifecycle {
    private boolean isRunning = false;

    @Autowired
    RpcServerInitialization rpcServerInitialization;
    @Override
    public void start() {
        log.info("Spring Initialization end!!! start RpcServer");
        rpcServerInitialization.startServer();
        isRunning = true;
    }

    @Override
    public void stop() {
        System.out.println("MyLifeCycle:stop");
        isRunning = false;
    }


    /**
     * 返回true,stop(Runnable callback)或stop()方法才会被执行,返回false,start方法才会被执行
     *
     * @return boolean
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @return 返回true时start()方法会被自动执行
     */
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    /**
     * SmartLifecycle子类的才有的方法, 当方法isRunning()返回true才执行
     */
    @Override
    public void stop(Runnable callback) {
        callback.run();
        isRunning = false;
    }

    /**
     * @return 执行顺序, 数值越小越先执行
     */
    @Override
    public int getPhase() {
        return 0;
    }
}
