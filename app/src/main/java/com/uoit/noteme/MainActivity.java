package com.uoit.noteme;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    MyRecyclerViewAdapter adapter;
    DatabaseHelper mDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);
        retrieveData();

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(new Intent(
                getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE)
        );

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        retrieveData();
    }

    public void retrieveData(){
        Cursor data = mDatabaseHelper.fetch();
        ArrayList<ArrayList<String>> listData = new ArrayList<ArrayList<String>>();

        while(data.moveToNext()){
            ArrayList<String> row = new ArrayList<>();
            row.add(data.getString(1));
            row.add(data.getString(2));
            row.add(data.getString(3));
            row.add(data.getString(4));
            listData.add(row);
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, listData);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

}