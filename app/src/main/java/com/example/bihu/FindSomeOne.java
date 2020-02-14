package com.example.bihu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindSomeOne extends AppCompatActivity {

    Button startFinding;
    EditText InPutId;
    EditText InPutName;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_some_one);

        Intent intent1 = getIntent();
        token = intent1.getStringExtra("token");

        startFinding = (Button)findViewById(R.id.SendRequestForFind);
        InPutId = (EditText) findViewById(R.id.InPutId);
        InPutName = (EditText)findViewById(R.id.InPutName);

        startFinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = InPutName.getText().toString();
                String id = InPutId.getText().toString();
                Intent intent = new Intent(FindSomeOne.this,GetThatMan.class);
                if(name.equals("")&&id.equals("")){
                    Toast.makeText(FindSomeOne.this,"请输入查询对象的信息",Toast.LENGTH_SHORT).show();
                }else if(id.equals("")){
                    intent.putExtra("token",token);
                    intent.putExtra("name",name);
                    intent.putExtra("id","");
                    intent.putExtra("type",2);
                    startActivity(intent);
                    finish();
                }else {
                    Log.d("Tip",id);
                    intent.putExtra("token",token);
                    intent.putExtra("name",name);
                    intent.putExtra("id",Integer.parseInt(id));
                    intent.putExtra("type",1);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
