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

/**
 * 工具适配器
 */
public class ToolRubbishSortAdapter extends RecyclerView.Adapter<ToolRubbishSortAdapter.MyViewHolder>{

    private Context context;
    private List<Object[]> rubbishList;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;
    private RequestOptions options;

    public ToolRubbishSortAdapter(Context context, List<Object[]> rubbishList) {
        this.context = context;
        this.rubbishList = rubbishList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.recyclerview_itemview_tools_rubbish_sort, viewGroup,
                false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        int aim = Integer.parseInt(String.valueOf(rubbishList.get(i)[0]));
        if (aim == 1){
            holder.iv_item_rubbish_sort_recommend.setVisibility(View.VISIBLE);
        }else {
            holder.iv_item_rubbish_sort_recommend.setVisibility(View.INVISIBLE);
        }
        // 干垃圾 湿垃圾 可回收垃圾 有害垃圾
        String name = String.valueOf(rubbishList.get(i)[1]);
        String type = String.valueOf(rubbishList.get(i)[2]);
        holder.tv_item_rubbish_sort_name.setText(name);
        if (type.equals("可回收垃圾")){
            holder.tv_item_rubbish_sort_type.setTextColor(context
                    .getResources().getColor(R.color.color_dark_blue));
        }
        if (type.equals("有害垃圾")){
            holder.tv_item_rubbish_sort_type.setTextColor(context
                    .getResources().getColor(R.color.color_red));
        }
        if (type.equals("干垃圾")){
            holder.tv_item_rubbish_sort_type.setTextColor(context
                    .getResources().getColor(R.color.color_rubbish_dry));
        }
        if (type.equals("湿垃圾")){
            holder.tv_item_rubbish_sort_type.setTextColor(context
                    .getResources().getColor(R.color.color_rubbish_wet));
        }
        holder.tv_item_rubbish_sort_type.setText(type);

    }

    @Override
    public int getItemCount() {
        return rubbishList.size();
    }

    /**
     * 注册监听器
     * @param itemClickInterface
     */
    public void setOnItemClick(MyItemClickInterface itemClickInterface){
        this.itemClickInterface = itemClickInterface;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_item_rubbish_sort_recommend;
        TextView tv_item_rubbish_sort_name;
        TextView tv_item_rubbish_sort_type;
        CardView cv_itemView_tool_rubbish_sort;

        public MyViewHolder(final View itemView) {
            super(itemView);

            iv_item_rubbish_sort_recommend = itemView.findViewById(R.id.iv_item_rubbish_sort_recommend);
            tv_item_rubbish_sort_name = itemView.findViewById(R.id.tv_item_rubbish_sort_name);
            tv_item_rubbish_sort_type = itemView.findViewById(R.id.tv_item_rubbish_sort_type);
            cv_itemView_tool_rubbish_sort = itemView.findViewById(R.id.cv_itemView_tool_rubbish_sort);

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
