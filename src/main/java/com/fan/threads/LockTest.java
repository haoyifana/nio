package com.fan.threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

    private static ReentrantLock reentrantLock = new ReentrantLock(true);

    private static Condition condition = reentrantLock.newCondition();

    public static void main(String[] args) {
        for (int i = 1; i < 10; i++) {
            new Thread(new Task(), "task_" + i).start();
        }
    }

    private static void task() {
        try {
            reentrantLock.lock();
            System.out.println("ThreadName: " + Thread.currentThread().getName());
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }

    private static class TaskNotify implements Runnable {
        public void run() {
            try {
                reentrantLock.lock();
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    private static class Task implements Runnable {

        public void run() {
            task();
        }
    }
}
