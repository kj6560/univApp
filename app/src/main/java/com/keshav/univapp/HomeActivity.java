package com.keshav.univapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import misc.AppSettings;
import misc.Category;
import misc.Event;
import misc.HomeLIstViewAdapter;
import misc.NetworkCall;
import misc.RecyclerViewAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    TextView locationText;
    ListView lv;
    List<Event> check;
    List<Category> categories;
    ProgressBar pb;

    LinearLayout linearLayoutCategory;
    RecyclerView recyclerView;
    LinearLayoutManager recylerViewLayoutManager;
    RecyclerViewAdapter recyclerViewAdapter;
    ImageView weather, notifications;
    SearchView searchView;
    HomeLIstViewAdapter adapter;

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        View mDecorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        mDecorView.setSystemUiVisibility(uiOptions);
        locationText = (TextView) findViewById(R.id.textView6);
        pb = (ProgressBar) findViewById(R.id.progressBar3);
        pb.setVisibility(View.INVISIBLE);
        lv = (ListView) findViewById(R.id.eventsListView);
        linearLayoutCategory = (LinearLayout) findViewById(R.id.linearLayoutCategory);
        recyclerView = (RecyclerView) findViewById(R.id.categoryRecycler);
        check = new ArrayList<Event>();
        weather = (ImageView) findViewById(R.id.weather);
        notifications = (ImageView) findViewById(R.id.notifications);
        viewPager = (ViewPager)findViewById(R.id.slider);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "To be implemented", Toast.LENGTH_SHORT).show();
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "To be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //getLastLocation();

        fetchEvents();
        fetchCategories();
        searchView = (SearchView) findViewById(R.id.searchView);

    }

    private void fetchEvents() {
        try {
            pb.setVisibility(View.VISIBLE);
            NetworkCall.get(AppSettings.eventsUrl, HomeActivity.this, new Callback() {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.INVISIBLE);
                                adapter = new HomeLIstViewAdapter(HomeActivity.this, check);
                                lv.setAdapter(adapter);
                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }

    private void fetchCategories() {
        try {
            categories = new ArrayList<>();
            categories.add(new Category(0, "All", "", "https://www.iconsdb.com/icons/download/black/football-16.png"));
            pb.setVisibility(View.VISIBLE);
            NetworkCall.get(AppSettings.categoriesUrl, HomeActivity.this, new Callback() {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.INVISIBLE);
                                recylerViewLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);

                                recyclerView.setLayoutManager(recylerViewLayoutManager);

                                recyclerViewAdapter = new RecyclerViewAdapter(HomeActivity.this, categories);

                                recyclerView.setAdapter(recyclerViewAdapter);

                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
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
//                            latitudeTextView.setText(location.getLatitude() + "");
//                            longitTextView.setText(location.getLongitude() + "");
                            locationText.setText("Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
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
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            locationText.setText("Lat: " + mLastLocation.getLatitude() + " Lon: " + mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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