package com.lifengming.rpc.core.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

import static java.lang.Math.min;

/**
 * @author lifengming
 * @since 2019.11.25
 */
@Component
public class ThreadPoolExecutorFactory {
    private static volatile ThreadPoolExecutorFactory instance;
    private ExecutorService executorService;

    private ThreadPoolExecutorFactory() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("lfm-rpc-server-pool-%d").build();
        int availableProcessor = Runtime.getRuntime().availableProcessors();
        int coreNum = availableProcessor / 2;
        // 用单例模式创建线程池，保留2个核心线程，最多线程为CPU个数的2n+1的两倍.
        int maxProcessor = (1 + availableProcessor * 2) * 2;
        executorService = new ThreadPoolExecutor(min(coreNum, 2), maxProcessor,
                60L, TimeUnit.SECONDS, new SynchronousQueue<>(), namedThreadFactory,new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolExecutorFactory getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolExecutorFactory.class) {
                if (instance == null) {
                    instance = new ThreadPoolExecutorFactory();
                }
            }
        }
        return instance;
    }

    public void executeTask(Runnable command) {
        executorService.execute(command);
    }
}
