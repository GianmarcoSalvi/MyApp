package com.example.myapplication;

import com.example.myapplication.model.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ServiceAPI {
    @GET("users/")
    Call<List<User>> getUser(@QueryMap Map<String, String> params);
}
