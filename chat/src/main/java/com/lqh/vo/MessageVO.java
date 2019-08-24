package com.lqh.vo;

/**
 * 服务端与客户端通信的载体
 */
public class MessageVO {

    /**
     * 根据用户发送的来告知服务器该执行什么操作
     * 1:登陆  2:私聊   3:群聊
     */
    private Integer type;

    /**
     * 服务端与客户端发送的信息
     * xxx上线了
     */
    private String msg;

    /**
     * 要发送的客户端是谁
     */
    private String to;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
