package me.inorimylove.threadpool;

@FunctionalInterface
public interface RejectPolicy<T> {
    void reject(BlockingQueque queque,T task);
}
