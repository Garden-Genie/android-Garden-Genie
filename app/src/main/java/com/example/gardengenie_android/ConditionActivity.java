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

public class ConditionActivity extends AppCompatActivity {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private TextView textCondition;
    private Token tokenInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);

        // OkHttp 클라이언트 인스턴스 가져오기
        OkHttpClient client = OkHttpClientProvider.getClient();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bitmap);

        // TODO : your-server-url
        String serverUrl = "http://your-server-url:8070/chat-gpt/question/advice";

        // spring 서버 통신
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.execute(serverUrl);

        textCondition = (TextView) findViewById(R.id.text_condition);

        // Token 클래스에서 token get
        tokenInstance = Token.getInstance();
        String token = tokenInstance.getToken();
        if (token != null) {
            // token 값
            Log.d("MusicActivity", "token 값 : " + token);
        } else {
            // token이 null인 경우
            Log.e("MusicActivity", "token 값이 null 입니다.");
        }
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

            Log.d("ConditionActivity", "서버로부터 받아온 result : " + result);
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

            textCondition.setText(contentValue);
        }
    }
}
