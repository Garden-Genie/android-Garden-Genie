package com.example.gardengenie_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.net.URL;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_camera;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private byte[] imageBytes;

    private static final int REQUEST_IMAGE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        btn_camera = findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    //사진 찍기
    public void takePicture() {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CODE);
        }
    }

    //결과값 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            Log.d("MainActivity", "imageView에 이미지를 설정했습니다.");

            // 파일 선택 후 GCS 업로드 작업 시작
            new UploadTask().execute();
            Log.d("MainActivity", "이미지를 Google Cloud Storage에 업로드했습니다.");

//            // Flask 서버로 이미지 전송을 위해
//            // 이미지를 JPEG 형식으로 압축하여 바이트 배열로 변환
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            imageBytes = baos.toByteArray();
//
//            // HTTP 요청 생성 후 Flask 서버로 전송
//            new NetworkTask().execute();


        }
        else {
            // 사진 촬영이 실패하거나 사용자가 취소한 경우에 대한 처리
            Log.d("MainActivity", "사진 촬영이 실패했거나 취소되었습니다.");
        }
    }

    // AsyncTask 클래스 정의
    private class UploadTask extends AsyncTask<Void, Void, Void> {
        // 백그라운드 작업 수행
        @Override
        protected Void doInBackground(Void... voids) {
            // GCS에 파일 업로드 작업 수행
            uploadImageToStorage(imageBitmap);

            return null;
        }
    }

    // GCS 버킷 이름
    private static final String BUCKET_NAME = "garden_genie_image";
    // GCS 인증 정보 파일 이름
    private static final String fileName = "garden-genie-de5ba7f29f1e.json";

    // 이미지를 Google Cloud Storage에 업로드하는 메소드
    private void uploadImageToStorage(Bitmap imageBitmap) {
        // 이미지를 바이트 배열로 변환
        byte[] imageData = getByteArrayFromBitmap(imageBitmap);

        // GCS 인증 정보 로드
        try {
            // AssetManager를 통해 인증 정보 파일 열기
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(fileName);

            // GoogleCredentials 객체 생성
            GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);

            // GCS 클라이언트 초기화
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            // GCS 버킷 참조
            Bucket bucket = storage.get(BUCKET_NAME);

            // 이미지 파일 이름 생성
            String imageName = "image_" + System.currentTimeMillis() + ".jpg";

            // GCS에 이미지 업로드
            Blob blob = bucket.create(imageName, imageData);

            // 업로드된 이미지의 URL 가져오기
            String imageUrl = blob.getMediaLink();
            System.out.println("이미지 url : " + imageUrl);

            // 이미지 URL을 flask로 전송
            sendImageUrlToFlask(imageUrl);


        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    // Bitmap을 바이트 배열로 변환하는 유틸리티 메서드
    private byte[] getByteArrayFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    // 이미지 URL을 flask 서버로 전송하는 메서드
    private void sendImageUrlToFlask(String imageUrl) {
        try {
            // TODO: your-local-ip-address를 로컬 IP 주소(ipconfig)로 교체한 후 실행
            // Flask 서버 엔드포인트 URL
            String url = "http://your-local-ip-address/upload_image_url";

            // HttpURLConnection 설정
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON 데이터 생성
            String json = "{\"image_url\": \"" + imageUrl + "\"}";

            // JSON 데이터 전송
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(json);
            writer.flush();
            writer.close();
            outputStream.close();

            // 서버로부터의 응답 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 전송 성공
                Log.d("MainActivity", "Flask 이미지 URL 전송이 성공했습니다.");
            } else {
                // 전송 실패
                Log.d("MainActivity", "Flask 이미지 URL 전송이 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private class NetworkTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            // 네트워크 작업 수행 (백그라운드 작업)
//            try {
//                // TODO: your-local-ip-address를 로컬 IP 주소(ipconfig)로 교체한 후 실행
//                URL url = new URL("http:// .. ");
//
//                // HTTP 연결 설정
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setDoOutput(true);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "image/jpeg");
//                conn.setRequestProperty("Content-Length", String.valueOf(imageBytes.length));
//
//                // 이미지 데이터 전송
//                OutputStream os = conn.getOutputStream();
//                os.write(imageBytes);
//                os.flush();
//                os.close();
//
//                // 서버 응답 코드
//                int responseCode = conn.getResponseCode();
//                System.out.println("Flask 서버 응답 코드: " + responseCode);
//
//                // 응답이 성공적으로 처리되었을 경우 (= 이미지 전송 성공)
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    Log.d("MainActivity", "응답이 성공하였습니다.");
//                }
//                // 응답이 실패한 경우 (= 이미지 전송 실패)
//                else {
//                    Log.d("MainActivity", "응답이 실패하였습니다.");
//                }
//            } catch (IOException e) {
//                // 예외 처리 코드 작성
//                e.printStackTrace();
//            }
//            return null;
//        }
//        // 백그라운드 작업 완료 후 자동 호출
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            // 네트워크 작업 완료 후 실행할 코드 작성
//            Log.d("MainActivity", "네트워크 작업이 완료되었습니다.");
//        }
//    }

}
