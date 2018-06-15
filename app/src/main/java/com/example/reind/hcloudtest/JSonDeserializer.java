package com.example.reind.hcloudtest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSonDeserializer{
    public User Deserialize(String JSON){

        Gson gson = new GsonBuilder().create();
        User user=gson.fromJson(JSON, User.class);
        return user;
        }
}
