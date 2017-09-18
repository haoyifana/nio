package com.fan.timeserver.aio;

public class TimeClient {

    public static void main(String[] args) {
        final int port = 8080;

        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
