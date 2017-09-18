package com.fan.timeserver.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端收到请求后，进行异步处理，成功或失败后，都将执行以下方法
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

    /**
     * 接收成功
     * @param result        连接建立后，形成的通道channel
     * @param attachment    附件
     */
    @Override
    public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
        // 这时，如果有新的客户端接入成功，继续调用Server的accept方法，接受这个客户端连接，最终形成一个循环
        attachment.asynchronousServerSocketChannel.accept(attachment, this);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 三个参数：ByteBuffer          :   接收缓冲区，用于从异步channel中读取数据包；
        //         A attachment        :   异步channel携带的附件，通知回调的时候作为入参使用；
        //         CompletionHandler   :   接收通知回调的业务handler。
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.latch.countDown();// 唤醒服务端线程，让服务端线程执行完毕
    }
}
