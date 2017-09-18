package com.fan.timeserver.bio.threadpool;

import com.fan.timeserver.bio.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 线程池支持的bio
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        final int port = 8080;

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                // 交给线程池来执行task
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (server != null) {
                System.out.println("The time server is close");
                server.close();
                server = null;
            }
        }
    }
}
