package com.example.testapplication2.Miscellaneous;

import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.dpToPixels;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class NowPlayingBackgroundCanvas extends View {
    Paint paint;
    int[] colorArray;
    float[] colorPositionArray;
    LinearGradient linearGradientLeftToRight;
    Paint paintTopToBottom;
    int[] colorArrayTopToBottom;
    float[] colorPositionArrayTopToBottom;
    LinearGradient linearGradientTopToBottom;
    int transparent = Color.parseColor("#00000000");
    float positionAsAFraction = 0;
    int width;
    int height;
    float widthToDraw;

    public NowPlayingBackgroundCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        colorArray = new int[]{transparent, Color.argb(200,Color.red(baseColor),Color.green(baseColor),Color.blue(baseColor)), transparent};
        colorPositionArray = new float[]{0, 0.8f, 1};
        paintTopToBottom = new Paint();
        colorArrayTopToBottom = new int[]{Color.parseColor("#CC000000"),Color.parseColor("#CC000000"), transparent};
        colorPositionArrayTopToBottom = new float[]{0,0f, 1};
        width = getWidth();
        height = getHeight();
        widthToDraw = width*positionAsAFraction;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
        widthToDraw = width*positionAsAFraction;
        linearGradientLeftToRight = new LinearGradient(0,0,widthToDraw,0,colorArray,colorPositionArray, Shader.TileMode.CLAMP);
        linearGradientTopToBottom = new LinearGradient(0,0,0,h,colorArrayTopToBottom,colorPositionArrayTopToBottom, Shader.TileMode.CLAMP);
        paint.setShader(linearGradientLeftToRight);
        paintTopToBottom.setShader(linearGradientTopToBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,width,height,paintTopToBottom);
        canvas.drawRect(0,0,widthToDraw,height,paint);
    }

    public void setPaintColor(){
        colorArray[1] = Color.argb(200,Color.red(baseColor),Color.green(baseColor),Color.blue(baseColor));
        linearGradientLeftToRight = new LinearGradient(0,0,widthToDraw,0,colorArray,colorPositionArray, Shader.TileMode.CLAMP);
        paint.setShader(linearGradientLeftToRight);
        this.invalidate();
    }

    public void updatePosition(){
        int duration = queueObject.nowPlaying.getDuration();
        if (duration>0) {
            positionAsAFraction = (float) queueObject.playbackPosition / duration;
            widthToDraw = width*positionAsAFraction;
        }
        linearGradientLeftToRight = new LinearGradient(0, 0, widthToDraw, 0, colorArray, colorPositionArray, Shader.TileMode.CLAMP);
        paint.setShader(linearGradientLeftToRight);
        this.invalidate();
    }

    public void setPositionToZero(){
        widthToDraw = 0;    //positionAsAFraction = 0;
        this.invalidate();
    }
}

