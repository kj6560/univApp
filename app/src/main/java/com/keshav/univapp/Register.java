package com.keshav.univapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import misc.AppSettings;
import misc.Helpers;
import misc.NetworkCall;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Register extends Activity {
    EditText firstName, lastName, email, phone, password;
    Button register;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        pb = (ProgressBar)findViewById(R.id.progressBar2);
        pb.setVisibility(View.INVISIBLE);
        firstName = (EditText) findViewById(R.id.editTextTextEmailAddress);
        lastName = (EditText) findViewById(R.id.editTextText);
        email = (EditText) findViewById(R.id.editTextText2);
        phone = (EditText) findViewById(R.id.editTextPhone);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        register = (Button) findViewById(R.id.button);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                String first_name = firstName.getText().toString();
                String last_name = lastName.getText().toString();
                String _email = email.getText().toString();
                String _phone = phone.getText().toString();
                String _password = password.getText().toString();
                if (!Helpers.isValidNumber(_phone)) {
                    flag = false;
                    phone.setError("Phone number is invalid");
                }
                if (!Helpers.isValidEmail(_email)) {
                    flag = false;
                    email.setError("Email id is invalid");
                }
                if (!Helpers.isValidPassword(_password)) {
                    flag = false;
                    password.setError("Password is invalid");
                }
                if(flag)
                    registerUser(first_name, last_name, _phone, _email, _password);
            }
        });

    }

    private void registerUser(String first_name, String last_name, String _phone, String _email, String _password) {
        try {
            pb.setVisibility(View.VISIBLE);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("first_name", first_name);
            jsonObject.put("last_name", last_name);
            jsonObject.put("phone", _phone);
            jsonObject.put("email", _email);
            jsonObject.put("password", _password);
            NetworkCall.post(AppSettings.registerUrl, jsonObject.toString(),Register.this, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("error", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        JSONObject job = new JSONObject(response.body().string());
                        if(job.getBoolean("success") && job.getBoolean("email")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pb.setVisibility(View.INVISIBLE);
                                    AlertDialog.Builder adb = new AlertDialog.Builder(Register.this);
                                    adb.setMessage("You have been successfully registered with us. An email has been sent to your specified email. please check your email and proceed to login");
                                    adb.setCancelable(false);
                                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            Intent intent = new Intent(Register.this,MainActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    adb.show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.d("error", "" + e.getMessage());
        }
    }
}