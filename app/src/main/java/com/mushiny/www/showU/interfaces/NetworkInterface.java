package com.mushiny.www.showU.interfaces;

import com.mushiny.www.showU.constant.Constants;
import com.mushiny.www.showU.entity.JokerCollectionEntity;
import com.mushiny.www.showU.entity.NewsEntity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface NetworkInterface {

    @GET(Constants.URL_PATH_JOKER)
    Call<JokerCollectionEntity> getLatestJoker(@QueryMap Map<String, Object> map);// 笑话大全

    @GET(Constants.URL_PATH_NEWS_HEADLINE)
    Call<NewsEntity> getNews(@QueryMap Map<String, Object> map);// 新闻头条

}
