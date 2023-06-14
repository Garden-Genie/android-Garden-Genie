package com.example.gardengenie_android;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TokenSender {
    private static final String TAG = "TokenSender";

    // TODO : flask-server-url
    private static final String SERVER_URL = "http://flask-server-url";
    private static final String AUTH_HEADER = "Authorization";

    public static void sendToken(String token, String imgurl) {
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
                    Log.e("d", token);

                    // 헤더에 토큰 추가
                    connection.setRequestProperty("Authorization", "Bearer " + token);

//                    //quest Body에 JSON 데이터 추가
//                    String json = "{\"image_url\":\"gs://garden_genie_image/poinsettia1.png\"}";
                    connection.setDoOutput(true);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(imgurl);
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

        task.execute(token);
    }
}
