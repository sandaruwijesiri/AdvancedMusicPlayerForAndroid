package com.example.testapplication2.Miscellaneous;

import static com.example.testapplication2.MainActivity.contrastColor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class QueueProgressCanvas extends View {
    Paint paint;
    float progress=0;

    public QueueProgressCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(contrastColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth()/2;
        int centerY = getHeight()/2;
        int r = Math.max(getWidth(),getHeight())*2;
        canvas.drawArc(centerX-r,centerY-r,centerX+r,centerY+r,-90,progress*360,true,paint);
    }

    public void setProgress(float progress){
        this.progress=progress;
        this.invalidate();
    }

    public void updatePaintColor(){
        float[] hsv = new float[3];
        Color.colorToHSV(contrastColor,hsv);
        //hsv[1]=1;
        //hsv[2]=1;
        paint.setColor(Color.HSVToColor(hsv));
        this.invalidate();
    }
}
