package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bihu.Data.RemberTheNameAndCode;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    Button signIn ;//= (Button)findViewById(R.id.button);
    Button assign ;//= (Button)findViewById(R.id.assign);
    TextView showWhetherRember;
    EditText name ;//= (EditText)findViewById(R.id.Name);
    EditText passWord ;//= (EditText)findViewById(R.id.Password);
    String nameData_net;
    String info;
    String token;
    RemberTheNameAndCode remberTheNameAndCode;
    ProgressBar progressBar;

    int status;
    int id;
    String headUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signIn = (Button)findViewById(R.id.button);
        assign = (Button)findViewById(R.id.button2);
        name = (EditText)findViewById(R.id.Name);
        passWord = (EditText)findViewById(R.id.Password);
        showWhetherRember = (TextView)findViewById(R.id.textView);
        progressBar = (ProgressBar)findViewById(R.id.pbForLogin);
        progressBar.setVisibility(View.GONE);
        remberTheNameAndCode = new RemberTheNameAndCode(this);
        remberTheNameAndCode.startThis();

        if(remberTheNameAndCode.getWhetherSaved()){
            showWhetherRember.setText("记住密码 √");
            HashMap<String,String> map = remberTheNameAndCode.getTheCodeAndName();
            String savedName = map.get("name");
            String savedCode = map.get("code");
            name.setText(savedName);
            passWord.setText(savedCode);
        }else {
            showWhetherRember.setText("记住密码 X");
        }

        showWhetherRember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(remberTheNameAndCode.getWhetherSaved()){
                    remberTheNameAndCode.dontSave();
                }else {
                    remberTheNameAndCode.save();
                }
                if(remberTheNameAndCode.getWhetherSaved()){
                    showWhetherRember.setText("记住密码 √");
                }else {
                    showWhetherRember.setText("记住密码 X");
                }
            }
        });

        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Assign.class);
                startActivity(intent);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                setNetRequest(Login.this);
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            parseJSON(msg.obj.toString());
        }
    };

    public void setNetRequest (final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("username",name.getText().toString())
                            .add("password",passWord.getText().toString())
                            .build();
                    Request request = new Request.Builder().url("http://bihu.jay86.com/login.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String resData = response.body().string();
                    Message message = new Message();
                    message.obj=resData;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("Tip","网络错误");
                }
            }
        }).start();
    }

    public void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.getInt("status");
            info = jsonObject.getString("info");
            if(status==200){
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                nameData_net = jsonObject1.getString("username");
                token = jsonObject1.getString("token");
                id = jsonObject1.getInt("id");
                headUrl = jsonObject1.getString("avatar");
                Intent intent = new Intent(Login.this, MainView.class);
                intent.putExtra("name",nameData_net);
                intent.putExtra("token",token);
                intent.putExtra("id",id);
                intent.putExtra("head",headUrl);
                if(remberTheNameAndCode.getWhetherSaved()){
                    remberTheNameAndCode.saveTheCode(name.getText().toString(),passWord.getText().toString());
                }
                progressBar.setVisibility(View.GONE);
                startActivity(intent);
            }else {
                Toast.makeText(this,info,Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"登陆失败",Toast.LENGTH_SHORT).show();
            System.out.println("JSON error");
        }
    }
}
