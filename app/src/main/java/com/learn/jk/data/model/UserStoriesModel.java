package com.learn.jk.data.model;

import java.util.ArrayList;

import omari.hamza.storyview.model.MyStory;

/**
this class get all data about stories of custom users
 */
public class UserStoriesModel {
    ArrayList<MyStory> stories;
    UserModel user;

    public UserStoriesModel(ArrayList<MyStory> stories, UserModel user) {
        this.stories = stories;
        this.user = user;
    }

    public ArrayList<MyStory> getStories() {
        return stories;
    }

    public UserModel getUser() {
        return user;
    }
}
