package com.example.imhungry3.Activity.Chat;

public class ChatClass {
    private String sender,reciver,textMsg,sendTime;

    public ChatClass(String sender, String reciver, String textMsg) {
        this.sender = sender;
        this.reciver = reciver;
        this.textMsg = textMsg;
    }
    public ChatClass(String sender, String reciver, String textMsg,String sendTime) {
        this.sender = sender;
        this.reciver = reciver;
        this.textMsg = textMsg;
        this.sendTime = sendTime;
    }

    public ChatClass() {
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getTextMsg() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }
}
