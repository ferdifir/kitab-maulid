package com.sherdle.webtoapp.service.api;

import com.sherdle.webtoapp.service.api.response.date.HijrDateResponse;
import com.sherdle.webtoapp.service.api.response.schedule.PrayersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("v1/gToH/{date}")
    Call<HijrDateResponse> getHijriDate(@Path("date") String date);

    @GET("v1/calendar/{year}/{month}")
    Call<PrayersResponse> getPrayerSchedule(
            @Path("year") int year,
            @Path("month") int month,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method
    );

    @GET("v1/calendar/{year}")
    Call<PrayersResponse> getPrayerCalendar(
            @Path("year") int year,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("method") int method
    );
}
