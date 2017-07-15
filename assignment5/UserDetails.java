package com.example.shwetashahane.assignment5;

/**
 * Created by shwetashahane on 4/14/17.
 */

public class UserDetails {
    static public String username = "";
    static public String password = "";
    static public String chatWith = "";

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserDetails.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserDetails.password = password;
    }

    public static String getChatWith() {
        return chatWith;
    }

    public static void setChatWith(String chatWith) {
        UserDetails.chatWith = chatWith;
    }
}
