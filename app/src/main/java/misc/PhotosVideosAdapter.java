package misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.keshav.univapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosVideosAdapter extends RecyclerView.Adapter<PhotosVideosAdapter.ViewHolder>{

    List<UserFiles> images_videos;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    int type;

    public PhotosVideosAdapter(Context context1, List<UserFiles> SubjectValues1,int type){

        images_videos = SubjectValues1;
        context = context1;
        this.type = type;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout view;
        public ViewHolder(View v){

            super(v);
            view = (LinearLayout) v.findViewById(R.id.view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.view_items,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        LinearLayout view = holder.view;
        if(type==1){

            ImageView iv  = new ImageView(context);
            Log.d("file_path",AppSettings.userPics+"/"+images_videos.get(position).file_path);
            Picasso.get().load(AppSettings.userPics+"/"+images_videos.get(position).getFile_path()).into(iv);
            view.addView(iv);
        }
    }



    @Override
    public int getItemCount(){

        return images_videos.size();
    }

}