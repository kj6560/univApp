package com.keshav.univapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.keshav.univapp.R;
import com.keshav.univapp.SocialProfile;
import com.keshav.univapp.databinding.FragmentHomeBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import misc.AppSettings;
import misc.Category;
import misc.Event;
import misc.HomeLIstViewAdapter;
import misc.SessionManager;
import misc.Sliders;
import misc.ViewPagerAdapter;

public class HomeFragment extends Fragment {
    List<Event> check;
    HomeLIstViewAdapter adapter;
    private FragmentHomeBinding binding;

    int filter, first_run = 0;

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    TextView locationText, temperatureText;
    HomeViewModel homeViewModel;
    double lat, lon;

    SessionManager sm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final ListView lv = binding.eventsListView;
        final TabLayout tabLayout = binding.tabLayout;
        final ViewPager viewPager = binding.slider;
        final SearchView searchView = binding.searchView;
        final ImageView circle_imageView = binding.circleImageView;
        sm = new SessionManager(getActivity());
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Picasso.get().load(AppSettings.profilePic + "/" + job.getString("image")).into(circle_imageView);
        } catch (Exception e) {

        }
        circle_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SocialProfile.class);
                getActivity().startActivity(i);
            }
        });
        temperatureText = binding.temperature;
        locationText = binding.textView6;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                adapter.filter(text);
                if (adapter.getCount() > 0) {
                    lv.setAdapter(adapter);
                }
                return true;
            }
        });
        tabLayout.setSelectedTabIndicator(R.drawable.category_bg_selected);
        tabLayout.setTabTextColors(R.color.white, R.color.white);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String text = tab.getText().toString();
                if (adapter != null) {
                    adapter.filterByCategory(text);
                    if (adapter.getCount() > 0) {
                        lv.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "There are no events for category " + tab.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        homeViewModel.getEvent(getActivity(), filter).observe(getActivity(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (events != null) {
                    check = events;
                    adapter = new HomeLIstViewAdapter(getActivity(), check);
                    lv.setAdapter(adapter);
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            String text = tab.getText().toString();
                            adapter.filter(text);
                            if (adapter.getCount() > 0) {
                                lv.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });
                }
            }
        });
        homeViewModel.getCategories(getActivity()).observe(getActivity(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        TabLayout.Tab tab = tabLayout.newTab();
                        tab.setId(categories.get(i).getId());
                        tab.setText(categories.get(i).getName());

                        tabLayout.addTab(tab);
                    }
                }
            }
        });
        homeViewModel.getSliders(getActivity()).observe(getActivity(), new Observer<List<Sliders>>() {
            @Override
            public void onChanged(List<Sliders> sliders) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), sliders);
                viewPager.setAdapter(viewPagerAdapter);

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            try {
                                Geocoder geocoder = new Geocoder(getActivity());
                                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                                Address address = addresses.get(0);
                                locationText.setText(address.getLocality() + "," + address.getAdminArea());
                            } catch (Exception e) {

                            }
                            homeViewModel.getTemperature(getActivity(), lat, lon).observe(getActivity(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    temperatureText.setText(s + "°C");
                                }
                            });


                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            homeViewModel.getTemperature(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude()).observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    temperatureText.setText(s + " °C");
                }
            });

            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                Address address = addresses.get(0);
                locationText.setText(address.getLocality() + "," + address.getAdminArea());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
}