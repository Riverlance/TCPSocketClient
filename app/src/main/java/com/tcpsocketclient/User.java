package com.tcpsocketclient;

import android.util.Pair;

import java.util.ArrayList;

public class User {
    String username;
    ArrayList<Pair<String, String>> messages = new ArrayList<>(); // Keys: <Self Username, Message>

    public User(String username) {
        this.username = username;
    }
}
