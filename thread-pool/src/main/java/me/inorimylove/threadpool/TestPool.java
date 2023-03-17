package me.inorimylove.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName TestPool
 * @Description TestPool
 * @Author xiaohuang
 * @Date 3/11/2023 9:11 PM
 * @Version 1.0
 */

@Slf4j(topic = "testPool")
public class TestPool {
    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(2, 1000, TimeUnit.MILLISECONDS, 2,(queque,task)->{
            queque.put(task);
        });
        //一直等待的策略
        for(int i=0;i<15;i++){
            int j =i;
            threadPool.execute(()->{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}",j);
            });
        }
    }
}
