package com.peony.utils;

public class Log {

    private static Log loga = new Log();
    private static Log logb = new Log();


    public static  void log(String string){
        System.out.println(string);
    }

    public static void test() throws Exception{

    }

    //静态块
    static {
        log("static code block");
    }


    public static void main(String[] args) {
        Log log = new Log();
    }


    //构造快
    {
        log("construct code block");
    }
}
