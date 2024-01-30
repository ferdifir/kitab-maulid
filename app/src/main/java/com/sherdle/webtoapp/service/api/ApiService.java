package com.sherdle.webtoapp.service.api;

import com.sherdle.webtoapp.service.api.response.date.HijrDateResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("v1/gToH/{date}")
    Call<HijrDateResponse> getHijriDate(@Path("date") String date);
}
