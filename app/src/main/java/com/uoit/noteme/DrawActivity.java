package com.uoit.noteme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawActivity extends AppCompatActivity {

    drawingCanvas canvas;
    String[] itemTypes = {"Circle", "Square", "Line", "Hollow Square", "Hollow Circle", "Text"};
    String[] itemSize = {"1", "3", "5", "10", "20", "30", "40", "50", "75", "100", "125", "150", "200"};
    Spinner itemTypeSpinner;
    Spinner itemSizeSpinner;
    EditText insertTextContent;
    boolean insertText = false;

    Float lineStartx = null;
    Float lineStarty = null;;
    ArrayList<float[]> lines = new ArrayList<float[]>();
    byte[] canvasBitmap = null;

    ArrayList<String[]> textToDraw = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        LinearLayout parent = findViewById(R.id.canvas);
        canvas = new drawingCanvas(this);
        parent.addView(canvas);

        insertTextContent = findViewById(R.id.InsertTextContent);

        //Populate Spinners
        itemTypeSpinner = (Spinner) findViewById(R.id.itemTypeView);
        itemSizeSpinner = (Spinner) findViewById(R.id.itemSizeView);

        ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemTypes);
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(adapterTypes);

        ArrayAdapter<String> adapterSize = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemSize);
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSizeSpinner.setAdapter(adapterSize);

        itemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = findViewById(R.id.TextInputView);

                if (adapterView.getSelectedItem().toString().equals("Text")){
                    //Show text input box
                    textView.setVisibility(View.VISIBLE);
                    insertTextContent.setVisibility(View.VISIBLE);
                    insertText = true;
                } else {
                    //Hide text input box
                    textView.setVisibility(View.GONE);
                    insertTextContent.setVisibility(View.GONE);
                    insertText = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Set finish event listener
        ImageView imageBack = findViewById(R.id.doneButton);
        imageBack.setOnClickListener(v -> onDonePressed());

    }

    private void onDonePressed() {
        Intent data = getIntent();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        canvas.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, stream);
        canvasBitmap = stream.toByteArray();

        if (canvasBitmap != null){
           data.putExtra("image", canvasBitmap);
        } else {
            Log.d("DEBUG", "Bitmap is null");
        }

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private class drawingCanvas extends View {
        Paint solidShape;
        Paint hollowShape;
        Paint linePaint;
        Paint textPaint;
        Path solidShapePath;
        Path hollowShapePath;
        public drawingCanvas(Context context){
            super(context);

            solidShape = new Paint();
            hollowShape = new Paint();
            hollowShape.setStyle(Paint.Style.STROKE);
            linePaint = new Paint();

            textPaint = new Paint();
            Paint textPaint = new Paint();
            textPaint.setColor(Color.DKGRAY);
            textPaint.setStyle(Paint.Style.FILL);

            solidShapePath = new Path();
            hollowShapePath = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            setDrawingCacheEnabled(true);

            for (int i = 0; i < lines.size(); i++) {
                Log.d("Debug", "Drawing line " + String.valueOf(i));
                linePaint.setStrokeWidth(lines.get(i)[4]);
                canvas.drawLine(lines.get(i)[0], lines.get(i)[1], lines.get(i)[2], lines.get(i)[3], linePaint);
            }

            for (int i = 0; i < textToDraw.size(); i++) {
                float xpos = Float.parseFloat(textToDraw.get(i)[1]);
                float ypos = Float.parseFloat(textToDraw.get(i)[2]);
                float size = Float.parseFloat(textToDraw.get(i)[3]);
                textPaint.setTextSize(size);
                canvas.drawText(textToDraw.get(i)[0], xpos, ypos, textPaint);
            }

            //Draw shapes
            canvas.drawPath(solidShapePath, solidShape);
            canvas.drawPath(hollowShapePath, hollowShape);

        }

        private void touchMove(float posx, float poxy){
            float size = Float.parseFloat(itemSizeSpinner.getSelectedItem().toString());

            String type = itemTypeSpinner.getSelectedItem().toString();
            switch (type){
                case "Circle":
                    solidShapePath.addCircle(posx, poxy, size, Path.Direction.CW);
                    break;
                case "Square":
                    RectF rect = new RectF(posx - size, poxy + size, posx + size, poxy - size);
                    solidShapePath.addRect(rect, Path.Direction.CW);
                    break;
                case "Hollow Square":
                    RectF rect2 = new RectF(posx - size, poxy + size, posx + size, poxy - size);
                    hollowShapePath.addRect(rect2, Path.Direction.CW);
                    break;
                case "Hollow Circle":
                    hollowShapePath.addCircle(posx, poxy, size, Path.Direction.CW);
                    break;
            }
        }

        private void drawLine(float posx, float posy){
            float size = Float.parseFloat(itemSizeSpinner.getSelectedItem().toString());
            String type = itemTypeSpinner.getSelectedItem().toString();

            if (type.equals("Line")){
                //Line not started
                if (lineStartx == null && lineStarty == null){
                    Log.d("DEBUG", "Line started");
                    lineStartx = posx;
                    lineStarty = posy;
                } else {
                    Log.d("DEBUG", "Line ended");
                    lines.add(new float[]{lineStartx, lineStarty, posx, posy, size});
                    drawLineReset();
                }
            }

        }

        private void drawLineReset(){
            lineStartx = null;
            lineStarty = null;
        }

        private void insertText(float posx, float posy){
            if (insertText){
                float size = Float.parseFloat(itemSizeSpinner.getSelectedItem().toString());
                textToDraw.add(new String[]{insertTextContent.getText().toString(), String.valueOf(posx), String.valueOf(posy), String.valueOf(size)});
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchMove(x,y);
                    drawLine(x, y);
                    insertText(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
}