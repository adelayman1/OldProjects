package com.learn.jk.data.model;

public class UserModel {
    private String date;
    private String name;
    private String hint;
    private String email;
    private String key;
    private String gender;
    private String photo;
    private String dateVisibility;
    private String whatLove;
    private boolean verified;
    private String accountType;
    private String websiteURL;
    private String day;
    private String month;
    private String year;

    public UserModel(String date, String name, String hint, String email, String key, String gender, String photo, String dateVisibility, String whatLove, boolean verified, String accountType, String websiteURL, String day, String month, String year) {
        this.date = date;
        this.name = name;
        this.hint = hint;
        this.email = email;
        this.key = key;
        this.gender = gender;
        this.photo = photo;
        this.dateVisibility = dateVisibility;
        this.whatLove = whatLove;
        this.verified = verified;
        this.accountType = accountType;
        this.websiteURL = websiteURL;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public UserModel() {
    }

    public boolean isVerified() {
        return verified;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public String getGender() {
        return gender;
    }

    public String getDateVisibility() {
        return dateVisibility;
    }

    public String getDate() {
        return date;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getHint() {
        return hint;
    }

    public String getEmail() {
        return email;
    }

    public String getKey() {
        return key;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getWhatLove() {
        return whatLove;
    }
}
