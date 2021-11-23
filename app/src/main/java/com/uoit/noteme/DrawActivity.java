package com.uoit.noteme;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class DrawActivity extends AppCompatActivity {

    drawingCanvas canvas;
    String[] itemTypes = {"Circle", "Square", "Line"};
    String[] itemSize = {"1", "3", "5", "10", "20", "30", "40", "50", "75", "100"};
    Spinner itemTypeSpinner;
    Spinner itemSizeSpinner;

    Float lineStartx = null;
    Float lineStarty = null;;
    ArrayList<float[]> lines = new ArrayList<float[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        LinearLayout parent = findViewById(R.id.canvas);
        canvas = new drawingCanvas(this);
        parent.addView(canvas);

        //Populate Spinners
        itemTypeSpinner = (Spinner) findViewById(R.id.itemTypeView);
        itemSizeSpinner = (Spinner) findViewById(R.id.itemSizeView);

        ArrayAdapter<String> adapterTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemTypes);
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(adapterTypes);

        ArrayAdapter<String> adapterSize = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, itemSize);
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSizeSpinner.setAdapter(adapterSize);

    }

    private class drawingCanvas extends View {
        Paint circlePaint;
        Paint linePaint;
        Path circlePath;
        public drawingCanvas(Context context){
            super(context);

            circlePaint = new Paint();
            linePaint = new Paint();
            circlePath = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            for (int i = 0; i < lines.size(); i++) {
                Log.d("Debug", "Drawing line " + String.valueOf(i));
                linePaint.setStrokeWidth(lines.get(i)[4]);
                canvas.drawLine(lines.get(i)[0], lines.get(i)[1], lines.get(i)[2], lines.get(i)[3], linePaint);
            }
            canvas.drawPath(circlePath, circlePaint);
        }

        private void touchMove(float posx, float poxy){
            float size = Float.parseFloat(itemSizeSpinner.getSelectedItem().toString());

            String type = itemTypeSpinner.getSelectedItem().toString();
            switch (type){
                case "Circle":
                    circlePath.addCircle(posx, poxy, size, Path.Direction.CW);
                    break;
                case "Square":
                    RectF rect = new RectF(posx - size, poxy + size, posx + size, poxy - size);
                    circlePath.addRect(rect, Path.Direction.CW);
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
                    canvas.invalidate();
                    drawLineReset();
                }
            }

        }

        private void drawLineReset(){
            lineStartx = null;
            lineStarty = null;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drawLine(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    canvas.touchMove(x, y);
                    canvas.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
}