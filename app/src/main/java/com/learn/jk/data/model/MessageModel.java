package com.learn.jk.data.model;

public class MessageModel {
    String key;
    String text;
    String senderUID;
    String receiverUID;

    public MessageModel(String key, String text, String senderUID, String receiverUID) {
        this.key = key;
        this.text = text;
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
    }

    public MessageModel() {
    }

    public String getKey() {
        return key;
    }

    public String getText() {
        return text;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }
}
