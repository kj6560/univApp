package misc;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.keshav.univapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoViewPagerAdapter extends RecyclerView.Adapter<VideoViewPagerAdapter.MyViewHolder>{

    List<UserFiles> userFiles;

    public VideoViewPagerAdapter(List<UserFiles> userFiles) {
        Log.d("here","reached here");
        this.userFiles = userFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.videoview_pager_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(userFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return userFiles.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{
        VideoView videoView;
        TextView title;
        TextView desc;
        TextView tags;
        ProgressBar pb;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoview);
            title = itemView.findViewById(R.id.textView13);
            desc = itemView.findViewById(R.id.textView14);
            tags = itemView.findViewById(R.id.textView15);
            pb = itemView.findViewById(R.id.progressBar4);
        }
        public void setData(UserFiles obj){
            videoView.setVideoPath(obj.getFile_path());
            title.setText(obj.getTitle());
            desc.setText(obj.getDescription());
            tags.setText(obj.getTags());
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