package com.peony.message;

import com.alibaba.fastjson.JSONObject;
import com.peony.bean.ChatMapping;
import com.peony.bean.Client;
import com.peony.bean.MessageMode;
import com.peony.bean.OnlineClientMapping;
import com.peony.utils.StringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;

/**
 * 客服消息处理器
 * <p>
 * 客服消息发送给用户
 * 客服消息发送给用户的前提是必须已经建立连接
 */
public class CSMessageHanler implements MessageHandler {
    @Override
    public void handlerMessage(ChannelHandlerContext context, MessageMode message) {
        if (context == null || message == null) {
            return;
        }
        MessageMode toSendMsg = message;
        if (message.getTo() == null || StringUtil.isBlank(message.getTo().getCid())) {
            toSendMsg.setContent("需要指定用户才能发送消息哦");
            toSendMsg.setFrom(new Client("", ""));
            context.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(toSendMsg)));
            return;
        }
        HashMap<String, Channel> userMap = OnlineClientMapping.get().userMap;
        if (userMap == null || userMap.isEmpty() || userMap.get(message.getTo().getCid()) == null) {
            toSendMsg.setContent("该用户不在线");
            toSendMsg.setFrom(new Client("", ""));
            context.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(toSendMsg)));
            return;
        }
        //检测该用户是否和别的客服已连线
        String customerServiceId = ChatMapping.get().chatMap.get(message.getTo().getCid());
        if (StringUtil.isBlank(customerServiceId) || customerServiceId.equals(message.getFrom().getCid())) {
            Channel toChannel = userMap.get(message.getTo().getCid());
            toChannel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(toSendMsg)));
            //如果客服首次发消息给用户，缓存连接关系
            if (StringUtil.isBlank(customerServiceId)) {
                ChatMapping.get().chatMap.put(message.getTo().getCid(),message.getFrom().getCid());
            }
        } else {
            toSendMsg.setContent("该用户已跟客服[" + customerServiceId + "]连线");
            context.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(toSendMsg)));
            return;
        }

    }
}
