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

import static com.example.testapplication2.MainActivity.dpToPixels;

public class PlaylistItemCanvas extends View {
    Paint paint;
    int[] colorArray;
    float[] colorPositionArray;
    LinearGradient linearGradientOfBackDrop;

    Paint paint2;
    int[] colorArray2;
    float[] colorPositionArray2;
    LinearGradient linearGradientOfMask;

    int black = Color.parseColor("#FF000000");
    int white = Color.parseColor("#FFFFFFFF");
    int transparent = Color.parseColor("#00000000");

    public PlaylistItemCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        colorArray = new int[]{transparent, black, black, transparent};
        colorPositionArray = new float[]{0, 0.1f,0.9f/*0.975f*/,1};

        paint = new Paint();

        colorArray2 = new int[]{black, black, transparent};
        colorPositionArray2 = new float[]{0, 0.5f,1};

        paint2 = new Paint();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        linearGradientOfBackDrop = new LinearGradient(getWidth(),getHeight(),0,0,colorArray,colorPositionArray, Shader.TileMode.MIRROR);
        paint.setShader(linearGradientOfBackDrop);

        linearGradientOfMask = new LinearGradient(getWidth(),0,0,getHeight(),colorArray2,colorPositionArray2, Shader.TileMode.MIRROR);
        paint2.setShader(linearGradientOfMask);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(0,0,getWidth(),getHeight(),5*dpToPixels,5*dpToPixels,paint);
        canvas.drawRoundRect(0,0,getWidth(),getHeight(),5*dpToPixels,5*dpToPixels,paint2);
    }

    public void SetColor(int colorInt){

        float[] hsv = new float[3];
        Color.colorToHSV(colorInt,hsv);
        hsv[2]=0.15f;
        colorInt = Color.HSVToColor(hsv);

        colorArray[1] = colorInt;
        colorArray[2] = colorInt;

        linearGradientOfBackDrop = new LinearGradient(getWidth(),getHeight(),0,0,colorArray,colorPositionArray, Shader.TileMode.MIRROR);

        paint.setShader(linearGradientOfBackDrop);

        this.invalidate();
    }
}
