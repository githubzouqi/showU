package com.mushiny.www.showU.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mushiny.www.showU.R;
import com.mushiny.www.showU.entity.Future5WeatherEntity;
import com.mushiny.www.showU.util.LogUtil;

import java.util.List;

/**
 * 城市未来5天天气适配器
 */
public class FutureWeatherAdapter extends RecyclerView.Adapter<FutureWeatherAdapter.MyViewHolder> {

    private Context context;
    private List<Future5WeatherEntity> future5WeatherList;
    private LayoutInflater inflater;


    public FutureWeatherAdapter(Context context, List<Future5WeatherEntity> future5WeatherList) {
        this.context = context;
        this.future5WeatherList = future5WeatherList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = inflater.inflate(R.layout.recyclerview_itemview_future_weather, viewGroup, false);
        FutureWeatherAdapter.MyViewHolder holder = new FutureWeatherAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        String date = future5WeatherList.get(position).getFuture_date();
        String weather = future5WeatherList.get(position).getFuture_weather();
        String direct = future5WeatherList.get(position).getFuture_direct();
        String temperature = future5WeatherList.get(position).getFuture_temperature();

        holder.tv_future_date.setText(date);
        holder.tv_future_weather.setText(weather);
        holder.tv_future_direct.setText(direct);
        holder.tv_future_temperature.setText(temperature);

    }

    @Override
    public int getItemCount() {
        return future5WeatherList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_future_date;
        TextView tv_future_weather;
        TextView tv_future_direct;
        TextView tv_future_temperature;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_future_date = itemView.findViewById(R.id.tv_future_date);
            tv_future_weather = itemView.findViewById(R.id.tv_future_weather);
            tv_future_direct = itemView.findViewById(R.id.tv_future_direct);
            tv_future_temperature = itemView.findViewById(R.id.tv_future_temperature);

        }
    }
}
