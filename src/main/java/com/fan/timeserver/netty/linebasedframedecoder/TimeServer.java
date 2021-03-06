package com.fan.timeserver.netty.linebasedframedecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeServer {

    public void bind(int port) throws Exception {
        // 配置服务端的nio线程组
        // 专门用于网络事件的处理，实际上就是reactor线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();         // 用于服务端接受客户端的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();       // 用于进行SocketChannel的网络读写
        try {
            ServerBootstrap b = new ServerBootstrap();              // netty用于启动nio服务端的辅助启动类，目的是降低服务端的开发复杂度

            b.group(bossGroup, workerGroup)                         // 线程组 配置到启动类
                    .channel(NioServerSocketChannel.class)          // 配置channel
                    .option(ChannelOption.SO_BACKLOG, 1024)   // 配置tcp参数
                    .childHandler(new ChildChannelHandler());       // handler类，用于处理网络I/O事件
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();                  // 用于异步操作的通知回调

            // 等待服务端监听端口关闭， 服务端链路关闭之后main函数才退出
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源及相关资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            // 新增两个解码器, LineBasedFrameDecoder + StringDecoder组合就是按行切换的文本解码器
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));  // 以换行符为结束标志的解码器，支持携带结束符和不携带结束符两种解码方式，同时支持配置单行的最大长度
            socketChannel.pipeline().addLast(new StringDecoder());                        // 将接收到的对象转换成字符串，然后继续调用后面的handler
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        new TimeServer().bind(port);
    }
}
