package com.example.gardengenie_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PhotoResultActivity extends AppCompatActivity {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    TextView textPltName;
    private Token tokenInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);

        // OkHttp 클라이언트 인스턴스 가져오기
        OkHttpClient client = OkHttpClientProvider.getClient();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        textPltName = (TextView) findViewById(R.id.text_pltName);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bitmap);

        String serverUrl = BaseUrl.BASE_URL + "/chat-gpt/question/name";

        // spring 서버 통신
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.execute(serverUrl);

        // Token 클래스에서 token get
        tokenInstance = Token.getInstance();
        String token = tokenInstance.getToken();
        if (token != null) {
            // token 값
            Log.d("PhotoResultActivity", "token 값 : " + token);
        } else {
            // token이 null인 경우
            Log.e("PhotoResultActivity", "token 값이 null 입니다.");
        }

        // explain
        ImageButton btn_explain = (ImageButton) findViewById(R.id.btn_explain);
        btn_explain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ExplainActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });

        // music
        ImageButton btn_music = (ImageButton) findViewById(R.id.btn_music);
        btn_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });

        // poem
        ImageButton btn_poem = (ImageButton) findViewById(R.id.btn_poem);
        btn_poem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PoemActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });

        // condition
        ImageButton btn_condition = (ImageButton) findViewById(R.id.btn_condition);
        btn_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ConditionActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
            }
        });
    }

    private class HttpRequest extends AsyncTask<String, Void, String> {
        private OkHttpClient client = new OkHttpClient();
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String result = "";

            try {
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + tokenInstance.getToken())
                        .post(RequestBody.create("", JSON))  // 빈 요청 바디 생성
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    result = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("PhotoResultActivity", "서버로부터 받아온 result : " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // doInBackground 이후 실행
            String contentValue = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray choicesArray = jsonObject.getJSONArray("choices");
                JSONObject choiceObject = choicesArray.getJSONObject(0);
                JSONObject messageObject = choiceObject.getJSONObject("message");
                String responseText = messageObject.getString("content");

                // 'response: '와 "'"을 제거하여 '포인세티아'만 추출
                int startIndex = responseText.indexOf("'") + 1;
                int endIndex = responseText.lastIndexOf("'");
                contentValue = responseText.substring(startIndex, endIndex);

                // PlantName 클래스의 전역 변수에 저장
                PlantName.setPlantName(contentValue);
                Log.d("PhotoResultActivity", "plantName 값 : " + PlantName.getPlantName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            textPltName.setText(contentValue);
        }
    }



}
