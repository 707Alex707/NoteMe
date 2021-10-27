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

    private final ClickListener listener;
    private DatabaseHelper dbh;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<ArrayList<String>> data, ArrayList<byte[]> image, ClickListener listener, DatabaseHelper mDatabaseHelper) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.img = image;
        this.listener = listener;
        this.dbh = mDatabaseHelper;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view, listener, getAdapter());
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<String> notes = mData.get(position);
        holder.title.setText(notes.get(1));
        holder.subtitle.setText(notes.get(2));
        holder.note.setText(notes.get(3));
        holder.container.setBackgroundColor(Color.parseColor(notes.get(4)));
        holder.noteImage.setImageBitmap(getImage(img.get(position)));

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

        private ImageView iconImageView;
        private WeakReference<ClickListener> listenerRef;
        private MyRecyclerViewAdapter adapter;

        ViewHolder(View itemView, ClickListener listener, MyRecyclerViewAdapter adapter) {
            super(itemView);
            title = itemView.findViewById(R.id.row_title);
            subtitle = itemView.findViewById(R.id.row_subtitle);
            note = itemView.findViewById(R.id.row_note);
            container = itemView.findViewById(R.id.note_container);
            noteImage = itemView.findViewById(R.id.noteImage);
            itemView.setOnClickListener(this);

            listenerRef = new WeakReference<>(listener);
            iconImageView = (ImageView) itemView.findViewById(R.id.imageButton3);
            iconImageView.setOnClickListener(this);

            this.adapter = adapter;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == iconImageView.getId()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Delete note")
                        .setMessage("Delete note " + adapter.getItem(getAdapterPosition()).get(1).toString() + "?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dbh.delete(adapter.getItem(getAdapterPosition()).get(0).toString());
                                ((MainActivity)view.getContext()).retrieveData("SELECT * FROM note_table");
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.create().show();
            } else {
                Intent launch = new Intent(view.getContext(), CreateNoteActivity.class);
                launch.putExtra("ID", adapter.getItem(getAdapterPosition()).get(0).toString());
                launch.putExtra("title", adapter.getItem(getAdapterPosition()).get(1).toString());
                launch.putExtra("subtitle", adapter.getItem(getAdapterPosition()).get(2).toString());
                launch.putExtra("content", adapter.getItem(getAdapterPosition()).get(3).toString());
                launch.putExtra("color", adapter.getItem(getAdapterPosition()).get(4).toString());
                launch.putExtra("img", adapter.getImageItem(getAdapterPosition()));
                view.getContext().startActivity(launch);
                //Toast.makeText(view.getContext(), "You clicked " + adapter.getItem(getAdapterPosition()) + " on row number " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    ArrayList getItem(int id) {
        // need to fix
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