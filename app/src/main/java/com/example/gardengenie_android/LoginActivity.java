package com.example.gardengenie_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private RetrofitClient retrofitClient;
    private PostApi postApi;
    private Token tokenInstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText login = findViewById(R.id.login_id);
        EditText pwd = findViewById(R.id.login_pwd);

        Button btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginResponse();
            }
        });

    }

    public void LoginResponse(){
        EditText login = findViewById(R.id.login_id);
        EditText pwd = findViewById(R.id.login_pwd);
        Button btn_login = findViewById(R.id.btn_login);

        String userID = login.getText().toString().trim();
        String userPassword = pwd.getText().toString().trim();



        LoginRequest loginRequest = new LoginRequest(userID, userPassword);

        retrofitClient = RetrofitClient.getInstance();
        postApi = RetrofitClient.getRetrofitInterface();

        postApi.getLoginResponse(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("retrofit", "Data fetch success");
                if (response.isSuccessful() && response.body()!=null){
                    LoginResponse result = response.body();
                    
                    String resultCode = result.getResultCode();
                    
                    String token = result.getToken();

                    // Token 클래스에 token 값 저장
                    tokenInstance = Token.getInstance();
                    tokenInstance.setToken(token);

                    Log.d("token", result.getToken());
                    String success = "200";
                    String errorPw = "400";
                    String errorID = "300";
                    
                    if (token != null){
                        setPreference(token, token);
                        Toast.makeText(LoginActivity.this, userID + "님 환영합니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        ActivityCompat.finishAffinity(LoginActivity.this);
                    }

                    else {
                        Toast.makeText(LoginActivity.this, "아이디 혹은 비밀번호를 확인하십시오.", Toast.LENGTH_LONG).show();
                    }




                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });



    }

    private void setPreference(String token, String token1) {
    }

}