package me.inorimylove.threadpool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName ArrayBlockingQueque
 * @Description ArrayBlockingQueque
 * @Author xiaohuang
 * @Date 3/11/2023 9:09 PM
 * @Version 1.0
 */
public class ArrayBlockingQueque<T> implements BlockingQueque<T> {

    private Deque<T> deque = new ArrayDeque();

    private ReentrantLock lock = new ReentrantLock();
    //生产者等待条件
    private Condition fullWaitSet = lock.newCondition();
    //消费者等待条件
    private Condition emptyWaitSet = lock.newCondition();

    private int capacity;

    public ArrayBlockingQueque(int capacity){
        this.capacity = capacity;
    }

    @Override
    public T take() {
        lock.lock();
        try {
            while (deque.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = deque.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }

    }

    @Override
    public T poll(long time, TimeUnit unit){
        lock.lock();
        try {
            long nanos = unit.toNanos(time);
            while (deque.isEmpty()) {
                try {
                    if(nanos<0L){
                        return null;
                    }
                   nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = deque.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(T element) {
        lock.lock();
        try {
            while (deque.size() == capacity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            deque.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T element, long time, TimeUnit unit) {
        lock.lock();
        try {
            long nanos = unit.toNanos(time);
            while (deque.size() == capacity) {
                try {
                    if(nanos<0L){
                        return false;
                    }
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            deque.addLast(element);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void tryPut(RejectPolicy<T> rejectPolicy,T task){
        lock.lock();
        try {
            if(deque.size()==capacity){
                rejectPolicy.reject(this,task);
            }else {
                deque.addLast(task);
                emptyWaitSet.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return deque.size();
        } finally {
            lock.unlock();
        }
    }
}
