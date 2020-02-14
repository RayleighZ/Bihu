package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bihu.NetTool.NewBeeTest;
import com.example.bihu.NetTool.UploadHelper;
import com.example.bihu.OpenAlbumTool.OpenCream;
import com.example.bihu.OpenAlbumTool.Open_Album;
import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetHead extends AppCompatActivity implements UploadHelper.Propose {

    Button Yes;
    Button OpenAlbum;
    Button openCream;

    ImageView preview;
    String Token;
    String headUrl;
    File file;
    File file2;
    Activity activity;
    public static final int CHOOSE_PHOTO = 2;
    static Open_Album open_album;
    private UploadHelper uploadHelper;
    private Uri imageUri;
    private OpenCream open_cream;
    private ProgressBar progressBar;
    private boolean shot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_head);
        activity = this;
        Intent intent = getIntent();
        Token = intent.getStringExtra("token");
        Yes = (Button) findViewById(R.id.Yes);
        OpenAlbum = (Button) findViewById(R.id.OpenPic);
        preview = (ImageView) findViewById(R.id.TestHead);
        open_album = new Open_Album(activity);
        openCream = (Button) findViewById(R.id.OpenCream);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        uploadHelper = new UploadHelper(this, this);

        open_cream = new OpenCream(this, this);

        openCream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SetHead.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SetHead.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    imageUri = open_cream.openCrema();
                }
            }
        });

        //检测是否拥有权限

        OpenAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(SetHead.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SetHead.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    open_album.openAlbum();
                }
            }
        });

        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(shot){
                    uploadHelper.postFile(uploadHelper.fileConvertToByteArray(file2));
                }else {
                    uploadHelper.postFile(uploadHelper.fileConvertToByteArray(file));
                }
            }
        });
    }

    public void setNetRequest() {
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("token", Token)
                                .add("avatar", headUrl)
                                .build();
                        Request request = new Request.Builder()
                                .post(requestBody)
                                .url("http://bihu.jay86.com/modifyAvatar.php")
                                .build();
                        Response response = client.newCall(request).execute();
                        String data = response.body().string();
                        Message message = new Message();
                        message.obj = data;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            paraJson(msg.obj.toString());
        }
    };

    public void paraJson(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            int status = jsonObject.getInt("status");
            if (status == 200) {
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();
                intent1.putExtra("url", headUrl);
                setResult(2, intent1);
                progressBar.setVisibility(View.GONE);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //打开相册方法

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    open_album.openAlbum();
                    //openAlbum();
                } else {
                    Toast.makeText(this, "宁拒绝了给与权限，介个是必须的", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2: {
                handleImageOnKitKat(data);
                break;
            }
            case 1: {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    preview.setImageBitmap(bitmap);
                    shot = true;
                    file2 = open_cream.getFile(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }

        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {

        String imagePath;
        imagePath = open_album.FindPath(data, this);
        setImage(imagePath);
    }


    private void setImage(String imagePath) {
        file = new File(imagePath);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        preview.setImageBitmap(bitmap);
    }

    @Override
    public void answer(String info) {
        headUrl = info;
        Log.d("Url", headUrl);
        setNetRequest();
    }
}
