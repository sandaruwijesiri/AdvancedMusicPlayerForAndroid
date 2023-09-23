package com.example.testapplication2.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Interfaces.DraggingItems;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.R;
import com.example.testapplication2.RecyclerViewAdapters.specificPlaylistFragmentRecyclerViewAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static com.example.testapplication2.Enums.EnumMessages.PLAYLIST_DATA_REWRITTEN;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_DATA_SET_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.SCROLL_TO_TOP;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.MainActivity.SelectedSongs;
import static com.example.testapplication2.MainActivity.allPlaylists;
import static com.example.testapplication2.MainActivity.archivePlaylistItem;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.rewritePlaylistData;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.MainActivity.updateSongsRecyclerViewItemConstraintLayout;
import static com.example.testapplication2.MainActivity.AllSongsExceptArchived;

public class SpecificPlaylistFragment extends MyFragmentSuperClass implements DraggingItems {

    public static String name = "SpecificPlaylistFragment";
    MainActivity mainActivity;
    ConstraintLayout specificPlaylistFragmentConstraintLayout;
    RecyclerView specificPlaylistRecyclerView;
    specificPlaylistFragmentRecyclerViewAdapter specificPlaylistRecyclerViewAdapter;
    ItemTouchHelper itemTouchHelper;
    CardView addToPlaylistCard;
    TextView addToPlaylistTextView;
    public static PlaylistItem previousPlaylist;

    public static boolean isArtistItem = false;

    public SpecificPlaylistFragment() {
        super("SpecificPlaylistFragment");
        weakReferencesToFragments.saveFragment(6,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.specific_playlist_fragment,container,false);

        mainActivity = ((MainActivity) requireActivity());
        addToPlaylistCard = rootView.findViewById(R.id.addToPlaylistCard);
        addToPlaylistTextView = rootView.findViewById(R.id.addToPlaylistTextView);


        if (currentPlaylist == archivePlaylistItem){
            addToPlaylistTextView.setText("Add To Archive");
        }else {
            addToPlaylistTextView.setText("Add To Playlist");
        }

        if (!isArtistItem && SelectedSongs.size()>0){
            addToPlaylistCard.setVisibility(View.VISIBLE);
        }else {
            addToPlaylistCard.setVisibility(View.GONE);
        }

        addToPlaylistCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedSongs.size()>0) {
                    boolean isArchive = currentPlaylist == archivePlaylistItem;
                    if (!isArchive) {
                        currentPlaylist.getSongItemsPlaylist().addAll(SelectedSongs);
                        rewritePlaylistData(true);
                        SelectedSongs.clear();
                        updateSongsRecyclerViewItemConstraintLayout = true;
                        ((MainActivity) weakReferencesToFragments.mainActivity.get()).IsSelectingSongs(false);
                    }else {
                        rewriteArchiveData();
                    }
                    specificPlaylistRecyclerViewAdapter.notifyDataSetChanged();
                    addToPlaylistCard.setVisibility(View.GONE);
                }
            }
        });

        specificPlaylistFragmentConstraintLayout = rootView.findViewById(R.id.specificPlaylistFragmentConstraintLayout);
        specificPlaylistRecyclerView = rootView.findViewById(R.id.specificPlaylistRecyclerView);
        specificPlaylistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        specificPlaylistRecyclerViewAdapter = new specificPlaylistFragmentRecyclerViewAdapter(mainActivity,requireContext(),specificPlaylistFragmentConstraintLayout, this, currentPlaylist);
        specificPlaylistRecyclerView.setAdapter(specificPlaylistRecyclerViewAdapter);

        itemTouchHelper = new ItemTouchHelper(callback);
        if (!isArtistItem) {
            itemTouchHelper.attachToRecyclerView(specificPlaylistRecyclerView);
        }

        mainActivity.scrollToTop.setVisible(currentPlaylist.getSongItemsPlaylist().size()>0);

        return rootView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mainActivity.scrollToTop.setVisible(!hidden && currentPlaylist.getSongItemsPlaylist().size()>0);
        if (!hidden){

            if (!isArtistItem && SelectedSongs.size()>0){
                addToPlaylistCard.setVisibility(View.VISIBLE);
            }else {
                addToPlaylistCard.setVisibility(View.GONE);
            }

            if (currentPlaylist == archivePlaylistItem){
                addToPlaylistTextView.setText("Add To Archive");
            }else {
                addToPlaylistTextView.setText("Add To Playlist");
            }

            specificPlaylistRecyclerViewAdapter.playlistItem = currentPlaylist;
            specificPlaylistRecyclerViewAdapter.notifyDataSetChanged();

            if (currentPlaylist!=previousPlaylist) {
                specificPlaylistRecyclerView.scrollToPosition(0);
            }

            if (isArtistItem){
                itemTouchHelper.attachToRecyclerView(null);
            }else {
                itemTouchHelper.attachToRecyclerView(specificPlaylistRecyclerView);
            }

        }else {
            if (rewritePlaylistData){
                rewritePlaylistData(false);
            }
            isArtistItem = false;
            specificPlaylistRecyclerViewAdapter.dismissSnackBars();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (rewritePlaylistData){
            rewritePlaylistData(false);
        }
        isArtistItem = false;
        specificPlaylistRecyclerViewAdapter.dismissSnackBars();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity.toolbar.setTitle("Playlists");
        isArtistItem = false;
        specificPlaylistRecyclerViewAdapter.dismissSnackBars();
    }

    @Override
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){
        if (SCROLL_TO_TOP.equals(message)){
            specificPlaylistRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void thumbnailUpdated(SongItem songItem){
        ArrayList<SongItem> playlist = currentPlaylist.getSongItemsPlaylist();
        System.out.println("ME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!:  " + currentPlaylist.getSongItemsPlaylist().size());
        for (int i=0;i<playlist.size();++i){
            System.out.println("I was called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            if (playlist.get(i)==songItem){
                specificPlaylistRecyclerViewAdapter.notifyItemChanged(i,R.id.thumbnailSongs);
            }
        }
    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle(currentPlaylist.getPlaylistName());
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }

    public void rewritePlaylistData(boolean isAddingToPlaylist){
        boolean isArchive = currentPlaylist ==archivePlaylistItem;
        String directoryName = "Playlists";
        if (isArchive) {
            directoryName = "Archive";
            weakReferencesToFragments.getMessage(SongsFragment.name, NOTIFY_DATA_SET_CHANGED,-1,null);
        }

        File playlistFile = new File(mainActivity.getDir(directoryName, MODE_PRIVATE), currentPlaylist.getPlaylistName() + ".ser");

        int duration;
        if (isAddingToPlaylist){
            duration = currentPlaylist.getDuration();
            for (SongItem songItem:SelectedSongs){
                duration = duration + songItem.getDuration();
                songItem.setIsSelected(false);
            }
        }else {
            duration = 0;
            for (SongItem songItem : currentPlaylist.getSongItemsPlaylist()) {
                duration = duration + songItem.getDuration();
            }
        }

        int durationInSeconds = duration / 1000;
        currentPlaylist.setDuration(duration);
        currentPlaylist.setDurationAsAString(
                theFormat.format(durationInSeconds / (60 * 60)) + ":" + theFormat.format(((durationInSeconds / 60) % 60)) + ":" + theFormat.format(durationInSeconds % (60)));

        try {
            FileOutputStream fileOut = new FileOutputStream(playlistFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(currentPlaylist);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            Methods.addToErrorLog(e.getMessage(),getContext());
            e.printStackTrace();
        }

        if (!isArchive) {
            weakReferencesToFragments.getMessage(PlaylistsFragment.name, PLAYLIST_DATA_REWRITTEN, allPlaylists.indexOf(currentPlaylist), null);
        }

        rewritePlaylistData = false;
    }

    public void rewriteArchiveData(){
        archivePlaylistItem.getSongItemsPlaylist().addAll(SelectedSongs);
        for (SongItem songItem:SelectedSongs){
            songItem.setIsSelected(false);
            AllSongsExceptArchived.remove(songItem);
        }
        SelectedSongs.clear();
        updateSongsRecyclerViewItemConstraintLayout = true;

        ((MainActivity) weakReferencesToFragments.mainActivity.get()).IsSelectingSongs(false);

        weakReferencesToFragments.getMessage(SongsFragment.name, NOTIFY_DATA_SET_CHANGED,-1,null);

        try {
            File playlistsOrderFile = new File(requireContext().getDir("Archive", MODE_PRIVATE), "Archive.ser");
            FileOutputStream fileOut = new FileOutputStream(playlistsOrderFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(archivePlaylistItem);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            Methods.addToErrorLog(e.getMessage(),getContext());
            e.printStackTrace();
        }
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | DOWN, LEFT | RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromposition = viewHolder.getAdapterPosition();
            int toposition = target.getAdapterPosition();

            Collections.swap(currentPlaylist.getSongItemsPlaylist() , fromposition, toposition);

            specificPlaylistRecyclerViewAdapter.notifyItemMoved(fromposition, toposition);
            System.out.println("QWERTYUIOP][';LKJHGFDSA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + fromposition + " : " + toposition);

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            if (direction==RIGHT || direction==LEFT){
                specificPlaylistRecyclerViewAdapter.removeItem(viewHolder.getAdapterPosition());
            }
        }

        @Override
        public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            if (viewSizeOutOfBounds > 0){
                if (msSinceStartScroll < 1000){
                    return viewSizeOutOfBounds;
                }else {
                    return 80;
                }
            }else{
                if (msSinceStartScroll < 1000){
                    return viewSizeOutOfBounds;
                }else {
                    return -80;
                }
            }
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            //viewHolder.itemView.setBackgroundColor(Color.parseColor("#555555"));
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            /*dragging = false;
            selectedsearchsongs.clear();
            selectedsearchsongs.addAll(selectedsongsalias);
            if (selectedsongscopy != null && selectedsongscopy.size()>0 && selectedsongscopy.get(0).getSongID().getPlaylist().equals(CurrentPlaylistName)) {
                selectedsongscopy.clear();
                selectedsongscopy.addAll(selectedsongsalias);
                currentsong = selectedsongscopy.indexOf(NowPlayingSong);
            }*/
        }
    };

    @Override
    public void StartDrag(int position) {
        itemTouchHelper.startDrag(Objects.requireNonNull(specificPlaylistRecyclerView.findViewHolderForAdapterPosition(position)));
    }
}
