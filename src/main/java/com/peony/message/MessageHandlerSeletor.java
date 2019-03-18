package com.peony.message;

import com.peony.bean.ClientType;
import com.peony.bean.MessageMode;

/**
 * 消息处理器选择器
 */
public class MessageHandlerSeletor {

    public static MessageHandler get(MessageMode message){
        String fromClinetType = message.getFrom().getCt();
        if (ClientType.CUSTOMER_SERVICE.equals(fromClinetType)){
            return new CSMessageHanler();
        }else if (ClientType.USER.equals(fromClinetType)){
            return new USMessageHandler();
        }
        return null;
    }
}
