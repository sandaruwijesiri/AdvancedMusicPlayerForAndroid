package com.example.testapplication2.Fragments;

import static com.example.testapplication2.MainActivity.artistArrayList;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.R;
import com.example.testapplication2.RecyclerViewAdapters.artistAdapter;

public class ArtistsFragment extends MyFragmentSuperClass {

    public static String name = "ArtistsFragment";
    MainActivity mainActivity;
    RecyclerView artistsRecyclerView;

    public ArtistsFragment() {
        super("ArtistsFragment");
        weakReferencesToFragments.saveFragment(9,this);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.artists_fragment,container,false);

        artistsRecyclerView = rootView.findViewById(R.id.artistsRecyclerView);

        mainActivity = ((MainActivity) requireActivity());

        artistsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        artistsRecyclerView.setAdapter(new artistAdapter(mainActivity,artistArrayList));

        return rootView;
    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Artists");
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }

    @Override
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){

    }

    public void thumbnailUpdated(SongItem songItem){

    }
}
