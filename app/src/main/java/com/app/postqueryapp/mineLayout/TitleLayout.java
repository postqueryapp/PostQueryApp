package com.app.postqueryapp.mineLayout;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.postqueryapp.R;

public class TitleLayout extends LinearLayout {
    public TitleLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.mine_title, this);

        TextView titleBack = (TextView) findViewById(R.id.title_back_mine);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });

//        LayoutInflater.from(context).inflate(R.layout.mine_title, this);
//
//        Button titleMineBack = (Button) findViewById(R.id.title_back);
//        titleMineBack.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((Activity) getContext()).finish();
//            }
//        });
//
//        LayoutInflater.from(context).inflate(R.layout.search_title, this);
//
//        Button titleSearchBack = (Button) findViewById(R.id.title_back);
//        titleSearchBack.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((Activity) getContext()).finish();
//            }
//        });
    }
}
