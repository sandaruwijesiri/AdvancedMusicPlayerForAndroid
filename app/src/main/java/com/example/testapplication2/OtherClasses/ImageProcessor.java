package com.example.testapplication2.OtherClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;

public class ImageProcessor {

    public static Bitmap getThumbnail(String path){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(path);
            byte[] byte1 = mediaMetadataRetriever.getEmbeddedPicture();
            mediaMetadataRetriever.release();
            if(byte1 != null) {
                return BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
            }
            else{
                return null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap CropBitmapTo1to1AspectRatio(Bitmap src){
        if (src != null) {
            int width = src.getWidth();
            int height = src.getHeight();

            if (height > width) {
                return Bitmap.createBitmap(src, 0, (height - width)/2, width, width);
            } else {
                return Bitmap.createBitmap(src, (width - height)/2, 0, height, height);
            }
        }else {
            return null;
        }
    }

    public static Bitmap CreatingLowResBitmap(Bitmap bitmapsrc, int finalwidth,int finalheight){
        if (bitmapsrc != null) {
            int width = bitmapsrc.getWidth();
            int height = bitmapsrc.getHeight();
            float widthRatio = ((float) width)/((float) finalwidth);
            float heightRatio = ((float) height)/((float) finalheight);

            if (widthRatio>1 || heightRatio>1) {

                int[] pixelArray = new int[width*height];
                bitmapsrc.getPixels(pixelArray,0,width,0,0,width,height);
                int[] intarray = new int[finalwidth*finalheight];
                int Counter = 0;
                short originalxeditor = 0; // range of short is from -32,768 to 32,767, used here since the maximum value reached by this variable is equal to finalwidthorheight, and it is normally within the range
                short originalyeditor = 0; // same as above

                float originalx = 0;
                float originaly = 0;

                Color color;
                float TotalAlpha;
                float TotalRed;
                float TotalGreen;
                float TotalBlue;
                int y;
                int pixelCount;
                int x;
                while ((int)(originaly+heightRatio)  <= (height)) {
                    originalx = 0;
                    originalxeditor = 0;
                    while ((int)(originalx+widthRatio) <= (width)) {
                        TotalAlpha = 0;
                        TotalRed = 0;
                        TotalGreen = 0;
                        TotalBlue = 0;

                        y = (int) originaly;
                        pixelCount = 0;
                        while (y + 1 <= (int) (heightRatio + originaly)) {
                            x = (int) originalx;
                            while (x + 1 <= (int)(widthRatio + originalx)) {
                                color = Color.valueOf(pixelArray[width*y + x]);
                                TotalAlpha = TotalAlpha + color.alpha();
                                TotalRed = TotalRed + color.red();
                                TotalGreen = TotalGreen + color.green();
                                TotalBlue = TotalBlue + color.blue();

                                ++pixelCount;
                                ++x;
                            }
                            ++y;
                        }
                        intarray[Counter] = Color.argb( (TotalAlpha / pixelCount),(TotalRed / pixelCount), (TotalGreen / pixelCount), (TotalBlue / pixelCount));

                        ++originalxeditor;
                        originalx = widthRatio*originalxeditor;
                        //originalx = originalx + Ratio; // causes a rounding off error and affects image if (originalx+Ratio) in (originalx+Ratio) <= (width) is not cast to type int
                        ++Counter;
                    }
                    ++originalyeditor;
                    originaly = heightRatio*originalyeditor;
                    //originaly = originaly + Ratio; // causes a rounding off error and affects image if (originaly+Ratio) in (originaly+Ratio) <= (width) is not cast to type int
                }

                return Bitmap.createBitmap(intarray, finalwidth, finalheight, Bitmap.Config.ARGB_8888);
            }else {return bitmapsrc;}
            //return Bitmap.createScaledBitmap(bitmapsrc, 44, 44, true); bad quality for a given width and height between the two ways this has been done
        }else {
            return null;
        }
    }

    public static Bitmap sharpenBitmap(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float sharpenFactor = 1f;
            float pRed;
            float pGreen;
            float pBlue;

            Color color;
            int[] resultPixels = new int[pixelArray.length];
            for (int p = 0; p < pixelArray.length; ++p) {
                int count = 0;
                float red = 0;
                float green = 0;
                float blue = 0;
                if (p%width != 0 && p>=width) {
                    color = Color.valueOf(pixelArray[p - width-1]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                if (p >= width) {
                    color = Color.valueOf(pixelArray[p - width]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                if (p%width != width-1 && p>=width) {
                    color = Color.valueOf(pixelArray[p - width+1]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                if ((p % width) != 0) {
                    color = Color.valueOf(pixelArray[p - 1]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                {
                    /*color = Color.valueOf(pixelArray[p]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;*/
                }
                if ((p +1) % width!=0) {
                    color = Color.valueOf(pixelArray[p + 1]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                if (p%width!=0 && p + width < pixelArray.length) {
                    color = Color.valueOf(pixelArray[p + width-1]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                if (p + width < pixelArray.length) {
                    color = Color.valueOf(pixelArray[p + width]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                if ((p+1)%width!=0 && p + width+1 < pixelArray.length) {
                    color = Color.valueOf(pixelArray[p + width+1]);
                    red = red + color.red();
                    green = green + color.green();
                    blue = blue + color.blue();
                    ++count;
                }
                /*
                float sharpenFactor = 1f;  // 1 does nothing, >1 increases contrast, 0<, <1 decreases contrast, <0 gives negative images.
                resultPixels[p] = Color.argb(1
                        , Math.max(Math.min(0.5f+((red / count)-0.5f)*sharpenFactor,1),0)
                        , Math.max(Math.min(0.5f+((green / count)-0.5f)*sharpenFactor,1),0)
                        , Math.max(Math.min(0.5f+((blue / count)-0.5f)*sharpenFactor,1),0)
                );*/
                /*float finRed=0;
                float finGreen=0;
                float finBlue=0;
                float sharpenFactor = 0.8f;

                if (red/count>0.5){
                    finRed=0.75f+(red/count - 0.75f)*sharpenFactor;
                    finGreen=0.75f+(green/count - 0.75f)*sharpenFactor;
                    finBlue=0.75f+(blue/count - 0.75f)*sharpenFactor;
                }else if (red/count<0.5){
                    finRed=0.25f+(red/count - 0.25f)*sharpenFactor;
                    finGreen=0.25f+(green/count - 0.25f)*sharpenFactor;
                    finBlue=0.25f+(blue/count - 0.25f)*sharpenFactor;
                }

                resultPixels[p] = Color.argb(1
                        , finRed
                        , finGreen
                        , finBlue
                );*/

                color = Color.valueOf(pixelArray[p]);
                pRed = color.red();
                pGreen = color.green();
                pBlue = color.blue();

                resultPixels[p] = Color.argb(1
                        , Math.max(Math.min(pRed+(pRed-red/count)*sharpenFactor,1),0)
                        , Math.max(Math.min(pGreen+(pGreen-green/count)*sharpenFactor,1),0)
                        , Math.max(Math.min(pBlue+(pBlue-blue/count)*sharpenFactor,1),0)
                );

            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Bitmap sharpenBitmapFaster(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float sharpenFactor = 1f;

            int[] resultPixels = new int[pixelArray.length];
            float[][] colorHolder = new float[pixelArray.length][3];
            float red;
            float green;
            float blue;
            float tempr;
            float tempg;
            float tempb;
            Color color;

            if (width>1 && height>1) {
                color = Color.valueOf(pixelArray[1]);
                tempr = color.red(); tempg = color.green(); tempb = color.blue();
                colorHolder[1][0] = color.red();
                colorHolder[1][1] = color.green();
                colorHolder[1][2] = color.blue();
                red=tempr; green=tempg; blue=tempb;

                color = Color.valueOf(pixelArray[width]);
                tempr = color.red(); tempg = color.green(); tempb = color.blue();
                colorHolder[width][0] = color.red();
                colorHolder[width][1] = color.green();
                colorHolder[width][2] = color.blue();
                red+=tempr; green+=tempg; blue+=tempb;

                color = Color.valueOf(pixelArray[width+1]);
                tempr = color.red(); tempg = color.green(); tempb = color.blue();
                colorHolder[width+1][0] = color.red();
                colorHolder[width+1][1] = color.green();
                colorHolder[width+1][2] = color.blue();
                red+=tempr; green+=tempg; blue+=tempb;

                color = Color.valueOf(pixelArray[0]);
                tempr = color.red(); tempg = color.green(); tempb = color.blue();
                colorHolder[0][0] = tempr;
                colorHolder[0][1] = tempg;
                colorHolder[0][2] = tempb;

                resultPixels[0] = Color.argb(1
                        , Math.max(Math.min(tempr + (tempr - red / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempg + (tempg - green / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempb + (tempb - blue / 3) * sharpenFactor, 1), 0)
                );

                for (int p = 1; p < width-1; ++p) {

                    red=colorHolder[p-1][0] + colorHolder[p-1+width][0] + colorHolder[p+width][0];
                    green=colorHolder[p-1][1] + colorHolder[p-1+width][1] + colorHolder[p+width][1];
                    blue=colorHolder[p-1][2] + colorHolder[p-1+width][2] + colorHolder[p+width][2];

                    color = Color.valueOf(pixelArray[p+1+width]);
                    tempr = color.red(); tempg = color.green(); tempb = color.blue();
                    colorHolder[p+1+width][0] = tempr;
                    colorHolder[p+1+width][1] = tempg;
                    colorHolder[p+1+width][2] = tempb;
                    red+=tempr; green+=tempg; blue+=tempb;

                    color = Color.valueOf(pixelArray[p+1]);
                    tempr = color.red(); tempg = color.green(); tempb = color.blue();
                    colorHolder[p+1][0] = tempr;
                    colorHolder[p+1][1] = tempg;
                    colorHolder[p+1][2] = tempb;
                    red+=tempr; green+=tempg; blue+=tempb;

                    tempr = colorHolder[p][0]; tempg = colorHolder[p][1]; tempb = colorHolder[p][2];
                    resultPixels[p] = Color.argb(1
                            , Math.max(Math.min(tempr + (tempr - red / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempg + (tempg - green / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempb + (tempb - blue / 5) * sharpenFactor, 1), 0)
                    );
                }

                red=colorHolder[width-2][0] + colorHolder[width*2-2][0] + colorHolder[width*2-1][0];
                green=colorHolder[width-2][1] + colorHolder[width*2-2][1] + colorHolder[width*2-1][1];
                blue=colorHolder[width-2][2] + colorHolder[width*2-2][2] + colorHolder[width*2-1][2];
                tempr = colorHolder[width-1][0]; tempg = colorHolder[width-1][1]; tempb = colorHolder[width-1][2];
                resultPixels[width-1] = Color.argb(1
                        , Math.max(Math.min(tempr + (tempr - red / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempg + (tempg - green / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempb + (tempb - blue / 3) * sharpenFactor, 1), 0)
                );

                for (int row=width;row<pixelArray.length-width;row+=width){

                    color = Color.valueOf(pixelArray[row+width]);
                    tempr = color.red(); tempg = color.green(); tempb = color.blue();
                    colorHolder[row+width][0] = color.red();
                    colorHolder[row+width][1] = color.green();
                    colorHolder[row+width][2] = color.blue();
                    red=tempr; green=tempg; blue=tempb;

                    color = Color.valueOf(pixelArray[row+width+1]);
                    tempr = color.red(); tempg = color.green(); tempb = color.blue();
                    colorHolder[row+width+1][0] = color.red();
                    colorHolder[row+width+1][1] = color.green();
                    colorHolder[row+width+1][2] = color.blue();
                    red+=tempr; green+=tempg; blue+=tempb;

                    red=red + colorHolder[row-width][0] + colorHolder[row-width+1][0] + colorHolder[row+1][0];
                    green=green + colorHolder[row-width][1] + colorHolder[row-width+1][1] + colorHolder[row+1][1];
                    blue=blue + colorHolder[row-width][2] + colorHolder[row-width+1][2] + colorHolder[row+1][2];
                    tempr = colorHolder[row][0]; tempg = colorHolder[row][1]; tempb = colorHolder[row][2];

                    resultPixels[row] = Color.argb(1
                            , Math.max(Math.min(tempr + (tempr - red / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempg + (tempg - green / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempb + (tempb - blue / 5) * sharpenFactor, 1), 0)
                    );

                    for (int p = 1; p < width-1; ++p) {

                        red=colorHolder[row+p-width-1][0] + colorHolder[row+p-width][0] + colorHolder[row+p-width+1][0] + colorHolder[row+p-1][0] + colorHolder[row+p+1][0] + colorHolder[row+p+width-1][0] + colorHolder[row+p+width][0];
                        green=colorHolder[row+p-width-1][1] + colorHolder[row+p-width][1] + colorHolder[row+p-width+1][1] + colorHolder[row+p-1][1] + colorHolder[row+p+1][1] + colorHolder[row+p+width-1][1] + colorHolder[row+p+width][1];
                        blue=colorHolder[row+p-width-1][2] + colorHolder[row+p-width][2] + colorHolder[row+p-width+1][2] + colorHolder[row+p-1][2] + colorHolder[row+p+1][2] + colorHolder[row+p+width-1][2] + colorHolder[row+p+width][2];

                        color = Color.valueOf(pixelArray[row+p+1+width]);
                        tempr = color.red(); tempg = color.green(); tempb = color.blue();
                        colorHolder[row+p+1+width][0] = tempr;
                        colorHolder[row+p+1+width][1] = tempg;
                        colorHolder[row+p+1+width][2] = tempb;
                        red+=tempr; green+=tempg; blue+=tempb;

                        tempr = colorHolder[row+p][0]; tempg = colorHolder[row+p][1]; tempb = colorHolder[row+p][2];
                        resultPixels[row+p] = Color.argb(1
                                , Math.max(Math.min(tempr + (tempr - red / 8) * sharpenFactor, 1), 0)
                                , Math.max(Math.min(tempg + (tempg - green / 8) * sharpenFactor, 1), 0)
                                , Math.max(Math.min(tempb + (tempb - blue / 8) * sharpenFactor, 1), 0)
                        );
                    }

                    red=colorHolder[row-2][0] + colorHolder[row-1][0] + colorHolder[row+width-2][0] + colorHolder[row+width*2-2][0] + colorHolder[row+width*2-1][0];
                    green=colorHolder[row-2][1] + colorHolder[row-1][1] + colorHolder[row+width-2][0] + colorHolder[row+width*2-2][1] + colorHolder[row+width*2-1][1];
                    blue=colorHolder[row-2][2] + colorHolder[row-1][2] + colorHolder[row+width-2][0] + colorHolder[row+width*2-2][2] + colorHolder[row+width*2-1][2];
                    tempr = colorHolder[row+width-1][0]; tempg = colorHolder[row+width-1][1]; tempb = colorHolder[row+width-1][2];
                    resultPixels[row+width-1] = Color.argb(1
                            , Math.max(Math.min(tempr + (tempr - red / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempg + (tempg - green / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempb + (tempb - blue / 5) * sharpenFactor, 1), 0)
                    );
                }

                red=colorHolder[pixelArray.length-width+1][0] + colorHolder[pixelArray.length-width*2+1][0] + colorHolder[pixelArray.length-width*2][0];
                green=colorHolder[pixelArray.length-width+1][1] + colorHolder[pixelArray.length-width*2+1][1] + colorHolder[pixelArray.length-width*2][1];
                blue=colorHolder[pixelArray.length-width+1][2] + colorHolder[pixelArray.length-width*2+1][2] + colorHolder[pixelArray.length-width*2][2];
                tempr = colorHolder[pixelArray.length-width][0]; tempg = colorHolder[pixelArray.length-width][1]; tempb = colorHolder[pixelArray.length-width][2];
                resultPixels[pixelArray.length-width] = Color.argb(1
                        , Math.max(Math.min(tempr + (tempr - red / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempg + (tempg - green / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempb + (tempb - blue / 3) * sharpenFactor, 1), 0)
                );

                for (int p = pixelArray.length-width+1; p < pixelArray.length-1; ++p) {

                    red=colorHolder[p+1][0] + colorHolder[p+1-width][0] + colorHolder[p-1][0] + colorHolder[p-1-width][0] + colorHolder[p-width][0];
                    green=colorHolder[p+1][1] + colorHolder[p+1-width][1] + colorHolder[p-1][1] + colorHolder[p-1-width][1] + colorHolder[p-width][1];
                    blue=colorHolder[p+1][2] + colorHolder[p+1-width][2] + colorHolder[p-1][2] + colorHolder[p-1-width][2] + colorHolder[p-width][2];

                    tempr = colorHolder[p][0]; tempg = colorHolder[p][1]; tempb = colorHolder[p][2];
                    resultPixels[p] = Color.argb(1
                            , Math.max(Math.min(tempr + (tempr - red / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempg + (tempg - green / 5) * sharpenFactor, 1), 0)
                            , Math.max(Math.min(tempb + (tempb - blue / 5) * sharpenFactor, 1), 0)
                    );
                }

                red=colorHolder[pixelArray.length-width-2][0] + colorHolder[pixelArray.length-width-1][0] + colorHolder[pixelArray.length-2][0];
                green=colorHolder[pixelArray.length-width-2][1] + colorHolder[pixelArray.length-width-1][1] + colorHolder[pixelArray.length-2][1];
                blue=colorHolder[pixelArray.length-width-2][2] + colorHolder[pixelArray.length-width-1][2] + colorHolder[pixelArray.length-2][2];
                tempr = colorHolder[pixelArray.length-1][0]; tempg = colorHolder[pixelArray.length-1][1]; tempb = colorHolder[pixelArray.length-1][2];
                resultPixels[pixelArray.length-1] = Color.argb(1
                        , Math.max(Math.min(tempr + (tempr - red / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempg + (tempg - green / 3) * sharpenFactor, 1), 0)
                        , Math.max(Math.min(tempb + (tempb - blue / 3) * sharpenFactor, 1), 0)
                );

                return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
            }
            else {return src;}
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Bitmap improveContrast(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float contrastFactor = 1.1f;
            float pivotValue = 0.5f;

            Color color;
            int[] resultPixels = new int[width * height];
            for (int p = 0; p < pixelArray.length; ++p) {
                color = Color.valueOf(pixelArray[p]);

                resultPixels[p] = Color.argb(1
                        , Math.max(Math.min(pivotValue+(color.red()-pivotValue)*contrastFactor,1),0)
                        , Math.max(Math.min(pivotValue+(color.green()-pivotValue)*contrastFactor,1),0)
                        , Math.max(Math.min(pivotValue+(color.blue()-pivotValue)*contrastFactor,1),0)
                );

            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Bitmap addFrequency(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float contrastFactor = 1.3f;
            float contrastFactor2 = 0.8f;
            float pivotValue = 0.5f;

            Color color;
            int[] resultPixels = new int[width * height];
            for (int p = 0; p < pixelArray.length; ++p) {
                color = Color.valueOf(pixelArray[p]);
                if ((p % 2== 0 && (p/width)%2==0) || (p % 2== 1 && (p/width)%2==1)) {

                    resultPixels[p] = Color.argb(1
                            , Math.max(Math.min(color.red() * contrastFactor, 1), 0)
                            , Math.max(Math.min(color.green() * contrastFactor, 1), 0)
                            , Math.max(Math.min(color.blue() * contrastFactor, 1), 0)
                    );
                } else {
                    resultPixels[p] = Color.argb(1
                            , Math.max(Math.min(color.red() * contrastFactor2, 1), 0)
                            , Math.max(Math.min(color.green() * contrastFactor2, 1), 0)
                            , Math.max(Math.min(color.blue() * contrastFactor2, 1), 0)
                    );
                }
            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            return null;
        }
    }

    public static Bitmap improveContrast2(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float contrastFactor = 0.8f;
            float pivotValue = 0.5f;

            float red;
            float green;
            float blue;

            float avg;
            float var;

            Color color;

            int[] resultPixels = new int[width * height];
            for (int p = 0; p < pixelArray.length; ++p) {
                color = Color.valueOf(pixelArray[p]);

                red = color.red();
                green = color.green();
                blue = color.blue();

                red = pivotValue+(red-pivotValue)*contrastFactor;
                green = pivotValue+(green-pivotValue)*contrastFactor;
                blue = pivotValue+(blue-pivotValue)*contrastFactor;

                avg = (red+green+blue)/3;
                var = (Math.abs(red-avg) + Math.abs(green-avg) + Math.abs(blue-avg))/3;

                contrastFactor = (float) (1.1*4.5/(4.5+var));

                resultPixels[p] = Color.argb(1
                        , Math.max(Math.min(pivotValue+(red-pivotValue)*contrastFactor,1),0)
                        , Math.max(Math.min(pivotValue+(green-pivotValue)*contrastFactor,1),0)
                        , Math.max(Math.min(pivotValue+(blue-pivotValue)*contrastFactor,1),0)
                );

            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Bitmap improveContrast3(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float contrastFactor = 1.1f;
            float pivotValue = 0.5f;

            Color color;
            int[] resultPixels = new int[width * height];

            float red = 0;
            float green = 0;
            float blue = 0;

            for (int i : pixelArray) {
                color = Color.valueOf(i);
                /*red += color.red();
                green += color.green();
                blue += color.blue();*/

                red += Math.max(Math.max(color.red(),color.green()),color.blue());
            }

            pivotValue = (red/*+green+blue*/)/(/*3**/width*height);System.out.println("pivot: " + pivotValue);

            for (int p = 0; p < pixelArray.length; ++p) {
                color = Color.valueOf(pixelArray[p]);

                resultPixels[p] = Color.argb(1
                        , Math.max(Math.min(pivotValue+(color.red()-pivotValue)*contrastFactor,1),0)
                        , Math.max(Math.min(pivotValue+(color.green()-pivotValue)*contrastFactor,1),0)
                        , Math.max(Math.min(pivotValue+(color.blue()-pivotValue)*contrastFactor,1),0)
                );

            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Bitmap improveContrastAnotherFutileAttempt(Bitmap src){
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);
            float red = 0;
            float green = 0;
            float blue = 0;

            float contrastFactor = 1.5f;
            Color color;
            int[] resultPixels = new int[pixelArray.length];

            for (int i : pixelArray) {
                color = Color.valueOf(i);
                red += color.red();
                green += color.green();
                blue += color.blue();
            }

            for (int p = 0; p < pixelArray.length; ++p) {
                color = Color.valueOf(pixelArray[p]);
                resultPixels[p] = Color.argb(1
                        , Math.max(Math.min(red/pixelArray.length+ (color.red()-red/pixelArray.length)*contrastFactor,1),0)
                        , Math.max(Math.min(green/pixelArray.length + (color.green()-green/pixelArray.length)*contrastFactor,1),0)
                        , Math.max(Math.min(blue/pixelArray.length + (color.blue()-blue/pixelArray.length)*contrastFactor,1),0)
                );

            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static Bitmap improveColor(Bitmap src){
        // doesn't work
        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();
            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            int[] resultPixels = new int[pixelArray.length];

            float[] hsv = new float[3];
            float satur;
            float val;
            for (int i =0;i< pixelArray.length;++i) {
                Color.colorToHSV(pixelArray[i],hsv);
                satur = hsv[1]*5;
                if (satur>0.999999)
                    satur=1;

                val = hsv[2]*5;
                if (val>0.999999)
                    val=1;
                hsv[2] =val;
                resultPixels[i] = Color.HSVToColor(hsv);
            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }else {
            System.out.println("NULL!!!!!!!!!!!!!!!!!!!!!");
            return null;
        }
    }

    public static int adjustBrightness(Color color, float requiredBrightness){
        int returnColor;
        float red = color.red();
        float green = color.green();
        float blue = color.blue();

        float factor = requiredBrightness/getPerceivedBrightness(color);

        float desiredRed = red*factor;
        float desiredGreen = green*factor;
        float desiredBlue = blue*factor;

        if (desiredRed>0.04045){
            desiredRed = (float) (Math.pow((float) Math.pow((red+0.055)/1.055,2.4)*factor,1/2.4)*1.055 - 0.055);
        }
        if (desiredGreen>0.04045){
            desiredGreen = (float) (Math.pow((float) Math.pow((green+0.055)/1.055,2.4)*factor,1/2.4)*1.055 - 0.055);
        }
        if (desiredBlue>0.04045){
            desiredBlue = (float) (Math.pow((float) Math.pow((blue+0.055)/1.055,2.4)*factor,1/2.4)*1.055 - 0.055);
        }

        returnColor = Color.rgb(Math.min(Math.max(desiredRed,0),1),Math.min(Math.max(desiredGreen,0),1),Math.min(Math.max(desiredBlue,0),1));

        System.out.println("Colors: " + red + ", " + green + ", " + blue + "\n" +
                "perceived: " + getPerceivedBrightness(color) + ", factor: " + factor + "\n" +
                "Desired: " + desiredRed + ", " + desiredGreen + ", " + desiredBlue + "\n" +
                "BRIGHTNNNNNNNNNNNNNNNNNNNNNN: " + getPerceivedBrightness(Color.valueOf(returnColor)));

        return returnColor;
    }

    public static float getPerceivedBrightness(Color color){
        float red = color.red();
        float green = color.green();
        float blue = color.blue();

        float red2;
        float green2;
        float blue2;

        if (red<=0.04045){
            red2= (float) (red/12.92);
        }else {
            red2 = (float) Math.pow((red+0.055)/1.055,2.4);
        }
        if (green<=0.04045){
            green2= (float) (green/12.92);
        }else {
            green2 = (float) Math.pow((green+0.055)/1.055,2.4);
        }
        if (blue<=0.04045){
            blue2= (float) (blue/12.92);
        }else {
            blue2 = (float) Math.pow((blue+0.055)/1.055,2.4);
        }

        float perceivedBrightness = (float) (0.2126*red2 + 0.7152*green2 + 0.0722*blue2);

        return perceivedBrightness;
    }

    public static Bitmap verticalFadingBitmap(Bitmap src, float howMuch){

        if (src!=null) {
            int width = src.getWidth();
            int height = src.getHeight();

            if (howMuch > 0.5) {
                howMuch = 0.5f;
            } else if (howMuch < 0) {
                howMuch = 0;
            }

            int topEnd = (int) (height * howMuch);
            int bottomEnd = (int) (height * (1-howMuch));

            int[] pixelArray = new int[width * height];
            src.getPixels(pixelArray, 0, width, 0, 0, width, height);

            int[] resultPixels = new int[width * height];
            Color color;
            float alpha;
            int row;
            for (int p = 0; p < pixelArray.length; ++p) {
                if (p < topEnd * width) {
                    color = Color.valueOf(pixelArray[p]);
                    alpha = color.alpha();
                    row = p / width;
                    alpha = row * alpha / topEnd;
                    resultPixels[p] = Color.argb(alpha, color.red(), color.green(), color.blue());
                } else if (p > bottomEnd * width) {
                    color = Color.valueOf(pixelArray[p]);
                    alpha = color.alpha();
                    row = p / width;
                    alpha = (height - row) * alpha / topEnd;
                    resultPixels[p] = Color.argb(alpha, color.red(), color.green(), color.blue());
                } else {
                    resultPixels[p] = pixelArray[p];
                }
            }
            return Bitmap.createBitmap(resultPixels, width, height, Bitmap.Config.ARGB_8888);
        }

        return null;
    }

    public static int getAvgColor(Bitmap bitmap){
        if (bitmap!=null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixelArray = new int[width * height];
            bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);

            Color color;
            float totalRed = 0;
            float totalGreen = 0;
            float totalBlue = 0;
            float red;
            float green;
            float blue;
            int count = 0;
            for (int i : pixelArray) {
                color = Color.valueOf(i);
                red = color.red();
                green = color.green();
                blue = color.blue();
                if ((red > 0.1 || green > 0.1 || blue > 0.1) && (red < 0.9 || green < 0.9 || blue < 0.9)) {
                    totalRed += red;
                    totalGreen += green;
                    totalBlue += blue;
                    ++count;
                }
            }

            if(count>0) {
                return Color.argb(1, totalRed / count, totalGreen / count, totalBlue / count);
            }else{
                return Color.argb(1,0.5f,0.5f,0.5f);
            }
        }

        return Color.argb(1,0.5f,0.5f,0.5f);
    }

    public static int getDominantColor(Bitmap bitmap){
        if (bitmap!=null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixelArray = new int[width * height];
            bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);

            float red;
            float green;
            float blue;
            int[] colors = new int[1000];

            float mean;
            float variance;
            for (int i : pixelArray) {
                red = (float)Color.red(i)/256;
                green = (float)Color.green(i)/256;
                blue = (float)Color.blue(i)/256;

                mean = (red+green+blue)/3;
                variance = (Math.abs(red-mean)+Math.abs(green-mean)+Math.abs(blue-mean))/3;

                if ((red > 0.1 || green > 0.1 || blue > 0.1) && variance>0.08) {
                    red *= 10;
                    green *= 10;
                    blue *= 10;

                    ++colors[(int) (((int) red) * 100 + ((int) green) * 10 + ((int) blue))];
                }
            }

            int maxIndex = 0;
            int max=0;
            for (int i=0;i<colors.length;++i){
                if (colors[i]>max) {
                    max = colors[i];
                    maxIndex = i;
                }
            }

            float finalRed = (float) ((maxIndex/100)*0.1);
            float finalGreen = (float) (((maxIndex%100)/10)*0.1);
            float finalBlue = (float) ((maxIndex%10)*0.1);

            return Color.argb(1, finalRed,finalGreen,finalBlue);

        }

        return Color.argb(1,0.5f,0.5f,0.5f);
    }

    public static int getDominantColorDiscourageSkinAndGreyOriginal(Bitmap bitmap){
        if (bitmap!=null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixelArray = new int[width * height];
            bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);

            int[] colorsWithoutSkinAndGrey = new int[360];
            int[] allColors = new int[360];
            float[] hsv = new float[3];
            int greyCount=0;
            int skinCount=0;
            int nonBlackCount=0;
            for (int i : pixelArray) {
                Color.colorToHSV(i,hsv);

                if (hsv[2]>0.1) {
                    ++nonBlackCount;
                    if (hsv[1]<0.2) {
                        ++greyCount;
                    }else {
                        if ((hsv[0] > 0 && hsv[0] < 50 && hsv[1] > 0.23 && hsv[1] < 0.68)) {
                            skinCount++;
                        } else {
                            ++colorsWithoutSkinAndGrey[(int) (hsv[0] % 360)];
                        }
                        ++allColors[(int) (hsv[0] % 360)];
                    }
                }
            }
            float[] color;
            if (nonBlackCount==0){
                color = new float[]{0, 0, 0.5f};
            }else {
                if (greyCount>0.5*nonBlackCount){
                    color = new float[]{0, 0, 0.5f};
                }else {
                    int[] loopThis;
                    if (skinCount>0.5*nonBlackCount){
                        loopThis=allColors;
                    }else {
                        loopThis=colorsWithoutSkinAndGrey;
                    }
                    int maxIndex = -1;
                    int max = 0;
                    for (int i = 0; i < loopThis.length; ++i) {
                        if (loopThis[i] > max) {
                            max = loopThis[i];
                            maxIndex = i;
                        }
                    }
                    if (maxIndex == -1) {
                        color = new float[]{0, 0, 0.5f};
                    } else {
                        //color = new float[]{maxIndex, 1, 1};
                        color = new float[]{(int)(maxIndex/30)*30, 1, 1};
                    }
                }
            }
            /*if (color[0]<50||color[0]>310) {
                color[0] = (color[0] + 180) % 360;
            }*/

            return Color.HSVToColor(color);
        }
        return Color.argb(1,0.5f,0.5f,0.5f);
    }
    public static int getDominantColorDiscourageSkinAndGrey(Bitmap bitmap){
        if (bitmap!=null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int[] pixelArray = new int[width * height];
            bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);

            int[] colorsWithoutSkinAndGrey = new int[360];
            int[] allColors = new int[360];
            float[] hsv = new float[3];
            int greyCount=0;
            int skinCount=0;
            int nonBlackCount=0;
            for (int i : pixelArray) {
                Color.colorToHSV(i,hsv);

                if (hsv[2]>0.1) {
                    ++nonBlackCount;
                    if (hsv[1]<0.2) {
                        ++greyCount;
                    }else {
                        if ((hsv[0] > 0 && hsv[0] < 50 && hsv[1] > 0.23 && hsv[1] < 0.68)) {
                            skinCount++;
                        } else {
                            ++colorsWithoutSkinAndGrey[(int) (hsv[0] % 360)];
                        }
                        ++allColors[(int) (hsv[0] % 360)];
                    }
                }
            }
            float[] color;
            if (nonBlackCount==0){
                color = new float[]{0, 0, 0.5f};
            }else {
                if (greyCount>0.5*nonBlackCount){
                    color = new float[]{0, 0, 0.5f};
                }else {
                    int[] loopThis;
                    if (skinCount>0.5*nonBlackCount){
                        loopThis=allColors;
                    }else {
                        loopThis=colorsWithoutSkinAndGrey;
                    }
                    int maxIndex = -1;
                    float max = 0;
                    for (int i = 0; i < loopThis.length; ++i) {
                        if (i<25||i>260||(i>35&&i<70))
                            continue;
                        float weight=1;
                        /*if (i<=35){
                            weight=0.0001f;
                        }*/
                        if (loopThis[i]*weight > max) {
                            max = loopThis[i]*weight;
                            maxIndex = i;
                        }
                    }
                    if (maxIndex == -1) {
                        color = new float[]{0, 0, 0.5f};
                    } else {
                        if (maxIndex>200)
                            maxIndex=200;
                        color = new float[]{maxIndex, 1, 1};
                    }
                }
            }

            return Color.HSVToColor(color);
        }
        return Color.argb(1,0.5f,0.5f,0.5f);
    }
    /*public static int getDominantColorDiscourageSkinAndGrey(Bitmap bitmap){
        if (bitmap!=null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int widthInset = (int) (width*0.1);
            int heightInset = (int) (height*0.1);

            int[] pixelArray = new int[width * height];
            bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);

            int[] colorsWithoutSkinAndGrey = new int[360];
            int[] allColors = new int[360];
            float[] hsv = new float[3];
            int greyCount=0;
            int skinCount=0;
            int nonBlackCount=0;
            for(int row=0;row<heightInset;++row){
                for (int column=0;column<widthInset;++column){
                    int i = pixelArray[row*width+column];
                    Color.colorToHSV(i,hsv);

                    if (hsv[2]>0.1) {
                        ++nonBlackCount;
                        if (hsv[1]<0.2) {
                            ++greyCount;
                        }else {
                            if ((hsv[0] > 0 && hsv[0] < 50 && hsv[1] > 0.23 && hsv[1] < 0.68)) {
                                skinCount++;
                            } else {
                                ++colorsWithoutSkinAndGrey[(int) (hsv[0] % 360)];
                            }
                            ++allColors[(int) (hsv[0] % 360)];
                        }
                    }
                }
            }
            float[] color;
            if (nonBlackCount==0){
                color = new float[]{0, 0, 0.5f};
            }else {
                if (greyCount>0.5*nonBlackCount){
                    color = new float[]{0, 0, 0.5f};
                }else {
                    int[] loopThis;
                    if (skinCount>0.5*nonBlackCount){
                        loopThis=allColors;
                    }else {
                        loopThis=colorsWithoutSkinAndGrey;
                    }
                    int maxIndex = -1;
                    float max = 0;
                    for (int i = 0; i < loopThis.length; ++i) {
                        if (i<25||i>200||(i>35&&i<70))
                            continue;
                        float weight=1;
                        if ((i>25&&i<35)){
                            weight=0.01f;
                        }
                        if (loopThis[i]*weight > max) {
                            max = loopThis[i]*weight;
                            maxIndex = i;
                        }
                    }
                    if (maxIndex == -1) {
                        color = new float[]{0, 0, 0.5f};
                    } else {
                        color = new float[]{maxIndex, 1, 1};
                    }
                }
            }

            return Color.HSVToColor(color);
        }
        return Color.argb(1,0.5f,0.5f,0.5f);
    }*/

}
