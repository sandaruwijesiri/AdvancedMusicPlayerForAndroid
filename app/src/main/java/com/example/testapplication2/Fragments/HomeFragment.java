package com.example.testapplication2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.R;
import com.example.testapplication2.RecyclerViewAdapters.homeFragmentRecyclerViewAdapter;

import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.displayWidthInPixels;
import static com.example.testapplication2.MainActivity.dpToPixels;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class HomeFragment extends MyFragmentSuperClass {

    public static String name = "HomeFragment";
    MainActivity mainActivity;
    RecyclerView homeRecyclerView;
    homeFragmentRecyclerViewAdapter homeFragmentRecyclerViewAdapter;

    StringBuilder stringBuilder;

    public HomeFragment() {
        super("HomeFragment");
        weakReferencesToFragments.saveFragment(1,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment,container,false);

        mainActivity = (MainActivity) requireActivity();

        homeRecyclerView = rootView.findViewById(R.id.homeFragmentRecyclerView);
        homeRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        homeFragmentRecyclerViewAdapter = new homeFragmentRecyclerViewAdapter(requireContext());
        homeRecyclerView.setAdapter(homeFragmentRecyclerViewAdapter);
        homeRecyclerView.setPadding((int) ((displayWidthInPixels / 2) - (150 * dpToPixels)),0, (int) ((displayWidthInPixels / 2) - (150 * dpToPixels)),0);

        stringBuilder = new StringBuilder();

        return  rootView;
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
        if (UPDATE_ITEM_COLORS.equals(message)){
            homeFragmentRecyclerViewAdapter.updateHomeRecyclerViewItemColors();
        }
    }

    public void thumbnailUpdated(SongItem songItem){

    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Home");
        mainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }
}
