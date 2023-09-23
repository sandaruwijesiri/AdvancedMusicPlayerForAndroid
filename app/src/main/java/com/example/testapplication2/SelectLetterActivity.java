package com.example.testapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.Fragments.SongsFragment;
import com.example.testapplication2.Interfaces.LetterScrollInterface;
import com.example.testapplication2.RecyclerViewAdapters.letterRecyclerViewAdapter;

import static com.example.testapplication2.Enums.EnumMessages.SELECT_LETTER_CRITERIA_CHANGED;
import static com.example.testapplication2.MainActivity.sortSongsByWhat;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class SelectLetterActivity extends Activity implements LetterScrollInterface {

    RecyclerView SelectLetterRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_letter_layout);
        SelectLetterRecyclerView = findViewById(R.id.SelectLetterRecyclerView);

        LinearLayoutManager layoutManager;
        if (sortSongsByWhat==0){
            layoutManager = new GridLayoutManager(getApplicationContext(),6); // GridLayoutManager extends LinearLayoutManager, so Grid... can be cast to Linear... .
        }else {
            layoutManager = new LinearLayoutManager(getApplicationContext());
        }
        SelectLetterRecyclerView.setLayoutManager(layoutManager);
        SelectLetterRecyclerView.setAdapter(new letterRecyclerViewAdapter(this));
        SelectLetterRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void scrollToLetter(String letter) {
        Bundle bundle = new Bundle();
        bundle.putString("Letter",letter);
        weakReferencesToFragments.getMessage(SongsFragment.name,SELECT_LETTER_CRITERIA_CHANGED,-1,null,bundle);
        finish();
    }

    @Override
    public void scrollToDuration(String duration) {
        Bundle bundle = new Bundle();
        bundle.putString("Duration",duration);
        weakReferencesToFragments.getMessage(SongsFragment.name,SELECT_LETTER_CRITERIA_CHANGED,-1,null,bundle);
        finish();
    }

    @Override
    public void scrollToLastAdded(String lastAdded) {
        Bundle bundle = new Bundle();
        bundle.putString("LastAdded",lastAdded);
        weakReferencesToFragments.getMessage(SongsFragment.name,SELECT_LETTER_CRITERIA_CHANGED,-1,null,bundle);
        finish();
    }

    @Override
    public void scrollToFavourite(String percentile) {
        Bundle bundle = new Bundle();
        bundle.putString("Favourite",percentile);
        weakReferencesToFragments.getMessage(SongsFragment.name,SELECT_LETTER_CRITERIA_CHANGED,-1,null,bundle);
        finish();
    }
}
