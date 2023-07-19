package com.keshav.univapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.keshav.univapp.Performance;
import com.keshav.univapp.R;
import com.keshav.univapp.SocialProfile;
import com.keshav.univapp.VideosActivity;
import com.keshav.univapp.databinding.FragmentHomeBinding;
import com.keshav.univapp.databinding.FragmentSocialProfileBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import misc.AppSettings;
import misc.Category;
import misc.Event;
import misc.HomeLIstViewAdapter;
import misc.NetworkCall;
import misc.PhotosVideosAdapter;
import misc.RecyclerViewAdapter;
import misc.SessionManager;
import misc.Sliders;
import misc.UserFiles;
import misc.ViewPagerAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SocialProfileFragment extends Fragment {
    private FragmentSocialProfileBinding binding;
    SocialProfileViewModel socialProfileModel;

    SessionManager sm;

    int view_switch = 0;
    int user_id = 0;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        socialProfileModel =
                new ViewModelProvider(this).get(SocialProfileViewModel.class);

        binding = FragmentSocialProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ImageView addUserFiles = binding.addUserFiles;
        final RecyclerView recyclerView = binding.recyclerView;
        final ImageView circle_imageView = binding.circleImageView;
        final TextView photos = binding.photos;
        final TextView videos = binding.videos;
        final TextView performance = binding.performance;
        final TextView profile_name=binding.profileName;
        final TextView abouInfo = binding.aboutInfo;
        sm = new SessionManager(getActivity());

        try {
            JSONObject job = new JSONObject(sm.getUser());
            Log.d("user", job.toString());
            user_id = job.getInt("id");
            profile_name.setText(job.getString("first_name")+" "+job.getString("last_name"));
            abouInfo.setText(job.getString("about"));
        } catch (Exception e) {

        }
        view_switch = 1;

        getUserFiles(view_switch, user_id, recyclerView, 1);

        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject job = new JSONObject(sm.getUser());
                    Log.d("user", job.toString());
                    int u_id = job.getInt("id");
                    getUserFiles(view_switch, u_id, recyclerView, 1);
                } catch (Exception e) {

                }

            }
        });
        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject job = new JSONObject(sm.getUser());
                    Log.d("user", job.toString());
                    int u_id = job.getInt("id");
                    Intent vids = new Intent(getActivity(), VideosActivity.class);
                    vids.putExtra("user_id", u_id);
                    getActivity().startActivity(vids);
                } catch (Exception e) {

                }
            }
        });
        performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent i = new Intent(getActivity(), Performance.class);
                    JSONObject job = new JSONObject(sm.getUser());
                    int u_id = job.getInt("id");
                    i.putExtra("user_id", u_id);
                    getActivity().startActivity(i);
                } catch (Exception e) {

                }

            }
        });

        addUserFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("video/*");
//                startActivityForResult(intent, 100);
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });
        sm = new SessionManager(getActivity());
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Picasso.get().load(AppSettings.profilePic + "/" + job.getString("image")).into(circle_imageView);
        } catch (Exception e) {

        }

        return root;
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
                    NetworkCall.uploadImage(getActivity(),new File(getPathFromURI(selectedImageUri)), user_id, new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d("exception",e.getMessage());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.d("image uploaded successfully",response.body().string());
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            try {

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            );
                        }
                    });
                    // Set the image in ImageView

                }
            }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    private void getUserFiles(int view_switch, int user_id, RecyclerView recyclerView, int type) {
        socialProfileModel.getUserFiles(getActivity(), user_id, view_switch).observe(getActivity(), new Observer<List<UserFiles>>() {
            @Override
            public void onChanged(List<UserFiles> userFiles) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), type == 1 ? 3 : 2);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(new PhotosVideosAdapter(getActivity(), userFiles, type));

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}