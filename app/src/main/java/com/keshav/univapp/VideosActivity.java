package com.keshav.univapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
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
    int user_id = 0, event_id = 0;

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

        if (getIntent().hasExtra("user_id")) {
            user_id = getIntent().getExtras().getInt("user_id");
            ufList = new ArrayList<>();
            fetchUserFiles(this, user_id, 2);
        } else if (getIntent().hasExtra("event_id")) {
            event_id = getIntent().getExtras().getInt("event_id");
            fetchEventGallery(VideosActivity.this, event_id);
        }
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    Dialog dialog = new Dialog(this);
                    dialog.setContentView(R.layout.vid_upload_layout);
                    EditText title = (EditText) dialog.findViewById(R.id.title);
                    EditText description = (EditText) dialog.findViewById(R.id.description);
                    EditText tags = (EditText) dialog.findViewById(R.id.tags);
                    Button btn = (Button) dialog.findViewById(R.id.uploadVideo);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            NetworkCall.uploadVideo(VideosActivity.this, new File(getPathFromURI(selectedImageUri)), user_id, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.d("exception", e.getMessage());
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        AlertDialog.Builder adb = new AlertDialog.Builder(VideosActivity.this);
                                                        adb.setCancelable(false);
                                                        adb.setMessage("Video Upload Successful");
                                                        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface1, int i) {
                                                                dialogInterface1.dismiss();
                                                            }
                                                        });
                                                        adb.show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                    );
                                }
                            }, title.getText().toString(), description.getText().toString(), tags.getText().toString());
                        }
                    });
                    dialog.show();

                    // Set the image in ImageView

                }
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void fetchUserFiles(Context context, int user_id, int type) {
        try {
            String url = user_id != 0 ? AppSettings.userFilesUrl + "?user_id=" + user_id + "&file_type=" + type : AppSettings.userFilesUrl + "?&file_type=" + type;
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
                            UserFiles userFiles = new UserFiles(job.getInt("file_type"), job.getInt("id"), job.getString("file_path"), job.getString("title"), job.getString("description"), job.getString("tags"), job.getInt("user_id"));
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

            NetworkCall.get(AppSettings.eventFiles + "?event_id=" + event_id, context, new Callback() {
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
                            for (int i = 0; i < jar.length(); i++) {
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
                        Log.d("exception", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }
}