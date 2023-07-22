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
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import misc.AppPreferences;
import misc.AppSettings;
import misc.NetworkCall;
import misc.SessionManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {
    Button login;
    TextView signup,forgotPassword;
    EditText user_email, user_password;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        login = (Button) findViewById(R.id.button);
        user_email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        user_password = (EditText) findViewById(R.id.editTextTextPassword);
        signup = (TextView) findViewById(R.id.textView11);
        forgotPassword = (TextView)findViewById(R.id.textView8);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pb.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email",user_email.getText().toString());
                    jsonObject.put("password",user_password.getText().toString());
                    NetworkCall.post(AppSettings.loginUrl, jsonObject.toString(),LoginActivity.this, new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.d("login","reached here");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            try {
                                JSONObject job = new JSONObject(response.body().string());
                                if(!job.has("error")){
                                    String _token = job.getString("token");
                                    JSONObject job1 = job.getJSONObject("user");
                                    SessionManager sm = new SessionManager(LoginActivity.this);
                                    sm.login(_token,job1.toString());
                                    AppPreferences appPreferences = new AppPreferences(LoginActivity.this);
                                    appPreferences.setPreference("showWelcome","no");
                                    runOnUiThread(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    pb.setVisibility(View.INVISIBLE);
                                                    Intent i = new Intent(LoginActivity.this,NewHomeActivity.class);
                                                    startActivity(i);
                                                }
                                            }
                                    );
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pb.setVisibility(View.INVISIBLE);
                                            AlertDialog.Builder adb = new AlertDialog.Builder(LoginActivity.this);
                                            adb.setMessage("Login Failed!!\nPlease check your credentials and try again");
                                            adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            adb.setCancelable(false);
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
                    Log.d("login", ""+e.getMessage());
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,Register.class);
                startActivity(i);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(i);
            }
        });
    }
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    Call post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json,JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}