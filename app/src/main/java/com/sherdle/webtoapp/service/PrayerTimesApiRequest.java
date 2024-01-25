package com.sherdle.webtoapp.service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.sherdle.webtoapp.Config;
import com.sherdle.webtoapp.model.ApiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrayerTimesApiRequest extends AsyncTask<Double, Void, String> {

    private static final String TAG = PrayerTimesApiRequest.class.getSimpleName();
    private PrayerTimesApiCallback callback;
    private String date;

    public PrayerTimesApiRequest(PrayerTimesApiCallback callback, String date) {
        this.callback = callback;
        this.date = date;
    }

    @Override
    protected String doInBackground(Double... coordinates) {
        if (coordinates.length < 2) {
            return null;
        }

        double latitude = coordinates[0];
        double longitude = coordinates[1];

        String year = date.substring(0,4);
        String month = date.substring(5,7);

        String apiUrl = Config.ADZAN_TIME_API + year + "/" + month + "?latitude="+ latitude+"&longitude="+longitude+"&method=2";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseStringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            reader.close();
            connection.disconnect();

            return responseStringBuilder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error in API request: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(result, ApiResponse.class);
            callback.onApiResult(apiResponse);
        } else {
            callback.onApiError();
        }
    }

    public interface PrayerTimesApiCallback {
        void onApiResult(ApiResponse result);

        void onApiError();
    }

}

