package com.example.testapplication2.Miscellaneous;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView01 extends RecyclerView {

    public CustomRecyclerView01(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //Fading Edge Customization

    @Override
    protected boolean isPaddingOffsetRequired() {
        return true;
    }

    @Override
    protected int getTopPaddingOffset() {
        return -getPaddingTop();
    }

    @Override
    protected int getBottomPaddingOffset() {
        //return 0;
        return getPaddingBottom();
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        return 1;
    }

    //
}
