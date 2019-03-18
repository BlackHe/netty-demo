package com.peony.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebSocketServer {

    public void start(int port) throws Exception{
        //接受请求的主线程
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        //处理请求的业务线程
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HandlerInitializer());
            //绑定端口
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("netty server is start at port:  "+ port);
            channel.closeFuture().sync();
        }finally {
            //优雅的终止线程，释放资源
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new WebSocketServer().start(8080);
    }
}
