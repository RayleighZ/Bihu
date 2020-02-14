package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Assign extends AppCompatActivity {
    Button resign;
    EditText name;
    EditText password;
    int status;
    String info;
    String token;
    String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign);
        resign = (Button)findViewById(R.id.join);
        name = (EditText)findViewById(R.id.Name_R);
        password = (EditText)findViewById(R.id.PassWord_R);
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNetRequest(Assign.this);
            }
        });
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message message){
            parseJSON(message.obj.toString());
        }
    };

    public void setNetRequest(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username",name.getText().toString())
                            .add("password",password.getText().toString())
                            .build();
                    Request request = new Request.Builder().url("http://bihu.jay86.com/register.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String resData = response.body().string();
                    Message message = new Message();
                    message.obj=resData;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.getInt("status");
            info = jsonObject.getString("info");
            Toast.makeText(this,info,Toast.LENGTH_SHORT).show();
            if(status == 200){
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                token = jsonObject1.getString("token");
                uname = jsonObject1.getString("username");
                Intent intent = new Intent(Assign.this,MainView.class);
                intent.putExtra("name",uname);
                intent.putExtra("token",token);
                startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
