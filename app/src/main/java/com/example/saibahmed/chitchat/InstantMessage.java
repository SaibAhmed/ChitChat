package com.example.saibahmed.chitchat;

public class InstantMessage {

    private String author;
    private String message;

    public InstantMessage(String author, String message) {

        this.author = author;
        this.message = message;
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
