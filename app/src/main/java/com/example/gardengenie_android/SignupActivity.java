package com.example.gardengenie_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {
    Retrofit retrofit;
    PostApi postApi;
    Call<List<User>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText editText = findViewById(R.id.signup_email);
        EditText editText1 = findViewById(R.id.signup_id);
        EditText editText2 = findViewById(R.id.signup_name);
        EditText editText3 = findViewById(R.id.signup_pwd);

        Button signup = findViewById(R.id.btn_signup);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editText.getText().toString();
                String id = editText1.getText().toString();
                String name = editText2.getText().toString();
                String pwd = editText3.getText().toString();

                if (email.length() != 0 && id.length() >= 5
                        && name.length() != 0 && pwd.length() >= 8) {

                    retrofit = new Retrofit.Builder()
                            .baseUrl("http://192.168.0.135:8070")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    postApi = retrofit.create(PostApi.class);

                    Call<User> call = postApi.createPost(name, id, pwd, email);

                    Toast.makeText(getApplicationContext(), "회원가입이 원료되었습니다.", Toast.LENGTH_LONG).show();

                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (!response.isSuccessful()) {



                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {

                        }
                    });


                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);



                } else {
                    Toast.makeText(getApplicationContext(), "아이디는 5자 이상, 비밀번호는 8자 이상으로 작성해주세요.", Toast.LENGTH_LONG).show();

                }
            }
        });



    }
}