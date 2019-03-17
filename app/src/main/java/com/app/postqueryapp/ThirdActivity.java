package com.app.postqueryapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ThirdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        ActionBar actionBar = getSupportActionBar();    // 获取系统自带的标题栏并隐藏，因为要使用自己定制的标题栏
        if(actionBar != null){
            actionBar.hide();
        }
    }
}
