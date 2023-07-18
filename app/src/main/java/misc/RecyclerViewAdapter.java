package misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.keshav.univapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    List<Category> SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;
    TextView textView;

    public RecyclerViewAdapter(Context context1,List<Category> SubjectValues1){

        SubjectValues = SubjectValues1;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public ImageView icon;
        public LinearLayout lin;
        public ViewHolder(View v){

            super(v);
            lin = (LinearLayout) v.findViewById(com.keshav.univapp.R.id.lin);
            textView = (TextView)v.findViewById(R.id.subject_textview);
            icon = (ImageView) v.findViewById(R.id.iconImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items,parent,false);

        viewHolder1 = new ViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        holder.textView.setText(SubjectValues.get(position).getName());
        if(position !=0){
            Picasso.get().load(SubjectValues.get(position).getIcon()).into(holder.icon);
        }else{
            holder.icon.setVisibility(View.GONE);
        }

        String cat= SubjectValues.get(position).getName();
        holder.lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.lin.setBackground(ContextCompat.getDrawable(context, R.drawable.category_bg_selected));

            }
        });
    }

    @Override
    public int getItemCount(){

        return SubjectValues.size();
    }
}