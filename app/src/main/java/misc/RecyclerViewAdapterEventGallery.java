package misc;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.keshav.univapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterEventGallery extends RecyclerView.Adapter<RecyclerViewAdapterEventGallery.ViewHolder>{

    List<String> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;

    public RecyclerViewAdapterEventGallery(Context context1,  List<String> SubjectValues1){

        SubjectValues = SubjectValues1;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image_event;
        public ViewHolder(View v){
            super(v);
            Log.d("reached adapter","adapter");
            image_event = (ImageView) v.findViewById(R.id.image_event);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items_event_gallery,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String eventFiles= SubjectValues.get(position);
        Log.d("eventFiles","reached here");
        Log.d("pic path",AppSettings.eventGallery+eventFiles);
        Picasso.get().load(AppSettings.eventGallery+eventFiles).into(holder.image_event);
    }

    @Override
    public int getItemCount(){

        return SubjectValues.size();
    }
}