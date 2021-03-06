package com.uoit.noteme;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import java.sql.Blob;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "note_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "title";
    private static final String COL3 = "subtitle";
    private static final String COL4 = "text";
    private static final String COL5 = "color";
    private static final String COL6 = "img";

    public DatabaseHelper(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT, "  + COL3 + " TEXT, "  + COL4+ " TEXT, " + COL5+ " TEXT, " + COL6 +" BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor fetch(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery(query, null);
        //Cursor cursor = db.query(TABLE_NAME, new String[]{this.COL1, this.COL2, this.COL3}, null, null, null, null, null);

        return data;
    }

    public boolean addData(String title, String subtitle, String note, String color, byte[] image){
        if(title.length() > 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, title);
            contentValues.put(COL3, subtitle);
            contentValues.put(COL4, note);
            contentValues.put(COL5, color);
            contentValues.put(COL6, image);

            Log.d(TAG, "addData: Adding " + title + ", " + subtitle + ", " + note + " to " + TABLE_NAME);

            long result = db.insert(TABLE_NAME, null, contentValues);

            //if date as inserted incorrectly it will return -1
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }else{
            return false;
        }
    }

    public void update(String id, String title, String subtitle, String note, String color, byte[] image) {
        if(title.length() > 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL2, title);
            contentValues.put(COL3, subtitle);
            contentValues.put(COL4, note);
            contentValues.put(COL5, color);
            contentValues.put(COL6, image);

            db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        }
    }

    public void delete(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"ID=?",new String[]{id});
    }
}
