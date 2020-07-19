package com.mushiny.www.showU.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.entity.NewsEntity;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.util.ScaleUtil;
import com.mushiny.www.showU.util.ScreenUtil;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    private Context context;
    private List<NewsEntity.DataBean> dataBeans;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;

    private RequestOptions options;
    private int w = 300;
    private int h = 300;

    public NewsAdapter(Context context, List<NewsEntity.DataBean> dataBeans) {
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

        View view = inflater.inflate(R.layout.recyclerview_itemview_new_list, viewGroup,
                false);
        NewsAdapter.MyViewHolder holder = new NewsAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.app_icon);// 设置占位图
            options.error(R.mipmap.load_error);
            options.override(w,h);// 指定加载图片大小
            options.fitCenter();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);// 缓存所有：原型、转换后的
//        options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }

        String title = dataBeans.get(position).getTitle();
        String imgUrl = "";
        if (dataBeans.get(position).getImgList() != null){
            imgUrl = dataBeans.get(position).getImgList().get(0);
        }
        String source = dataBeans.get(position).getSource();
        String postTime = dataBeans.get(position).getPostTime();
        if (TextUtils.isEmpty(postTime)){
            postTime = "";
        }

        holder.tv_title.setText(Html.fromHtml(title));
        if (!TextUtils.isEmpty(imgUrl)){
            Glide.with(context).load(Uri.parse(imgUrl)).apply(options).into(holder.iv_new_list);
        }
        if (TextUtils.isEmpty(source)){
            source = context.getResources().getString(R.string.app_name);
        }
        holder.tv_source_time.setText(source + "  " + postTime);

    }

    @Override
    public int getItemCount() {
        return dataBeans.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView iv_new_list;
        TextView tv_source_time;
        CardView cardView;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_new_list = itemView.findViewById(R.id.iv_new_list);
            tv_source_time = itemView.findViewById(R.id.tv_source_time);

            // 设置图片宽高
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv_new_list
                    .getLayoutParams();
            w = new ScreenUtil().getScreenSize(ScreenUtil.WIDTH, context)
                    - ScaleUtil.dip2px(context, 20);
            h = (int) (w * (0.618f));
            params.width= w;
            params.height = h;
            iv_new_list.setLayoutParams(params);

            cardView = itemView.findViewById(R.id.cardView);
            // cardView 击事件
            cardView.setOnClickListener(new View.OnClickListener() {
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
