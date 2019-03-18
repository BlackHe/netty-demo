package com.peony.bean;

import java.util.HashMap;

/**
 * 已经建立对话连接的客户端之间的映射
 * 用户id <---> 客服id
 * 如果涉及到转接，修改映射即可
 */
public class ChatMapping {

    private static ChatMapping mapping;

    public HashMap<String, String> chatMap;

    private ChatMapping() {
        if (chatMap == null) {
            chatMap = new HashMap<>();
        }
    }

    public static ChatMapping get() {
        if (mapping == null) {
            synchronized (ChatMapping.class) {
                if (mapping == null) {
                    mapping = new ChatMapping();
                }
            }
        }
        return mapping;
    }
}
