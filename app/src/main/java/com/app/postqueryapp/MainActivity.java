package com.app.postqueryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.app.postqueryapp.adapter.QueryAdapter;
import com.app.postqueryapp.api.KdniaoTrackQueryAPI;
import com.app.postqueryapp.dto.QueryInformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends BaseActivity {

    /** 主屏幕显示的 文字 变量 */
    private List<QueryInformation> informationList = new ArrayList<>();

    private List<QueryInformation> inforHandles = new ArrayList<>();

    QueryAdapter adapter = null;

    RecyclerView recyclerView = null;

    private Runnable runnable = null;

    private String result = null;

    private Handler handler= null;



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

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        dialog.setTitle("查询中");
        dialog.setMessage("请稍后......");
        dialog.show();
        // 获得滑动控件视图
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        /**
         * 在handlerMessage()方法里，取到线程通信队列MessageQueue里的信息， 复原成ArrayList传入滑动控件适配器
         */
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                ArrayList<String> infos = new ArrayList<>();
                infos = data.getStringArrayList("info");
                String status = data.getString("status");
                for(int i =infos.size() - 1; i >= 0; i--){
                    QueryInformation information = new QueryInformation();
                    information.setTime(infos.get(i));
                    i--;
                    if(i == -1)
                        break;
                    information.setInfo(infos.get(i));
                    inforHandles.add(information);
                }
                Log.i("mylog","请求结果-->" + infos.toString());
                if(inforHandles.size() == 0 ){
                    QueryInformation information = new QueryInformation();
                    information.setInfo("暂无物流轨迹");
                    inforHandles.add(information);

                    adapter = new QueryAdapter(inforHandles);
                    recyclerView.setAdapter(adapter);
                    dialog.cancel();
                }
                else{
                    String selectCompany = getIntent().getStringExtra("selectCompany");
                    String selectNumber = getIntent().getStringExtra("selectNumber");
                    adapter = new QueryAdapter(inforHandles, selectNumber, selectCompany, status);
                    recyclerView.setAdapter(adapter);
                    dialog.cancel();
                }
            }
        };

        /**
         * 多线程接口， 实现多线程
         */
        runnable = new Runnable(){
            @Override
            public void run(){

                try{
                    // 下面一大排代码，是从api获得数据， 然后剪切数据为QueryInfomation, 传入ArryList里
                    KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
                    String selectCode = getIntent().getStringExtra("selectCode");
                    String selectNumber = getIntent().getStringExtra("selectNumber");
                    Thread.sleep(1000);
                    result = api.getQueryInformation(selectCode, selectNumber);
                    String ifNull =jxJson("Traces", result);
                    if(!ifNull.equals("[]")){

                        String state = jxJson("State", result);
                        switch (state){
                            case "2":state = "在途中";break;
                            case "3":state = "已签收";break;
                            case "4":state = "问题件";break;
                            default :break;
                        }

                        result = jxJson("Traces", result);
                        int acceptStation = 0;
                        int acceptTime = 0;
                        acceptStation = result.indexOf("\"AcceptStation\":\"", acceptStation) + 17;
                        acceptTime = result.indexOf("\",\"AcceptTime\"", acceptStation);

                        for(;!(acceptStation == -1 && acceptTime == -1);){
                            QueryInformation info = new QueryInformation();

                            info.setInfo(result.substring(acceptStation, acceptTime));
                            acceptStation = result.indexOf("\"},{\"AcceptStation\":\"", acceptTime);
                            if(acceptStation == -1){
                                info.setTime(result.substring(acceptTime, acceptTime + 20));
                                break;
                            }
                            acceptTime += 16;
                            info.setTime(result.substring(acceptTime, acceptStation));
                            acceptTime = result.indexOf("\",\"AcceptTime\":\"", acceptStation);
                            acceptStation += 21;
                            informationList.add(info);
                        }

                        if(informationList.size() != 0){
                            informationList.get(0).setStatus(state);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                // 设置bundle用于设置复杂数据， 然后传入线程通信队列MessageQueue里
                Bundle bundle = new Bundle();
                ArrayList<String> infos = new ArrayList<>();
                // 将从api获取并截取到的QueryInformation类，传入到StringList里， 便于设置bundle后，设置Message的data
                for(QueryInformation data:informationList){
                    infos.add(data.getInfo());
                    infos.add(data.getTime());
                }
                bundle.putStringArrayList("info", infos);
                if(informationList.size() != 0){
                    bundle.putString("status", informationList.get(0).getStatus());
                }
                Message msg = Message.obtain();
                msg.setData(bundle);
                // 这里是向线程通信队列MessageQueue 发送Message
                handler.sendMessage(msg);
            }
        };
        new Thread(runnable).start();
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

    /**
     * 截取JSON字符串中的某个属性的整个字符串 的工具方法
     * @param mkey
     * @param strJson
     * @return
     */
    public String jxJson(String mkey, String strJson) {
        String value = "";
        try {
            JSONObject json = new JSONObject(strJson);
            Iterator iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next() + "";

                if (json.getString(key).startsWith("{")) {
                    value = jxJson(mkey, json.getString(key));
                    break;
                } else {
                    if (key.equals(mkey)) {
                        value = json.getString(key);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (value.equals("null")) {
            value = "";
        }
        return value;
    }
}
