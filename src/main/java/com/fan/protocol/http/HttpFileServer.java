package com.fan.protocol.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

    private static final String DEFAULT_URL = "/";

    public void run(final int port, final String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());                            // http请求消息解码器
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));  // 将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());                           // http响应解码器
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());                           // 支持异步发送的最大码流(这里用来发送文件)
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));                 // 自定义handler
                        }
                    });

            ChannelFuture future = b.bind("192.168.1.4", port).sync();
            System.out.println("HTTP 文件目录服务器启动，网址是 : http://192.168.1.4:" + port + url);

            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String url = DEFAULT_URL;

        new HttpFileServer().run(port, url);
    }
}
