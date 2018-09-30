package com.example.saibahmed.chitchat;

public class InstantMessage {

    private String author;
    private String message;

    public InstantMessage(String message, String author) {

        this.author=author;
        this.message=message;
    }

    //optional
    public InstantMessage(){

    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }


}
