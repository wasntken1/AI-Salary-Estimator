package com.example.projekuas.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
}