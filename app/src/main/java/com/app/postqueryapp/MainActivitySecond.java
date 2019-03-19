package com.app.postqueryapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.postqueryapp.mineFragment.IndexFragment;
import com.app.postqueryapp.mineFragment.MineFragment;
import com.app.postqueryapp.mineFragment.MyFragmentPagerAdapter;
import com.app.postqueryapp.mineFragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivitySecond extends BaseActivity implements View.OnClickListener {
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private ViewPager viewPager;
    private LinearLayout searchLayout, indexLayout, mineLayout;
    private TextView search, index, mine, tvCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_second);

        ActionBar actionBar = getSupportActionBar();    // 获取系统自带的标题栏并隐藏，因为要使用自己定制的标题栏
        if(actionBar != null){
            actionBar.hide();
        }

        initView();

        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        searchLayout = (LinearLayout) findViewById(R.id.search_layout);
        indexLayout = (LinearLayout) findViewById(R.id.index_layout);
        mineLayout = (LinearLayout) findViewById(R.id.mine_layout);

        indexLayout.setOnClickListener(this);
        searchLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);

        search = (TextView) findViewById(R.id.search_post);
        index = (TextView) findViewById(R.id.index);
        mine = (TextView) findViewById(R.id.mine);

        index.setSelected(true);
        tvCurrent = index;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        viewPager.setOffscreenPageLimit(2); //设置向左和向右都缓存limit个页面
    }

    private void initData() {
        Fragment indexFragment = new IndexFragment();
        Fragment searchFragment = new SearchFragment();
        Fragment mineFragment = new MineFragment();

        TextView changeTitle = findViewById(R.id.title);
        changeTitle.setText("首页");

        fragments.add(indexFragment);
        fragments.add(searchFragment);
        fragments.add(mineFragment);

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//      MyFragmentStatePagerAdapter adapter = new MyFragmentStatePagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
//        TextView changeTitle = findViewById(R.id.title);
//        if(tvCurrent.equals(search)){
//            changeTitle.setText("查询快递");
//        }
//        else if(tvCurrent.equals(index)){
//            changeTitle.setText("首页");
//        }
//        else{
//            changeTitle.setText("我的中心");
//        }
        changeTab(v.getId());
    }

    private void changeTab(int id) {
        TextView changeTitle = findViewById(R.id.title);
        tvCurrent.setSelected(false);
        switch (id) {
            case
                R.id.index_layout:
                changeTitle.setText("首页");
                viewPager.setCurrentItem(0);
            case 0:
                changeTitle.setText("首页");
                index.setSelected(true);
                tvCurrent = index;
                break;
            case
                R.id.search_layout:
                changeTitle.setText("查询快递");
                viewPager.setCurrentItem(1);
            case 1:
                changeTitle.setText("查询快递");
                search.setSelected(true);
                tvCurrent = search;
                break;
            case
                R.id.mine_layout:
                changeTitle.setText("我的中心");
                viewPager.setCurrentItem(2);
            case 2:
                changeTitle.setText("我的中心");
                mine.setSelected(true);
                tvCurrent = mine;
                break;
            default:
                break;
        }
    }
}
