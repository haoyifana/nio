package com.fan.timeserver.nio;

public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);

        // 启动一个线程负责 Selector 轮询
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}
