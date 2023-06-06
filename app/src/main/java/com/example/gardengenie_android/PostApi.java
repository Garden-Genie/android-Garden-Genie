package com.example.gardengenie_android;

import com.example.gardengenie_android.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostApi {


    @POST("/register")
    Call<User> createPost(
            @Body SignupRequest signupRequest
    );

    @POST("/login")
    Call<LoginResponse> getLoginResponse(
            @Body LoginRequest loginRequest
    );
}
