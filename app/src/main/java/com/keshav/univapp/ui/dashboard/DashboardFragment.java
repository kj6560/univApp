package com.keshav.univapp.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.keshav.univapp.VideosActivity;
import com.keshav.univapp.databinding.FragmentDashboardBinding;

import java.util.List;

import misc.UserFiles;
import misc.VideoViewPagerAdapter;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        Intent i = new Intent(getActivity(), VideosActivity.class);
//        getActivity().startActivity(i);
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        final ViewPager2 viewPager2 = binding.viewPager2;
//        dashboardViewModel.getUserFiles(getActivity(),2).observe(getActivity(), new Observer<List<UserFiles>>() {
//            @Override
//            public void onChanged(List<UserFiles> userFiles) {
//                viewPager2.setAdapter(new VideoViewPagerAdapter(userFiles));
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}