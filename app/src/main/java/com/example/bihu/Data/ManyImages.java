package com.example.bihu.Data;

import android.util.Log;

import java.util.ArrayList;

public class ManyImages {

    static public ArrayList<String> devide (String oriImages){
        int head = 0;
        int end;
        ArrayList <String> images = new ArrayList<>();
        for (int i = 0; i < oriImages.length(); i++) {
            if(oriImages.charAt(i) == ','){
                end = i;
                String image = oriImages.substring(head,end);
                Log.d("DivideUrl",image);
                images.add(image);
                head = i+1;
            }
            if(i == oriImages.length() - 1){
                String image = oriImages.substring(head,i+1);
                Log.d("DivideUrl",image);
                images.add(image);
            }
        }
        return images;
    }
}
