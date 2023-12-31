package misc;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkCall {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient client = new OkHttpClient();

    public static Call post(String url, String json, Context context, Callback callback) {
        RequestBody body = RequestBody.create(json,JSON);
        Request request;
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        Call call = client.newCall(request);
        try{

            call.enqueue(callback);
        }catch (Exception e){
            Log.d("exceptResp",""+e.getMessage());
        }
        return call;
    }

    public static Call postPay(String url, String json, Context context, Callback callback) {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);
        RequestBody body = RequestBody.create(json,JSON);
        Request request;
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .post(body)
                .build();
        Call call = client.newCall(request);
        try{

            call.enqueue(callback);
        }catch (Exception e){
            Log.d("exceptResp",""+e.getMessage());
        }
        return call;
    }

    public static Call get(String url, Context context, Callback callback){

        OkHttpClient okHttpClient = new OkHttpClient();
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        String _url = url+"?token="+token;
        Request request = new Request.Builder()
                .url(_url)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call call = okHttpClient.newCall(request);
        try{
            call.enqueue(callback);
        }catch (Exception e){
            Log.d("exceptResp",""+e.getMessage());
        }
        return  call;
    }

    public static String uploadProfilePic(Context context,File imageFile,int user_id,Callback callback) {
        // Create an instance of OkHttpClient
        OkHttpClient client = new OkHttpClient();
        // Create the request body with the image file
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile))
                .build();
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        // Create the request
        Request request = new Request.Builder()
                .url(AppSettings.profilePicUpload+"?user_id="+user_id)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try {
            Call call = client.newCall(request);
            call.enqueue(callback);
        } catch (Exception e) {
            Log.d("upload exception", e.getMessage());
        }
        return null;
    }
    public static String uploadImage(Context context,File imageFile,int user_id,Callback callback) {
        // Create an instance of OkHttpClient
        OkHttpClient client = new OkHttpClient();
        // Create the request body with the image file
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile))
                .build();
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        // Create the request
        Request request = new Request.Builder()
                .url(AppSettings.userImageUpload+"?user_id="+user_id)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try {
            Call call = client.newCall(request);
            call.enqueue(callback);
        } catch (Exception e) {
            Log.d("upload exception", e.getMessage());
        }
        return null;
    }
    public static String uploadVideo(Context context,File videoFile,int user_id,Callback callback,String title,String description,String tags) {
        // Create an instance of OkHttpClient
        OkHttpClient client = new OkHttpClient();
        // Create the request body with the image file
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video_file", videoFile.getName(), RequestBody.create(MediaType.parse("video/*"), videoFile))
                .build();
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getToken();
        // Create the request
        Request request = new Request.Builder()
                .url(AppSettings.videoUpload+"?user_id="+user_id+"&title="+title+"&description="+description+"&tags="+tags)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try {
            Call call = client.newCall(request);
            call.enqueue(callback);
        } catch (Exception e) {
            Log.d("upload exception", e.getMessage());
        }
        return null;
    }
}
