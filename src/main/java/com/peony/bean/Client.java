package com.peony.bean;

import lombok.Data;

@Data
public class Client {

    public Client(String ct,String cid){
        this.ct = ct;
        this.cid = cid;
    }

    /**
     * client type
     * 客户端类型
     */
    private String ct;

    /**
     * client id
     * 客户端ID
     */
    private String cid;

    public void setCt(String ct) {
        this.ct = ct == null ? "" : ct;
    }

    public void setCid(String cid) {
        this.cid = cid == null ? "" : cid;
    }
}
