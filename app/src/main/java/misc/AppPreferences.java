package misc;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    public Context context;
    public SharedPreferences sp,sp1;
    public SharedPreferences.Editor ed,ed2;
    public AppPreferences(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("app",Context.MODE_PRIVATE);
        sp1 = context.getSharedPreferences("session",Context.MODE_PRIVATE);
        ed = sp.edit();
        ed2 = sp1.edit();
    }
    public void setPreference(String preference_name,String preference_value){
        ed2.putString(preference_name,preference_value);
        ed2.commit();
    }
    public String getPrefrence(String preference_name){
        return sp1.getString(preference_name,null);
    }
}
