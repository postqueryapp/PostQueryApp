package com.app.postqueryapp;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动控制类
 * onCreate()活动时，在BaseActivity类中的onCreate()方法中加入到此类的静态变量中。
 * 如果想要随时退出程序（比如退出程序按钮），则调用此类中的finishAll()方法。
 */
public class ActivityController {

    public static List<Activity> activityList = new ArrayList<>();

    /**
     * 为activityList添加一个活动
     * @param activity
     */
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }

    /**
     * 从activityList中移除一个活动
     * @param activity
     */
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }

    /**
     * 销毁所有活动
     */
    public static void finishAll(){
        for(Activity activity:activityList){
            activity.finish();
        }
        activityList.clear();
    }

    /**
     * 退出登录，销毁活动，只剩一个登录界面活动
     */
    public static void outSign(){
        while(activityList.size() > 1){
            activityList.remove(activityList.size() - 1);
        }
    }
}
