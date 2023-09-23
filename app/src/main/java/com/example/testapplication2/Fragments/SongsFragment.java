package com.example.testapplication2.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Enums.EnumMessages;
import com.example.testapplication2.Interfaces.AccessToMainActivity;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.R;
import com.example.testapplication2.RecyclerViewAdapters.songsFragmentRecyclerViewAdapter;
import com.example.testapplication2.RecyclerViewItemDecorations.songsFragmentRecyclerViewItemDecoration;
import com.example.testapplication2.SelectLetterActivity;
import com.example.testapplication2.SortByActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.testapplication2.Enums.EnumMessages.ATTACH_ITEM_TOUCH_LISTENER;
import static com.example.testapplication2.Enums.EnumMessages.SCROLL_TO_TOP;
import static com.example.testapplication2.Enums.EnumMessages.SELECT_LETTER_CRITERIA_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.SORT_CRITERIA_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_ITEM_CHANGED_THUMBNAIL;
import static com.example.testapplication2.Enums.EnumMessages.SET_TOOLBAR_ICON;
import static com.example.testapplication2.Enums.EnumMessages.SET_TOOLBAR_ICON_TO_DEFAULT;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_SELECTED_ITEMS;
import static com.example.testapplication2.MainActivity.AllSongsExceptArchived;
import static com.example.testapplication2.MainActivity.SelectedSongs;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.dpToPixels;
import static com.example.testapplication2.MainActivity.sortSongsByMode;
import static com.example.testapplication2.MainActivity.sortSongsByWhat;
import static com.example.testapplication2.MainActivity.updateSongsRecyclerViewItemConstraintLayout;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;

public class SongsFragment extends MyFragmentSuperClass {


    public static String name = "SongsFragment";
    RecyclerView songsFragmentRecyclerView;
    songsFragmentRecyclerViewItemDecoration itemDecoration;
    public ArrayList<SongItem> songsFragmentAllSongs = new ArrayList<>();
    ArrayList<SongItem> songsFragmentAllSongsParent = new ArrayList<>();
    songsFragmentRecyclerViewAdapter songsRecyclerViewAdapter;
    MainActivity mainActivity;
    ConstraintLayout songsFragmentConstraintLayout;

    EditText editTextSearchSongsFragment;
    ImageView clearEditTextButtonSongsFragment;
    String beforeText = "";

    LinearLayoutManager linearLayoutManager;
    TextView Letter;
    String alphabet23 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    ImageView sortByImageView;

    private OnItemTouchListener itemTouchListener = new OnItemTouchListener(){
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            return super.onInterceptTouchEvent(rv, e);
        }
    };

    private RecyclerView.ViewHolder previousViewHolderForSelecting = null;

    boolean movingUp = false;
    boolean movingDown = false;
    float initialY = 0;
    float finalY = 0;

    public SongsFragment() {
        super("SongsFragment");
        weakReferencesToFragments.saveFragment(2,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.songs_fragment, container, false);
        Letter = rootView.findViewById(R.id.Letter);
        Letter.setOnClickListener(letterOnClickListener);

        sortByImageView = rootView.findViewById(R.id.sortByImageView);
        sortByImageView.setOnClickListener(sortByImageViewOnClickListener);

        editTextSearchSongsFragment = rootView.findViewById(R.id.editTextSearchSongsFragment);
        editTextSearchSongsFragment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeText = charSequence.toString().trim();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String editTextString = editable.toString().trim();
                if (!editTextString.equals(beforeText)) {
                    songsRecyclerViewAdapter.getFilter().filter(editTextString);
                    songsFragmentRecyclerView.scrollToPosition(0);
                }
            }
        });

        clearEditTextButtonSongsFragment = rootView.findViewById(R.id.clearEditTextButtonSongsFragment);
        clearEditTextButtonSongsFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextSearchSongsFragment.setText("");
            }
        });

        mainActivity = ((MainActivity) requireActivity());
        songsFragmentConstraintLayout = rootView.findViewById(R.id.playlistsFragmentConstraintLayout);
        songsFragmentRecyclerView = rootView.findViewById(R.id.songsFragmentRecyclerView);

        linearLayoutManager = new LinearLayoutManager(requireContext());
        songsFragmentRecyclerView.setLayoutManager(linearLayoutManager);
        songsRecyclerViewAdapter = new songsFragmentRecyclerViewAdapter(requireContext(),songsFragmentConstraintLayout,songsFragmentAllSongs,songsFragmentAllSongsParent,mainActivity);
        songsFragmentRecyclerView.setAdapter(songsRecyclerViewAdapter);
        itemDecoration = new songsFragmentRecyclerViewItemDecoration(songsFragmentRecyclerView,songsFragmentAllSongs,Letter);
        songsFragmentRecyclerView.addItemDecoration(itemDecoration);
        songsFragmentRecyclerView.setItemViewCacheSize(2);

        getMessage(EnumMessages.NOTIFY_DATA_SET_CHANGED, -1, null, null);

        Letter.setTextColor(baseColor);
        DrawableCompat.setTint(sortByImageView.getDrawable(),baseColor);
        DrawableCompat.setTint(clearEditTextButtonSongsFragment.getDrawable(),baseColor);

        mainActivity.scrollToTop.setVisible(songsFragmentAllSongs.size()>0);

        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        mainActivity.scrollToTop.setVisible(!hidden && songsFragmentAllSongs.size()>0);
        if (!hidden) {
            if (SelectedSongs.size()>0) {
                weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
            }

            if (updateSongsRecyclerViewItemConstraintLayout) {
                updateSelectedItems();
            }
        }else {
            weakReferencesToFragments.getMessage(MainActivity.name,SET_TOOLBAR_ICON_TO_DEFAULT,-1,null);
        }
    }

    private final View.OnClickListener sortByImageViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(requireContext(), SortByActivity.class);
            startActivity(intent);
        }
    };

    private final View.OnClickListener letterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(requireContext(), SelectLetterActivity.class);
            startActivity(intent);
        }
    };

    public void updateSelectedItems(){
        RecyclerView.LayoutManager layout = songsFragmentRecyclerView.getLayoutManager();
        int lastVisiblePosition = 0;
        int firstVisiblePosition = 0;
        if (layout instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layout;
            firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
        }else {
            firstVisiblePosition = 4;
            lastVisiblePosition = -5;//so that for loop doesn't run.
        }

        for (int i = firstVisiblePosition - 4; i <= lastVisiblePosition + 4; i = i + 1) { // when recyclerview is scrolled to one end, the items cached on the other end is 2*itemviewcachesize. (Checked by me, not documentation)
            if (i >= 0 && i < songsFragmentAllSongs.size()) {
                songsRecyclerViewAdapter.notifyItemChanged(i, R.id.listItemSongsConstraintLayout);
            }
        }

        updateSongsRecyclerViewItemConstraintLayout = false;
    }

    @Override
    public void getMessage(Enum message, int position, SongItem item, Bundle bundle){
        if (EnumMessages.NOTIFY_DATA_SET_CHANGED.equals(message)){
            if (AllSongsExceptArchived !=null){
                songsFragmentAllSongs.clear();
                songsFragmentAllSongs.addAll(AllSongsExceptArchived);
                songsFragmentAllSongsParent.clear();
                songsFragmentAllSongsParent.addAll(AllSongsExceptArchived);

                SortSongs(sortSongsByWhat,sortSongsByMode,songsFragmentAllSongs);
                SortSongs(sortSongsByWhat,sortSongsByMode,songsFragmentAllSongsParent);

                Letter.setText("");
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) Letter.getLayoutParams();
                if (sortSongsByWhat==2) {
                    layoutParams.startToStart = R.id.playlistsFragmentConstraintLayout;
                    layoutParams.endToEnd = R.id.playlistsFragmentConstraintLayout;
                    layoutParams.leftMargin = (int) (20*dpToPixels);
                }else {
                    layoutParams.startToStart = R.id.guideline2;
                    layoutParams.endToEnd = R.id.guideline2;
                    layoutParams.leftMargin = 0;
                }
            }
            songsRecyclerViewAdapter.notifyDataSetChanged();
        }else if (NOTIFY_ITEM_CHANGED_THUMBNAIL.equals(message)){
            int index = songsFragmentAllSongs.indexOf(item);
            if (index>=0 && index<songsFragmentAllSongs.size()) {
                songsRecyclerViewAdapter.notifyItemChanged(index, R.id.thumbnailSongs);
            }
        }else if (UPDATE_SELECTED_ITEMS.equals(message)){
            updateSelectedItems();
        }else if (UPDATE_ITEM_COLORS.equals(message)){
            Letter.setTextColor(baseColor);
            DrawableCompat.setTint(sortByImageView.getDrawable(),baseColor);
            DrawableCompat.setTint(clearEditTextButtonSongsFragment.getDrawable(),baseColor);
            songsFragmentRecyclerViewItemDecoration.setPaintColor();
            songsFragmentRecyclerView.invalidateItemDecorations();
        }else if (ATTACH_ITEM_TOUCH_LISTENER.equals(message)){
            songsFragmentRecyclerView.addOnItemTouchListener(itemTouchListener);
        }else if (SORT_CRITERIA_CHANGED.equals(message)){

            SortSongs(sortSongsByWhat,sortSongsByMode,songsFragmentAllSongs);
            SortSongs(sortSongsByWhat, sortSongsByMode, songsFragmentAllSongsParent);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) Letter.getLayoutParams();
            if (sortSongsByWhat==2) {
                Letter.setText("");
                layoutParams.startToStart = R.id.playlistsFragmentConstraintLayout;
                layoutParams.endToEnd = R.id.playlistsFragmentConstraintLayout;
                layoutParams.leftMargin = (int) (20*dpToPixels);
            }else {
                Letter.setText("");
                layoutParams.startToStart = R.id.guideline2;
                layoutParams.endToEnd = R.id.guideline2;
                layoutParams.leftMargin = 0;
            }
        }else if (SELECT_LETTER_CRITERIA_CHANGED.equals(message)){
            letterSelected(bundle);
        }else if (SCROLL_TO_TOP.equals(message)){
            songsFragmentRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void thumbnailUpdated(SongItem songItem){
        int index = songsFragmentAllSongs.indexOf(songItem);
        if (index>=0 && index<songsFragmentAllSongs.size()){
            songsRecyclerViewAdapter.notifyItemChanged(index,R.id.thumbnailSongs);
        }
    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Songs");
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }

    public void SortSongs(int sortByWhat, int sortByMode, ArrayList<SongItem> songItemArrayList){

        if (sortByWhat==0){
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            ArrayList<SongItem> temporary = new ArrayList<>();
            SongItem song;
            for (int i = 0;i<songItemArrayList.size();++i){
                song = songItemArrayList.get(i);
                if (!alphabet.contains(song.getSongName().substring(0,1))){
                    temporary.add(song);
                    songItemArrayList.remove(i);
                    --i;
                }
            }

            temporary.sort(new Comparator<SongItem>() {
                @Override
                public int compare(SongItem songItem, SongItem t1) {
                    return songItem.getSongName().compareToIgnoreCase(t1.getSongName());
                }
            });

            songItemArrayList.sort(new Comparator<SongItem>() {
                @Override
                public int compare(SongItem songItem, SongItem t1) {
                    return songItem.getSongName().compareToIgnoreCase(t1.getSongName());
                }
            });

            songItemArrayList.addAll(0,temporary);
            temporary.clear();
        }else  if (sortByWhat==1){
            songItemArrayList.sort(new Comparator<SongItem>() {
                @Override
                public int compare(SongItem songItem, SongItem t1) {
                    return Long.compare(songItem.getDuration(), t1.getDuration());
                }
            });
        }else if (sortByWhat==2){
            songItemArrayList.sort(new Comparator<SongItem>() {
                @Override
                public int compare(SongItem songItem, SongItem t1) {
                    return Long.compare(t1.getCreationTimeSinceEpochInMillis(), songItem.getCreationTimeSinceEpochInMillis());
                }
            });
        }else if (sortByWhat==3){
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            songItemArrayList.sort(new Comparator<SongItem>() {
                @Override
                public int compare(SongItem songItem, SongItem t1) {
                    if (songItem.getFavScore()==t1.getFavScore()){
                        String sName = songItem.getSongName();
                        String tName = t1.getSongName();
                        int sLength=sName.length();
                        int tLength=tName.length();
                        int minLength = Math.min(sLength,tLength);
                        char sChar;
                        char tChar;
                        boolean sContains;
                        boolean tContains;
                        for (int i=0;i<minLength;++i){
                            sChar=sName.charAt(i);
                            tChar=tName.charAt(i);
                            if (Character.toLowerCase(sChar)!=Character.toLowerCase(tChar)){
                                sContains=alphabet.indexOf(sChar)!=-1;
                                tContains=alphabet.indexOf(tChar)!=-1;
                                if ((sContains&&tContains) || (!sContains&&!tContains)){
                                    return sName.compareToIgnoreCase(tName);
                                }else if (sContains){
                                    return 1;
                                }else {
                                    return -1;
                                }
                            }
                        }
                        return Integer.compare(sLength,tLength);
                    }else {
                        return Float.compare(t1.getFavScore(), songItem.getFavScore());
                    }
                }
            });
        }

        if (sortByMode==1){
            Collections.reverse(songItemArrayList);
        }

        songsRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void letterSelected(Bundle bundle){
        int scrollPos = songsFragmentAllSongs.size() - 1;
        if (sortSongsByWhat==0) {
            String letter = bundle.getString("Letter");
            if (sortSongsByWhat == 0) {
                if ("#".equals(letter)) {
                    if (sortSongsByMode == 0) {
                        scrollPos = 0;
                    }
                } else {
                    int alphabetIndex = alphabet23.indexOf(letter);
                    for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                        int indexOfThis = alphabet23.indexOf(songsFragmentAllSongs.get(i).getSongName().substring(0, 1).toUpperCase());
                        if ((indexOfThis >= alphabetIndex && sortSongsByMode == 0) || (indexOfThis <= alphabetIndex && sortSongsByMode == 1)) {
                            scrollPos = i;
                            break;
                        }
                    }
                }
            }
        }else if (sortSongsByWhat==1){
            String duration = bundle.getString("Duration");
            if ("0-1 min".equals(duration)){
                if (sortSongsByMode==0){scrollPos=0;}
                else {
                    for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                        if ((songsFragmentAllSongs.get(i).getDuration() <= 60000)) {
                            scrollPos = i;
                            break;
                        }
                    }
                }
            }else if ("1-2 min".equals(duration)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    int durationOfThis = songsFragmentAllSongs.get(i).getDuration();
                    if ((durationOfThis >= 60000 && sortSongsByMode == 0) || (durationOfThis <= 120000 && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else if ("2-3 min".equals(duration)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    int durationOfThis = songsFragmentAllSongs.get(i).getDuration();
                    if ((durationOfThis >= 120000 && sortSongsByMode == 0) || (durationOfThis <= 180000 && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else if ("3-4 min".equals(duration)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    int durationOfThis = songsFragmentAllSongs.get(i).getDuration();
                    if ((durationOfThis >= 180000 && sortSongsByMode == 0) || (durationOfThis <= 240000 && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else if ("4-5 min".equals(duration)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    int durationOfThis = songsFragmentAllSongs.get(i).getDuration();
                    if ((durationOfThis >= 240000 && sortSongsByMode == 0) || (durationOfThis <= 300000 && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else {
                if (sortSongsByMode==1){scrollPos=0;}
                else {
                    for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                        if ((songsFragmentAllSongs.get(i).getDuration() >= 300000)) {
                            scrollPos = i;
                            break;
                        }
                    }
                }
            }
        }else if (sortSongsByWhat==2){
            String lastAdded = bundle.getString("LastAdded");
            long timeNow = System.currentTimeMillis();
            if ("Last two weeks".equals(lastAdded)){
                if (sortSongsByMode==0){scrollPos=0;}
                else {
                    for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                        if (((timeNow - songsFragmentAllSongs.get(i).getCreationTimeSinceEpochInMillis()) <= 1000*60*60*24*7*2L)) {
                            scrollPos = i;
                            break;
                        }
                    }
                }
            }else if ("Last 30 days".equals(lastAdded)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    long timeAfterAdding = timeNow-songsFragmentAllSongs.get(i).getCreationTimeSinceEpochInMillis();
                    if ((timeAfterAdding >= 1000*60*60*24*7*2L && sortSongsByMode == 0) || (timeAfterAdding <= 1000*60*60*24* 30L && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else if ("Six months".equals(lastAdded)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    long timeAfterAdding = timeNow-songsFragmentAllSongs.get(i).getCreationTimeSinceEpochInMillis();
                    if ((timeAfterAdding >= 1000*60*60*24* 30L && sortSongsByMode == 0) || (timeAfterAdding <= 1000*60*60*24* 180L && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else if ("This year".equals(lastAdded)){
                for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                    long timeAfterAdding = timeNow-songsFragmentAllSongs.get(i).getCreationTimeSinceEpochInMillis();
                    if ((timeAfterAdding >= 1000*60*60*24* 180L && sortSongsByMode == 0) || (timeAfterAdding <= 1000*60*60*24* 365L && sortSongsByMode == 1)) {
                        scrollPos = i;
                        break;
                    }
                }
            }else {
                if (sortSongsByMode==1){scrollPos=0;}
                else {
                    for (int i = 0; i < songsFragmentAllSongs.size(); ++i) {
                        if (((timeNow-songsFragmentAllSongs.get(i).getCreationTimeSinceEpochInMillis()) >= 1000*60*60*24* 365L)) {
                            scrollPos = i;
                            break;
                        }
                    }
                }
            }
        }else if (sortSongsByWhat==3) {
            String percentile = bundle.getString("Favourite");
            if ("75%-100%".equals(percentile)) {
                if (sortSongsByMode == 0) {
                    scrollPos = 0;
                }else if (sortSongsByMode==1){
                    scrollPos = songsFragmentAllSongs.size()*3/4;
                }
            }else if ("50%-75%".equals(percentile)) {
                if (sortSongsByMode == 0) {
                    scrollPos = songsFragmentAllSongs.size()/4;
                }else if (sortSongsByMode==1){
                    scrollPos = songsFragmentAllSongs.size()/2;
                }
            }else if ("25%-50%".equals(percentile)) {
                if (sortSongsByMode == 0) {
                    scrollPos = songsFragmentAllSongs.size()/2;
                }else if (sortSongsByMode==1){
                    scrollPos = songsFragmentAllSongs.size()/4;
                }
            }else if ("0%-50%".equals(percentile)) {
                if (sortSongsByMode == 0) {
                    scrollPos = songsFragmentAllSongs.size()*3/4;
                }else if (sortSongsByMode==1){
                    scrollPos = 0;
                }
            }
        }
        if (songsFragmentAllSongs.size()>0)
            linearLayoutManager.scrollToPositionWithOffset(scrollPos, 0);
    }


    abstract public class OnItemTouchListener implements RecyclerView.OnItemTouchListener{
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            RecyclerView.LayoutManager layout = rv.getLayoutManager();
            int action = e.getAction();
            if (action==MotionEvent.ACTION_DOWN || action==MotionEvent.ACTION_POINTER_DOWN || action==MotionEvent.ACTION_MOVE){
                finalY = e.getY();
                View v = rv.findChildViewUnder(e.getX(),e.getY());
                if (v!=null) {
                    songsFragmentRecyclerViewAdapter.ViewHolder vwhldr = (songsFragmentRecyclerViewAdapter.ViewHolder) rv.getChildViewHolder(v);
                    int pos = vwhldr.getAdapterPosition();
                    if (previousViewHolderForSelecting==null){
                        previousViewHolderForSelecting=vwhldr;
                    }else if (!vwhldr.equals(previousViewHolderForSelecting) || (movingUp != movingDown && movingUp != finalY<initialY)) {
                        if (SelectedSongs.size()>0){
                            SongItem songItem = songsFragmentAllSongs.get(pos);
                            if (SelectedSongs.contains(songItem)) {
                                boolean wasAllSelected = SelectedSongs.size()==songsFragmentAllSongs.size();
                                SelectedSongs.remove(songItem.setIsSelected(false));
                                if (SelectedSongs.size()==0){
                                    ((AccessToMainActivity) mainActivity).IsSelectingSongs(false);
                                }
                                songsFragmentRecyclerViewAdapter.selectViewHolder(vwhldr,false);
                                if (wasAllSelected){
                                    weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                                }
                            } else {
                                SelectedSongs.add(songItem.setIsSelected(true));
                                songsFragmentRecyclerViewAdapter.selectViewHolder(vwhldr,true);
                            }
                            if (SelectedSongs.size()==songsFragmentAllSongs.size()){
                                weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                            }
                        }else {
                            ((AccessToMainActivity) mainActivity).IsSelectingSongs(true);
                            SelectedSongs.add(songsFragmentAllSongs.get(pos).setIsSelected(true));
                            songsFragmentRecyclerViewAdapter.selectViewHolder(vwhldr,true);
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                        if (SelectedSongs.size()>0) {
                            mainActivity.toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
                            if (SelectedSongs.size()==songsFragmentAllSongs.size()){
                                weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                            }
                        } else {
                            mainActivity.toolbar.setSubtitle(null);
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                        previousViewHolderForSelecting =vwhldr;
                    }
                }
                if (initialY!=0){movingUp = finalY<initialY;movingDown = !movingUp;}
                initialY=finalY;
                return true;
            }else if (action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_POINTER_UP){
                previousViewHolderForSelecting = null;
                movingUp = false;
                movingDown = false;
                initialY = 0;
                finalY = 0;
                rv.removeOnItemTouchListener(this);
                return false;
            }else {
                return true;
            }
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            RecyclerView.LayoutManager layout = rv.getLayoutManager();
            int action = e.getAction();
            System.out.println("the action: "+action);
            if (action==MotionEvent.ACTION_DOWN || action==MotionEvent.ACTION_POINTER_DOWN || action==MotionEvent.ACTION_MOVE){
                finalY = e.getY();
                View v = rv.findChildViewUnder(e.getX(),e.getY());
                if (v!=null) {
                    songsFragmentRecyclerViewAdapter.ViewHolder vwhldr = (songsFragmentRecyclerViewAdapter.ViewHolder) rv.getChildViewHolder(v);
                    int pos = vwhldr.getAdapterPosition();
                    if (previousViewHolderForSelecting==null){
                        previousViewHolderForSelecting=vwhldr;
                    }else if (!vwhldr.equals(previousViewHolderForSelecting) || (movingUp != movingDown && movingUp != finalY<initialY)) {
                        if (SelectedSongs.size()>0){
                            SongItem songItem = songsFragmentAllSongs.get(pos);
                            if (SelectedSongs.contains(songItem)) {
                                boolean wasAllSelected = SelectedSongs.size()==songsFragmentAllSongs.size();
                                SelectedSongs.remove(songItem.setIsSelected(false));
                                if (SelectedSongs.size()==0){
                                    ((AccessToMainActivity) mainActivity).IsSelectingSongs(false);
                                }
                                songsFragmentRecyclerViewAdapter.selectViewHolder(vwhldr,false);
                                if (wasAllSelected){
                                    weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                                }
                            } else {
                                SelectedSongs.add(songItem.setIsSelected(true));
                                songsFragmentRecyclerViewAdapter.selectViewHolder(vwhldr,true);
                            }
                            if (SelectedSongs.size()==songsFragmentAllSongs.size()){
                                weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                            }
                        }else {
                            ((AccessToMainActivity) mainActivity).IsSelectingSongs(true);
                            SelectedSongs.add(songsFragmentAllSongs.get(pos).setIsSelected(true));
                            songsFragmentRecyclerViewAdapter.selectViewHolder(vwhldr,true);
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                        if (SelectedSongs.size()>0) {
                            mainActivity.toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
                            if (SelectedSongs.size()==songsFragmentAllSongs.size()){
                                weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                            }
                        } else {
                            mainActivity.toolbar.setSubtitle(null);
                            weakReferencesToFragments.getMessage(MainActivity.name, SET_TOOLBAR_ICON, -1, null);
                        }
                        previousViewHolderForSelecting =vwhldr;
                    }
                }
                if (initialY!=0){movingUp = finalY<initialY;movingDown = !movingUp;}
                initialY=finalY;
                System.out.println(movingUp);
            }else if (action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_POINTER_UP){
                previousViewHolderForSelecting = null;
                movingUp = false;
                movingDown = false;
                initialY = 0;
                finalY = 0;
                rv.removeOnItemTouchListener(this);
            }
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
