package com.keshav.univapp.ui.dashboard;

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
import java.util.List;

import misc.AppSettings;
import misc.NetworkCall;
import misc.UserFiles;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashboardViewModel extends ViewModel {

    private  MutableLiveData<List<UserFiles>> mUserFiles;
    public LiveData<List<UserFiles>> getUserFiles(Context context, int type) {
        if (mUserFiles == null) {
            mUserFiles = new MutableLiveData<>();
            fetchUserFiles(context,type);
        }
        return mUserFiles;
    }
    private void fetchUserFiles(Context context,  int type) {
        try {
            NetworkCall.get(AppSettings.userFilesUrl + "?file_type=" + type, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONArray arr = new JSONArray(response.body().string());
                        List<UserFiles> ufList = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject job = arr.getJSONObject(i);
                            UserFiles userFiles = new UserFiles(job.getInt("file_type"),job.getInt("id"),job.getString("file_path"),job.getString("title"),job.getString("description"),job.getString("tags"),job.getInt("user_id") );
                            ufList.add(userFiles);
                        }
                        mUserFiles.postValue(ufList);
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