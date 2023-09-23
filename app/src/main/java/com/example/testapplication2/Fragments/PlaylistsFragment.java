package com.example.testapplication2.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Interfaces.DraggingItems;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.R;
import com.example.testapplication2.RecyclerViewAdapters.playlistsFragmentRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static com.example.testapplication2.Enums.EnumMessages.CREATE_NEW_PLAYLIST;
import static com.example.testapplication2.Enums.EnumMessages.PLAYLIST_DATA_REWRITTEN;
import static com.example.testapplication2.Enums.EnumMessages.PLAYLIST_RENAMED;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_DATA_SET_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_ITEM_CHANGED_PLAYLISTS_THUMBNAIL;
import static com.example.testapplication2.Enums.EnumMessages.PLAYLIST_CREATED;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.MainActivity.allPlaylists;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.displayWidthInPixels;
import static com.example.testapplication2.MainActivity.dpToPixels;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class PlaylistsFragment extends MyFragmentSuperClass implements DraggingItems {

    public static String name = "PlaylistsFragment";
    RecyclerView playlistsRecyclerView;
    playlistsFragmentRecyclerViewAdapter playlistsRecyclerViewAdapter;
    GridLayoutManager gridLayoutManager;
    MainActivity mainActivity;
    ConstraintLayout playlistsFragmentConstraintLayout;
    ItemTouchHelper playlistsItemTouchHelper;
    FloatingActionButton newPlaylistFAB;

    public PlaylistsFragment() {
        super("PlaylistsFragment");
        weakReferencesToFragments.saveFragment(3,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playlists_fragment,container,false);

        mainActivity = ((MainActivity) requireActivity());
        playlistsFragmentConstraintLayout = rootView.findViewById(R.id.playlistsFragmentConstraintLayout);
        newPlaylistFAB = rootView.findViewById(R.id.newPlaylistFAB);
        playlistsRecyclerView = rootView.findViewById(R.id.playlistsRecyclerView);
        playlistsRecyclerView.setPadding((int) ((displayWidthInPixels / 2) - (140 * dpToPixels)),0, (int) ((displayWidthInPixels / 2) - (140 * dpToPixels)), (int) (103*dpToPixels));

        gridLayoutManager = new GridLayoutManager(requireContext(),2);
        playlistsRecyclerView.setLayoutManager(gridLayoutManager);
        playlistsRecyclerViewAdapter = new playlistsFragmentRecyclerViewAdapter(mainActivity,requireContext(),playlistsFragmentConstraintLayout);
        playlistsRecyclerView.setAdapter(playlistsRecyclerViewAdapter);

        newPlaylistFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weakReferencesToFragments.getMessage(MainActivity.name, CREATE_NEW_PLAYLIST,-1,null);
            }
        });
        newPlaylistFAB.setBackgroundTintList(ColorStateList.valueOf(baseColor));

        playlistsItemTouchHelper = new ItemTouchHelper(playlistsCallback);
        playlistsItemTouchHelper.attachToRecyclerView(playlistsRecyclerView);

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
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){
        if(PLAYLIST_CREATED.equals(message)){
            playlistsRecyclerViewAdapter.notifyItemInserted(allPlaylists.size()-1);
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(requireContext()){
                @Override
                protected int calculateTimeForScrolling(int dx) {
                    return 100;
                }

                @Override
                protected int calculateTimeForDeceleration(int dx) {
                    return 100;
                }
            };
            smoothScroller.setTargetPosition(allPlaylists.size()-1);
            gridLayoutManager.startSmoothScroll(smoothScroller);
        }else if (NOTIFY_DATA_SET_CHANGED.equals(message)){
            playlistsRecyclerViewAdapter.notifyDataSetChanged();
        }else if (PLAYLIST_RENAMED.equals(message)){
            playlistsRecyclerViewAdapter.notifyItemChanged(position,R.id.playlistName);
        }else if (PLAYLIST_DATA_REWRITTEN.equals(message)){
            playlistsRecyclerViewAdapter.notifyItemChanged(position,R.id.playlistNoOfTracks);
            playlistsRecyclerViewAdapter.notifyItemChanged(position,R.id.playlistDuration);
            playlistsRecyclerViewAdapter.notifyItemChanged(position,R.id.playlistImageView);
        }else if (NOTIFY_ITEM_CHANGED_PLAYLISTS_THUMBNAIL.equals(message)){
            for (int i=0;i<allPlaylists.size();++i){
                playlistsRecyclerViewAdapter.notifyItemChanged(i,R.id.playlistImageView);
            }
        }else if (UPDATE_ITEM_COLORS.equals(message)){
            newPlaylistFAB.setBackgroundTintList(ColorStateList.valueOf(baseColor));
        }
    }

    public void thumbnailUpdated(SongItem songItem){
        for (int i=0;i<allPlaylists.size();++i){
            if (allPlaylists.get(i).getSongItemsPlaylist().size()>0 && allPlaylists.get(i).getSongItemsPlaylist().get(0)==songItem){
                playlistsRecyclerViewAdapter.notifyItemChanged(i,R.id.thumbnailSongs);
            }
        }
    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Playlists");
        mainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }


    ItemTouchHelper.Callback playlistsCallback = new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | DOWN | LEFT | RIGHT, 0);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromposition = viewHolder.getAdapterPosition();
            int toposition = target.getAdapterPosition();

            if (toposition > fromposition){

                allPlaylists.add(toposition + 1,allPlaylists.get(fromposition));
                allPlaylists.remove(fromposition);

            }else if (toposition < fromposition){

                allPlaylists.add(toposition,allPlaylists.get(fromposition));
                allPlaylists.remove(fromposition + 1);

            }

            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromposition, toposition);

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            if (viewSizeOutOfBounds > 0){
                if (msSinceStartScroll < 1000){
                    return 1;//viewSizeOutOfBounds;
                }else {
                    return 30;
                }
            }else{
                if (msSinceStartScroll < 1000){
                    return -1;//viewSizeOutOfBounds;
                }else {
                    return -30;
                }
            }
            //return super.interpolateOutOfBoundsScroll(recyclerView,viewSize,viewSizeOutOfBounds,totalSize,msSinceStartScroll);
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            //viewHolder.itemView.setBackgroundColor(Color.parseColor("#555555"));
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

            File file = new File(requireContext().getDir("Preferences", MODE_PRIVATE),"PlaylistsOrder");
            StringBuilder writeThis = new StringBuilder();

            for (PlaylistItem playlistItem:allPlaylists){
                writeThis.append(playlistItem.getPlaylistName()).append("\n");
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file,false);
                fileOutputStream.write(writeThis.toString().getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                Methods.addToErrorLog(e.getMessage(),getContext());
                e.printStackTrace();
            }


        }
    };

    @Override
    public void StartDrag(int position) {
        playlistsItemTouchHelper.startDrag(Objects.requireNonNull(playlistsRecyclerView.findViewHolderForAdapterPosition(position)));
    }
}
