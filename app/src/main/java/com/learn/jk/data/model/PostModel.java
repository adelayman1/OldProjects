package com.learn.jk.data.model;

import java.io.Serializable;

public class PostModel implements Serializable {
    private String uid;
    private String key;
    private long time;
    private String hint;
    private String image;
    private String type;
    private String video;
    private String visible;
    private int commentEnable;

    public PostModel() {
    }

    public PostModel(String uid, String key, long time, String hint, String image, String type, String visible, int commentEnable) {

        this.uid = uid;
        this.key = key;
        this.time = time;
        this.hint = hint;
        this.image = image;
        this.type = type;
        this.visible = visible;
        this.commentEnable = commentEnable;
    }

    public PostModel( String uid, String key, long time, String hint, String image, String type, String video, String visible, int commentEnable) {

        this.uid = uid;
        this.key = key;
        this.time = time;
        this.hint = hint;
        this.image = image;
        this.type = type;
        this.video = video;
        this.visible = visible;
        this.commentEnable = commentEnable;
    }

    public String getType() {
        return type;
    }

    public String getVideo() {
        return video;
    }

    public int getCommentEnable() {
        return commentEnable;
    }

    public String getVisible() {
        return visible;
    }


    public String getUid() {
        return uid;
    }

    public String getKey() {
        return key;
    }

    public long getTime() {
        return time;
    }

    public String getHint() {
        return hint;
    }

    public String getImage() {
        return image;
    }
}
