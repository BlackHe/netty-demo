package com.peony.utils;

public class StringUtil {

    /**
     * 判断可变字符串数组中是否有空元素
     * @param params
     * @return
     */
    public static boolean isBlank(String... params){
        for (String param : params){
            if (param == null || param.isEmpty()){
                return true;
            }
        }
        return false;
    }

}
