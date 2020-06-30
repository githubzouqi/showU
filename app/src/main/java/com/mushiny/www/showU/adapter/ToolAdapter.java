package com.mushiny.www.showU.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.mushiny.www.showU.interfaces.MyItemClickInterface;
import com.mushiny.www.showU.util.ScaleUtil;
import com.mushiny.www.showU.util.ScreenUtil;

import java.util.List;

/**
 * 工具适配器
 */
public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.MyViewHolder>{

    private Context context;
    private List<Object[]> toolList;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;
    private RequestOptions options;

    public ToolAdapter(Context context, List<Object[]> toolList) {
        this.context = context;
        this.toolList = toolList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.recyclerview_itemview_tools, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        if (options == null){
            options = new RequestOptions();
            options.placeholder(R.mipmap.ic_launcher_round);// 设置占位图
//            options.override(200,200);// 指定加载图片大小
            options.override(Target.SIZE_ORIGINAL);// 加载图片原始尺寸
//            options.skipMemoryCache(true);// 禁用内存缓存。默认是开启的
        }

        String content = String.valueOf(toolList.get(i)[0]);
        Integer resourceId = Integer.parseInt(String.valueOf(toolList.get(i)[1]));
        holder.tv_item_tool.setText(content);
        Glide.with(context).load(resourceId).apply(options).into(holder.iv_item_tool);

    }

    @Override
    public int getItemCount() {
        return toolList.size();
    }

    /**
     * 注册监听器
     * @param itemClickInterface
     */
    public void setOnItemClick(MyItemClickInterface itemClickInterface){
        this.itemClickInterface = itemClickInterface;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_item_tool;
        TextView tv_item_tool;
        CardView cv_itemView_tool;

        public MyViewHolder(final View itemView) {
            super(itemView);

            iv_item_tool = itemView.findViewById(R.id.iv_item_tool);
            tv_item_tool = itemView.findViewById(R.id.tv_item_tool);
            cv_itemView_tool = itemView.findViewById(R.id.cv_itemView_tool);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    cv_itemView_tool.getLayoutParams();
            int width = new ScreenUtil().getScreenSize(ScreenUtil.WIDTH, context);
            int w = (width / 2) - ScaleUtil.dip2px(context, 26);
            int h = (int) (w / (0.9f));
            params.width = w;
            params.height = h;
            cv_itemView_tool.setLayoutParams(params);

            cv_itemView_tool.setOnClickListener(new View.OnClickListener() {
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
