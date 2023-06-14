package com.example.gardengenie_android;

import static android.app.Activity.RESULT_OK;

import static com.example.gardengenie_android.LoginActivity.token;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private Bitmap imageBitmap;

    // imageUrl 변수
    static String imageUrl;
    private static final int REQUEST_IMAGE_CODE = 101;
    static String gcsUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ImageButton btn_camera = view.findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        return view;
    }

    // newInstance constructor for creating fragment with arguments
    public static MainFragment newInstance(int page, String title) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    //사진 찍기
    public void takePicture() {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (imageTakeIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CODE);
        }
    }

    //결과값 가져오기
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            // 파일 선택 후 GCS 업로드 작업 시작
            new MainFragment.UploadTask().execute();
            Log.d("MainFragment", "이미지를 Google Cloud Storage에 업로드했습니다.");

            TokenSender.sendToken(token, gcsUrl);

            // 이미지 intent
            Intent intent = new Intent(getActivity().getApplicationContext(), PhotoResultActivity.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("image", byteArray);

            startActivity(intent);
        }
        else {
            // 사진 촬영이 실패하거나 사용자가 취소한 경우에 대한 처리
            Log.d("MainFragment", "사진 촬영이 실패했거나 취소되었습니다.");
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
            AssetManager assetManager = getActivity().getAssets();
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
            imageUrl = blob.getMediaLink();
            System.out.println("이미지 url : " + imageUrl);

            URL url = new URL(imageUrl);

            String host = url.getHost(); // "storage.googleapis.com"
            String path = url.getPath(); // "/download/storage/v1/b/garden_genie_image/o/image_1686653529088.jpg"

            String object = path.split("/")[7]; // "garden_genie_image"
            gcsUrl = "gs://garden_genie_image/"+ object; // "gs://garden_genie_image/o/image_1686653529088.jpg"

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