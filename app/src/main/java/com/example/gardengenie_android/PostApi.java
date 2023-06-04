package com.example.gardengenie_android;

import com.example.gardengenie_android.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostApi {

    @FormUrlEncoded
    @POST("User")
    Call<User> createPost(
            @Field("user_name") String user_name,
            @Field("user_id") String user_id,
            @Field("user_pwd") String user_pwd,
            @Field("user_email") String user_email
    );
}
