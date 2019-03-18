package com.peony.BIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * BIO通信demo
 */
public class MyBIOSocketServer {

    public static void main(String[] args) throws Exception {
        final ServerSocket socket = new ServerSocket();
        SocketAddress socketAddress = new InetSocketAddress(8080);
        socket.bind(socketAddress);
        byte[] bytes = new byte[1024];
        byte[] bytes1 = new byte[1024];
        byte[] bytes2 = new byte[1024];
        while (true) {
            Socket client = socket.accept();
            new ClientSocketHandler(client).start();
        }

    }
    static class ClientSocketHandler extends Thread{
        private Socket socket;
        public ClientSocketHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            handler(socket);
        }

        private void handler(Socket client) {
            System.out.println("[客户端] "+ client.getRemoteSocketAddress());
            OutputStream out = null;
            InputStream in = null;
            try {
                in = client.getInputStream();
                in.read();
                out = client.getOutputStream();
                out.write("hello \n world".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
