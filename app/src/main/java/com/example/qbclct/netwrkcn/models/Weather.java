package com.example.qbclct.netwrkcn.models;

/**
 * Created by QBCLCT on 18/7/16.
 */
public class Weather {
    private String language;
    private String date;
    private String text;
    private String imgUrl;

    public String getimgUrl() {
        return imgUrl;
    }

    public void setimgUrl(String region) {
        this.imgUrl = region;
    }

    public String getText() {
        return text;
    }

    public void setText(String country) {
        this.text = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String city) {
        this.date = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



}
