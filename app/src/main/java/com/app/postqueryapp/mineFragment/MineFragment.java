package com.app.postqueryapp.mineFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.postqueryapp.ActivityController;
import com.app.postqueryapp.R;

/**
 * 我的中心界面 碎片
 */
public class MineFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_second, container, false);

        /** 定义一个 按钮， 并设置监听事件  用于退出程序*/
        Button buttonOut = view.findViewById(R.id.button_out);
        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityController.finishAll();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        /** 定义一个 按钮， 并设置监听事件 用于退出登录 */
        Button outSign = view.findViewById(R.id.button_out_sign);
        outSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityController.outSign();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        return view;
    }

}
