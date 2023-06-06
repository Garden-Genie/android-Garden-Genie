package com.example.gardengenie_android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetApi {
    @GET("User")
    Call<List<User>> getUser();
}