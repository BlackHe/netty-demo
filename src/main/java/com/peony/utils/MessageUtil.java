package com.peony.utils;

import com.peony.bean.MessageMode;

public class MessageUtil {

    /**
     * 验证消息格式合法性
     * @param message
     * @return
     */
    public static boolean verify(MessageMode message){
        if (message == null || message.getFrom() == null){
            return false;
        }
        if (StringUtil.isBlank(message.getContent(),message.getFrom().getCid(),message.getFrom().getCt())){
            return false;
        }
        return true;
    }
}
