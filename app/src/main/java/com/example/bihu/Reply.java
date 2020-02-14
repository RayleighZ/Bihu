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
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.bihu.SetHead.CHOOSE_PHOTO;

public class Reply extends AppCompatActivity implements UploadHelper.Propose {

    String token;
    String headUrl="";
    String id;
    EditText editText;
    Button button;
    Button open;
    Button shot;
    OpenCream open_cream;
    Uri imageUri;
    ImageView thatImage;
    File file;
    int status;
    Activity activity;
    static Open_Album open_album;
    private UploadHelper uploadHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        Intent intent = getIntent();
        activity = this;
        uploadHelper = new UploadHelper(this, this);
        token = intent.getStringExtra("token");
        id = intent.getStringExtra("id");
        editText = (EditText) findViewById(R.id.rreply);
        button = (Button) findViewById(R.id.send_reply);
        thatImage = (ImageView) findViewById(R.id.imafeForReply);
        open = (Button) findViewById(R.id.button4);
        shot = (Button) findViewById(R.id.ShotForReply);
        open_album = new Open_Album(activity);
        open_cream = new OpenCream(this, this);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Reply.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Reply.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    open_album.openAlbum();
                }
            }
        });

        shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Reply.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Reply.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    imageUri = open_cream.openCrema();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNetRequest(Reply.this);
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
                open.setText("再上传一张");
                break;
            }
            case 1: {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    thatImage.setImageBitmap(bitmap);
                    File file = open_cream.getFile(bitmap);
                    uploadHelper = new UploadHelper(this, this);
                    uploadHelper.postFile(uploadHelper.fileConvertToByteArray(file));
                    shot.setText("再上传一张");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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
        thatImage.setImageBitmap(bitmap);
        file = new File(imagePath);
        UploadHelper uploadHelper = new UploadHelper(this, this);
        uploadHelper.postFile(uploadHelper.fileConvertToByteArray(file));
    }

    @Override
    public void answer(String info) {
        headUrl = headUrl+info+",";
        Log.d("Url", headUrl);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            paraseJson(msg.obj.toString());
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
                        Log.d("finalUrl",headUrl);
                        requestBody = new FormBody.Builder()
                                .add("qid", id)
                                .add("content", editText.getText().toString())
                                .add("token", token)
                                .add("images", headUrl)
                                .build();
                    } else {
                        requestBody = new FormBody.Builder()
                                .add("qid", id)
                                .add("content", editText.getText().toString())
                                .add("token", token)
                                .build();
                    }

                    Request request = new Request.Builder().url("http://bihu.jay86.com/answer.php")
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

    public void paraseJson(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            status = jsonObject.getInt("status");
            String info = jsonObject.getString("info");
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
