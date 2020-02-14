package com.example.bihu.Resercher;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TotalPagesFinder {

    private String token;
    private int totalPages;
    private callBack callBack;

    public TotalPagesFinder(String token,callBack callBack){
        this.token = token;
        this.callBack = callBack;
    }

    public void getTotalPages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
                            .readTimeout(60000, TimeUnit.MILLISECONDS)
                            .build();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("page", 0 + "")
                            .add("token", token)
                            .build();
                    Request request = new Request.Builder().url("http://bihu.jay86.com/getQuestionList.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    message.obj = responseData;
                    handler2.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            solveJson(msg.obj.toString());
        }
    };

    public interface callBack{
        void getPages(int pages);
    }

    private void solveJson(String JsonData){
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            totalPages = jsonObject1.getInt("totalPage");
            callBack.getPages(totalPages);
            Log.d("Tip","完成页数的检索"+totalPages);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
