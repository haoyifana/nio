package com.fan.timeserver.aio;

public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;

        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(asyncTimeServerHandler).start();
    }
}
