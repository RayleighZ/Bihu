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
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bihu.R;
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

public class LookFavorite extends AppCompatActivity {

    RecyclerView recyclerView;
    RvAdapter rvAdapter;
    String token;
    ArrayList<Asker> list=new ArrayList<>();
    Button refresh;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    int page = 0;
    int totalPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_favorite);
        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        refresh = (Button)findViewById(R.id.RefreshTheFavorite);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayoutForFavorite);
        progressBar = (ProgressBar)findViewById(R.id.LoadingForFavorite);
        initView();
        setNetRequest(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                setNetRequest(LookFavorite.this);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                setNetRequest(LookFavorite.this);
            }
        });
    }
    public void initView(){
        rvAdapter = new RvAdapter(this,token,this);
        recyclerView = (RecyclerView)findViewById(R.id.LookTheFavoriteRv);
        recyclerView.setAdapter(rvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void setNetRequest(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60000, TimeUnit.MILLISECONDS)
                            .readTimeout(60000,TimeUnit.MILLISECONDS)
                            .build();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("page",page+"")
                            .add("token",token)
                            .build();
                    Request request = new Request.Builder().url("http://bihu.jay86.com/getFavoriteList.php")
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
            int fakePage = page + 1;
            //Toast.makeText(this,"第"+fakePage+"/"+totalPages+"页",Toast.LENGTH_SHORT).show();
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
                System.out.println(imageUrl);
                String avatar = jsonObject2.getString("authorAvatar");
                String time = jsonObject2.getString("recent");
                boolean yes = jsonObject2.getBoolean("is_exciting");
                boolean yes2 = jsonObject2.getBoolean("is_naive");
                int id = jsonObject2.getInt("id");
                int authoeId = jsonObject2.getInt("authorId");
                askers[i] = new Asker(name,content,title,cai,zan,huifu,id,imageUrl,avatar,yes,yes2,true,time,authoeId);
                list.add(askers[i]);
            }
            if(list.isEmpty()){
                Toast.makeText(this,"暂无收藏的问题",Toast.LENGTH_SHORT).show();
            }
            rvAdapter.setData(list);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"数据错误,RayleighZ要被骂了",Toast.LENGTH_SHORT).show();
        }
    }
}
