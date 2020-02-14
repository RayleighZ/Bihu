package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.bihu.SetHead.CHOOSE_PHOTO;

public class AskQuestion extends AppCompatActivity implements UploadHelper.Propose {
    EditText title;
    EditText questions;
    Button send;
    Button upImage;
    Button shotTheImage;
    String token;
    String info;
    String headUrl = "";
    File file;
    ImageView theImage;
    Activity activity;
    static Open_Album open_album;
    private OpenCream open_cream;
    private UploadHelper uploadHelper;
    private Uri imageUri;
    int status;
    int uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);
        title = (EditText) findViewById(R.id.title2);
        questions = (EditText) findViewById(R.id.question2);
        send = (Button) findViewById(R.id.askquestion);
        upImage = (Button) findViewById(R.id.button3);
        theImage = (ImageView) findViewById(R.id.UpImageForAsk);
        shotTheImage = (Button) findViewById(R.id.ShotTheImageForAsk);
        activity = this;
        open_album = new Open_Album(activity);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        uid = intent.getIntExtra("uid", -1);
        open_cream = new OpenCream(this, this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNetRequest(AskQuestion.this);
            }
        });

        shotTheImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AskQuestion.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AskQuestion.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    imageUri = open_cream.openCrema();
                }
            }
        });

        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AskQuestion.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AskQuestion.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    open_album.openAlbum();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    open_album.openAlbum();
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
            case CHOOSE_PHOTO: {
                handleImageOnKitKat(data);
                upImage.setText("再上传一张");
                break;
            }
            case 1: {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    theImage.setImageBitmap(bitmap);
                    File file = open_cream.getFile(bitmap);
                    uploadHelper = new UploadHelper(this, this);
                    uploadHelper.postFile(uploadHelper.fileConvertToByteArray(file));
                    shotTheImage.setText("再上传一张");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = open_album.FindPath(data, this);
        setImage(imagePath);
    }

    private void setImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        theImage.setImageBitmap(bitmap);
        UploadHelper uploadHelper = new UploadHelper(this, this);
        file = new File(imagePath);
        uploadHelper.postFile(uploadHelper.fileConvertToByteArray(file));
    }

    @Override
    public void answer(String info) {
        headUrl = headUrl+info+",";
        Log.d("Url", headUrl);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            loadJson(message.obj.toString());
        }
    };


    public void setNetRequest(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody;
                    if (headUrl != null) {
                        headUrl = headUrl.substring(0,headUrl.length()-1);
                        requestBody = new FormBody.Builder()
                                .add("title", title.getText().toString())
                                .add("content", questions.getText().toString())
                                .add("token", token)
                                .add("images", headUrl)
                                .build();
                    } else {
                        requestBody = new FormBody.Builder()
                                .add("title", title.getText().toString())
                                .add("content", questions.getText().toString())
                                .add("token", token)
                                .build();
                    }

                    Request request = new Request.Builder().url("http://bihu.jay86.com/question.php")
                            .post(requestBody)
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

    public void loadJson(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.getInt("status");
            info = jsonObject.getString("info");
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
