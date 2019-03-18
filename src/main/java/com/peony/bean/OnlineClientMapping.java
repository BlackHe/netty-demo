package com.peony.bean;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * 在线的客户端id和channel的对应关系
 */
public class OnlineClientMapping {


    private static OnlineClientMapping mapping;


    /**
     * 在线客服
     */
    public HashMap<String, Channel> customServiceMap;


    /**
     * 在线用户
     */
    public HashMap<String, Channel> userMap;


    private OnlineClientMapping() {
        if (customServiceMap == null) {
            customServiceMap = new HashMap<>();
        }
        if (userMap == null) {
            userMap = new HashMap<>();
        }
    }

    public void removeByClientId(String clientId) {
        userMap.remove(clientId);
        customServiceMap.remove(clientId);
    }

    public static OnlineClientMapping get() {
        if (mapping == null) {
            synchronized (OnlineClientMapping.class) {
                if (mapping == null) {
                    mapping = new OnlineClientMapping();
                }
            }
        }
        return mapping;
    }

}
