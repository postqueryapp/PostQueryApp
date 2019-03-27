package com.app.postqueryapp.mineFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.postqueryapp.ActivityController;
import com.app.postqueryapp.MainActivity;
import com.app.postqueryapp.MainActivitySecond;
import com.app.postqueryapp.R;
import com.app.postqueryapp.bean.SpinnearBean;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * 物流查询界面 碎片
 */
public class SearchFragment extends Fragment {


    // 所填快递单号
    private EditText selectNumber = null;

    // 查询界面
    private View searchView = null;

    // 物流公司对应的编号
    private String selectCode = null;

    // 是否记住查询信息
    private CheckBox checkBoxInfo = null;

    private Button buttonDelete = null;

    private TextView hobbyTv;//选择快递公司
    /**快递公司列表集合*/
    private ArrayList<SpinnearBean> mHobbyList;
    private ArrayList<String> mHobbyNameList;//用于选择器显示
    private OptionsPickerView mHobbyPickerView;//选择器

    private int outProgress = 0;

    private Date startTime = null;

    private Date endTime = null;

    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                //这边判断,如果是back的按键被点击了   就自己拦截实现掉
                if (i == KeyEvent.KEYCODE_BACK) {
                    outProgress++;
                    if(startTime != null && outProgress == 2){
                        endTime = new Date();
                        if(endTime.getTime() - startTime.getTime() < 3000){
                            ActivityController.finishAll();
                        }
                        else{
                            outProgress = 0;
                            startTime = null;
                            endTime = null;
                        }
                    }
                    if(outProgress == 1){
                        Toast.makeText(searchView.getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        startTime = new Date();
                    }
                    return true;//表示处理了
                }
            }
            return false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_third, container, false);
        // 查询界面
        searchView = view;

        // 所填快递单号
        selectNumber = view.findViewById(R.id.input);

        // 是否记住查询信息
        checkBoxInfo = view.findViewById(R.id.checkBox_info);



        initViews();
        initDatas();
        initEvents();

        //监听back必须设置的
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        //然后在写这个监听器
        view.setOnKeyListener(backlistener);

        /**
         * 查询按钮 监听事件
         */
        try{
            SharedPreferences pref = searchView.getContext().getSharedPreferences("Info", MODE_PRIVATE);
            checkBoxInfo.setChecked(pref.getBoolean("isMemoryInfo", false));
            if(pref.getBoolean("isMemoryInfo", false)){
                System.out.println("正在提取查询记录");
                selectNumber.setText(pref.getString("MemoryNumber",null));
                System.out.println("提取时MemoryCompany为" + pref.getString("MemoryCompany",null));
                hobbyTv.setText(pref.getString("MemoryCompany",null));
                selectCode = mHobbyList.get(mHobbyNameList.indexOf(hobbyTv.getText().toString())).getParaValue();
            }
        }catch (Exception e){
            System.out.println("取查询记录出错");
        }

        buttonDelete = view.findViewById(R.id.button_delete);
        buttonDelete.setVisibility(View.INVISIBLE);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNumber.setText(null);
            }
        });

//        selectNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buttonDelete.setVisibility(View.VISIBLE);
//            }
//        });

        selectNumber.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    buttonDelete.setVisibility(View.VISIBLE);
                } else {
                    buttonDelete.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button search = view.findViewById(R.id.button_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 定义焦点视图
                View focuse = null;
                // 定义校验布尔值
                boolean cancel = false;

                selectNumber.setError(null);

                // 查询信息 校验
                if(TextUtils.isEmpty(selectNumber.getText())){
                    selectNumber.setError("请填写快递单号");
                    focuse = selectNumber;
                    cancel = true;
                }

                // 查询信息 校验
                if(selectCode == null || selectCode.length()== 0){
                    hobbyTv.setError("请选择快递公司");
                    focuse = hobbyTv;
                    cancel = true;
                }

                // cancel为false，则校验成功， 传去查询物流信息， true则失败， 获得焦点
                if(cancel){
                    focuse.requestFocus();
                }
                else{
                    if(checkBoxInfo.isChecked()){
                        System.out.println("正在保存查询记录");
                        SharedPreferences.Editor editor = searchView.getContext().getSharedPreferences("Info", MODE_PRIVATE).edit();
                        editor.putString("MemoryNumber", selectNumber.getText().toString());
                        editor.putString("MemoryCompany", hobbyTv.getText().toString());
                        System.out.println("MemoryCompany为" + hobbyTv.getText().toString());
                        editor.putBoolean("isMemoryInfo", checkBoxInfo.isChecked());
                        editor.apply();
                    } else{
                        System.out.println("正在取消保存查询记录");
                        SharedPreferences.Editor editor = searchView.getContext().getSharedPreferences("Info", MODE_PRIVATE).edit();
                        editor.putBoolean("isMemoryInfo", checkBoxInfo.isChecked());
                        editor.apply();
                    }
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("selectCompany",hobbyTv.getText().toString());
                    intent.putExtra("selectCode",selectCode);
                    intent.putExtra("selectNumber",selectNumber.getText().toString());
                    startActivity(intent);
                }

            }
        });

//        /**
//         * 选择物流公司按钮 监听事件
//         */
//        Button select = view.findViewById(R.id.button_select);
//        select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowChoise();
//            }
//        });

        return view;
    }

//    /**
//     * 选择物流公司 选择框弹出
//     */
//    private void ShowChoise()
//    {
//
//        // 定义弹出框
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 设置弹框的标题
//        builder.setTitle("快递公司");
//        // 指定下拉列表的显示数据
//        final String[] cities = {"中通快递", "圆通速递", "韵达速递", "邮政快递", "EMS", "京东快递", "宅急送", "优速快递", "德邦快递", "TNT快递", "UPS", "DHL", "FEDEX联邦(国内件）", "FEDEX联邦(国际件）"};
//        // 选择某个公司其对应的编号
//        final String[] codes = {"ZTO", "YTO", "YD", "YZPY", "EMS", "JD", "ZJS", "UC", "DBL", "TNT", "UPS", "DHL", "FEDEX", "FEDEX_GJ"};
//        // 设置下拉列表选择项， 并设置每个选项的监听事件
//        builder.setItems(cities, new DialogInterface.OnClickListener()
//        {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                Toast.makeText(getActivity(), "选择的快递公司为：" + cities[which], Toast.LENGTH_SHORT).show();
//                selectCompany = searchView.findViewById(R.id.select);
//                selectCompany.setText(cities[which]);
//                selectCode = codes[which];
//            }
//        });
//        // 弹出选择框
//        builder.show();
//    }

    private void initViews() {
        hobbyTv = searchView.findViewById(R.id.hobbyTv);
    }

    private void initDatas() {
        //========================================初始化快递公司列表集合========================================
        mHobbyList = new ArrayList<SpinnearBean>();
        mHobbyNameList = new ArrayList<String>();

        //模拟获取数据集合
        try{
            mHobbyList = parseJsonArray("spinners.txt");
        }catch (Exception e) {
            e.printStackTrace();
        }
        for(SpinnearBean spinnearBean : mHobbyList){
            mHobbyNameList.add(spinnearBean.getParaName());
        }

        //============初始化选择器============
        initHobbyOptionPicker();
        //如果想要直接赋值的话，这样写
        /*if(mHobbyNameList.size() > 0){
            hobbyTv.setText(mHobbyNameList.get(0));//默认展现第一个
        }*/
    }

    //初始化快递公司选择器
    private void initHobbyOptionPicker() {
        mHobbyPickerView = new OptionsPickerBuilder(searchView.getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = mHobbyNameList.get(options1);
                hobbyTv.setText(tx);
                selectCode = mHobbyList.get(options1).getParaValue();
                Toast.makeText(searchView.getContext(), selectCode, Toast.LENGTH_SHORT).show();
            }
        })
//                .setDecorView((RelativeLayout)searchView.findViewById(R.id.search_layout))//必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                .setTitleText("选择快递公司")//标题文字
                .setTitleSize(20)//标题文字大小
                .setTitleColor(getResources().getColor(R.color.pickerview_title_text_color))//标题文字颜色
                .setCancelText("取消")//取消按钮文字
                .setCancelColor(getResources().getColor(R.color.pickerview_cancel_text_color))//取消按钮文字颜色
                .setSubmitText("确定")//确认按钮文字
                .setSubmitColor(getResources().getColor(R.color.pickerview_submit_text_color))//确定按钮文字颜色
                .setContentTextSize(20)//滚轮文字大小
                .setTextColorCenter(getResources().getColor(R.color.pickerview_center_text_color))//设置选中文本的颜色值
                .setLineSpacingMultiplier(1.8f)//行间距
                .setDividerColor(getResources().getColor(R.color.pickerview_divider_color))//设置分割线的颜色
                .setSelectOptions(0)//设置选择的值
                .build();

        mHobbyPickerView.setPicker(mHobbyNameList);//添加数据
    }

    private void initEvents() {
        //选择快递公司的下拉菜单点击事件
        hobbyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) searchView.getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(selectNumber.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//同上，editText也为你要收起键盘的那个EditText对象控件
                mHobbyPickerView.show();
            }
        });
    }

    public static final String LISTROOTNODE = "spinnerList";
    public static final String KEY_LISTITEM_NAME = "paraName";
    public static final String KEY_LISTITEM_VALUE = "paraValue";
    public static final String KEY_LISTITEM_CHECKCOLOR = "checkColor";

    /**
     * 解析JSON文件的简单数组
     */
    private ArrayList<SpinnearBean> parseJsonArray(String fileName) throws Exception{

        ArrayList<SpinnearBean> itemsList = new ArrayList<SpinnearBean>();

        String jsonStr = getStringFromAssert(searchView.getContext(), fileName);
        if(jsonStr.equals("")){
            return null;
        }
        JSONObject allData = new JSONObject(jsonStr);  //全部内容变为一个项
        JSONArray jsonArr = allData.getJSONArray(LISTROOTNODE); //取出数组
        for(int x = 0;x<jsonArr.length();x++){
            SpinnearBean model = new SpinnearBean();
            JSONObject jsonobj = jsonArr.getJSONObject(x);
            model.setParaName(jsonobj.getString(KEY_LISTITEM_NAME));
            model.setParaValue(jsonobj.getString(KEY_LISTITEM_VALUE));
            if(jsonobj.has(KEY_LISTITEM_CHECKCOLOR)){
                model.setCheckColor(jsonobj.getString(KEY_LISTITEM_CHECKCOLOR));
            }
            model.setSelectedState(false);
            itemsList.add(model);
            model = null;
        }
        return itemsList;
    }

    /**
     * 访问assets目录下的资源文件，获取文件中的字符串
     * @param filePath - 文件的相对路径，例如："listdata.txt"或者"/www/listdata.txt"
     * @return 内容字符串
     * */
    public String getStringFromAssert(Context mContext, String filePath) {

        String content = ""; // 结果字符串
        try {
            InputStream is = mContext.getResources().getAssets().open(filePath);// 打开文件
            int ch = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream(); // 实现了一个输出流
            while ((ch = is.read()) != -1) {
                out.write(ch); // 将指定的字节写入此 byte 数组输出流
            }
            byte[] buff = out.toByteArray();// 以 byte 数组的形式返回此输出流的当前内容
            out.close(); // 关闭流
            is.close(); // 关闭流
            content = new String(buff, "UTF-8"); // 设置字符串编码
        } catch (Exception e) {
            Toast.makeText(mContext, "对不起，没有找到指定文件！", Toast.LENGTH_SHORT)
                    .show();
        }
        return content;
    }
}
