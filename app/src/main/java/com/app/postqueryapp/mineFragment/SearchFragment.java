package com.app.postqueryapp.mineFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.postqueryapp.MainActivity;
import com.app.postqueryapp.MainActivitySecond;
import com.app.postqueryapp.R;

/**
 * 物流查询界面 碎片
 */
public class SearchFragment extends Fragment {

    // 所选物流公司
    private EditText selectCompany = null;

    // 所填快递单号
    private EditText selectNumber = null;

    // 查询界面
    private View searchView = null;

    // 物流公司对应的编号
    private String selectCode = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_third, container, false);
        // 查询界面
        searchView = view;
        // 所填快递单号
        selectNumber = view.findViewById(R.id.input);
        // 所选物流公司
        selectCompany = searchView.findViewById(R.id.select);
        selectCompany.setFocusableInTouchMode(false);       // 设置为不可编辑

        /**
         * 查询按钮 监听事件
         */
        Button search = view.findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 定义焦点视图
                View focuse = null;
                // 定义校验布尔值
                boolean cancel = false;

                // 初始化错误为空
                selectCompany.setError(null);
                selectNumber.setError(null);

                // 查询信息 校验
                if(TextUtils.isEmpty(selectNumber.getText())){
                    selectNumber.setError("请填写快递单号");
                    focuse = selectNumber;
                    cancel = true;
                }
                if(TextUtils.isEmpty(selectCompany.getText())){
                    selectCompany.setError("请选择快递公司");
                    focuse = selectCompany;
                    cancel = true;
                }

                // cancel为false，则校验成功， 传去查询物流信息， true则失败， 获得焦点
                if(cancel){
                    focuse.requestFocus();
                }
                else{
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("selectCode",selectCode);
                    intent.putExtra("selectNumber",selectNumber.getText().toString());
                    startActivity(intent);
                }

            }
        });

        /**
         * 选择物流公司按钮 监听事件
         */
        Button select = view.findViewById(R.id.button_select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowChoise();
            }
        });

        return view;
    }

    /**
     * 选择物流公司 选择框弹出
     */
    private void ShowChoise()
    {

        // 定义弹出框
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // 设置弹框的标题
        builder.setTitle("快递公司");
        // 指定下拉列表的显示数据
        final String[] cities = {"中通快递", "圆通速递", "韵达速递", "邮政快递", "EMS", "京东快递", "宅急送", "优速快递", "德邦快递", "TNT快递", "UPS", "DHL", "FEDEX联邦(国内件）", "FEDEX联邦(国际件）"};
        // 选择某个公司其对应的编号
        final String[] codes = {"ZTO", "YTO", "YD", "YZPY", "EMS", "JD", "ZJS", "UC", "DBL", "TNT", "UPS", "DHL", "FEDEX", "FEDEX_GJ"};
        // 设置下拉列表选择项， 并设置每个选项的监听事件
        builder.setItems(cities, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getActivity(), "选择的快递公司为：" + cities[which], Toast.LENGTH_SHORT).show();
                selectCompany = searchView.findViewById(R.id.select);
                selectCompany.setText(cities[which]);
                selectCode = codes[which];
            }
        });
        // 弹出选择框
        builder.show();
    }


}
