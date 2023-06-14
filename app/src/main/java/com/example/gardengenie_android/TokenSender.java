package com.example.gardengenie_android;

import static com.example.gardengenie_android.MainFragment.gcsUrl;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TokenSender {
    private static final String TAG = "TokenSender";
    private static final String SERVER_URL = BaseUrl.FLASK_URL;
    private static final String AUTH_HEADER = "Authorization";
    static String image;

    public static void sendToken(String t, String imgurl) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    // URL 설정
                    URL url = new URL(SERVER_URL);

                    // HttpURLConnection을 통해 연결 설정
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");

                    connection.setRequestProperty("Content-Type", "application/json");
                    Log.e("d", t);

                    // 헤더에 토큰 추가
                    connection.setRequestProperty("Authorization", "Bearer " + t);

                    String img = "{\"image_url\":\"" + gcsUrl + "\"}";

                    connection.setDoOutput(true);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(img);
                    Log.d(TAG, gcsUrl);
                    outputStream.flush();
                    outputStream.close();

                    // 서버 응답 확인
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 성공적으로 요청된 경우, 응답 데이터 읽기
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }

                        bufferedReader.close();
                        inputStream.close();
                        Log.d("respose log", response.toString());

                        try {
                            // JSON 응답 문자열
                            String jsonResponse = response.toString();

                            // JSONObject 생성
                            JSONObject jsonObject = new JSONObject(jsonResponse);

                            // "plant_name" 키의 값을 추출
                            String plantName = jsonObject.getString("plant_name");
                            image = plantName;

                            // 추출한 값을 출력하거나 원하는 대로 사용
                            Log.d("PlantName", plantName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        return response.toString();

                    } else {
                        // 요청 실패한 경우
                        Log.e(TAG, "Request failed. Response Code: " + responseCode);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                // 서버 응답 처리
                if (result != null) {
                    Log.d(TAG, "Server Response: " + result);
                } else {
                    Log.e(TAG, "Server Response is null");
                }
            }
        };

        task.execute(t);
    }


}
