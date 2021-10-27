package com.uoit.noteme;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class CreateNoteActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;
    String noteColor = "#F4CA5E";
    EditText title;
    EditText subtitle;
    EditText note;
    String noteID;
    ImageView noteImg;

    private static final int SELECT_PICTURE = 252;
    private byte[] selectedImageBytes = new byte[0];

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
        noteImg = findViewById(R.id.imgActivity);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        ImageView imageDone = findViewById(R.id.imageSave);
        imageDone.setOnClickListener(v -> AddData());

        //addImageButton
        ImageButton imageButton = findViewById(R.id.addImageButton);
        imageButton.setOnClickListener(v -> selectImage());

        if (extras != null){
            this.noteID = extras.getString("ID");
            title.setText(extras.getString("title"));
            subtitle.setText(extras.getString("subtitle"));
            note.setText(extras.getString("content"));
            this.noteColor = extras.getString("color");
            selectedImageBytes = extras.getByteArray("img");
            noteImg.setImageBitmap(getImage(selectedImageBytes));
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
            int IDExists = (mDatabaseHelper.fetch("SELECT 1 FROM note_table WHERE ID=" + noteID)).getCount();
            if (IDExists == 1){
                mDatabaseHelper.update(noteID, titleText, subtitleText, noteText, this.noteColor, this.selectedImageBytes);
                onBackPressed();
            }
            else{
                mDatabaseHelper.addData(titleText, subtitleText, noteText, this.noteColor, this.selectedImageBytes);
                Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }

    public void setColor(View view) {
        this.noteColor = view.getTag().toString();
        view.getRootView().setBackgroundColor(Color.parseColor(view.getTag().toString()));
    }

    //https://stackoverflow.com/a/2636538
    //https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media
    //Gets an image and saves the bytes to var selectedImageBytes
    public void selectImage(){
        boolean havePermission = checkPermissionForReadExternalStorage();
        Log.d("DEBUG",  "Have storage permission: " + String.valueOf(havePermission));
        if (havePermission){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
        }
    }

    //Checks if pictures to access files was granted
    private boolean checkPermissionForReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Permission granted
            return true;
        } else {
            //Permission not granted, request it
            String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(perms, 1);
            return false;
        }
    }

    //Handle saving of pictures to var selectedImageBytes
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri uri = data.getData();
                ContentResolver cr = getBaseContext().getContentResolver();
                try {
                    InputStream inputStream;
                    inputStream = cr.openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    selectedImageBytes = baos.toByteArray();

                    // Display image
                    noteImg.setImageBitmap(getImage(selectedImageBytes));
                    Log.d("DEBUG", "Image with size of " + String.valueOf(selectedImageBytes.length) + " bytes saved");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        for (int i = 0; i < permissions.length; i++) {
            Log.d("DEBUG", "Permission " + permissions[i] + " " + grantResults[i]);
            if (Objects.equals(permissions[i], Manifest.permission.READ_EXTERNAL_STORAGE)){
                if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this,"Permission required to access files", Toast.LENGTH_SHORT).show();
                } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    selectImage();
                }
            }
        }
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
