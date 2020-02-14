package com.example.bihu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class MainView extends AppCompatActivity {

    String userName;
    String token;
    Button reply;
    Button quest;
    Button SetInfo;
    Button getFavorite;
    Button LookMysely;
    Button Finder;
    String headUrl;
    ImageView head;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);
        final Intent intent = getIntent();
        TextView textView = (TextView) findViewById(R.id.UserName);


        userName = intent.getStringExtra("name");
        token = intent.getStringExtra("token");
        id = intent.getIntExtra("id", -1);
        headUrl = intent.getStringExtra("head");


        quest = (Button) findViewById(R.id.Ask);
        reply = (Button) findViewById(R.id.look);
        getFavorite = (Button) findViewById(R.id.getFavorite);
        SetInfo = (Button) findViewById(R.id.setInfo);
        head = (ImageView) findViewById(R.id.UserHead);
        LookMysely = (Button)findViewById(R.id.LookYourSelf);
        Finder = (Button)findViewById(R.id.Finder);

        if(headUrl != null && !headUrl.equals("") &&!headUrl.equals("null")){
            Glide.with(this).load(headUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error).into(head);
        }

        textView.setText(userName);

        Finder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainView.this,FindSomeOne.class);
                intent1.putExtra("token",token);
                startActivity(intent1);
            }
        });

        LookMysely.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainView.this,GetThatMan.class);
                intent1.putExtra("token",token);
                intent1.putExtra("id",id);
                startActivity(intent1);
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainView.this, GetOtherPeopleQuestions.class);
                intent1.putExtra("token", token);
                startActivity(intent1);
            }
        });
        quest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainView.this, AskQuestion.class);
                intent1.putExtra("token", token);
                intent1.putExtra("uid", id);
                startActivity(intent1);
            }
        });

        SetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainView.this);
                dialog.setTitle("选择要修改的信息");
                dialog.setPositiveButton("密码", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(MainView.this, SetCode.class);
                        intent1.putExtra("token", token);
                        startActivityForResult(intent1,1);
                    }
                });
                dialog.setNegativeButton("头像", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(MainView.this, SetHead.class);
                        intent1.putExtra("token", token);
                        startActivityForResult(intent1,2);
                    }
                });
                dialog.show();
            }
        });

        getFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainView.this,LookFavorite.class);
                intent1.putExtra("token",token);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == 1){
                token = data.getStringExtra("token");
            }
            if(requestCode == 2){
                Glide.with(this)
                        .load(data.getStringExtra("url"))
                        .into(head);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
