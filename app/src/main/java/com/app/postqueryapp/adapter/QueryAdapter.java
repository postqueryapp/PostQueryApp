package com.app.postqueryapp.adapter;

import android.support.v7.widget.RecyclerView;
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
    public QueryAdapter(List<QueryInformation> informationList){
        this.informationList = informationList;
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
        QueryInformation queryInformation = informationList.get(position);
        holder.time.setText(queryInformation.getTime());
        holder.info.setText(queryInformation.getInfo());
    }

    /**
     * 重写的方法， 用于返回滑动控件的长度
     * @return
     */
    @Override
    public int getItemCount(){
        System.out.println(informationList.size());
        return informationList.size();
    }
}
