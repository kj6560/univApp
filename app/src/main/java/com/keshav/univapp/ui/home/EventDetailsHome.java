package com.keshav.univapp.ui.home;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.keshav.univapp.EventGallery;
import com.keshav.univapp.SocialProfile;
import com.keshav.univapp.databinding.FragmentEventDetailsHomeBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import misc.AppSettings;
import misc.Event;
import misc.EventPartners;
import misc.SessionManager;

public class EventDetailsHome extends Fragment {

    private EventDetailsHomeViewModel mViewModel;
    SessionManager sm;
    TextView locationText, temperatureText;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    EventDetailsHomeViewModel viewModel;
    double lat, lon;
    int event_id;
    private @NonNull FragmentEventDetailsHomeBinding binding;
    public static EventDetailsHome newInstance() {
        return new EventDetailsHome();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventDetailsHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(EventDetailsHomeViewModel.class);
        final ImageView circle_imageView = binding.circleImageView;
        sm = new SessionManager(getActivity());
        event_id = getActivity().getIntent().getExtras().getInt("event_id");
        final Button eventGallery = binding.gallery;
        eventGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getActivity(), EventGallery.class);
                i1.putExtra("event_id",event_id);
                getActivity().startActivity(i1);
            }
        });
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Picasso.get().load(AppSettings.profilePic+"/"+job.getString("image")).into(circle_imageView);
        }catch (Exception e){
            Log.d("exception",e.getMessage());
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
        viewModel.getEvent(getActivity(),event_id).observe(getActivity(), new Observer<Event>() {
            @Override
            public void onChanged(Event events) {
                if (events != null) {
                    final ImageView imageView = binding.eventImage;
                    Picasso.get().load(AppSettings.eventImageUrl+"/"+events.getEvent_image()).into(imageView);
                    final TextView event_name = binding.eventName;
                    event_name.setText(events.getEvent_name());
                    final TextView event_date = binding.eventDate;
                    final TextView event_location = binding.eventLocation;
                    try {
                        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(events.getEvent_date());
                        String strDate = new SimpleDateFormat("MMMM dd yyyy").format(date);
                        event_date.setText(strDate);
                    }catch (Exception e){

                    }
                    final TextView event_bio = binding.eventBio;
                    event_bio.setText(events.getEvent_bio());
                    event_location.setText(events.getEvent_location());
                    WebView mWebView = binding.videoview;
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.setWebViewClient(new WebViewClient());
                    mWebView.loadUrl(events.getEvent_live_link());

                }
            }
        });
        viewModel.getEventPartners(getActivity(),event_id).observe(getActivity(), new Observer<List<EventPartners>>() {

            @Override
            public void onChanged(List<EventPartners> eventPartners) {
                final LinearLayout ll = binding.partners;
                for(int i=0;i<eventPartners.size();i++){
                    ImageView iv = new ImageView(getActivity());
                    iv.setId(i);
                    iv.setLayoutParams(new LinearLayout.LayoutParams(150,150));
                    Picasso.get().load(AppSettings.partnerPicsUrl+"/"+eventPartners.get(i).getPartner_logo()).into(iv);
                    ll.addView(iv);
                }
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();
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
                            viewModel.getTemperature(getActivity(), lat, lon).observe(getActivity(), new Observer<String>() {
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
            viewModel.getTemperature(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude()).observe(getActivity(), new Observer<String>() {
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