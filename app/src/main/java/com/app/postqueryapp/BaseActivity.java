package com.app.postqueryapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Base类
 * 继承自AppCompatActivity, 然后其他所有活动类继承此类
 * 重写onCreate()方法打印当前活动类的类名
 * 重写onDestroy()方法，用于调用ActivityController类中的removeActivity()方法，当有一个活动销毁时，ActivityController中静态变量list要移除一个活动
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * 重写onCreate()方法打印当前活动类的类名，便于知晓当前运行的是哪一个活动
     * 当一个活动执行onCreate()方法时，在ActivityController类中静态变量list添加一个活动
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());
        ActivityController.addActivity(this);
    }

    /**
     * 重写onDestroy()方法，用于调用ActivityController类中的removeActivity()方法，当有一个活动销毁时，ActivityController中静态变量list要移除一个活动
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityController.removeActivity(this);
    }
}


