package misc;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.keshav.univapp.R;

import java.util.List;

public class VideoViewEventPagerAdapter extends RecyclerView.Adapter<VideoViewEventPagerAdapter.MyViewHolder>{

    List<String> eventFiles;
    public VideoViewEventPagerAdapter(List<String> userFiles) {
        this.eventFiles = userFiles;
    }

    @NonNull
    @Override
    public VideoViewEventPagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoview_pager_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(eventFiles.get(position));
    }





    @Override
    public int getItemCount() {
        return eventFiles.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{
        VideoView videoView;
        ProgressBar pb;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = (VideoView) itemView.findViewById(R.id.videoview);
            pb = (ProgressBar) itemView.findViewById(R.id.progressBar4);
        }
        public void setData(String obj){
            videoView.setVideoPath("https://www.univsportatech.com/uploads/users/docs/images/"+obj);
            Log.d("vid_path","https://www.univsportatech.com/uploads/users/docs/images/"+obj);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    pb.setVisibility(View.INVISIBLE);
                    mediaPlayer.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    pb.setVisibility(View.INVISIBLE);
                    mediaPlayer.start();
                }
            });
        }
    }

}