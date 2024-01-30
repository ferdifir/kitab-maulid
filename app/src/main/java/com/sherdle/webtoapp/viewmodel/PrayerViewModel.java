package com.sherdle.webtoapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sherdle.webtoapp.service.api.ApiService;
import com.sherdle.webtoapp.service.api.RetrofitClient;
import com.sherdle.webtoapp.service.api.response.date.HijrDateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrayerViewModel extends ViewModel {
    private MutableLiveData<String> hijrDate = new MutableLiveData<>();
    private final ApiService service;

    public PrayerViewModel() {
        service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
    }

    public void getHijrDate(String date) {
        Call<HijrDateResponse> call = service.getHijriDate(date);
        call.enqueue(new Callback<HijrDateResponse>() {
            @Override
            public void onResponse(Call<HijrDateResponse> call, Response<HijrDateResponse> response) {
                if (response.isSuccessful()) {
                    String date = response.body().getData().getHijri().getDay();
                    String month = response.body().getData().getHijri().getMonth().getEn();
                    String year = response.body().getData().getHijri().getYear();
                    hijrDate.setValue(date + " " + month + " " + year);
                } else {
                    hijrDate.setValue("");
                }
            }

            @Override
            public void onFailure(Call<HijrDateResponse> call, Throwable t) {
                hijrDate.setValue("");
            }
        });
    }

    public LiveData<String> getHijrDateValue() {
        return hijrDate;
    }

}
