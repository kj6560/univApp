package misc;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public SharedPreferences sp;
    public SharedPreferences.Editor ed;
    public Context context;

    public SessionManager(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    public boolean isLoggedIn(){
        return sp.getBoolean("isLoggedIn",false);
    }
    public void login(String token,String user){
        ed.putString("token",token);
        ed.putString("user",user);
        ed.putBoolean("isLoggedIn",true);
        ed.commit();
    }
    public String getToken(){
        return sp.getString("token",null);
    }

    public String getUser(){
        return sp.getString("user","");
    }
    public void setUser(String user){
        ed.putString("user",user);
        ed.commit();
    }
}
