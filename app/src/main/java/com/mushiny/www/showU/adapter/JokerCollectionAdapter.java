package com.mushiny.www.showU.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.entity.JokerCollectionEntity;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;

import java.util.List;

public class JokerCollectionAdapter extends RecyclerView.Adapter<JokerCollectionAdapter.MyViewHolder> {

    private Context context;
    private List<JokerCollectionEntity.DataBean> dataBeans;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;
    private RequestOptions options;

    public JokerCollectionAdapter(Context context, List<JokerCollectionEntity.DataBean> dataBeans) {
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_itemview_joker, viewGroup,
                false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.app_icon);// 设置占位图
//            options.override(200,200);// 指定加载图片大小
            options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }

        holder.tv_joker_content.setText(Html.fromHtml(dataBeans.get(position).getContent()));
        holder.tv_joker_update_time.setText(dataBeans.get(position).getUpdateTime());
        Glide.with(context).asGif()
                .load(R.drawable.joker_image).apply(options).into(holder.iv_joker);

    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_joker_content;
        TextView tv_joker_update_time;
        ImageView iv_joker;
        LinearLayout linear_item_joker;

        public MyViewHolder(final View itemView) {
            super(itemView);
            tv_joker_content = itemView.findViewById(R.id.tv_joker_content);
            tv_joker_update_time = itemView.findViewById(R.id.tv_joker_update_time);
            iv_joker = itemView.findViewById(R.id.iv_joker);
            linear_item_joker = itemView.findViewById(R.id.linear_item_joker);

//            linear_item_joker.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (itemClickInterface != null){
//                        itemClickInterface.OnRecyclerViewItemClick(itemView, getAdapterPosition());
//                    }
//                }
//            });

        }
    }

}
