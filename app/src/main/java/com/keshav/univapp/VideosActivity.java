package com.keshav.univapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import misc.AppSettings;
import misc.NetworkCall;
import misc.UserFiles;
import misc.VideoViewEventPagerAdapter;
import misc.VideoViewPagerAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class VideosActivity extends AppCompatActivity {
    List<UserFiles> ufList;
    ViewPager2 viewPager2;
    int user_id=0,event_id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        View mDecorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        mDecorView.setSystemUiVisibility(uiOptions);
        viewPager2 = (ViewPager2) findViewById(R.id.viewPager2);

        if(getIntent().hasExtra("user_id")){
            user_id = getIntent().getExtras().getInt("user_id");
            ufList = new ArrayList<>();
            fetchUserFiles(this, user_id, 2);
        }else if(getIntent().hasExtra("event_id")){
            event_id = getIntent().getExtras().getInt("event_id");
            fetchEventGallery(VideosActivity.this,  event_id);
        }

    }

    private void fetchUserFiles(Context context, int user_id, int type) {
        try {
            String url = user_id !=0 ?AppSettings.userFilesUrl + "?user_id=" + user_id + "&file_type=" + type:AppSettings.userFilesUrl + "?&file_type=" + type;
            NetworkCall.get(url, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONArray arr = new JSONArray(response.body().string());
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject job = arr.getJSONObject(i);
                            UserFiles userFiles = new UserFiles(job.getInt("file_type"),job.getInt("id"),job.getString("file_path"),job.getString("title"),job.getString("description"),job.getString("tags"),job.getInt("user_id") );
                            ufList.add(userFiles);
                        }
                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        viewPager2.setAdapter(new VideoViewPagerAdapter(ufList));
                                    }
                                }
                        );
                    } catch (Exception e) {
                        Log.d("exception user files", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }

    private void fetchEventGallery(Context context, int event_id) {
        try {

            NetworkCall.get(AppSettings.eventFiles+"?event_id="+event_id, context, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error aaya", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        if (response.code() == 200) {
                            JSONArray jar = new JSONArray(response.body().string());
                            List<String> eventGallery = new ArrayList<>();
                            for (int i=0;i<jar.length();i++){
                                JSONObject job = jar.getJSONObject(i);
                                String videoUrl = job.getString("event_video");
                                eventGallery.add(videoUrl);
                            }
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            viewPager2.setAdapter(new VideoViewEventPagerAdapter(eventGallery));
                                        }
                                    }
                            );
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