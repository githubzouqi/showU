package com.mushiny.www.showU.interfaces;

import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.JokerCollectionEntity;
import com.mushiny.www.showU.entity.NewsEntity;
import com.mushiny.www.showU.entity.WeatherCityListEntity;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface NetworkInterface {

    /* -聚合数据- */
    @GET(Constants.URL_PATH_NEWS_HEADLINE)
    Call<NewsEntity> getNews(@QueryMap Map<String, Object> map);// 新闻头条

    @GET(Constants.URL_PATH_WEATHER_CITY_LIST)
    Call<WeatherCityListEntity> getCities(@QueryMap Map<String, Object> map);// 天气：支持的城市列表

    @GET(Constants.URL_PATH_WEATHER_TYPES)
    Call<ResponseBody> getWeatherTypes(@QueryMap Map<String, Object> map);// 天气：天气种类列表

    @GET(Constants.URL_PATH_WEATHER_FORECAST_QUERY)
    Call<ResponseBody> getCityWeather(@QueryMap Map<String, Object> map);// 天气：根据城市查询天气
    /* -聚合数据- */

    /* -RollToolsApi，开放易用的接口服务- */
    @GET("rubbish/type")// ?name=西瓜
    Call<ResponseBody> getRubbishType(@QueryMap Map<String, Object> map);// 垃圾分类查询

    @GET("jokes/list/random")
    Call<JokerCollectionEntity> getRandomJokers();// 随机笑话

    @GET("image/girl/list/random")
    Call<ResponseBody> getRandomGirl();// 随机福利图片

    @GET("news/types")
    Call<ResponseBody> getNewsType();// 免费最新新闻 - 获取所有新闻类型列表

    // 免费最新新闻 - 根据新闻类型获取新闻列表
    @GET("news/list") // ?typeId=510&page=1
    Call<NewsEntity> getNewsList(@QueryMap Map<String, Object> map);

    @GET("news/details") // ?newsId=FFSC6SPV00097U7H
    Call<ResponseBody> getNewsDetail(@QueryMap Map<String, Object> map);// 免费最新新闻 - 详情

    @GET("daily_word/recommend") // ?count=1
    Call<ResponseBody> getDailyWord(@QueryMap Map<String, Object> map);// 每日精美语句

    @GET("weather/forecast/{site}")// @Path主要用于Get请求，用于替换Url路径中的变量字符
    Call<ResponseBody> getWeather(@Path("site") String site);//  获取特定城市今天及未来天气信息
    /* -RollToolsApi，开放易用的接口服务- */

    // 蒲公英内测
    @POST("apiv2/app/view")
    @FormUrlEncoded
    Call<ResponseBody> getAppInfo(@Field(Constants.pgyer_api_key) String _api_key,
                                  @Field(Constants.pgyer_app_key) String appKey);// 获取应用最新信息

}
