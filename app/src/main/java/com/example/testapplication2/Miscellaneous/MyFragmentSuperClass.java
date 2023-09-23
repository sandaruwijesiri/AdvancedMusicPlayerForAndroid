package com.example.testapplication2.Miscellaneous;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.testapplication2.DataClasses.SongItem;

public class MyFragmentSuperClass extends Fragment {


    public String name;

    public MyFragmentSuperClass(String name) {
        this.name = name;
    }

    public void updateData(){}

    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){

    }
}
