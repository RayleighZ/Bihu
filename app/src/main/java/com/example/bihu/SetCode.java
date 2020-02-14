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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetCode extends AppCompatActivity {

    Button confirm;
    EditText newCode;
    EditText newCode2;
    String NewCode;
    String NewCode2;
    String token;
    String userName;
    String headUrl;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_code);
        confirm = (Button)findViewById(R.id.confirmChange);
        newCode = (EditText)findViewById(R.id.newCode);
        newCode2 = (EditText)findViewById(R.id.newCode2);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCode = newCode.getText().toString();
                NewCode2 = newCode2.getText().toString();
                Intent intent = getIntent();
                token = intent.getStringExtra("token");
                id = intent.getIntExtra("id",-1);
                userName = intent.getStringExtra("userName");
                headUrl = intent.getStringExtra("headUrl");

                if(NewCode2.equals(NewCode)){
                    SetNetRequest(SetCode.this);
                }else {
                    Toast.makeText(SetCode.this,"两次密码输入不相同",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            paraJSON(msg.obj.toString());
        }
    };

    public void paraJSON (String JsonData){
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            int status = jsonObject.getInt("status");
            if(status == 200){
                Toast.makeText(SetCode.this,"修改成功",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                String token = jsonObject1.getString("token");
                Intent intent1 = new Intent();
                intent1.putExtra("token",token);
                setResult(2,intent1);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SetNetRequest(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("wdnmd1");
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("token",token)
                            .add("password",NewCode)
                            .build();
                    Request request = new Request.Builder()
                            .post(requestBody)
                            .url("http://bihu.jay86.com/changePassword.php")
                            .build();
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    Message message = new Message();
                    message.obj = data;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
