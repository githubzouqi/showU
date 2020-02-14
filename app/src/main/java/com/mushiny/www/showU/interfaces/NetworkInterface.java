package com.mushiny.www.showU.interfaces;

import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.JokerCollectionEntity;
import com.mushiny.www.showU.entity.NewsEntity;
import com.mushiny.www.showU.entity.WeatherCityListEntity;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface NetworkInterface {

    @GET(Constants.URL_PATH_JOKER)
    Call<JokerCollectionEntity> getLatestJoker(@QueryMap Map<String, Object> map);// 笑话大全

    @GET(Constants.URL_PATH_NEWS_HEADLINE)
    Call<NewsEntity> getNews(@QueryMap Map<String, Object> map);// 新闻头条

    @GET(Constants.URL_PATH_WEATHER_CITY_LIST)
    Call<WeatherCityListEntity> getCities(@QueryMap Map<String, Object> map);// 天气：支持的城市列表

    @GET(Constants.URL_PATH_WEATHER_TYPES)
    Call<ResponseBody> getWeatherTypes(@QueryMap Map<String, Object> map);// 天气：天气种类列表

    @GET(Constants.URL_PATH_WEATHER_FORECAST_QUERY)
    Call<ResponseBody> getCityWeather(@QueryMap Map<String, Object> map);// 天气：根据城市查询天气

}
