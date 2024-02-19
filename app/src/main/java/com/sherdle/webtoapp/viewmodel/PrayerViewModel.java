package com.sherdle.webtoapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sherdle.webtoapp.service.api.ApiService;
import com.sherdle.webtoapp.service.api.RetrofitClient;
import com.sherdle.webtoapp.service.api.response.date.HijrDateResponse;
import com.sherdle.webtoapp.service.api.response.schedule.Timings;
import com.sherdle.webtoapp.service.db.AppDatabase;
import com.sherdle.webtoapp.service.db.PrayerEntity;
import com.sherdle.webtoapp.utils.Helper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrayerViewModel extends AndroidViewModel {
    private final MutableLiveData<String> hijrDate = new MutableLiveData<>();

    private final ApiService service;
    private final AppDatabase database;
    private final Executor executor;

    public PrayerViewModel(Application application) {
        super(application);
        service = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        database = AppDatabase.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<PrayerEntity>> getPrayerSchedule() {
        MutableLiveData<List<PrayerEntity>> prayers = new MutableLiveData<>();
        String today = getFormattedDate(Calendar.getInstance().getTime());
        String tomorrow = getFormattedDate(getTomorrowDate());
        executor.execute(() -> {
            List<PrayerEntity> todayList = database.prayerDao().getPrayersByDate(today);
            List<PrayerEntity> tomorrowList = database.prayerDao().getPrayersByDate(tomorrow);
            List<PrayerEntity> tempList = new ArrayList<>();
            tempList.addAll(todayList);
            tempList.addAll(tomorrowList);
            prayers.postValue(tempList);
        });
        return prayers;
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private Date getTomorrowDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
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
