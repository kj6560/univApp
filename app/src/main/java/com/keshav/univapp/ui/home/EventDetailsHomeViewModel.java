package com.keshav.univapp.ui.home;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.keshav.univapp.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import misc.AppSettings;
import misc.Event;
import misc.EventPartners;
import misc.NetworkCall;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventDetailsHomeViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<String> mTemp;
    public MutableLiveData<Event> mEvent;
    public MutableLiveData<List<EventPartners>> mPartners;

    public LiveData<List<EventPartners>> getEventPartners(Context context,int event_id){
        if(mPartners == null){
            mPartners = new MutableLiveData<>();
            fetchEventPartners(context,event_id);
        }
        return mPartners;
    }

    private void fetchEventPartners(Context context, int event_id) {
        try {
            NetworkCall.get(AppSettings.eventPartners+"?event_id="+event_id, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.code() == 200) {
                            JSONArray jar = new JSONArray(response.body().string());
                            List<EventPartners> eventPartners = new ArrayList<>();
                            for (int i=0;i<jar.length();i++){
                                JSONObject job = jar.getJSONObject(i);
                                EventPartners event = new EventPartners(
                                        job.getInt("id"),
                                        job.getString("partner_name"),
                                        job.getString("partner_logo")
                                );
                                eventPartners.add(event);
                            }

                            mPartners.postValue(eventPartners);
                        }
                    } catch (Exception e) {
                        Log.d("exception",e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }

    ;
    public LiveData<String> getTemperature(Context context, double lat, double lon) {
        if (mTemp == null) {
            mTemp = new MutableLiveData<>();
            fetchTemperature(context,lat,lon);
        }
        return mTemp;
    }
    public LiveData<Event> getEvent(Context context,int event_id) {
        if (mEvent == null) {
            mEvent = new MutableLiveData<>();
            fetchEvents(context,event_id);
        }
        return mEvent;
    }
    private void fetchTemperature(Context context,double lat,double lon) {
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
    private void fetchEvents(Context context,int event_id) {
        try {
            NetworkCall.get(AppSettings.eventsUrl+"?id="+event_id, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.code() == 200) {
                            JSONObject job = new JSONObject(response.body().string());
                                Event event = new Event(
                                        job.getInt("id"),
                                        job.getString("event_name"),
                                        job.getString("event_date"),
                                        job.getString("event_bio"),
                                        job.getString("event_location"),
                                        job.getString("event_image"),
                                        job.getInt("event_category"),
                                        job.getString("name"),
                                        job.getString("event_live_link")
                                );
                            mEvent.postValue(event);
                        }
                    } catch (Exception e) {
                        Log.d("exception",e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }
}