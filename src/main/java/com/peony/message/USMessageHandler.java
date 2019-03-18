package com.peony.message;

import com.alibaba.fastjson.JSONObject;
import com.peony.bean.Client;
import com.peony.bean.MessageMode;
import com.peony.bean.OnlineClientMapping;
import com.peony.utils.StringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户消息处理器
 *
 * 接收到用户的消息
 * 将此消息响应给单个客服或所有客服
 */
public class USMessageHandler implements MessageHandler {
    @Override
    public void handlerMessage(ChannelHandlerContext context, MessageMode message) {
        if (context == null || message == null){
            return;
        }
        MessageMode theMessage = message;
        //没有在线客服
        HashMap<String, Channel> csChannelMap = OnlineClientMapping.get().customServiceMap;
        if (csChannelMap == null || csChannelMap.isEmpty()){
            theMessage.setContent("亲，客服小姐姐还没上线，请拨打客服电话或稍后再试!");
            theMessage.setFrom(new Client("",""));
            context.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message)));
            return;
        }
        theMessage = message;
        //有在线客服，但没有建立客服连线，将此消息全发给在线客服
        if (message.getTo() == null || StringUtil.isBlank(message.getTo().getCid())){
            for (Map.Entry<String, Channel> entry : csChannelMap.entrySet()){
                entry.getValue().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(theMessage)));
            }
        //有在线客服且已连线,发给该客服
        }else {
            Channel channel = csChannelMap.get(message.getTo().getCid());
            if (channel == null){
                theMessage.setContent("亲，客服小姐姐正在路上，请稍等!");
                theMessage.setFrom(new Client("",""));
                context.channel().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(theMessage)));
            }
            channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(message)));
        }
    }
}
