package com.example.bihu.Data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

public class RemberTheNameAndCode {
    private Context context;
    public RemberTheNameAndCode (Context context){
        this.context = context;
    }

    public void startThis(){
        SharedPreferences preferences = context.getSharedPreferences("Data_Code",0);
        SharedPreferences.Editor editor = preferences.edit();
        String tryy = preferences.getString("name","RayJoeLeighZ");
        if(tryy.equals("RayJoeLeighZ")){
            editor.putBoolean("isSaved",false);
        }
    }

    public void saveTheCode(String name,String code){
        SharedPreferences.Editor editor = context.getSharedPreferences("Data_Code",0).edit();
        editor.putString("name",name);
        //稍微的加密一下
        editor.putString("code",code+"123123");
        editor.putBoolean("isSaved",true);
        editor.apply();
    }

    public HashMap<String,String> getTheCodeAndName(){
        SharedPreferences preferences = context.getSharedPreferences("Data_Code",0);
        String name = preferences.getString("name","");
        String FakeCode = preferences.getString("code","");
        String code = FakeCode.substring(0,FakeCode.length()-6);
        HashMap<String,String> hashMap = new HashMap<String, String>();
        hashMap.put("name",name);
        hashMap.put("code",code);
        return hashMap;
    }

    public void dontSave(){
        SharedPreferences.Editor editor = context.getSharedPreferences("Data_Code",0).edit();
        editor.putBoolean("isSaved",false);
        editor.apply();
    }

    public void save(){
        SharedPreferences.Editor editor = context.getSharedPreferences("Data_Code",0).edit();
        editor.putBoolean("isSaved",true);
        editor.apply();
    }

    public boolean getWhetherSaved(){
        SharedPreferences preferences = context.getSharedPreferences("Data_Code",0);
        return preferences.getBoolean("isSaved",false);
    }
}
