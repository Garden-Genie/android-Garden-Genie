package com.example.gardengenie_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class MusicActivity extends AppCompatActivity {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private TextView textMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // OkHttp 클라이언트 인스턴스 가져오기
        OkHttpClient client = OkHttpClientProvider.getClient();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bitmap);

        // TODO : your-server-url
        String serverUrl = "http://your-server-url:8070/chat-gpt/question/music";

        // spring 서버 통신
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.execute(serverUrl);

        textMusic = (TextView) findViewById(R.id.text_music);
    }

    private class HttpRequest extends AsyncTask<String, Void, String> {
        private OkHttpClient client = new OkHttpClient();
//        LoginResponse loginResponse = new LoginResponse();
//        String token = loginResponse.getToken();

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String result = "";

            try {
                Request request = new Request.Builder()
                        .url(url)
//                        .addHeader("Authorization", "Bearer " + token)  // Add Authorization header with the token
                        .post(RequestBody.create("", JSON))  // 빈 요청 바디 생성
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    result = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("MusicActivity", "서버로부터 받아온 result : " + result);
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
                contentValue = choiceObject.getString("content");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            textMusic.setText(contentValue);
        }
    }
}
