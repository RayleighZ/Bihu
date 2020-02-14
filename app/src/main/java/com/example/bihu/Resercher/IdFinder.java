package com.example.bihu.Resercher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bihu.RvDataTool.Asker;
import com.example.bihu.RvDataTool.Replyer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IdFinder implements TotalPagesFinder.callBack{
    private ArrayList<Asker> theaskers;
    private Context context;
    private int theId;
    private String token;
    private int totalPages;
    private TotalPagesFinder totalPagesFinder;
    private int i;
    private backArrlist backArrlist;
    private ProgressBar progressBar;
    private int type;
    private String theName;

    public IdFinder(int id, String token,backArrlist backArrlist,Context context,ProgressBar progressBar,int type,String theName) {
        this.theId = id;
        this.token = token;
        this.backArrlist = backArrlist;
        this.context = context;
        this.progressBar = progressBar;
        this.type = type;
        this.theName = theName;
    }

    public void main(){
        theaskers = new ArrayList<>();
        totalPagesFinder = new TotalPagesFinder(token,this);
        totalPagesFinder.getTotalPages();
    }

    public void setNetRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for ( i = 0; i < totalPages; i++) {
                        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(600000, TimeUnit.MILLISECONDS)
                                .readTimeout(60000, TimeUnit.MILLISECONDS)
                                .build();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("page", i + "")
                                .add("token", token)
                                .build();
                        Request request = new Request.Builder().url("http://bihu.jay86.com/getQuestionList.php")
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        String responseData = response.body().string();
                        Message message = new Message();
                        message.obj = responseData;
                        handler.sendMessage(message);
                        Log.d("Tip","正在检索"+i+"页");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            paraJson(msg.obj.toString());
        }
    };

    private void paraJson(String JsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            totalPages = jsonObject1.getInt("totalPage");
            JSONArray jsonArray = jsonObject1.getJSONArray("questions");
            Asker[] askers = new Asker[jsonArray.length()];
            Log.d("Tip","有"+totalPages+"页");
            //Toast.makeText(context,"正在检索"+i+"页/"+totalPages+"页",Toast.LENGTH_SHORT).show();
            progressBar.setProgress(100*i/totalPages);
            if (progressBar.getProgress()==100){
                progressBar.setVisibility(View.GONE);
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String name = jsonObject2.getString("authorName");
                String title = jsonObject2.getString("title");
                String content = jsonObject2.getString("content");
                String cai = jsonObject2.getString("naive");
                String zan = jsonObject2.getString("exciting");
                String huifu = jsonObject2.getString("answerCount");
                String imageUrl = jsonObject2.getString("images");
                String time = jsonObject2.getString("recent");
                String avatar = jsonObject2.getString("authorAvatar");
                boolean yes = jsonObject2.getBoolean("is_exciting");
                boolean yes2 = jsonObject2.getBoolean("is_naive");
                int id = jsonObject2.getInt("id");
                int authoeId = jsonObject2.getInt("authorId");
                boolean is_best = jsonObject2.getBoolean("is_favorite");
                askers[i] = new Asker(name, content, title, cai, zan, huifu, id, imageUrl, avatar, yes, yes2, is_best, time,authoeId);
                if(type == 1){
                    if (askers[i].authorId == theId) {
                        Log.d("Tip","找到一个");
                        theaskers.add(askers[i]);
                    }
                } else if(type == 2){
                    if (askers[i].name.equals(theName)) {
                        Log.d("Tip","找到一个");
                        theaskers.add(askers[i]);
                    }
                } else {
                    Toast.makeText(context,"系统没有找到检索类型",Toast.LENGTH_SHORT).show();
                }

            }
            if(i == totalPages){
                if(theaskers.isEmpty()){
                    Toast.makeText(context,"查无此人",Toast.LENGTH_SHORT).show();
                }
                Log.d("Tip","返回数组");
                backArrlist.getArrlist(theaskers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getPages(int pages){
        totalPages = pages;
        setNetRequest();
    }

    public interface backArrlist{
        void getArrlist(ArrayList<Asker> askers);
    }
}
