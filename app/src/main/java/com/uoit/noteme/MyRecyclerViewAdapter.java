package com.uoit.noteme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;



public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ArrayList<String>> mData;
    private ArrayList<byte[]> img;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<ArrayList<String>> data, ArrayList<byte[]> image) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.img = image;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<String> notes = mData.get(position);
        byte[] image = img.get(position);
        holder.title.setText(notes.get(1));
        holder.subtitle.setText(notes.get(2));
        holder.note.setText(notes.get(3));
        holder.container.setBackgroundColor(Color.parseColor(notes.get(4)));
        holder.noteImage.setImageBitmap(getImage(image));

        // Set image view invisible so no white space
        if(img.get(position).length  == 0){
            holder.noteImage.setVisibility(View.GONE);
        }

        // On click listener for container
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launch = new Intent(view.getContext(), CreateNoteActivity.class);
                launch.putExtra("ID", notes.get(0));
                launch.putExtra("title", notes.get(1));
                launch.putExtra("subtitle", notes.get(2));
                launch.putExtra("content", notes.get(3));
                launch.putExtra("color", notes.get(4));
                launch.putExtra("img", image);
                view.getContext().startActivity(launch);
            }
        });

        // On click listener for trash icon
        holder.iconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete note")
                        .setMessage("Delete note " + notes.get(1) + "?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseHelper dbh = new DatabaseHelper(view.getContext());
                                dbh.delete(notes.get(0));
                                ((MainActivity) view.getContext()).retrieveData("SELECT * FROM note_table");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.create().show();
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public MyRecyclerViewAdapter getAdapter(){
        return this;
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView subtitle;
        TextView note;
        LinearLayout container;
        ImageView noteImage;

        ImageView iconImageView;
        private WeakReference<ClickListener> listenerRef;
        private MyRecyclerViewAdapter adapter;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.row_title);
            subtitle = itemView.findViewById(R.id.row_subtitle);
            note = itemView.findViewById(R.id.row_note);
            container = itemView.findViewById(R.id.note_container);
            noteImage = itemView.findViewById(R.id.noteImage);
            iconImageView = (ImageView) itemView.findViewById(R.id.imageButton3);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ArrayList getItem(int id) {
        return mData.get(id);
    }

    // convenience method for getting data at click position
    byte[] getImageItem(int id) {
        // need to fix
        return img.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}