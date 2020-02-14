package com.example.bihu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ShowThatMan extends AppCompatActivity {

    Button deepFind;
    TextView thatManName;
    TextView thatManId;
    ImageView thatManHead;
    String name;
    int id;
    String url;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_that_man);
        deepFind = (Button)findViewById(R.id.DeepFind);
        thatManHead = (ImageView) findViewById(R.id.ThatManHead);
        thatManId = (TextView) findViewById(R.id.ThatManID);
        thatManName = (TextView)findViewById(R.id.ThatManName);
        Intent intent1 = getIntent();
        id = intent1.getIntExtra("id",-1);
        url = intent1.getStringExtra("url");
        token = intent1.getStringExtra("token");
        name = intent1.getStringExtra("name");
        if(!url.equals("")){
            Glide.with(this)
                    .load(url)
                    .into(thatManHead);
        }
        thatManName.setText(name);
        thatManId.setText(id+"");

        deepFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11 = new Intent(ShowThatMan.this,GetThatMan.class);
                intent11.putExtra("token",token);
                intent11.putExtra("id",id);
                startActivity(intent11);
            }
        });
    }
}
