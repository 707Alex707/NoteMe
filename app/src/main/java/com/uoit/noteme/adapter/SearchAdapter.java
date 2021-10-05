package com.uoit.noteme.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.uoit.noteme.R;


class SearchViewHolder extends RecyclerView.ViewHolder{
    public TextView title,subtitle,note,color;

    public SearchViewHolder(View itemView){
        super(itemView);

        //title = (TextView)itemView.findViewById(R.id.title);
        //subtitle = (TextView)itemView.findViewById(R.id.subtitle);
        //note = (TextView)itemView.findViewById(R.id.note);
        //color = (TextView)itemView.findViewById(R.id.color);
    }
}

public class SearchAdapter {
}
