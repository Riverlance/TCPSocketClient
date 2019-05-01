package com.tcpsocketclient;

import java.util.ArrayList;

public class User {
    String username;
    ArrayList<Message> messages = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }
}
