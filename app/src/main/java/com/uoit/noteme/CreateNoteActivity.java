package com.uoit.noteme;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateNoteActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    String noteColor = "#F4CA5E";
    EditText title;
    EditText subtitle;
    EditText note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
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

        if (extras != null){
            title.setText(extras.getString("title"));
            subtitle.setText(extras.getString("subtitle"));
            note.setText(extras.getString("content"));
            this.noteColor = extras.getString("color");
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(Color.parseColor(this.noteColor));
        }
    }

    public void AddData() {
        String titleText = title.getText().toString();
        String subtitleText =  subtitle.getText().toString();
        String noteText =  note.getText().toString();

        //Check note title length
        EditText editText = findViewById(R.id.inputNoteTitle);
        String inputNoteTitleText = editText.getText().toString();
        if(inputNoteTitleText.length() == 0){
            Toast.makeText(this,"A title must be entered to save a note. Press back to discard note", Toast.LENGTH_LONG).show();
        }else{
            mDatabaseHelper.addData(titleText, subtitleText, noteText, this.noteColor);
            Toast.makeText(this,"Data Successfully Inserted!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    public void setColor(View view) {
        this.noteColor = view.getTag().toString();
        view.getRootView().setBackgroundColor(Color.parseColor(view.getTag().toString()));
    }

}
