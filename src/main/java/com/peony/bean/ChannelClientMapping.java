package com.peony.bean;

import java.util.HashMap;

/**
 * 连接通道和client ID 的对应关系
 * channelId <---> cid
 */
public class ChannelClientMapping {

    public HashMap<String,String> map;

    private static ChannelClientMapping mapping;

    private ChannelClientMapping(){
        if (map == null){
            map = new HashMap<>();
        }
    }

    public static ChannelClientMapping get(){
        if (mapping == null){
            synchronized(ChannelClientMapping.class){
                if (mapping == null){
                    mapping = new ChannelClientMapping();
                }
            }
        }
        return mapping;
    }
}
