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
import misc.Category;
import misc.Event;
import misc.NetworkCall;
import misc.Sliders;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeViewModel extends ViewModel {

    public MutableLiveData<List<Event>> mEvent;

    public MutableLiveData<String> mTemp;

    public MutableLiveData<List<Category>> mCategory;

    public MutableLiveData<List<Sliders>> mSliders;

    public LiveData<List<Event>> getEvent(Context context, int filter) {
        if (mEvent == null) {
            mEvent = new MutableLiveData<>();
            fetchEvents(context, filter);
        }
        return mEvent;
    }

    public LiveData<List<Category>> getCategories(Context context) {
        if (mCategory == null) {
            mCategory = new MutableLiveData<>();
            fetchCategories(context);
        }
        return mCategory;
    }

    public LiveData<List<Sliders>> getSliders(Context context) {
        if (mSliders == null) {
            mSliders = new MutableLiveData<>();
            fetchSliders(context);
        }
        return mSliders;
    }

    public LiveData<String> getTemperature(Context context, double lat, double lon) {
        if (mTemp == null) {
            mTemp = new MutableLiveData<>();
            fetchTemperature(context,lat,lon);
        }
        return mTemp;
    }

    private void fetchEvents(Context context, int filter) {
        try {
            NetworkCall.get(AppSettings.eventsUrl, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.code() == 200) {
                            JSONArray jar = new JSONArray(response.body().string());
                            List<Event> check = new ArrayList<>();
                            for (int i = 0; i < jar.length(); i++) {
                                JSONObject job = jar.getJSONObject(i);
                                Log.d("image", job.getString("event_image"));
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
                                check.add(event);
                            }
                            mEvent.postValue(check);
                        }
                    } catch (Exception e) {
                        Intent i =new Intent(context, LoginActivity.class);
                        context.startActivity(i);
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }

    private void fetchSliders(Context context) {
        try {
            NetworkCall.get(AppSettings.sliderUrl, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    try {
                        JSONArray jar = new JSONArray(response.body().string());
                        List<Sliders> check = new ArrayList<>();
                        for (int i = 0; i < jar.length(); i++) {
                            JSONObject job = jar.getJSONObject(i);
                            Sliders sliders = new Sliders(
                                    job.getString("image")

                            );
                            Log.d("image", sliders.toString());
                            check.add(sliders);
                        }

                        mSliders.postValue(check);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }

    private void fetchCategories(Context context) {
        try {
            List<Category> categories = new ArrayList<>();
            //categories.add(new Category(0, "All", "", "https://www.iconsdb.com/icons/download/black/football-16.png"));

            NetworkCall.get(AppSettings.categoriesUrl, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONArray jar = new JSONArray(response.body().string());

                        for (int i = 0; i < jar.length(); i++) {
                            JSONObject job = jar.getJSONObject(i);
                            Category _category = new Category(
                                    job.getInt("id"),
                                    job.getString("name"),
                                    job.getString("description"),
                                    job.getString("icon")
                            );
                            categories.add(_category);
                        }
                        mCategory.postValue(categories);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
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
}