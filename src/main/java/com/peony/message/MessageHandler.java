package com.peony.message;

import com.peony.bean.MessageMode;
import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理器
 */
public interface MessageHandler {

    void handlerMessage(ChannelHandlerContext context, MessageMode message);
}
