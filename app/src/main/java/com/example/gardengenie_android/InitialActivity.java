package com.example.gardengenie_android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class InitialActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private Button initial_signup, initial_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        tts = new TextToSpeech(this, this);



        initial_login = findViewById(R.id.initial_login);
        initial_signup = findViewById(R.id.initial_signup);
        initial_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                startActivity(intent);

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(){
        CharSequence text = "가든지니를 이용하기 위해서는 회원가입이 필요합니다. 회원가입을 하시려면 상단 버튼, 로그인을 하시려면 중간 버튼, 비회원으로 이용하시려면 하단 버튼을 클릭해주시거나 원하시는 기능을 말씀해주세요. ";
        tts.setPitch((float) 0.6);
        tts.setSpeechRate((float) 0.1);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @Override
    protected void onDestroy() {
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int i) {
        if(i == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREA);

            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS", "This");
            }else{
                speakOut();
            }
        }

            else {
                Log.e("TTS", "failed");
            }
        }

}