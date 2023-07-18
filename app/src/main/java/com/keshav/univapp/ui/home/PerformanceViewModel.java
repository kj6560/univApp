package com.keshav.univapp.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import misc.AppSettings;
import misc.NetworkCall;
import misc.UserFiles;
import misc.UserPerformance;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PerformanceViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<Map<Integer, List<UserPerformance>>> userPerformance;

    public LiveData<Map<Integer, List<UserPerformance>>> getUserPerformance(Context context, int user_id) {
        if (userPerformance == null) {
            userPerformance = new MutableLiveData<>();
            fetchUserPerformance(context, user_id);
        }
        return userPerformance;
    }


    private void fetchUserPerformance(Context context, int user_id) {
        try {
            Log.d("url", AppSettings.userPerformanceUrl + "?user_id=" + user_id);
            NetworkCall.get(AppSettings.userPerformanceUrl + "?user_id=" + user_id, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        Map<Integer, List<UserPerformance>> map = new HashMap<>();
                        JSONObject job1 = new JSONObject(response.body().string());
                        Iterator<String> iter = job1.keys(); //This should be the iterator you want.
                        String keys[] = new String[job1.length()];
                        for (int i = 0; iter.hasNext(); i++) {
                            keys[i] = iter.next();

                        }

                        for (int i = 0; i < keys.length; i++) {
                            Log.d("key",keys[i]);
                            JSONArray jsonArray = job1.getJSONArray(keys[i]);
                            List<UserPerformance>userPerformances = new ArrayList<>();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject job = jsonArray.getJSONObject(j);
                                int event_id = job.getInt("event_id");
                                String event_name = job.getString("event_name");
                                String event_date = job.getString("event_date");
                                String event_location = job.getString("event_location");
                                String event_result_key = job.getString("event_result_key");
                                String event_result_value = job.getString("event_result_value");
                                UserPerformance performance = new UserPerformance(event_id, event_result_key, event_result_value, event_name, event_date, event_location);
                                userPerformances.add(performance);
                            }

                            map.put(Integer.parseInt(keys[i]),userPerformances);
                        }


                        userPerformance.postValue(map);

                    } catch (Exception e) {
                        Log.d("exception user files", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }
}