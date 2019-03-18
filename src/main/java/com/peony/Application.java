package com.peony;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Application {



    protected final void testMethod(){
        System.out.println("testMethod");
    }
    //utf-8下，一个中文字符占3个字节
    //GB2312下，一个中文字符占2个字节
    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
        ArrayList<Object> list = new ArrayList<>();
        byte[] bytes_utf8 = new String("何文先").getBytes("utf-8");
        byte[] bytes_gb2312 = new String("何文先").getBytes("gb2312");
        System.out.println("UTF-8编码，一个中文字符占["+bytes_utf8.length/3+"]个字节");
        System.out.println("GB2312编码，一个中文字符占["+bytes_gb2312.length/3+"]个字节");


    }
}
