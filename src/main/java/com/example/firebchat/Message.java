package com.example.firebchat;

import java.util.Date;

/**
 * Created by ПОДАРУНКОВИЙ on 21.08.2017.
 */

public class Message {
    String user;
    long time;
    String message;

    public Message(String user, String message) {
        this.user = user;
        this.message = message;


        this.time = new Date().getTime();
    }
    public Message(){

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
