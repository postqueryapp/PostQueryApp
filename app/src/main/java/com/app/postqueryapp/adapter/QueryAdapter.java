package com.app.postqueryapp.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.postqueryapp.R;
import com.app.postqueryapp.dto.QueryInformation;

import java.util.List;

/**
 * 适配器， 用于滑动控件的适配
 */
public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder>{

    private List<QueryInformation> informationList;

    private String postNumber;

    private String postCompany;

    private String status;

    /**
     * 定义一个信息框体， 承载一条详细信息 和 其创建时间
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        TextView info;

        public ViewHolder(View view){
            super(view);
            time = view.findViewById(R.id.time);
            info = view.findViewById(R.id.info);
        }
    }

    /**
     * 适配器的构造方法， 用于初始化数据
     * @param informationList
     */
    public QueryAdapter(List<QueryInformation> informationList, String postNumber, String postCompany, String status){
        this.informationList = informationList;
        this.postNumber = postNumber;
        this.postCompany = postCompany;
        this.status = status;
        QueryInformation queryInformation = new QueryInformation();
        queryInformation.setInfo("单号：" + postNumber);
        queryInformation.setTime(postCompany + " 状态：" + status);
        informationList.add(0,queryInformation);
    }

    public QueryAdapter(List<QueryInformation> informationList, String postNumber, String postCompany){
        this.informationList = informationList;
        this.postNumber = postNumber;
        this.postCompany = postCompany;
        QueryInformation queryInformation = new QueryInformation();
        queryInformation.setInfo("单号：" + postNumber + "  " + postCompany);
        queryInformation.setTime(postCompany);
        informationList.add(0,queryInformation);
    }

    /**
     * 重写的onCreate()方法， 用于以informationList为变量去构造信息框体 ViewHolder，再将其返回，用于构造整体的 快递查询结果的滑动控件
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    /**
     * 重写方法， 用于设置信息框体
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(position == 0 && informationList.get(1).getInfo().equals("暂无物流轨迹")){
            QueryInformation queryInformation = informationList.get(position);
            holder.info.setText("  " + queryInformation.getInfo() + "  " + queryInformation.getTime());
            holder.info.setTextColor(Color.RED);
            holder.info.setGravity(Gravity.CENTER);
            holder.info.getLayoutParams().width = 875;
            holder.time.setVisibility(View.GONE);
        }
        else if(getItemCount() == 2 && informationList.get(1).getInfo().equals("暂无物流轨迹")){
            QueryInformation queryInformation = informationList.get(position);
            holder.info.setText(queryInformation.getInfo());
            holder.info.setTextColor(Color.RED);
            holder.info.setGravity(Gravity.CENTER);
            holder.info.getLayoutParams().width = 875;
            holder.time.setVisibility(View.GONE);
        }
        if(position == 0 && !informationList.get(1).getInfo().equals("暂无物流轨迹")){
            QueryInformation queryInformation = informationList.get(position);
            holder.info.setText("  " + queryInformation.getInfo() + "  " + queryInformation.getTime());
            holder.info.setGravity(Gravity.CENTER);
            holder.info.setTextColor(Color.rgb(76, 175, 89));
            holder.time.setVisibility(View.GONE);
            holder.info.getLayoutParams().width = 875;
        }
        else{
            QueryInformation queryInformation = informationList.get(position);
            holder.time.setText(queryInformation.getTime());
            holder.info.setText(queryInformation.getInfo());
        }
    }

    /**
     * 重写的方法， 用于返回滑动控件的长度
     * @return
     */
    @Override
    public int getItemCount(){
        return informationList.size();
    }
}
