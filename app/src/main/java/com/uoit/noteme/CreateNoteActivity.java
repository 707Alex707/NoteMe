package com.uoit.noteme;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateNoteActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    String noteColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        mDatabaseHelper = new DatabaseHelper(this);

        EditText title = findViewById(R.id.inputNoteTitle);
        EditText subtitle = findViewById(R.id.inputNoteSubTitle);
        EditText note = findViewById(R.id.inputNote);

        String titleText = title.getText().toString();
        String subtitleText =  subtitle.getText().toString();
        String noteText =  note.getText().toString();

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        ImageView imageDone = findViewById(R.id.imageSave);
        imageDone.setOnClickListener(v -> AddData(titleText,subtitleText, noteText, this.noteColor));
        imageDone.setOnClickListener(v -> onBackPressed());
    }

    public void AddData(String title, String subtitle, String notes, String color) {
        boolean insertData = mDatabaseHelper.addData(title, subtitle, notes, color);

        if (insertData) {
            Toast.makeText(this,"Data Successfully Inserted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void setColorBlack(View view) {
        this.noteColor = "black";
        view.getRootView().setBackgroundColor(Color.parseColor("#000000"));
    }

    public void setColorYellow(View view) {
        this.noteColor = "yellow";
        view.getRootView().setBackgroundColor(Color.parseColor("#FFFF00"));
    }

    public void setColorBlue(View view) {
        this.noteColor = "blue";
        view.getRootView().setBackgroundColor(Color.parseColor("#0000FF"));
    }

    public void setColorGreen(View view) {
        this.noteColor = "green";
        view.getRootView().setBackgroundColor(Color.parseColor("#008000"));
    }
}