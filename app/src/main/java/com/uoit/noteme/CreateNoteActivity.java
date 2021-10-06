package com.uoit.noteme;

import android.content.Intent;
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
    EditText title;
    EditText subtitle;
    EditText note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        mDatabaseHelper = new DatabaseHelper(this);
        this.findViewById(R.id.textYellow).getRootView().setBackgroundColor(Color.parseColor("#F4CA5E"));

        title = findViewById(R.id.inputNoteTitle);
        subtitle = findViewById(R.id.inputNoteSubTitle);
        note = findViewById(R.id.inputNote);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        ImageView imageDone = findViewById(R.id.imageSave);
        imageDone.setOnClickListener(v -> AddData());
    }

    public void AddData() {
        String titleText = title.getText().toString();
        String subtitleText =  subtitle.getText().toString();
        String noteText =  note.getText().toString();

        boolean insertData = mDatabaseHelper.addData(titleText, subtitleText, noteText, this.noteColor);

        if (insertData) {
            Toast.makeText(this,"Data Successfully Inserted!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void setColorRed(View view) {
        this.noteColor = "#FF6A6A";
        view.getRootView().setBackgroundColor(Color.parseColor("#FF6A6A"));
    }

    public void setColorYellow(View view) {
        this.noteColor = "#F4CA5E";
        view.getRootView().setBackgroundColor(Color.parseColor("#F4CA5E"));
    }

    public void setColorBlue(View view) {
        this.noteColor = "#6AA6FF";
        view.getRootView().setBackgroundColor(Color.parseColor("#6AA6FF"));
    }

    public void setColorGreen(View view) {
        this.noteColor = "#76DC8F";
        view.getRootView().setBackgroundColor(Color.parseColor("#76DC8F"));
    }
}
