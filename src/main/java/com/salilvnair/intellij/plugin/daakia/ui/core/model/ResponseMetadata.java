package com.salilvnair.intellij.plugin.daakia.ui.core.model;

public class ResponseMetadata {
    private String sizeText;
    private String timeTaken;
    private int statusCode;
    public ResponseMetadata(){}
    public ResponseMetadata(int statusCode, String timeTaken, String sizeText) {
        this.sizeText = sizeText;
        this.timeTaken = timeTaken;
        this.statusCode = statusCode;
    }

    public String getSizeText() {
        return sizeText;
    }

    public void setSizeText(String sizeText) {
        this.sizeText = sizeText;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
