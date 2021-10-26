package com.uoit.noteme;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
        retrieveData("SELECT * FROM note_table");

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(new Intent(
                getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE)
        );
        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0){
                    String query = "SELECT * FROM " + "note_table" + " WHERE title LIKE '%" + charSequence.toString() + "%'";
                    retrieveData(query);
                }else{
                    retrieveData("SELECT * FROM " + "note_table");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent launch = new Intent(this, CreateNoteActivity.class);
        System.out.println(adapter.getItem(position));
        launch.putExtra("title", adapter.getItem(position).get(0).toString());
        launch.putExtra("subtitle", adapter.getItem(position).get(1).toString());
        launch.putExtra("content", adapter.getItem(position).get(2).toString());
        launch.putExtra("color", adapter.getItem(position).get(3).toString());
        startActivity(launch);
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();
        retrieveData("SELECT * FROM note_table");
    }

    public void retrieveData(String query){
        Cursor data = mDatabaseHelper.fetch(query);
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