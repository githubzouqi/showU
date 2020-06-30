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
public class ToolWeatherAdapter extends RecyclerView.Adapter<ToolWeatherAdapter.MyViewHolder>{

    private Context context;
    private List<String[]> weatherList;
    private LayoutInflater inflater;
    private MyItemClickInterface itemClickInterface;
    private RequestOptions options;

    public ToolWeatherAdapter(Context context, List<String[]> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.recyclerview_itemview_tools_weather, viewGroup,
                false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        String main = weatherList.get(i)[0];
        String temp = weatherList.get(i)[1];
        String wind = weatherList.get(i)[2];

        holder.tv_item_weather_main.setText(main);
        holder.tv_item_weather_temp.setText(temp);
        holder.tv_item_weather_wind.setText(wind);

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    /**
     * 注册监听器
     * @param itemClickInterface
     */
    public void setOnItemClick(MyItemClickInterface itemClickInterface){
        this.itemClickInterface = itemClickInterface;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_item_weather_main;
        TextView tv_item_weather_temp;
        TextView tv_item_weather_wind;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tv_item_weather_main = itemView.findViewById(R.id.tv_item_weather_main);
            tv_item_weather_temp = itemView.findViewById(R.id.tv_item_weather_temp);
            tv_item_weather_wind = itemView.findViewById(R.id.tv_item_weather_wind);

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
