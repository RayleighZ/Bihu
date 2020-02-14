package com.example.bihu.NetTool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.qiniu.common.Zone;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadHelper {
    private static final String SK = "X89OfauARryV21fO_YKybdW8r4T2ypqR9co1sTYr";
    private static final String AK = "yunIlMLzBqAsEkAx09sH8G0_2ZLbMxoxfoHrOJPJ";
    public static final String BKN  = "bihu-head";
    private static UploadManager uploadManageer;
    private static String upToken;
    public static String MyUrl;
    Context context;
    String headUrl;
    Propose propose;

    public UploadHelper (Context context,Propose propose){
        this.context = context;
        this.propose = propose;
    }

    public void postFile(final byte [] bytes){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Configuration cfg = new Configuration(Zone.zone2());
                    uploadManageer = new UploadManager(cfg);
                    Auth auth = Auth.create(AK,SK);
                    upToken = auth.uploadToken(BKN);
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String key = df.format(new Date());
                    final com.qiniu.http.Response response = uploadManageer.put(bytes,key,upToken);
                    DefaultPutRet putRet = new Gson().fromJson(response.bodyString(),DefaultPutRet.class);
                    MyUrl = "http://" + "q54y4u59f.bkt.clouddn.com"+"/"+putRet.key;
                    Message message = new Message();
                    message.obj = MyUrl;
                    handler2.sendMessage(message);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler2 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            BackInMain(msg);
        }
    };

    public void BackInMain(Message msg){
        try {
            headUrl = msg.obj.toString();
            if(propose != null){
                propose.answer(headUrl);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface Propose{
        void answer(String info);
    }

    public byte[] fileConvertToByteArray(File file) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
