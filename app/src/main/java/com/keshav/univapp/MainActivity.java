package com.keshav.univapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

import misc.AppPreferences;
import misc.SessionManager;

public class MainActivity extends Activity {
Button login,register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        AppPreferences appPreferences = new AppPreferences(this);
        String welcome = appPreferences.getPrefrence("showWelcome");
        if(welcome!=null && welcome.contentEquals("no")){
            SessionManager sm = new SessionManager(this);
            try{
                if(sm.isLoggedIn()){
                    Intent i = new Intent(this,NewHomeActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(this,LoginActivity.class);
                    startActivity(i);
                }
            }catch(Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,Register.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}