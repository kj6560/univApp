package com.keshav.univapp.ui.notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.keshav.univapp.NewHomeActivity;
import com.keshav.univapp.SocialProfile;
import com.keshav.univapp.databinding.FragmentNotificationsBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import misc.AppSettings;
import misc.NetworkCall;
import misc.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationsFragment extends Fragment {
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    TextView locationText, temperatureText, edit;
    private FragmentNotificationsBinding binding;

    EditText first_name, last_name, phone, email, birthday, gender, married, height, weight, address_line1, city, state, pincode;

    String user_id, first__name, last__name, __email, __number, _birthday, _gender, _married, _height, _weight, _address_line1, _city, _state, _pincode,_image;
    Button saveDetails;
    double lat, lon;

    ImageView circle_profile;
    SessionManager sm;

    NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        final ImageView circle_imageView = binding.circleImageView;
        sm = new SessionManager(getActivity());
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Log.d("profilepicpath",AppSettings.profilePic+job.getString("image"));
            Picasso.get().load(AppSettings.profilePic+job.getString("image")).into(circle_imageView);
        }catch (Exception e){

        }
        circle_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SocialProfile.class);
                getActivity().startActivity(i);
            }
        });
        try {
            JSONObject job = new JSONObject(sm.getUser());
            Log.d("imageUpload response",job.toString());
            user_id = "" + job.getInt("id");
            _image = job.getString("image");
            first__name = job.getString("first_name");
            last__name = job.getString("last_name");
            __email = job.getString("email");
            __number = job.getString("number").contentEquals("null") ? "" : job.getString("number");
            _birthday = job.getString("birthday").contentEquals("null") ? "" : job.getString("birthday");
            _gender = job.getInt("gender") == 1 ? "Male" : "Female";
            _married = job.getInt("married") == 1 ? "yes" : "no";
            _height = job.getString("height");
            _weight = job.getString("weight");
            _address_line1 = job.getString("address_line1");
            _city = job.getString("city");
            _state = job.getString("state");
            _pincode = job.getString("pincode");

        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        View root = binding.getRoot();

        edit = binding.edit;
        temperatureText = binding.temperature;
        locationText = binding.textView6;
        first_name = binding.firstName;
        circle_profile = binding.circleProfileImage;
        if (first__name != null)
            first_name.setText(first__name);
        last_name = binding.lastName;
        if (last__name != null)
            last_name.setText(last__name);
        phone = binding.phoneNumber;
        if (__number != null)
            phone.setText(__number);
        email = binding.email;
        if (__email != null)
            email.setText(__email);
        birthday = binding.birthday;
        if (_birthday != null)
            birthday.setText(_birthday.replace("00:00:00", ""));
        gender = binding.gender;
        if (_gender != null)
            gender.setText(_gender);
        married = binding.married;
        if (_married != null)
            married.setText(_married);
        height = binding.height;
        if (_height != null)
            height.setText("" + _height);
        weight = binding.weight;
        if (_weight != null)
            weight.setText("" + _weight);
        address_line1 = binding.addressLine1;
        if (_address_line1 != null)
            address_line1.setText(_address_line1);
        city = binding.city;
        if (_city != null)
            city.setText(_city);
        state = binding.state;
        if (_state != null)
            state.setText(_state);
        pincode = binding.pincode;
        if (_pincode != null)
            pincode.setText(_pincode);
        saveDetails = binding.saveDetails;
        if(_image !=null)
            Picasso.get().load(AppSettings.profilePic+_image).into(circle_profile);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });
        saveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!married.getText().toString().contentEquals("null")) {
                        if (!married.getText().toString().contentEquals("yes") && !married.getText().toString().contentEquals("no")) {
                            married.setError("it takes only yes or no");
                        }
                    }
                    if (!gender.getText().toString().contentEquals("null")) {
                        if (!gender.getText().toString().contentEquals("Male") && !gender.getText().toString().contentEquals("Female") && !gender.getText().toString().contentEquals("Other")) {
                            gender.setError("it takes only Male or Female or Other");
                        }
                    }
                    int gend = 1;
                    if (gender.getText().toString().contentEquals("Female")) {
                        gend = 2;
                    } else if (gender.getText().toString().contentEquals("Other")) {
                        gend = 3;
                    }
                    int marr = 0;
                    if (married.getText().toString().contentEquals("yes")) {
                        marr = 1;
                    }
                    JSONObject mainJob = new JSONObject();
                    JSONObject job = new JSONObject();
                    job.put("user_id", user_id);
                    job.put("first_name", first_name.getText().toString());
                    job.put("last_name", last_name.getText().toString());
                    job.put("email", email.getText().toString());
                    job.put("number", phone.getText().toString());
                    job.put("birthday", birthday.getText().toString());
                    job.put("gender", gend);
                    job.put("height", height.getText().toString());
                    job.put("weight", weight.getText().toString());
                    job.put("married", marr);

                    JSONObject job1 = new JSONObject();
                    job1.put("address_line1", address_line1.getText().toString());
                    job1.put("city", city.getText().toString());
                    job1.put("state", state.getText().toString());
                    job1.put("pincode", pincode.getText().toString());
                    mainJob.put("personal_details", job);
                    mainJob.put("address_details", job1);
                    Log.d("payload", mainJob.toString());
                    NetworkCall.postPay(AppSettings.setProfileUrl, mainJob.toString(), getActivity(), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("exception aaya", e.getMessage());
                                        }
                                    }
                            );
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                JSONObject job = new JSONObject(response.body().string());
                                JSONObject job1 = job.getJSONObject("user");
                                sm.setUser(job1.toString());
                                Intent i = new Intent(getActivity(), NewHomeActivity.class);
                                getActivity().startActivity(i);
                            } catch (Exception e) {
                                Log.d("update user profile", "" + e.getMessage());
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.d("exception", e.getMessage());
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

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                // Get the url from data
                final Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    NetworkCall.uploadProfilePic(getActivity(),new File(getPathFromURI(selectedImageUri)), Integer.parseInt(user_id), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d("exception",e.getMessage());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                            try{
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                JSONObject job = new JSONObject(sm.getUser());
                                job.put("image",jsonObject.getString("image"));
                                sm.setUser(job.toString());
                            }catch (Exception e){
                                Log.d("user exception",e.getMessage());
                            }
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                circle_profile.setImageURI(selectedImageUri);
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

    /* Get the real path from the URI */
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
                            notificationsViewModel.getTemperature(getActivity(), lat, lon).observe(getActivity(), new Observer<String>() {
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
            notificationsViewModel.getTemperature(getActivity(), mLastLocation.getLatitude(), mLastLocation.getLongitude()).observe(getActivity(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    temperatureText.setText(s + " °C");
                }
            });
            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),1);
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