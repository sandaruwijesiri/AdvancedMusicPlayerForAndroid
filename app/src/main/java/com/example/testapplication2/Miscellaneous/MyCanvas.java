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

public class MyCanvas extends View {
    Paint paint;
    int[] colorArray;
    float[] colorPositionArray;

    int[] colorArray2;
    float[] colorPositionArray2;
    Paint paint2;
    LinearGradient linearGradientTopToBottom;
    LinearGradient linearGradientRightToLeft;

    int configuration = 0;
    ArrayList<int[]> colorArrays = new ArrayList<>();

    int[] colorArrayBackup;
    int configurationBackup = -1;

    int black = Color.parseColor("#FF000000");
    int white = Color.parseColor("#FFFFFFFF");
    int transparent = Color.parseColor("#00000000");

    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        colorArray = new int[]{black, black, black, black};
        colorPositionArray = new float[]{0, 0.1f,1/*0.975f*/,1};

        colorArray2 = new int[]{black,transparent, black};
        colorPositionArray2 = new float[]{0,0.5f,1};

        paint = new Paint();
        paint2 = new Paint();

        colorArrays.add(new int[]{Color.parseColor("#FFF07828"),Color.parseColor("#FF000000")
                ,Color.parseColor("#FF000000"),Color.parseColor("#FFF07828")});
        colorArrays.add(new int[]{Color.parseColor("#FF78F028"),Color.parseColor("#FF000000")
                ,Color.parseColor("#FF000000"),Color.parseColor("#FF78F028")});
        colorArrays.add(new int[]{Color.parseColor("#FF28F078"),Color.parseColor("#FF000000")
                ,Color.parseColor("#FF000000"),Color.parseColor("#FF28F078")});
        colorArrays.add(new int[]{Color.parseColor("#FF2878F0"),Color.parseColor("#FF000000")
                ,Color.parseColor("#FF000000"),Color.parseColor("#FF2878F0")});
        colorArrays.add(new int[]{Color.parseColor("#FF7828F0"),Color.parseColor("#FF000000")
                ,Color.parseColor("#FF000000"),Color.parseColor("#FF7828F0")});
        colorArrays.add(new int[]{Color.parseColor("#FFF02878"),Color.parseColor("#FF000000")
                ,Color.parseColor("#FF000000"),Color.parseColor("#FFF02878")});

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

        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint2);
    }

    public void CycleThrough(){

        if (configurationBackup==configuration){
            colorArray = colorArrayBackup;
            colorArrayBackup = null;
            configurationBackup = -1;
            --configuration;
        }else {
            if (colorArrayBackup == null) {
                colorArrayBackup = colorArray;
                configurationBackup = configuration;
            }
            colorArray = colorArrays.get(configuration);
        }

        linearGradientTopToBottom = new LinearGradient(0,0,0,(float)this.getHeight(),colorArray,colorPositionArray, Shader.TileMode.MIRROR);

        paint.setShader(linearGradientTopToBottom);

        ++configuration;
        configuration = configuration%colorArrays.size();

        this.invalidate();
    }


    public void SetColor(int colorInt){

        float[] hsv = new float[3];
        Color.colorToHSV(colorInt,hsv);
        hsv[2]*=0.2;
        colorInt = Color.HSVToColor(hsv);

        colorArrayBackup = null;
        configurationBackup = -1;
        colorArray = new int[]{colorInt, black
                , black,colorInt};

        linearGradientTopToBottom = new LinearGradient(0,0,0,(float)this.getHeight(),colorArray,colorPositionArray, Shader.TileMode.MIRROR);

        paint.setShader(linearGradientTopToBottom);

        //weakReferencesToFragments.getMessage("HomeFragment","updateItemColors",colorInt,null);

        this.invalidate();
    }
}
