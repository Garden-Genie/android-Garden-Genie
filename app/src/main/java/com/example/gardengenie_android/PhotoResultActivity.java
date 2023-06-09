package com.example.gardengenie_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;


public class PhotoResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView.setImageBitmap(bitmap);

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




}
