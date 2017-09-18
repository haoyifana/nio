package com.fan.timeserver.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 传统的阻塞bio
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        final int port = 8080;

        ServerSocket server = null;

        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port: " + port);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
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
