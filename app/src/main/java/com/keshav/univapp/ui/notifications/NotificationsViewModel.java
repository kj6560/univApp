package com.keshav.univapp.ui.notifications;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationsViewModel extends ViewModel {
    public MutableLiveData<String> mTemp;
    public LiveData<String> getTemperature(Context context, double lat, double lon) {
        if (mTemp == null) {
            mTemp = new MutableLiveData<>();
            fetchTemperature(context,lat,lon);
        }
        return mTemp;
    }
    private void fetchTemperature(Context context, double lat, double lon) {
        try {
            OkHttpClient client = new OkHttpClient();
            String mUrl = "https://weatherapi-com.p.rapidapi.com/current.json?q="+lat+"%2C"+lon;
            Log.d("weather1",mUrl);
            Request request = new Request.Builder()
                    .url(mUrl)
                    .get()
                    .addHeader("X-RapidAPI-Key", "0ef8897533msh92a58facfc04e7cp1bbe46jsn233b9d65094d")
                    .addHeader("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                    .build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("weather: ",e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try{
                        JSONObject job = new JSONObject(response.body().string());
                        JSONObject job1 = job.getJSONObject("current");
                        String temp = job1.getString("temp_c");
                        mTemp .postValue(temp);
                    }catch (Exception e){
                        Log.d("we_ex",e.getMessage());
                    }
                }
            });

        } catch (Exception e) {

        }
    }
}