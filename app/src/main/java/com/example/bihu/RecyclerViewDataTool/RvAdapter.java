package com.example.bihu.RecyclerViewDataTool;

import android.app.Activity;
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
import com.example.bihu.LookTheReply;
import com.example.bihu.Reply;
import com.example.bihu.R;
import com.example.bihu.RvDataTool.Asker;
import com.example.bihu.ShowThatMan;
import com.example.bihu.ShowTheImage;
import com.example.bihu.vpAdapter.MViewPageAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyInnerViewHolder> {
    private ArrayList<Asker> askerArrayList;
    private ArrayList<String> images;
    private MViewPageAdapter viewPageAdapter;
    Context context;
    String token;
    Activity activity;
    boolean whetherExciting = false;
    boolean whetherNaive = false;
    boolean whetherFavorite = false;
    boolean isForExciting = false;
    int status;
    int goodOrBad = 1;
    int counter = 0;

    public RvAdapter(Context context, String token, Activity activity) {
        this.context = context;
        this.token = token;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyInnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_people_question, parent, false);
        return new MyInnerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyInnerViewHolder holder, final int position) {

        whetherExciting = askerArrayList.get(position).is_exciting;
        whetherNaive = askerArrayList.get(position).is_naive;
        whetherFavorite = askerArrayList.get(position).is_favorite;

        //初始化问题

        holder.question.setText(askerArrayList.get(position).setence);
        holder.name.setText(askerArrayList.get(position).name);
        holder.title.setText(askerArrayList.get(position).title);
        holder.huifu.setText(askerArrayList.get(position).huifu);
        holder.cai.setText(askerArrayList.get(position).cai);
        holder.zan.setText(askerArrayList.get(position).zan);
        //特殊的ViewPager

        viewPageAdapter = new MViewPageAdapter(ManyImages.devide(askerArrayList.get(position).ImageUrl), context);
        holder.viewPager.setAdapter(viewPageAdapter);
        int fakeCurr = holder.viewPager.getCurrentItem()+1;
        holder.pagesOfImagee.setText(fakeCurr+"/"+ManyImages.devide(askerArrayList.get(position).ImageUrl).size());
        if(ManyImages.devide(askerArrayList.get(position).ImageUrl).isEmpty()){
            holder.pagesOfImagee.setVisibility(View.GONE);
        }
        if(askerArrayList.get(position).ImageUrl == null || askerArrayList.get(position).ImageUrl.equals("") || askerArrayList.get(position).ImageUrl.equals("null")){
            holder.pagesOfImagee.setVisibility(View.GONE);
        }
        final int total = ManyImages.devide(askerArrayList.get(position).ImageUrl).size();

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
                            Log.d("GoToThat", ManyImages.devide(askerArrayList.get(position).ImageUrl).get(holder.viewPager.getCurrentItem()));
                            intent1.putExtra("imageUrl", ManyImages.devide(askerArrayList.get(position).ImageUrl)
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

        if (askerArrayList.get(position).time != null) {
            holder.recent.setText("最近回复:" + askerArrayList.get(position).time);
        }

        if (askerArrayList.get(position).is_exciting) {
            holder.exciting.setBackgroundResource(R.drawable.ic_tag_praise_selected);
        } else {
            holder.exciting.setBackgroundResource(R.drawable.ic_tag_praise);
        }

        if (askerArrayList.get(position).is_favorite) {
            holder.ILoveThis.setBackgroundResource(R.drawable.ic_collected_star);
        } else {
            holder.ILoveThis.setBackgroundResource(R.drawable.ic_collect_star);
        }

        if (askerArrayList.get(position).is_naive) {
            holder.naive.setBackgroundResource(R.drawable.ic_tag_tread_selected);
        } else {
            holder.naive.setBackgroundResource(R.drawable.ic_tag_tread);
        }

        View view = LayoutInflater.from(context).inflate(R.layout.vp_layout, null);

        Glide.with(context)
                .load(askerArrayList.get(position).head)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(holder.head);

        if(askerArrayList.get(position).head == null || askerArrayList.get(position).head.equals("") || askerArrayList.get(position).head.equals("null")){
            holder.head.setImageResource(R.drawable.hbls);
        }

        //设置Item的点击事件

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ShowThatMan.class);
                intent1.putExtra("name", askerArrayList.get(position).name);
                intent1.putExtra("id", askerArrayList.get(position).authorId);
                intent1.putExtra("token", token);
                intent1.putExtra("url", askerArrayList.get(position).head);
                context.startActivity(intent1);
            }
        });

        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ShowTheImage.class);
                intent1.putExtra("imageUrl", askerArrayList.get(position).head);
                context.startActivity(intent1);
            }
        });

        holder.reavt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Reply.class);
                intent.putExtra("id", askerArrayList.get(position).id + "");
                intent.putExtra("token", token);
                context.startActivity(intent);
            }
        });

        holder.look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LookTheReply.class);
                intent.putExtra("id", askerArrayList.get(position).id + "");
                intent.putExtra("token", token);
                context.startActivity(intent);
            }
        });

        holder.exciting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForExciting = true;
                goodOrBad = 1;
                if (!askerArrayList.get(position).is_exciting) {
                    holder.exciting.setBackgroundResource(R.drawable.ic_tag_praise_selected);
                    int a = Integer.parseInt(askerArrayList.get(position).zan) + 1;
                    holder.zan.setText(a + "");
                    askerArrayList.get(position).zan = a + "";
                    askerArrayList.get(position).is_exciting = true;
                    whetherExciting = true;
                    setGoodNetrequest("http://bihu.jay86.com/exciting.php", token, askerArrayList.get(position).id + "", "1");
                } else {
                    holder.exciting.setBackgroundResource(R.drawable.ic_tag_praise);
                    int a = Integer.parseInt(askerArrayList.get(position).zan) - 1;
                    holder.zan.setText(a + "");
                    askerArrayList.get(position).zan = a + "";
                    askerArrayList.get(position).is_exciting = false;
                    whetherExciting = false;
                    setGoodNetrequest("http://bihu.jay86.com/cancelExciting.php", token, askerArrayList.get(position).id + "", "1");
                }
            }
        });

        holder.naive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForExciting = true;
                goodOrBad = 0;
                if (!askerArrayList.get(position).is_naive) {
                    holder.naive.setBackgroundResource(R.drawable.ic_tag_tread_selected);
                    int a = Integer.parseInt(askerArrayList.get(position).cai) + 1;
                    holder.cai.setText(a + "");
                    askerArrayList.get(position).cai = a + "";
                    askerArrayList.get(position).is_naive = true;
                    whetherNaive = true;
                    setGoodNetrequest("http://bihu.jay86.com/naive.php", token, askerArrayList.get(position).id + "", "1");
                } else {
                    holder.naive.setBackgroundResource(R.drawable.ic_tag_tread);
                    int a = Integer.parseInt(askerArrayList.get(position).cai) - 1;
                    holder.cai.setText(a + "");
                    askerArrayList.get(position).cai = a + "";
                    askerArrayList.get(position).is_naive = false;
                    whetherNaive = false;
                    setGoodNetrequest("http://bihu.jay86.com/cancelNaive.php", token, askerArrayList.get(position).id + "", "1");
                }
            }
        });

        holder.ILoveThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isForExciting = false;
                if (!askerArrayList.get(position).is_favorite) {
                    holder.ILoveThis.setBackgroundResource(R.drawable.ic_collected_star);
                    askerArrayList.get(position).is_favorite = true;
                    whetherFavorite = true;
                    setGoodNetrequest("http://bihu.jay86.com/favorite.php", token, askerArrayList.get(position).id + "", "0");
                } else {
                    holder.ILoveThis.setBackgroundResource(R.drawable.ic_collect_star);
                    askerArrayList.get(position).is_favorite = false;
                    whetherFavorite = false;
                    setGoodNetrequest("http://bihu.jay86.com/cancelFavorite.php", token, askerArrayList.get(position).id + "", "0");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (askerArrayList != null) {
            return askerArrayList.size();
        } else {
            return 0;
        }
    }

    public void setData(ArrayList list) {
        this.askerArrayList = list;
    }

    public class MyInnerViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView head;
        TextView question;
        TextView title;
        TextView cai;
        TextView zan;
        TextView exciting;
        TextView naive;
        TextView huifu;
        TextView recent;
        TextView pagesOfImagee;
        Button reavt;
        Button look;
        Button ILoveThis;
        ViewPager viewPager;

        public MyInnerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.AskerName);
            question = itemView.findViewById(R.id.Question);
            head = itemView.findViewById(R.id.head);
            title = itemView.findViewById(R.id.title);
            cai = itemView.findViewById(R.id.cai);
            zan = itemView.findViewById(R.id.zan);
            huifu = itemView.findViewById(R.id.huifu);
            reavt = itemView.findViewById(R.id.I_wanna_repeat);
            look = itemView.findViewById(R.id.LookReply);
            exciting = itemView.findViewById(R.id.ggoooodd);
            naive = itemView.findViewById(R.id.bbaadd);
            ILoveThis = itemView.findViewById(R.id.ILoveThis);
            recent = itemView.findViewById(R.id.Time);
            pagesOfImagee = itemView.findViewById(R.id.imagePages);
            viewPager = itemView.findViewById(R.id.ImagesPageVp);
        }
    }

    public void setGoodNetrequest(final String url, final String token, final String id, final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (type.equals("0")) {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("qid", id)
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
                    } else {
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
            counter++;
            boolean firstTime = true;
            JSONObject jsonObject = new JSONObject(JsonData);
            status = jsonObject.getInt("status");
            if (status == 200 && whetherExciting && goodOrBad == 1 && firstTime && isForExciting) {
                Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
                //whetherExciting = true;
                firstTime = false;
            }

            if (status == 200 && !whetherExciting && goodOrBad == 1 && firstTime && isForExciting) {
                Toast.makeText(context, "取消点赞成功", Toast.LENGTH_SHORT).show();
                //whetherExciting = false;
                firstTime = false;
            }

            if (status == 200 && whetherNaive && goodOrBad == 0 && firstTime && isForExciting) {
                Toast.makeText(context, "嘲讽成功", Toast.LENGTH_SHORT).show();
                //whetherNaive = true;
                firstTime = false;
            }

            if (status == 200 && !whetherNaive && goodOrBad == 0 && firstTime && isForExciting) {
                Toast.makeText(context, "取消嘲讽成功", Toast.LENGTH_SHORT).show();
                //whetherNaive = false;
                firstTime = false;
            }

            if (status == 200 && whetherFavorite && firstTime && !isForExciting) {
                Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
                //whetherFavorite = true;
                firstTime = false;
            }

            if (status == 200 && !whetherFavorite && firstTime && !isForExciting) {
                Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
                //whetherFavorite = false;
                firstTime = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
