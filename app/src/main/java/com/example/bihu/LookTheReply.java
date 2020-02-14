package com.example.bihu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bihu.RecyclerViewDataTool.BasicRvAdapter;
import com.example.bihu.R;
import com.example.bihu.RvDataTool.Replyer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LookTheReply extends AppCompatActivity {

    RecyclerView recyclerView;
    BasicRvAdapter rvAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String token;
    String qid;
    int page = 0;
    int totalPages;
    ArrayList<Replyer> replyerArrayList = new ArrayList<>();
    Button refresh;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_the_reply);
        refresh = (Button)findViewById(R.id.RefreshTheReply);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayoutForReply);
        progressBar = (ProgressBar)findViewById(R.id.LoadingForReply);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        qid = intent.getStringExtra("id");
        initiView();
        setNetRequest();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                replyerArrayList.clear();
                setNetRequest();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                setNetRequest();
            }
        });
    }

    public void initiView(){
        recyclerView = findViewById(R.id.ReplyRv);

        rvAdapter = new BasicRvAdapter(this);
        recyclerView.setAdapter(rvAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            paraseJson(msg.obj.toString());
            rvAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    public void setNetRequest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("token",token)
                            .add("page",page+"")
                            .add("qid",qid)
                            .build();
                    Request request = new Request.Builder()
                            .post(requestBody)
                            .url("http://bihu.jay86.com/getAnswerList.php")
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

    public void paraseJson(String JsonData){
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            totalPages = jsonObject1.getInt("totalPage");
            int fakePage = page+1;
            Toast.makeText(this,"第"+fakePage+"/"+totalPages+"页",Toast.LENGTH_SHORT).show();
            JSONArray jsonArray = jsonObject1.getJSONArray("answers");
            Replyer [] replyers = new Replyer[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String content = jsonObject2.getString("content");
                String good = jsonObject2.getString("exciting");
                String bad = jsonObject2.getString("naive");
                String name = jsonObject2.getString("authorName");
                String head = jsonObject2.getString("authorAvatar");
                String best = jsonObject2.getInt("best")+"";
                boolean yes = jsonObject2.getBoolean("is_exciting");
                boolean yes1 = jsonObject2.getBoolean("is_naive");
                int id = jsonObject2.getInt("id");
                String date = jsonObject2.getString("date");
                String image = jsonObject2.getString("images");
                replyers[i] = new Replyer(good,bad,content,name,head,yes,yes1,token,id,best,qid,image,date);
                replyerArrayList.add(replyers[i]);
            }
            if(replyerArrayList.isEmpty()){
                Toast.makeText(this,"并没有回复",Toast.LENGTH_SHORT).show();
            }
            rvAdapter.setReplyers(replyerArrayList);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"数据问题,RayleighZ要被骂了",Toast.LENGTH_SHORT).show();
        }
    }
}
