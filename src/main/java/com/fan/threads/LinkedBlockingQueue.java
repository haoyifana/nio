package com.fan.threads;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedBlockingQueue {

    private int maxSize = 10;

    private AtomicInteger currentSize = new AtomicInteger();

    private final ReentrantLock putLock = new ReentrantLock();
    private final ReentrantLock getLock = new ReentrantLock();

    private Condition putCond = putLock.newCondition();
    private Condition getCond = getLock.newCondition();

    private AtomicInteger getWaitCount = new AtomicInteger();
    private AtomicInteger putWaitCount = new AtomicInteger();

    private Node first;
    private Node last;

    private static class Node {
        String val;
        Node prev;
        Node next;

        public Node(String val) {
            this.val = val;
        }
    }

    public LinkedBlockingQueue(int maxSize) {
        this.maxSize = maxSize;
        currentSize.set(0);
    }

    /**
     * 入队操作
     * @param val
     */
    public void put(String val) throws InterruptedException {
        try {
            putLock.lock();
            while (currentSize.intValue() >= this.maxSize) {
                putWaitCount.incrementAndGet();
                putCond.await();
            }
            Node node = new Node(val);
            if (currentSize.intValue() == 0 && first == null && last == null) {
                first = node;
                last = first;
            } else {
                last.next = node;
                last = node;
            }
            currentSize.incrementAndGet();
            try {
                getLock.lock();
                if (getWaitCount.intValue() > 0) {
                    getCond.signal();
                    getWaitCount.decrementAndGet();
                }
            } finally {
                getLock.unlock();
            }
        } finally {
            putLock.unlock();
        }
    }

    /**
     * 出队操作
     * @return
     */
    public String take() throws InterruptedException {
        try {
            getLock.lock();
            while (currentSize.intValue() <= 0) {
                getWaitCount.incrementAndGet();
                getCond.await();
            }

            if (last == null) {
                throw new RuntimeException("get error!!!");
            }
            Node node = first;
            first = first.next;
            currentSize.decrementAndGet();
            try {
                putLock.lock();
                if (putWaitCount.intValue() > 0) {
                    putCond.signal();
                    putWaitCount.decrementAndGet();
                }
            } finally {
                putLock.unlock();
            }
            return node.val;
        } finally {
            getLock.unlock();
        }
    }
}
