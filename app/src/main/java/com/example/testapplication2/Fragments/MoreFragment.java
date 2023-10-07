package com.example.testapplication2.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.testapplication2.Enums.EnumMessages.LOAD_SPECIFIC_PLAYLIST_FRAGMENT;
import static com.example.testapplication2.MainActivity.archivePlaylistItem;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.setCurrentPlaylistItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MoreFragment extends MyFragmentSuperClass {

    public static String name = "MoreFragment";
    static MainActivity mainActivity;

    TextView archiveTextView;

    TextView ShowErrorLogTextView;
    TextView HideErrorLogTextView;
    TextView ClearErrorLogTextView;
    TextView ErrorContentTextview;

    public MoreFragment() {
        super("MoreFragment");
        weakReferencesToFragments.saveFragment(5,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.more_fragment,container,false);

        mainActivity = ((MainActivity) requireActivity());

        archiveTextView = rootView.findViewById(R.id.archiveTextView);
        archiveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currentPlaylist = archivePlaylistItem;
                setCurrentPlaylistItem(archivePlaylistItem);
                weakReferencesToFragments.getMessage(MainActivity.name, LOAD_SPECIFIC_PLAYLIST_FRAGMENT,-1,null);
            }
        });


        ShowErrorLogTextView = rootView.findViewById(R.id.ShowErrorLogTextView);
        HideErrorLogTextView = rootView.findViewById(R.id.HideErrorLogTextView);
        ClearErrorLogTextView = rootView.findViewById(R.id.ClearErrorLogTextView);
        ErrorContentTextview = rootView.findViewById(R.id.ErrorContentTextview);

        ShowErrorLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File errorLog = new File(getContext().getDir("ErrorLogDirectory", MODE_PRIVATE), "ErrorLog.txt");
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    FileInputStream fileInputStream = new FileInputStream(errorLog);
                    int data = fileInputStream.read();
                    for (;data!=-1;data=fileInputStream.read()){
                        stringBuilder.append((char) data);
                    }
                } catch (IOException e) {
                    Methods.addToErrorLog(e.getMessage(), getContext());
                    e.printStackTrace();
                }

                ErrorContentTextview.setText(stringBuilder.toString());
            }
        });

        HideErrorLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorContentTextview.setText("");
            }
        });

        ClearErrorLogTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorContentTextview.setText("");
                File errorLog = new File(getContext().getDir("ErrorLogDirectory", MODE_PRIVATE), "ErrorLog.txt");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(errorLog);
                    fileOutputStream.write("".getBytes());
                } catch (IOException e) {
                    Methods.addToErrorLog(e.getMessage(), getContext());
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
        }
    }

    @Override
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){

    }

    public void thumbnailUpdated(SongItem songItem){

    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("More");
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }
}
