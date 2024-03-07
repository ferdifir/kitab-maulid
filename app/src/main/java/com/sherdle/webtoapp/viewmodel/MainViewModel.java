package com.sherdle.webtoapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sherdle.webtoapp.service.api.ApiService;
import com.sherdle.webtoapp.service.api.RetrofitClient;
import com.sherdle.webtoapp.service.api.response.schedule.Data;
import com.sherdle.webtoapp.service.api.response.schedule.MonthData;
import com.sherdle.webtoapp.service.api.response.schedule.PrayersResponse;
import com.sherdle.webtoapp.service.db.AppDatabase;
import com.sherdle.webtoapp.service.db.PrayerEntity;
import com.sherdle.webtoapp.util.DataStatus;

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

public class MainViewModel extends AndroidViewModel {
    private final ApiService api;
    private final AppDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();
    public final MutableLiveData<DataStatus> dataStatus = new MutableLiveData<>();
    public final MutableLiveData<PrayerEntity> prayers = new MutableLiveData<>();
    public final MutableLiveData<Pair<PrayerEntity, String>> dataSchedule = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        api = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        db = AppDatabase.getInstance(application);
    }

    private String getFormattedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    public void getPrayerSchedule(double lat, double lon) {
        executor.execute(() -> {
            String today = getFormattedDate(Calendar.getInstance().getTime());
            List<PrayerEntity> data = db.prayerDao().getPrayersByDate(today);
            if (data.isEmpty()) {
                dataStatus.postValue(DataStatus.LOADING);
                api.getPrayerCalendar(Calendar.getInstance().get(Calendar.YEAR), lat, lon, 2).enqueue(new Callback<PrayersResponse>() {
                    @Override
                    public void onResponse(Call<PrayersResponse> call, Response<PrayersResponse> response) {
                        Log.d("MainViewModel", String.valueOf(response.body().getData()));
                        if (response.isSuccessful()) {
                            List<PrayerEntity> prayerEntities = new ArrayList<>();
                            Data data = response.body().getData();
                            List<MonthData> dataList = new ArrayList<>();
                            dataList.addAll(data.getJanuary());
                            dataList.addAll(data.getFebruary());
                            dataList.addAll(data.getMarch());
                            dataList.addAll(data.getApril());
                            dataList.addAll(data.getMay());
                            dataList.addAll(data.getJune());
                            dataList.addAll(data.getJuly());
                            dataList.addAll(data.getAugust());
                            dataList.addAll(data.getSeptember());
                            dataList.addAll(data.getOctober());
                            dataList.addAll(data.getNovember());
                            dataList.addAll(data.getDecember());
                            for (MonthData dataMonth : dataList) {
                                PrayerEntity prayerEntity = new PrayerEntity(
                                        dataMonth.getDate().getGregorian().getDate(),
                                        dataMonth.getTimings().getSunset().substring(0, 5),
                                        dataMonth.getTimings().getAsr().substring(0, 5),
                                        dataMonth.getTimings().getIsha().substring(0, 5),
                                        dataMonth.getTimings().getFajr().substring(0, 5),
                                        dataMonth.getTimings().getDhuhr().substring(0, 5),
                                        dataMonth.getTimings().getMaghrib().substring(0, 5),
                                        dataMonth.getTimings().getSunrise().substring(0, 5),
                                        dataMonth.getTimings().getMidnight().substring(0, 5),
                                        dataMonth.getTimings().getImsak().substring(0, 5)
                                );
                                prayerEntities.add(prayerEntity);
                            }
                            executor.execute(() -> {
                                db.prayerDao().insertPrayer(prayerEntities);
                                dataStatus.postValue(DataStatus.SUCCESS);
                                List<PrayerEntity> prayerEntityList = db.prayerDao().getPrayersByDate(getFormattedDate(Calendar.getInstance().getTime()));
                                Calendar tomorrow = Calendar.getInstance();
                                tomorrow.add(Calendar.DAY_OF_YEAR, 1);
                                String tomorrowImsak = db.prayerDao().getPrayersByDate(getFormattedDate(tomorrow.getTime())).get(0).getImsak();
                                dataSchedule.postValue(new Pair<>(prayerEntityList.get(0), tomorrowImsak));
                            });
                        } else {
                            Log.d("MainViewModel", String.valueOf(response.errorBody()));
                            dataStatus.postValue(DataStatus.ERROR);
                        }
                    }

                    @Override
                    public void onFailure(Call<PrayersResponse> call, Throwable t) {
                        Log.d("MainViewModel", t.getMessage());
                        dataStatus.postValue(DataStatus.ERROR);
                    }
                });
            }
        });
    }
}
