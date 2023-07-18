package com.keshav.univapp.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import misc.AppSettings;
import misc.Event;
import misc.NetworkCall;
import misc.UserFiles;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SocialProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    public MutableLiveData<List<UserFiles>> userFiles;

    public LiveData<List<UserFiles>> getUserFiles(Context context,int user_id,int file_type) {
        if (userFiles == null) {
            userFiles = new MutableLiveData<>();
            fetchUserFiles(context,user_id,file_type);
        }
        return userFiles;
    }


    private void fetchUserFiles(Context context,int user_id,int type) {
        try {
            Log.d("url",AppSettings.userFilesUrl+"?user_id="+user_id+"&file_type="+type);
            NetworkCall.get(AppSettings.userFilesUrl+"?user_id="+user_id+"&file_type="+type, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                            JSONArray arr = new JSONArray(response.body().string());
                            List<UserFiles> ufList = new ArrayList<>();
                            if(arr.length()>0){
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject job = arr.getJSONObject(i);

                                    String file_path = job.getString("file_path");
                                    int file_type = job.getInt("file_type");
                                    UserFiles uf = new UserFiles(job.getInt("file_type"), job.getInt("id"), file_path, job.getString("title"),job.getString("description"),job.getString("tags"),job.getInt("user_id"));
                                    ufList.add(uf);
                                    Log.d("user_files",uf.toString());
                                }
                                userFiles.postValue(ufList);
                            }
                    } catch (Exception e) {
                        Log.d("exception user files",e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }
}