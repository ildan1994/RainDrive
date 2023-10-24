package com.example.raindriveiter1_10.model;

public class IntroScreenItems {
    private String title;
    private String description;
    private int screenImg;

    public IntroScreenItems(String title, String description, int screenImg) {
        this.title = title;
        this.screenImg = screenImg;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getScreenImg() {
        return screenImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScreenImg(int screenImg) {
        this.screenImg = screenImg;
    }
}
