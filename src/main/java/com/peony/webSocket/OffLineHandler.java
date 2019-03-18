package com.peony.webSocket;

import com.peony.bean.ChannelClientMapping;
import com.peony.bean.ChatMapping;
import com.peony.bean.OnlineClientMapping;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端下线处理器
 */
public class OffLineHandler {

    /**
     * 移除线上的客户端
     * @param context
     */
    public static void removeClient(ChannelHandlerContext context){
        //通道id
        String channelId = context.channel().id().asShortText();
        //客户端id
        String clientId = ChannelClientMapping.get().map.get(channelId);

        //1.删除对话连接器中的该客户端
        ChatMapping.get().chatMap.remove(clientId);

        //2.删除在线管理器中的该客户端
        OnlineClientMapping.get().removeByClientId(clientId);

        //2.删除信道管理器中的该客户端
        ChannelClientMapping.get().map.remove(channelId);

    }
}
