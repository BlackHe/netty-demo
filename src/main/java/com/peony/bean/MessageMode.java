package com.peony.bean;

import lombok.Data;

@Data
public class MessageMode {

    /**
     * 消息id
     */
    private String msgId;

    /**
     * 消息时间
     * yyyy-mm-dd hh24:mm:ss
     */
    private String time;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息来源
     */
    private Client from;


    /**
     * 消息去向
     */
    private Client to;
}
