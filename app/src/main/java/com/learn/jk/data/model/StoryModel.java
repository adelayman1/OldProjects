package com.learn.jk.data.model;

public class StoryModel {
    long num;
    String uid;

    public StoryModel(long num, String uid) {
        this.num = num;
        this.uid=uid;
    }


    public long getNum() {
        return num;
    }

    public String getUid() {
        return uid;
    }
}
