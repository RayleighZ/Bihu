package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bihu.RecyclerViewDataTool.RvAdapter;
import com.example.bihu.RvDataTool.Asker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetOtherPeopleQuestions extends AppCompatActivity {

    String token;
    RecyclerView recyclerView;
    RvAdapter rvAdapter;
    ArrayList<Asker> list=new ArrayList<>();
    int page = 0;
    int totalPages;
    Button refresh;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_questions);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        initView();
        setNetRequest(this);
        refresh = (Button)findViewById(R.id.refresh);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayoutGetQuestion);
        progressBar = (ProgressBar)findViewById(R.id.LoadingQuestion);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                setNetRequest(GetOtherPeopleQuestions.this);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                setNetRequest(GetOtherPeopleQuestions.this);
            }
        });
    }

    public void setNetRequest(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("page",page+"")
                            .add("token",token)
                            .build();
                    Request request = new Request.Builder().url("http://bihu.jay86.com/getQuestionList.php")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Message message = new Message();
                    message.obj = responseData;
                    handler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void initView(){
        rvAdapter = new RvAdapter(this,token,this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(rvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            parseJSON(msg.obj.toString());
            rvAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    public void parseJSON(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            totalPages = jsonObject1.getInt("totalPage");
            int fakePage = page+1;
            Toast.makeText(this,"第"+fakePage+"/"+totalPages+"页",Toast.LENGTH_SHORT).show();
            JSONArray jsonArray = jsonObject1.getJSONArray("questions");
            Asker [] askers = new Asker[jsonArray.length()];
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
                Log.d("Url",avatar);
                boolean yes = jsonObject2.getBoolean("is_exciting");
                boolean yes2 = jsonObject2.getBoolean("is_naive");
                int id = jsonObject2.getInt("id");
                Log.d("Tip",yes+"");
                boolean is_best = jsonObject2.getBoolean("is_favorite");
                int authoeId = jsonObject2.getInt("authorId");
                askers[i] = new Asker(name,content,title,cai,zan,huifu,id,imageUrl,avatar,yes,yes2,is_best,time,authoeId);
                list.add(askers[i]);
            }
            rvAdapter.setData(list);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"数据问题,RayleighZ要被骂了",Toast.LENGTH_SHORT).show();
        }
    }

}
