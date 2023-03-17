package me.inorimylove.threadpool;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ThreadPool
 * @Description ThreadPool
 * @Author xiaohuang
 * @Date 3/11/2023 9:10 PM
 * @Version 1.0
 */
@Slf4j(topic = "thread pool")
public class ThreadPool {
    //任务队列
    private BlockingQueque<Runnable> taskQueque;
    //线程集合
    private HashSet<Work> works = new HashSet<>();
    //核心线程数
    private int coreSize;

    //获取任务的超时时间
    private long timeout;

    private TimeUnit timeUnit;

    private RejectPolicy<Runnable> rejectPolicy;

    public void execute(Runnable task) {
        synchronized (works) {
            if (works.size() < coreSize) {
                Work work = new Work(task);
                log.debug("新增worker{}", work);
                works.add(work);
                work.start();
            } else {
                log.debug("加入任务队列{}", task);
                taskQueque.tryPut( rejectPolicy,task);
            }
        }
    }

    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int capacity,RejectPolicy rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.rejectPolicy = rejectPolicy;
        taskQueque = new ArrayBlockingQueque<>(capacity);
    }

    class Work extends Thread {
        private Runnable task;

        public Work(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //1）当task不为空，执行任务
            //2）当task执行完毕，从队列中获取任务执行
            while (task != null || (task = taskQueque.poll(timeout, timeUnit)) != null) {
                try {
                    log.debug("正在执行任务{}",task);
                    task.run();

                } catch (Exception e) {

                } finally {
                    task = null;
                }
            }
            synchronized (works) {
                log.debug("worker被移除{}",this);
                works.remove(this);
            }
        }
    }
}
