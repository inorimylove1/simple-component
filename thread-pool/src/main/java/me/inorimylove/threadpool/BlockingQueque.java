package me.inorimylove.threadpool;

import java.util.concurrent.TimeUnit;

public interface BlockingQueque<T> {
    //阻塞获取
    public T take();

    //阻塞添加
    public void put(T element);

    //有超时的阻塞获取
    public T poll(long time, TimeUnit unit);

    //有超时的阻塞添加
    public boolean offer(T element, long time, TimeUnit unit);

    public void tryPut(RejectPolicy<T> rejectPolicy,T task);

    public int size();
}
