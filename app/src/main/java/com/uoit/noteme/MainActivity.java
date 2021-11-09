package com.uoit.noteme;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

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
                    String query = "SELECT * FROM " + "note_table" + " WHERE title LIKE '%" + charSequence.toString() + "%' OR subtitle LIKE '%" + charSequence.toString() + "%' OR text LIKE '%" + charSequence.toString() + "%'";
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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item1:
                        exportData();
                        return true;
                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    @Override
    public void onItemClick(View view, int position) {
    }

    @Override
    public void onResume(){
        super.onResume();
        retrieveData("SELECT * FROM note_table");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void exportData(){
        Cursor data = mDatabaseHelper.fetch("SELECT * FROM note_table");

        File file = new File(this.getFilesDir(), "export.json");
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        // Check if file exists
        if(!file.exists()){
            try{
                file.createNewFile();
                fileWriter = new FileWriter(file.getAbsoluteFile());
                bufferedWriter = new BufferedWriter(fileWriter);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        try {
            JSONArray json = new JSONArray();
            while(data.moveToNext()){
                JSONObject innerJSON = new JSONObject();
                innerJSON.put("title", data.getString(1));
                innerJSON.put("subtitle", data.getString(2));
                innerJSON.put("text", data.getString(3));
                byte[] image = data.getBlob(5);
                innerJSON.put("image bytes", Base64.getEncoder().encodeToString(image));
                json.put(innerJSON);
            }
            fileWriter = new FileWriter(file.getAbsoluteFile());
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(json.toString(1));
            bufferedWriter.close();
        }catch (JSONException | IOException e){
            e.printStackTrace();
        }
        Toast.makeText(this,"Exported Data", Toast.LENGTH_SHORT).show();
    }

    public void retrieveData(String query){
        Cursor data = mDatabaseHelper.fetch(query);
        ArrayList<ArrayList<String>> listData = new ArrayList<ArrayList<String>>();
        ArrayList<byte[]> imageData = new ArrayList<byte[]>();

        while(data.moveToNext()){
            ArrayList<String> row = new ArrayList<>();
            row.add(data.getString(0));
            row.add(data.getString(1));
            row.add(data.getString(2));
            row.add(data.getString(3));
            row.add(data.getString(4));
            listData.add(row);
            imageData.add(data.getBlob(5));
        }

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, listData, imageData);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }
}