package com.mushiny.www.showU.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.mushiny.www.showU.R;
import com.mushiny.www.showU.interfaces.MyItemClickInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 工具适配器
 */
public class ToolLogisticsAdapter extends RecyclerView.Adapter<ToolLogisticsAdapter.MyViewHolder>{

    private Context context;
    private List<String[]> infoList;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;
    private RequestOptions options;

    public ToolLogisticsAdapter(Context context, List<String[]> infoList) {
        this.context = context;
        this.infoList = infoList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.recyclerview_itemview_tool_logistics, viewGroup,
                false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String time = infoList.get(position)[0];
        String desc = infoList.get(position)[1];

        holder.tv_logistics_time.setText(time);
        holder.tv_logistics_desc.setText(desc);

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    /**
     * 注册监听器
     * @param itemClickInterface
     */
    public void setOnItemClick(MyItemClickInterface itemClickInterface){
        this.itemClickInterface = itemClickInterface;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_logistics_time)TextView tv_logistics_time;
        @BindView(R.id.tv_logistics_desc)TextView tv_logistics_desc;

        public MyViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            /*
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    cv_itemView_tool_rubbish_sort.getLayoutParams();
            int width = new ScreenUtil().getScreenSize(ScreenUtil.WIDTH, context);
            int w = (width / 2) - ScaleUtil.dip2px(context, 26);
            int h = (int) (w / (0.9f));
            params.width = w;
            params.height = h;
            cv_itemView_tool_rubbish_sort.setLayoutParams(params);
            */

        }
    }

}
