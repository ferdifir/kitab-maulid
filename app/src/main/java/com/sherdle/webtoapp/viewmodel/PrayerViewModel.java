package com.sherdle.webtoapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sherdle.webtoapp.service.api.ApiService;
import com.sherdle.webtoapp.service.api.RetrofitClient;
import com.sherdle.webtoapp.service.api.response.date.HijrDateResponse;
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

public class PrayerViewModel extends AndroidViewModel {
    private final MutableLiveData<String> hijrDate = new MutableLiveData<>();
    public final MutableLiveData<DataStatus> dataStatus = new MutableLiveData<>();
    public final MutableLiveData<List<PrayerEntity>> prayers = new MutableLiveData<>();
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

    public void deleteAllData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.prayerDao().deleteAllPrayers();
            }
        });
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

    public void getPrayerSchedule(double lat, double lon) {
        executor.execute(() -> {
            String today = getFormattedDate(Calendar.getInstance().getTime());
            List<PrayerEntity> data = database.prayerDao().getPrayersByDate(today);
            if (data.isEmpty()) {
                dataStatus.postValue(DataStatus.LOADING);
                service.getPrayerCalendar(Calendar.getInstance().get(Calendar.YEAR), lat, lon, 2).enqueue(new Callback<PrayersResponse>() {
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
                                database.prayerDao().insertPrayer(prayerEntities);
                                dataStatus.postValue(DataStatus.SUCCESS);
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                                List<PrayerEntity> prayerEntityList = database.prayerDao().getPrayersByDate(getFormattedDate(Calendar.getInstance().getTime()));
                                List<PrayerEntity> tomorrow = database.prayerDao().getPrayersByDate(getFormattedDate(calendar.getTime()));
                                List<PrayerEntity> combinedList = new ArrayList<>();
                                combinedList.addAll(prayerEntityList);
                                combinedList.addAll(tomorrow);
                                prayers.postValue(combinedList);
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
