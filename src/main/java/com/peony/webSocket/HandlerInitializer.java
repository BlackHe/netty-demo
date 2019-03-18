package com.peony.webSocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 初始化handler链
 */
public class HandlerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //http协议编解码处理器
        pipeline.addLast("http-codec",new HttpServerCodec());
        //以块的方式读写
        pipeline.addLast("http-chunked",new ChunkedWriteHandler());
        //聚合请求或响应内容到FullHttpRequest或FullHttpResponse
        pipeline.addLast("aggregator",new HttpObjectAggregator(65535));
        //自定义的websocket处理器
        pipeline.addLast("webSocketServerHandler",new WebSocketServerHandler());
    }
}
