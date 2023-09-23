package com.example.testapplication2.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.example.testapplication2.MainActivity.AllSongsExceptArchived;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Database.AppDatabase;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.R;

import java.io.File;
import java.util.Comparator;

public class SettingsFragment extends MyFragmentSuperClass {

    public static String name = "SettingsFragment";
    Button deleteDatabaseButton;
    Button deleteAppDataButton;
    Switch keepTrackOfFrequentSongsSwitch;
    MainActivity mainActivity;

    public static boolean keepTrackOfFrequentSongs;

    public SettingsFragment() {
        super("SettingsFragment");
        weakReferencesToFragments.saveFragment(7,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings_fragment,container,false);

        mainActivity = ((MainActivity) requireActivity());

        deleteDatabaseButton = rootView.findViewById(R.id.deleteDatabaseButton);
        deleteAppDataButton = rootView.findViewById(R.id.deleteAppDataButton);
        keepTrackOfFrequentSongsSwitch = rootView.findViewById(R.id.keepTrackOfFrequentSongsSwitch);

        deleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabaseThread runnable = new deleteDatabaseThread();
                new Thread(runnable).start();
            }
        });

        deleteAppDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File archiveDirectory = requireContext().getDir("Archive",MODE_PRIVATE);
                File[] archive = archiveDirectory.listFiles();
                for (File file:archive){
                    file.delete();
                }
                archiveDirectory.delete();

                File playlistsDirectory = requireContext().getDir("Playlists",MODE_PRIVATE);
                File[] playlists = playlistsDirectory.listFiles();
                for (File file:playlists){
                    file.delete();
                }
                playlistsDirectory.delete();

                File preferencesDirectory = requireContext().getDir("Preferences",MODE_PRIVATE);
                File[] preferences = preferencesDirectory.listFiles();
                for (File file:preferences){
                    file.delete();
                }
                preferencesDirectory.delete();

                File queueDirectory = requireContext().getDir("Queue",MODE_PRIVATE);
                File[] queue = queueDirectory.listFiles();
                for (File file:queue){
                    file.delete();
                }
                queueDirectory.delete();

                Toast.makeText(requireContext(),"Appdata deleted",Toast.LENGTH_SHORT).show();

            }
        });

        keepTrackOfFrequentSongsSwitch.setChecked(keepTrackOfFrequentSongs);
        keepTrackOfFrequentSongsSwitch.setText( (keepTrackOfFrequentSongs) ? "Keeping track of frequent songs" : "Not keeping track of frequent songs" );

        keepTrackOfFrequentSongsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keepTrackOfFrequentSongs = !keepTrackOfFrequentSongs;
                keepTrackOfFrequentSongsSwitch.setText( (keepTrackOfFrequentSongs) ? "Keeping track of frequent songs" : "Not keeping track of frequent songs" );
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("keepTrackOfFrequentSongs",keepTrackOfFrequentSongs);
                editor.apply();
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
    public void getMessage(Enum message, int position, SongItem item, Bundle bundle){

    }

    public void thumbnailUpdated(SongItem songItem){

    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Settings");
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }

    class deleteDatabaseThread implements Runnable{
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            AppDatabase db = Room.databaseBuilder(requireContext(),
                    AppDatabase.class, "AppDatabase").build();
            db.clearAllTables();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"Database Successfully Deleted",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
