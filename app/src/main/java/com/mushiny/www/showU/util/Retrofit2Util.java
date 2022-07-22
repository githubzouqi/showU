package com.mushiny.www.showU.util;

import android.support.annotation.NonNull;

import com.mushiny.www.showU.constant.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit2Util {

    // 添加拦截器，添加header信息
    private static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader(Constants.ROLL_TOOLS_API_APP_ID,
                            Constants.ROLL_TOOLS_API_APP_ID_VALUE)
                    .addHeader(Constants.ROLL_TOOLS_API_APP_SECRET,
                            Constants.ROLL_TOOLS_API_APP_SECRET_VALUE)
                    .build();
            return chain.proceed(request);
        }
    }).build();

    public static <T> T create(@NonNull String baseUrl,@NonNull Class<?> interfaceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())// 设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())// 支持RxJava平台
                .build();
        return (T) retrofit.create(interfaceClass);
    }

    /**
     * RollToolsApi，开放易用的接口服务
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public static <T> T createWithROLLHeader(@NonNull Class<?> interfaceClass){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ROLL_TOOLS_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return (T) retrofit.create(interfaceClass);
    }

}
