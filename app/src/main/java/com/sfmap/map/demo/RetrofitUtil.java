package com.sfmap.map.demo;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    private volatile static RetrofitUtil sInstance;
    private Retrofit mRetrofit;
    private ApiService apiService;
    private RetrofitUtil(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://193.112.85.146/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiService = mRetrofit.create(ApiService.class);
    }
    public static RetrofitUtil getInstance(){
        if (sInstance == null){
            synchronized(RetrofitUtil.class){
                if (sInstance == null){
                    sInstance = new RetrofitUtil();
                }
            }
        }
        return sInstance;
    }
    public ApiService getApiService(){
        return apiService;
    }
}
