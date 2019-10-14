package com.lfm.rpc.client;

import java.util.concurrent.CountDownLatch;


public class TestCountDownLatch {

    static int n = 0;

    public static void main(String[] args) {

        int thread_num =3;

        final CountDownLatch countDown = new CountDownLatch(thread_num);

        long start = System.currentTimeMillis();
        System.out.println("start:"+start);

        for (int i = 0; i < thread_num; i++) {

            //模拟多线程执行任务 ,启动10个线程, 
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 比如你想测多线程环境下 饿汉式懒汉式 执行效率
                    // 可在里面执行要测试的代码,我就简单模拟下
                    for (int i = 0; i < 1000; i++) {
                        n++;
                    }
                    System.out.println("线程：" + Thread.currentThread().getName() + " 任务执行完毕");
                    //计数器减一
                    countDown.countDown();
                }

            }).start();
        }

        try {
            //主线程就一直阻塞了
            System.out.println("dsds");
            countDown.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("线程：" + Thread.currentThread().getName() + " 恢复,开始接着执行");

        long end = System.currentTimeMillis() - start;

        System.out.println("执行时间：" + end);
    }

}