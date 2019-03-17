package com.app.postqueryapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends BaseActivity {

    /**
     * 第二活动初始化类（生命周期类）用于加载布局、绑定事件
     * 时间：2019-03-14
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);     // 调用父类方法，并启动主活动（活动要在AndroidManifest.xml里注册）
        setContentView(R.layout.activity_second);

        ActionBar actionBar = getSupportActionBar();    // 获取系统自带的标题栏并隐藏，因为要使用自己定制的标题栏
        if(actionBar != null){
            actionBar.hide();
        }

        /** 定义一个 按钮， 并设置监听事件， 获得主活动传递的url */
        Button buttonSecond = (Button) findViewById(R.id.button_second_one);
        buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);     // 定义一个拥有专属行为的活动， 这里是为打开浏览器网页，并没有注册此活动
                intent.setData(Uri.parse(getIntent().getStringExtra("urldd")));
                startActivity(intent);
            }
        });

        /** 定义一个 按钮， 并设置监听事件 */
        Button buttonPre = (Button) findViewById(R.id.button_pre);
        buttonPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();       // 定义一个传参活动， 这里是为返回主活动时传递参数，并没有注册此活动
                intent.putExtra("resultMessage","Hello, PreActivity");      // 设置传递参数的 键值对
                setResult(RESULT_OK, intent);       // 设置返回结果， 一个返回类型， 一个参数类活动
                finish();       // 销毁活动，回到主活动
            }
        });

        /** 定义一个 按钮， 并设置监听事件 */
        Button buttonOut = (Button) findViewById(R.id.button_out);
        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityController.finishAll();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    /**
     * Back键时， 返回主活动参数
     */
    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("resultMessage", "Hello, PreActivity");
        setResult(RESULT_OK, intent);
        finish();
    }
}
