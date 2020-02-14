package com.example.bihu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.bihu.RecyclerViewDataTool.RvAdapter;
import com.example.bihu.Resercher.IdFinder;
import com.example.bihu.RvDataTool.Asker;

import java.util.ArrayList;

public class GetThatMan extends AppCompatActivity implements IdFinder.backArrlist{

    int PeoId;
    int type;
    String token;
    String name;
    IdFinder idFinder;
    RecyclerView recyclerView;
    RvAdapter rvAdapter;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_that_man);
        progressBar = (ProgressBar)findViewById(R.id.ReserachProgress);
        Intent intent1 = getIntent();

        PeoId = intent1.getIntExtra("id",-1);
        token = intent1.getStringExtra("token");
        type = intent1.getIntExtra("type",1);
        name  = intent1.getStringExtra("name");

        Log.d("Tip","id;"+ PeoId);
        idFinder = new IdFinder(PeoId,token,this,this,progressBar,type,name);
        idFinder.main();

    }

    @Override
    public void getArrlist(ArrayList<Asker> askers){
        Log.d("Tip","拿到数据");
        rvAdapter = new RvAdapter(this,token,this);
        recyclerView = findViewById(R.id.TheRv);
        recyclerView.setAdapter(rvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        rvAdapter.setData(askers);
        rvAdapter.notifyDataSetChanged();
    }
}
