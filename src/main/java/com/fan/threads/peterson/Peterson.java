package com.fan.threads.peterson;

/**
 * 当一个进程在临界区内执行，没有其他进程被允许在临界区内执行
 */
public class Peterson implements Runnable {

    private static boolean[] in = {false, false};
    private static volatile int turn = -1;

    public static void main(String[] args) {
        new Thread(new Peterson(0), "Thread - 0").start();
        new Thread(new Peterson(1), "Thread - 1").start();
    }

    private final int id;

    public Peterson(int i) {
        this.id = i;
    }

    private int other() {
        return id == 0 ? 1 : 0;
    }

    @Override
    public void run() {
        in[id] = true;              // id进入

        turn = other();             // 标记轮到另一个

        while (in[other()] && turn == other()) {                // 另一个进入 且 轮到另一个执行 就等待(阻塞)
            System.out.println("[" + id + "] - Waiting...");
        }

        // 临界区
        System.out.println("[" + id + "] - Working (" + ((!in[other()]) ? "other done" : "my turn") + ")");

        in[id] = false;             // id完成，退出
    }
}