package com.peony.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * NIO通信demo
 */
public class MyNIOSocketServer {
    public static void main(String[] args) throws Exception{
        ServerSocketChannel channel = ServerSocketChannel.open();
        //设置服务端channel为非阻塞模式
        channel.configureBlocking(false);
        ServerSocket socket = channel.socket();
        //监听端口
        socket.bind(new InetSocketAddress(8080));
        //获取多路服务器的实例
        Selector selector = Selector.open();
        //将channel注册到多路复用器上，监听accept事件
        channel.register(selector, SelectionKey.OP_ACCEPT);
        while(true){
            //就绪的key
            if ((selector.select()) == 0){
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                handler(key);
                iterator.remove();
            }

        }
    }
    public static void handler(SelectionKey key){
        try{
            if (key.isAcceptable()) {
                handlerAccept(key);
                key.channel().register(key.selector(),SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                handlerRead(key);
                key.channel().register(key.selector(),SelectionKey.OP_WRITE);
            }
            if (key.isWritable()) {
                handlerWrite(key);
            }
        }catch (ClosedChannelException e){
            e.printStackTrace();
        }

    }

    private static void handlerWrite(SelectionKey key) {
    }

    private static void handlerRead(SelectionKey key) {
    }

    private static void handlerAccept(SelectionKey key) {
    }
}
