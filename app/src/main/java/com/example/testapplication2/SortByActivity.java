package com.example.testapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.testapplication2.Enums.EnumMessages.SORT_CRITERIA_CHANGED;
import static com.example.testapplication2.MainActivity.sortSongsByMode;
import static com.example.testapplication2.MainActivity.sortSongsByWhat;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

import com.example.testapplication2.Fragments.SongsFragment;
import com.example.testapplication2.OtherClasses.Methods;

public class SortByActivity extends Activity {
    Button cancelButton;
    Button doneButton;
    Spinner whatSpinner;
    Spinner modeSpinner;

    int whatSpinnerSelectedIndex;
    int modeSpinnerSelectedIndex;

    int sortbywhat;
    int sortbymode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sort_by_layout);

        sortbywhat = sortSongsByWhat;
        sortbymode = sortSongsByMode;

        cancelButton = findViewById(R.id.SortByLayoutCancel);
        doneButton = findViewById(R.id.SortByLayoutDone);
        whatSpinner = findViewById(R.id.SortByLayoutWhatSpinner);
        modeSpinner = findViewById(R.id.SortByLayoutModeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_by_what_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        whatSpinner.setAdapter(adapter);
        whatSpinner.setSelection(sortSongsByWhat);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sort_by_mode_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapter2);
        modeSpinner.setSelection(sortSongsByMode);

        whatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                whatSpinnerSelectedIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                modeSpinnerSelectedIndex = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortSongsByWhat = whatSpinnerSelectedIndex;
                sortSongsByMode = modeSpinnerSelectedIndex;
                weakReferencesToFragments.getMessage(SongsFragment.name, SORT_CRITERIA_CHANGED,-1,null);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sortbywhat != whatSpinnerSelectedIndex || sortbymode != modeSpinnerSelectedIndex) {

            File file = new File(getApplicationContext().getDir("Preferences", MODE_PRIVATE), "SortData");
            StringBuilder writeThis = new StringBuilder();

            try {
                writeThis.append(whatSpinnerSelectedIndex).append("*").append(modeSpinnerSelectedIndex);
            } catch (Exception e) {
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }


            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                fileOutputStream2.write(writeThis.toString().getBytes());
                fileOutputStream2.close();
            } catch (IOException e) {
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }
        }
    }
}
