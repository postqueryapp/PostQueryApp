package com.app.postqueryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    /** 主屏幕显示的 文字 变量 */
    private TextView mTextMessage;

//    /** 定义一个 底部菜单 选择监听器 */
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//    };

    /**
     * 主活动初始化类（生命周期类）用于加载布局、绑定事件
     * 时间：2019-03-14
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     // 调用父类方法，并启动主活动（活动要在AndroidManifest.xml里注册）
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();    // 获取系统自带的标题栏并隐藏，因为要使用自己定制的标题栏
        if(actionBar != null){
            actionBar.hide();
        }

        mTextMessage = (TextView) findViewById(R.id.message);      // 赋予全局变量 mTextMessage 值为一开始菜单界面的text

//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);     // 定义一个 底部导航 变量
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);      // 传入一个 菜单选择监听器

        /** 定义一个 按钮， 并设置监听事件 */
        Button button = (Button) findViewById(R.id.button_mine);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出消息
//                Toast.makeText(MainActivity.this, "You clicked Button", Toast.LENGTH_SHORT).show();

                // 显式活动
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);

                Intent intent = new Intent("com.example.activitytest.ACTION_START");        // 隐式活动， 此活动在AndroidManifest.xml里用action注册了

                // 自我添加的隐式活动类型
//                intent.addCategory("com.example.activitytest.MY_CATEGORY");

                String url = "http://baidu.com";        // 定义一个字符串， 向下一个活动传参时用到
                intent.putExtra("urldd",url);       // 将url字符串放入intent里， 传参时使用
                startActivityForResult(intent, 1);      // 开启下一个活动（ForResult是想要下一个活动销毁时返回一个参数，下面重写一方法获得下一个活动返回的参数）
            }
        });

        Button buttonSearch = (Button) findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显式活动
                Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 主活动获得第二活动销毁传递的参数类
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String returnedData = data.getStringExtra("resultMessage");
                    Toast.makeText(MainActivity.this, returnedData, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }



}
