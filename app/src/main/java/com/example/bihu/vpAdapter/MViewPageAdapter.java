package com.example.bihu.vpAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.bihu.R;

import java.util.ArrayList;

public class MViewPageAdapter extends PagerAdapter {
    private ArrayList<String>mData;
    private Context context;
    public MViewPageAdapter(ArrayList<String>mData,Context context){
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.vp_layout,null);
        ImageView imageView = view.findViewById(R.id.imageView);
        if(mData.get(position) != null && !mData.get(position).equals("") &&!mData.get(position).equals("null")){
            Glide.with(context)
                    .load(mData.get(position))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(imageView);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
