package com.keshav.univapp.ui.home;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keshav.univapp.Performance;
import com.keshav.univapp.R;
import com.keshav.univapp.SocialProfile;
import com.keshav.univapp.VideosActivity;
import com.keshav.univapp.databinding.FragmentPerformanceBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import misc.AppSettings;
import misc.PhotosVideosAdapter;
import misc.RecyclerViewAdapterPerformance;
import misc.SessionManager;
import misc.UserFiles;
import misc.UserPerformance;

public class PerformanceFragment extends Fragment {

    private PerformanceViewModel mViewModel;
    FragmentPerformanceBinding binding;
    SessionManager sm;

    int view_switch=0;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PerformanceViewModel.class);
        binding = FragmentPerformanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ImageView circle_imageView = binding.circleImageView;
        final RecyclerView recyclerView = binding.recyclerViewPerformance;
        final TextView photos = binding.photos;
        final TextView videos = binding.videos;
        final TextView performance = binding.performance;
        sm = new SessionManager(getActivity());
        int user_id=0;
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Log.d("user",job.toString());
            user_id = job.getInt("id");
        }catch (Exception e){

        }
        view_switch = 1;



        photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i1 = new Intent(getActivity(), SocialProfile.class);
                    getActivity().startActivity(i1);
                }catch (Exception e){

                }

            }
        });
        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject job = new JSONObject(sm.getUser());
                    Log.d("user",job.toString());
                    int u_id = job.getInt("id");
                    Intent vids = new Intent(getActivity(), VideosActivity.class);
                    vids.putExtra("user_id",u_id);
                    getActivity().startActivity(vids);
                }catch (Exception e){

                }
            }
        });
        performance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Intent i = new Intent(getActivity(), Performance.class);
                    JSONObject job = new JSONObject(sm.getUser());
                    int u_id = job.getInt("id");
                    i.putExtra("user_id",u_id);
                    getActivity().startActivity(i);
                }catch (Exception e){

                }

            }
        });
        sm = new SessionManager(getActivity());
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Picasso.get().load(AppSettings.profilePic+"/"+job.getString("image")).into(circle_imageView);
            getUserPerformance(user_id,recyclerView);
        }catch (Exception e){

        }
        return root;
    }
    private void getUserPerformance(int user_id,RecyclerView recyclerView) {
        mViewModel.getUserPerformance(getActivity(),user_id).observe(getActivity(), new Observer<Map<Integer, List<UserPerformance>>>() {
            @Override
            public void onChanged(Map<Integer, List<UserPerformance>> userPerformance) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(new RecyclerViewAdapterPerformance(getActivity(),userPerformance));

            }
        });
    }
}