package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowTheImage extends AppCompatActivity {

    ImageView imageView;
    String ImageUrl;
    Bitmap bitmap;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_the_image);

        Intent intent = getIntent();
        ImageUrl = intent.getStringExtra("imageUrl");
        imageView = (ImageView)findViewById(R.id.ShowBigImage);

        Glide.with(this)
                .load(ImageUrl)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowTheImage.this);
                dialog.setTitle("要储存图像吗亲");
                dialog.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveImage(ImageUrl);
                    }
                });
                dialog.setNegativeButton("并不是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       finish();
                    }
                });
                dialog.show();
            }
        });
    }

    public void saveImage(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String dri = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/";
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String key = df.format(new Date());
                    file = new File(dri + key +".png");
                    URL imageUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection)imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    OutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                    Message message = new Message();
                    message.obj = "OK";
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            solveTheThread();
        }
    };

    public void solveTheThread(){
        Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
        finish();
    }
}
