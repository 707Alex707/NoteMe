package com.uoit.noteme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DrawActivity extends AppCompatActivity {

    drawingCanvas canvas;
    Paint circlePaint;
    Path circlePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        canvas = new drawingCanvas(this);
        setContentView(canvas);

        //drawingCanvas drawingCanvas = findViewById(R.id.drawingCanvasView);
        //canvas = new drawingCanvas(this);
        //drawingCanvas.addView(canvas);

        //drawingCanvas.setContent

        //setContentView(R.layout.activity_draw2);
    }

    private class drawingCanvas extends View {
        public drawingCanvas(Context context){
            super(context);

            circlePaint = new Paint();
            circlePath = new Path();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawPath( circlePath,  circlePaint);
        }

        private void touchMove(float posx, float poxy){
            //circlePath.reset();
            circlePath.addCircle(posx, poxy, 20, Path.Direction.CW);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //touch_start(x, y);
                canvas.invalidate();
                //postInvalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                canvas.touchMove(x, y);
                canvas.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //touch_up();
                //invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}