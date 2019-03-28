package com.app.postqueryapp;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.postqueryapp.mineFragment.MineFragment;
import com.app.postqueryapp.mineFragment.MyFragmentPagerAdapter;
import com.app.postqueryapp.mineFragment.SearchFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 底部导航滑动控件（主界面的左右滑动）
 */
public class MainActivitySecond extends BaseActivity implements View.OnClickListener {
    // 碎片list
    private List<Fragment> fragments = new ArrayList<Fragment>();
    // 翻页效果视图
    private ViewPager viewPager;
    // 查询界面布局 和 我的中心界面布局
    private LinearLayout searchLayout, mineLayout;
    // 查询物流文字 和 我的中心文字 和 当前碎片
    private TextView search, mine, tvCurrent;

    private int outProgress = 0;

    private Date startTime = null;

    private Date endTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_second);

        ActionBar actionBar = getSupportActionBar();    // 获取系统自带的标题栏并隐藏，因为要使用自己定制的标题栏
        if(actionBar != null){
            actionBar.hide();
        }

        // 初始化布局
        initView();

        // 初始化碎片界面
        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        searchLayout = (LinearLayout) findViewById(R.id.search_layout);
        mineLayout = (LinearLayout) findViewById(R.id.mine_layout);

        searchLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);

        search = (TextView) findViewById(R.id.search_post);
        mine = (TextView) findViewById(R.id.mine);

        search.setSelected(true);
        tvCurrent = search;

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
        Fragment searchFragment = new SearchFragment();
        Fragment mineFragment = new MineFragment();

        TextView changeTitle = findViewById(R.id.title);
        changeTitle.setText("查询快递");

        fragments.add(searchFragment);
        fragments.add(mineFragment);

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//      MyFragmentStatePagerAdapter adapter = new MyFragmentStatePagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        TextView textView = findViewById(R.id.search_post);
        textView.setTextColor(getResources().getColor(R.color.color_Bottom));

        ImageView imageView = findViewById(R.id.search_image);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.search2));
    }

    /**
     * 切换碎片页面的方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        changeTab(v.getId());
    }

    private void changeTab(int id) {
        TextView changeTitle = findViewById(R.id.title);

        TextView textViewSearch = findViewById(R.id.search_post);
        TextView textViewMine = findViewById(R.id.mine);

        ImageView imageViewSearch = findViewById(R.id.search_image);
        ImageView imageViewMine = findViewById(R.id.mine_image);
        tvCurrent.setSelected(false);
        switch (id) {
            case
                R.id.search_layout:
                changeTitle.setText("查询快递");
                viewPager.setCurrentItem(0);

                textViewSearch.setTextColor(getResources().getColor(R.color.color_Bottom));
                textViewMine.setTextColor(getResources().getColor(R.color.pickerview_cancel_text_color));

                imageViewSearch.setImageDrawable(getResources().getDrawable(R.drawable.search2));
                imageViewMine.setImageDrawable(getResources().getDrawable(R.drawable.center1));
            case 0:
                changeTitle.setText("查询快递");
                search.setSelected(true);
                tvCurrent = search;

                textViewSearch.setTextColor(getResources().getColor(R.color.color_Bottom));
                textViewMine.setTextColor(getResources().getColor(R.color.pickerview_cancel_text_color));

                imageViewSearch.setImageDrawable(getResources().getDrawable(R.drawable.search2));
                imageViewMine.setImageDrawable(getResources().getDrawable(R.drawable.center1));
                break;
            case
                R.id.mine_layout:
                changeTitle.setText("我的中心");
                viewPager.setCurrentItem(1);

                findViewById(R.id.input).clearFocus();
                textViewMine.setTextColor(getResources().getColor(R.color.color_Bottom));
                textViewSearch.setTextColor(getResources().getColor(R.color.pickerview_cancel_text_color));

                imageViewSearch.setImageDrawable(getResources().getDrawable(R.drawable.search1));
                imageViewMine.setImageDrawable(getResources().getDrawable(R.drawable.center2));
            case 1:
                changeTitle.setText("我的中心");
                mine.setSelected(true);
                tvCurrent = mine;

                findViewById(R.id.input).clearFocus();
                textViewMine.setTextColor(getResources().getColor(R.color.color_Bottom));
                textViewSearch.setTextColor(getResources().getColor(R.color.pickerview_cancel_text_color));

                imageViewSearch.setImageDrawable(getResources().getDrawable(R.drawable.search1));
                imageViewMine.setImageDrawable(getResources().getDrawable(R.drawable.center2));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            outProgress++;
            if(startTime != null && outProgress == 2){
                endTime = new Date();
                if(endTime.getTime() - startTime.getTime() < 3000){
                    ActivityController.finishAll();
                }
                else{
                    outProgress = 1;
                    startTime = null;
                    endTime = null;
                }
            }
            if(outProgress == 1){
                Toast.makeText(MainActivitySecond.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                startTime = new Date();
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    return true;

                case KeyEvent.KEYCODE_MENU:
                    return true;

                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
