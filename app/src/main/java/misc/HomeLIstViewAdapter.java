package misc;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.keshav.univapp.EventDetails;
import com.keshav.univapp.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeLIstViewAdapter extends BaseAdapter  {
    List<Event> arrayList;
    List<Event> arraylist1;
    private final Context context;
    public List<Event> orig;
    public HomeLIstViewAdapter(Context context,List<Event> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.arraylist1 = new ArrayList<Event>();
        this.arraylist1.addAll(arrayList);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(arraylist1);
        } else {
            for (Event wp : arraylist1) {
                if (wp.getEvent_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByCategory(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(arraylist1);
        } else {
            for (Event wp : arraylist1) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrayList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event subjectData = arrayList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.home_list_row, null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, EventDetails.class);
                    i.putExtra("event_id",subjectData.getId());
                    context.startActivity(i);
                }
            });
            TextView eventName = convertView.findViewById(com.keshav.univapp.R.id.eventName);
            TextView eventDate = convertView.findViewById(R.id.eventDate);
            TextView eventBio = convertView.findViewById(R.id.eventBio);
            ImageView imag = convertView.findViewById(R.id.eventImage);
            Button register = convertView.findViewById(R.id.registerForEvent);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "clicked on register for event", Toast.LENGTH_SHORT).show();
                }
            });
            eventName.setText(subjectData.event_name);

            String strDate = null;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(subjectData.event_date);
                strDate = new SimpleDateFormat("MMMM dd yyyy").format(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            eventDate.setText(strDate);
            eventBio.setText(subjectData.event_bio.substring(0,50));
            Picasso.get().load(AppSettings.eventImageUrl+"/"+subjectData.event_image).into(imag);

        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
