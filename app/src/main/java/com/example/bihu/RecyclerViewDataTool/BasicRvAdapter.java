package com.example.bihu.RecyclerViewDataTool;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.bihu.Data.ManyImages;
import com.example.bihu.R;
import com.example.bihu.RvDataTool.Replyer;
import com.example.bihu.ShowTheImage;
import com.example.bihu.vpAdapter.MViewPageAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BasicRvAdapter extends RecyclerView.Adapter<BasicRvAdapter.MyInnerViewHolder> {

    ArrayList<Replyer> replyers = new ArrayList<>();
    Context context;
    int goodOrBad;
    String token;
    boolean whetherExciting = false;
    boolean whetherNaive = false;
    int status;
    private MViewPageAdapter viewPageAdapter;

    public BasicRvAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyInnerViewHolder holder, final int position) {
        holder.content.setText(replyers.get(position).contest);
        holder.badnum.setText(replyers.get(position).badnum);
        holder.goodnum.setText(replyers.get(position).goodnum);
        holder.name.setText(replyers.get(position).name);
        holder.bestNum.setText(replyers.get(position).best);
        token = replyers.get(position).Token;
        Glide.with(context)
                .load(replyers.get(position).head)
                .placeholder(R.drawable.hbls)
                .error(R.drawable.hbls).into(holder.head);
        /**Glide.with(context)
                .load(replyers.get(position).imageUrl)
                .into(holder.images);*/
        whetherExciting = replyers.get(position).whetherExciting;
        whetherNaive = replyers.get(position).whetherNaive;

        holder.time.setText("回复时间："+replyers.get(position).time);

        if(replyers.get(position).whetherExciting){
            holder.good.setBackgroundResource(R.drawable.ic_tag_praise_selected);
        }else {
            holder.good.setBackgroundResource(R.drawable.ic_tag_praise);
        }

        if(replyers.get(position).whetherNaive){
            holder.bad.setBackgroundResource(R.drawable.ic_tag_tread_selected);
        } else {
            holder.bad.setBackgroundResource(R.drawable.ic_tag_tread);
        }

        viewPageAdapter = new MViewPageAdapter(ManyImages.devide(replyers.get(position).imageUrl), context);
        holder.viewPager.setAdapter(viewPageAdapter);
        int fakeCurr = holder.viewPager.getCurrentItem()+1;
        holder.pagesOfImagee.setText(fakeCurr+"/"+ ManyImages.devide(replyers.get(position).imageUrl).size());
        if(ManyImages.devide(replyers.get(position).imageUrl).isEmpty()){
            holder.pagesOfImagee.setVisibility(View.GONE);
        }
        final int total = ManyImages.devide(replyers.get(position).imageUrl).size();

        holder.viewPager.setOnTouchListener(new View.OnTouchListener() {
            int flage = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:{
                        flage = 0;
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        flage = 1;
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        if(flage == 0){
                            Intent intent1 = new Intent(context, ShowTheImage.class);
                            Log.d("GoToThat", ManyImages.devide(replyers.get(position).imageUrl).get(holder.viewPager.getCurrentItem()));
                            intent1.putExtra("imageUrl", ManyImages.devide(replyers.get(position).imageUrl)
                                    .get(holder.viewPager.getCurrentItem()));
                            context.startActivity(intent1);
                        }
                        break;
                    }
                }
                return false;
            }
        });

        holder.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int fakePosition = position+1;
                holder.pagesOfImagee.setText(fakePosition+"/"+total);
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        holder.good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodOrBad = 1;
                if(!replyers.get(position).whetherExciting){
                    holder.good.setBackgroundResource(R.drawable.ic_tag_praise_selected);
                    whetherExciting = true;
                    int a = Integer.parseInt(replyers.get(position).goodnum) + 1;
                    holder.goodnum.setText(a+"");
                    replyers.get(position).goodnum = a +"";
                    setGoodNetrequest("http://bihu.jay86.com/exciting.php", token, replyers.get(position).id+ "", "2","");
                } else {
                    holder.good.setBackgroundResource(R.drawable.ic_tag_praise);
                    whetherExciting = false;
                    int a = Integer.parseInt(replyers.get(position).goodnum) - 1;
                    holder.goodnum.setText(a+"");
                    replyers.get(position).goodnum = a +"";
                    setGoodNetrequest("http://bihu.jay86.com/cancelExciting.php", token,replyers.get(position).id + "", "2","");
                }
            }
        });

        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ShowTheImage.class);
                intent1.putExtra("imageUrl",replyers.get(position).head);
                context.startActivity(intent1);
            }
        });


        holder.bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodOrBad = 0;
                if (!whetherNaive){
                    holder.bad.setBackgroundResource(R.drawable.ic_tag_tread_selected);
                    whetherNaive = true;
                    int a = Integer.parseInt(replyers.get(position).badnum) + 1;
                    holder.badnum.setText(a+"");
                    replyers.get(position).badnum = a +"";
                    setGoodNetrequest("http://bihu.jay86.com/naive.php", token, replyers.get(position).id + "", "2","");
                } else {
                    holder.bad.setBackgroundResource(R.drawable.ic_tag_tread);
                    whetherNaive = false;
                    int a = Integer.parseInt(replyers.get(position).badnum) - 1;
                    holder.badnum.setText(a+"");
                    replyers.get(position).badnum = a +"";
                    setGoodNetrequest("http://bihu.jay86.com/cancelNaive.php", token, replyers.get(position).id + "", "2","");
                }
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodOrBad = 2;
                setGoodNetrequest("http://bihu.jay86.com/accept.php",token,replyers.get(position).id+"","0",replyers.get(position).qid);
            }
        });
    }
    public void setGoodNetrequest(final String url, final String token, final String id, final String type, final String qid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(type.equals("0")){
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("qid", qid)
                                .add("aid",id)
                                .add("token", token)
                                .build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        String data = response.body().string();
                        Message message = new Message();
                        message.obj = data;
                        handler.sendMessage(message);
                    }else {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("id", id)
                                .add("token", token)
                                .add("type", type)
                                .build();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(requestBody)
                                .build();
                        Response response = client.newCall(request).execute();
                        String data = response.body().string();
                        Message message = new Message();
                        message.obj = data;
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            paraseJson(msg.obj.toString());
        }
    };

    public void paraseJson(String JsonData) {
        try {
            boolean firstTime = true;
            JSONObject jsonObject = new JSONObject(JsonData);
            status = jsonObject.getInt("status");
            if(status == 200 && whetherExciting && goodOrBad == 1&& firstTime){
                Toast.makeText(context,"点赞成功",Toast.LENGTH_SHORT).show();
                //whetherExciting = true;
                firstTime = false;
            }

            if(status == 200 && !whetherExciting && goodOrBad == 1 && firstTime){
                Toast.makeText(context,"取消点赞成功",Toast.LENGTH_SHORT).show();
                //whetherExciting = false;
                firstTime = false;
            }
            if(status == 200 && whetherNaive && goodOrBad == 0 && firstTime){
                Toast.makeText(context,"嘲讽成功",Toast.LENGTH_SHORT).show();
                //whetherNaive = true;
                firstTime = false;
            }
            if(status == 200 && !whetherNaive && goodOrBad == 0 && firstTime){
                Toast.makeText(context,"取消嘲讽成功",Toast.LENGTH_SHORT).show();
                //whetherNaive = false;
                firstTime = false;
            }
            if(status == 200 && goodOrBad == 2 && firstTime){
                Toast.makeText(context,"采纳成功",Toast.LENGTH_SHORT).show();
                firstTime = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.innerview,parent,false);
        return new BasicRvAdapter.MyInnerViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        if(replyers != null){
            return replyers.size();
        }else {
            return 0;
        }
    }

    public void setReplyers(ArrayList list){
        replyers = list;
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView name;
        TextView goodnum;
        TextView badnum;
        TextView bestNum;
        TextView time;
        TextView pagesOfImagee;
        Button good;
        Button bad;
        Button accept;
        ImageView head;
        ViewPager viewPager;

        public MyInnerViewHolder (@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.repeat);
            name = itemView.findViewById(R.id.name);
            goodnum = itemView.findViewById(R.id.goodnum);
            badnum = itemView.findViewById(R.id.badnum);
            good = itemView.findViewById(R.id.good);
            bad = itemView.findViewById(R.id.bad);
            head = itemView.findViewById(R.id.head);
            bestNum = itemView.findViewById(R.id.AcceptNum);
            accept = itemView.findViewById(R.id.Accept);
            time = itemView.findViewById(R.id.Time2);
            viewPager = itemView.findViewById(R.id.VpForReply);
            pagesOfImagee = itemView.findViewById(R.id.ReplyImageCount);
        }
    }
}
