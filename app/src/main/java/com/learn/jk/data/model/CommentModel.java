package com.learn.jk.data.model;

public class CommentModel {
    String commentText;
    String key;
    String userUID;

    public CommentModel(String commentText, String key, String userUID) {
        this.commentText = commentText;
        this.key = key;
        this.userUID = userUID;
    }

    public CommentModel() {
    }

    public String getCommentText() {
        return commentText;
    }

    public String getKey() {
        return key;
    }

    public String getUserUID() {
        return userUID;
    }
}
