package com.example.testapplication2.Miscellaneous;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class SongItemCanvas extends View {
    Paint paint;
    int[] colorArray;
    float[] colorPositionArray;

    int[] colorArray2;
    float[] colorPositionArray2;
    Paint paint2;
    LinearGradient linearGradientTopToBottom;
    LinearGradient linearGradientRightToLeft;

    int black = Color.parseColor("#FF000000");
    int white = Color.parseColor("#FFFFFFFF");
    int transparent = Color.parseColor("#00000000");

    public SongItemCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        colorArray = new int[]{black, transparent, transparent, black};
        colorPositionArray = new float[]{0, 0.5f,0.5f/*0.975f*/,1};

        colorArray2 = new int[]{black,black, black};
        colorPositionArray2 = new float[]{0,0.05f,1};

        paint = new Paint();
        paint2 = new Paint();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        linearGradientTopToBottom = new LinearGradient(0,0,0,(float)this.getHeight(),colorArray,colorPositionArray, Shader.TileMode.MIRROR);
        linearGradientRightToLeft = new LinearGradient(0,0,(float)getWidth(),0,colorArray2,colorPositionArray2, Shader.TileMode.MIRROR);

        paint.setShader(linearGradientTopToBottom);
        paint2.setShader(linearGradientRightToLeft);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0,0,getWidth(),getHeight(),paint2);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
    }

    public void SetColor(int colorInt){

        float[] hsv = new float[3];
        Color.colorToHSV(colorInt,hsv);
        hsv[2]*=0.2;
        colorInt = Color.HSVToColor(hsv);

        colorArray2 = new int[]{colorInt, black, black};

        linearGradientRightToLeft = new LinearGradient(0,0,(float)getWidth(),0,colorArray2,colorPositionArray2, Shader.TileMode.MIRROR);

        paint2.setShader(linearGradientRightToLeft);

        this.invalidate();
    }

    public void SetColor(int colorInt, boolean isSelected){

        if (isSelected) {

            int[] colors;

            float[] hsv = new float[3];
            Color.colorToHSV(colorInt, hsv);
            hsv[2] *=0.2;
            colorInt = Color.HSVToColor(hsv);

            colorArray2 = new int[]{colorInt, black, black};

            hsv[2]=1;
            colors = new int[]{Color.HSVToColor(hsv), black, black};
            float[] position = new float[]{0, 0.8f, 1};

            linearGradientRightToLeft = new LinearGradient(0, 0, (float) getWidth(), 0, colors, position, Shader.TileMode.MIRROR);

            paint2.setShader(linearGradientRightToLeft);

            this.invalidate();
        }else {
            SetColor(colorInt);
        }
    }

    public void select(boolean select){

        int[] colors;
        float[] position;
        if (select) {
            float[] hsv = new float[3];
            Color.colorToHSV(colorArray2[0], hsv);
            hsv[2] = 1;

            colors = new int[]{Color.HSVToColor(hsv), colorArray2[1], colorArray2[2]};
            position = new float[]{0, 0.8f, 1};
        }else {
            colors = colorArray2;
            position = colorPositionArray2;
        }

        linearGradientRightToLeft = new LinearGradient(0,0,(float)getWidth(),0,colors,position, Shader.TileMode.MIRROR);

        paint2.setShader(linearGradientRightToLeft);

        this.invalidate();
    }
}
