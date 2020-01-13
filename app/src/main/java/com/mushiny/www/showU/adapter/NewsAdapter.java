package com.mushiny.www.showU.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.entity.NewsEntity;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context context;
    private List<NewsEntity.ResultBean.DataBean> dataBeans;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;

    private RequestOptions options;
    private int WIDTH = 300;
    private int HEIGHT = 300;

    public NewsAdapter(Context context, List<NewsEntity.ResultBean.DataBean> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;
        this.inflater = LayoutInflater.from(context);
    }

    /**
     * 注册监听器
     * @param itemClickInterface
     */
    public void setOnItemClick(MyItemClickInterface itemClickInterface){
        this.itemClickInterface = itemClickInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.recyclerview_itemview_news, viewGroup, false);
        NewsAdapter.MyViewHolder holder = new NewsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.ic_launcher_round);// 设置占位图
            options.override(WIDTH,HEIGHT);// 指定加载图片大小
//        options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }

        String title = dataBeans.get(position).getTitle();
        String date = dataBeans.get(position).getDate();
        String author_name = dataBeans.get(position).getAuthor_name();
        String url = dataBeans.get(position).getUrl();

        // 图片的链接地址
        String pic_s1 = dataBeans.get(position).getThumbnail_pic_s();
        String pic_s2 = dataBeans.get(position).getThumbnail_pic_s02();
        String pic_s3 = dataBeans.get(position).getThumbnail_pic_s03();

        if (!TextUtils.isEmpty(title)){
            holder.tv_title.setText(title);
        }else {
            holder.tv_title.setText("---");
        }

        if (!TextUtils.isEmpty(date)){
            holder.tv_date.setText(date);
        }else {
            holder.tv_date.setText("---");
        }

        if (!TextUtils.isEmpty(author_name)){
            holder.tv_author_name.setText(author_name);
        }else {
            holder.tv_author_name.setText("---");
        }

        if (!TextUtils.isEmpty(pic_s1)){
            holder.iv_s01.setVisibility(View.VISIBLE);
            Glide.with(context).load(pic_s1).apply(options).into(holder.iv_s01);
        }

        if (!TextUtils.isEmpty(pic_s2)){
            holder.iv_s02.setVisibility(View.VISIBLE);
            Glide.with(context).load(pic_s2).apply(options).into(holder.iv_s02);
        }

        if (!TextUtils.isEmpty(pic_s3)){
            holder.iv_s03.setVisibility(View.VISIBLE);
            Glide.with(context).load(pic_s3).apply(options).into(holder.iv_s03);
        }

    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView iv_s01;
        ImageView iv_s02;
        ImageView iv_s03;
        TextView tv_author_name;
        TextView tv_date;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_s01 = itemView.findViewById(R.id.iv_s01);
            iv_s02 = itemView.findViewById(R.id.iv_s02);
            iv_s03 = itemView.findViewById(R.id.iv_s03);
            tv_author_name = itemView.findViewById(R.id.tv_author_name);
            tv_date = itemView.findViewById(R.id.tv_date);

            // 标题点击事件
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickInterface != null){
                        itemClickInterface.OnRecyclerViewItemClick(itemView, getAdapterPosition());
                    }
                }
            });

        }
    }
}
