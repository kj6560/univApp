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

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.keshav.univapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterPerformance extends RecyclerView.Adapter<RecyclerViewAdapterPerformance.ViewHolder>{

    Map<Integer, List<UserPerformance>> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;

    public RecyclerViewAdapterPerformance(Context context1, Map<Integer, List<UserPerformance>> SubjectValues1){

        SubjectValues = SubjectValues1;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView eventTitle,event_date,event_location;
        LinearLayout lin;
        public ViewHolder(View v){

            super(v);
            eventTitle = (TextView) v.findViewById(R.id.eventTitle);
            event_date = (TextView) v.findViewById(R.id.eventDate);
            event_location = (TextView) v.findViewById(R.id.eventLocation);
            lin = (LinearLayout) v.findViewById(R.id.lin);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items_performance,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        List<UserPerformance> userPerformance = SubjectValues.get(SubjectValues.keySet().toArray()[position]);
        holder.eventTitle.setText(userPerformance.get(0).getEvent_name());
        holder.event_date.setText(userPerformance.get(0).getEvent_date());
        holder.event_location.setText(userPerformance.get(0).getEvent_location());
        for (int i = 0; i < userPerformance.size(); i++) {
            LinearLayout lin1 = new LinearLayout(context);
            lin1.setWeightSum(2);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

            layoutParams.setMargins(30, 20, 30, 0);
            lin1.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv1 = new TextView(context);
            tv1.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            tv1.setLayoutParams(params);
            tv1.setAllCaps(true);
            tv1.setGravity(Gravity.LEFT);
            tv1.setTextColor(Color.BLACK);
            Typeface typeface = ResourcesCompat.getFont(context, R.font.merriweather_light);
            tv1.setTypeface(typeface);
            tv1.setText(userPerformance.get(i).getEvent_result_key());
            TextView tv2 = new TextView(context);
            tv2.setAllCaps(true);
            tv2.setLayoutParams(params);
            tv2.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            tv2.setGravity(Gravity.RIGHT);
            tv2.setTextColor(Color.BLACK);
            tv2.setTypeface(typeface);
            tv2.setText(userPerformance.get(i).getEvent_result_value());
            lin1.addView(tv1);
            lin1.addView(tv2);
            holder.lin.addView(lin1,layoutParams);
        }
    }

    @Override
    public int getItemCount(){

        return SubjectValues.size();
    }
}