package com.peony.BIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MyBIOClient {
    public static void main(String[] args) {
        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress(8080);
        try {
            socket.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
