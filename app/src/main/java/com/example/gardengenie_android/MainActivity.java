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
    private static final String fileName = "garden-genie-b3c4d94d7071.json";

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
            Log.d("MainActivity", "이미지 URL : " + imageUrl);

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



}
