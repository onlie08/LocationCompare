package com.sfmap.map.demo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("pushFile")
    Observable<ReturnBean> pushFile(@Part List<MultipartBody.Part> partList);
}
