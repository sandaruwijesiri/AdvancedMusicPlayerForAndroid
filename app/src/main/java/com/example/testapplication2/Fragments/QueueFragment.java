package com.example.testapplication2.Fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.Interfaces.DraggingItems;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.Miscellaneous.QueueProgressCanvas;
import com.example.testapplication2.R;
import com.example.testapplication2.RecyclerViewAdapters.queueFragmentRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.DOWN;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static androidx.recyclerview.widget.ItemTouchHelper.UP;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_ITEM_INSERTED;
import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_SONG_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_THUMBNAIL_FOUND;
import static com.example.testapplication2.Enums.EnumMessages.QUEUE_DETAILS_UPDATED;
import static com.example.testapplication2.Enums.EnumMessages.SCROLL_TO_TOP;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_QUEUE_PROGRESS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_QUEUE;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_STOP_AFTER_THIS;
import static com.example.testapplication2.MainActivity.SelectedSongs;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.currentPlaylist;
import static com.example.testapplication2.MainActivity.displayWidthInPixels;
import static com.example.testapplication2.MainActivity.lowResDefaultBitmap;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.songPlayingWhenActivityStops;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.MainActivity.thumbnailPixels;
import static com.example.testapplication2.MainActivity.updateQueue;
import static com.example.testapplication2.MainActivity.updateSongsRecyclerViewItemConstraintLayout;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.ImageProcessor.getThumbnail;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;

public class QueueFragment extends MyFragmentSuperClass implements DraggingItems {

    public static String name = "QueueFragment";
    RecyclerView queueRecyclerView;
    queueFragmentRecyclerViewAdapter queueFragmentRecyclerViewAdapter;
    MainActivity mainActivity;

    ImageView BigImage;

    ImageView SmallImage;
    CardView SmallImageCardView;

    FloatingActionButton floatingActionButton;
    TextView DurationTextView;
    TextView NoOfTracksTextView;
    TextView ArtistsTextView;

    CardView addToQueueCard;

    ItemTouchHelper itemTouchHelper;
    MotionLayout QueueFragmentMotionLayout;

    Bitmap bitmapInImageView;

    QueueProgressCanvas queueProgressCanvas;

    public QueueFragment() {
        super("QueueFragment");
        weakReferencesToFragments.saveFragment(4,this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.queue_fragment,container,false);
/*
        ((MotionLayout) rootView).setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                //motionLayout.setInteractionEnabled(false);
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });*/

        mainActivity = ((MainActivity) requireActivity());
        BigImage = rootView.findViewById(R.id.QueueFragmentBigImage);
        SmallImage = rootView.findViewById(R.id.QueueFragmentSmallImage);
        SmallImageCardView = rootView.findViewById(R.id.QueueFragmentSmallImageCardView);
        queueProgressCanvas = rootView.findViewById(R.id.queueProgressCanvas);
        floatingActionButton = rootView.findViewById(R.id.floatingActionButtonQueue);
        DurationTextView = rootView.findViewById(R.id.durationQueueFragment);
        ArtistsTextView = rootView.findViewById(R.id.artistsQueueFragment);
        NoOfTracksTextView = rootView.findViewById(R.id.noOfTracksQueueFragment);
        queueRecyclerView = rootView.findViewById(R.id.queueFragmentRecyclerView);
        addToQueueCard = rootView.findViewById(R.id.addToQueueCard);
        QueueFragmentMotionLayout = rootView.findViewById(R.id.QueueFragmentMotionLayout);

        bitmapInImageView=lowResDefaultBitmap;

        if (SelectedSongs.size()>0){
            addToQueueCard.setVisibility(View.VISIBLE);
        }else {
            addToQueueCard.setVisibility(View.GONE);
        }

        addToQueueCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedSongs.size()>0) {
                    boolean bool = queueObject.QueueArray.size() == 0;
                    for (SongItem songItem : SelectedSongs) {
                        songItem.setIsSelected(false);
                    }
                    queueObject.addAll(SelectedSongs);

                    if (bool && queueObject.QueueArray.size() > 0) {
                        queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                    }
                    SelectedSongs.clear();
                    updateSongsRecyclerViewItemConstraintLayout = true;
                    ((MainActivity) weakReferencesToFragments.mainActivity.get()).IsSelectingSongs(false);
                    addToQueueCard.setVisibility(View.GONE);
                    queueFragmentRecyclerViewAdapter.notifyDataSetChanged();
                    SaveQueue(requireContext());
                }
            }
        });

        //QueueFragmentMotionLayout.getLayoutTransition().setDuration(10000);     //  Set animation properties of layoutTransitions(Changing visibility of addToQueueCard).

        getMessage(QUEUE_DETAILS_UPDATED, -1, null, null);

        queueRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        queueFragmentRecyclerViewAdapter = new queueFragmentRecyclerViewAdapter(mainActivity,requireContext(),QueueFragmentMotionLayout,this);
        queueRecyclerView.setAdapter(queueFragmentRecyclerViewAdapter);

        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(queueRecyclerView);

        SmallImageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
                if (index>=0 && index<queueObject.QueueArray.size()) {
                    queueRecyclerView.smoothScrollToPosition(index);
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().pause();
                } else{
                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
                }
            }
        });
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(baseColor));

        MediaControllerCompat mediaControllerCompat = MediaControllerCompat.getMediaController(mainActivity);
        if (mediaControllerCompat!=null) {
            playbackStateChanged(mediaControllerCompat.getPlaybackState().getState());
        }

        mainActivity.saveToPlaylist.setVisible(queueObject.QueueArray.size()>0);
        mainActivity.clearQueue.setVisible(queueObject.QueueArray.size()>0);
        mainActivity.scrollToTop.setVisible(queueObject.QueueArray.size()>0);
        setBigImage();

        updateQueue = false;

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        mainActivity.saveToPlaylist.setVisible(!hidden && queueObject.QueueArray.size()>0);
        mainActivity.clearQueue.setVisible(!hidden && queueObject.QueueArray.size()>0);
        mainActivity.scrollToTop.setVisible(!hidden && queueObject.QueueArray.size()>0);
        if (!hidden) {

            if (SelectedSongs.size()>0){
                addToQueueCard.setVisibility(View.VISIBLE);
            }else {
                addToQueueCard.setVisibility(View.GONE);
            }

            if (updateQueue) {
                queueFragmentRecyclerViewAdapter.notifyDataSetChanged();
                updateQueue = false;
            }
        }
    }

    public void playbackStateChanged(int pbState){
        if (pbState == PlaybackStateCompat.STATE_PLAYING) {
            floatingActionButton.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_pause_24));
        } else{
            floatingActionButton.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_play_24));
        }
    }

    @Override
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){
        if (QUEUE_DETAILS_UPDATED.equals(message)){
            int durationInSeconds = queueObject.durationInMillis / 1000;
            /*int durationCompletedInSeconds = (queueObject.durationCompletedTillNowPlayingSong + queueObject.playbackPosition) / 1000;
            String duration = theFormat.format(durationCompletedInSeconds / (60 * 60)) + ":" + theFormat.format(((durationCompletedInSeconds / 60) % 60)) + ":" + theFormat.format(durationCompletedInSeconds % (60)) +
                    "/" + theFormat.format(durationInSeconds / (60 * 60)) + ":" + theFormat.format(((durationInSeconds / 60) % 60)) + ":" + theFormat.format(durationInSeconds % (60));*/
            String duration = "Duration: " + theFormat.format(durationInSeconds / (60 * 60)) + ":" + theFormat.format(((durationInSeconds / 60) % 60)) + ":" + theFormat.format(durationInSeconds % (60));
            String tracks;
            if (queueObject.QueueArray.size()==1){
                tracks = "1 Track";
            }else {
                tracks = queueObject.QueueArray.size() + " Tracks";
            }
            DurationTextView.setText(duration);
            NoOfTracksTextView.setText(tracks);
            mainActivity.saveToPlaylist.setVisible(queueObject.QueueArray.size()>0 && !this.isHidden());
            mainActivity.clearQueue.setVisible(queueObject.QueueArray.size()>0 && !this.isHidden());
            mainActivity.scrollToTop.setVisible(queueObject.QueueArray.size()>0 && !this.isHidden());


            HashMap<String, Integer> artistsHashMap = new HashMap<String, Integer>();
            String artist="";
            for (SongItem song:queueObject.QueueArray){
                artist = song.getWrapperForCommonDataSongItem().getArtistApi();
                if (artist!=null && !"<unknown>".equals(artist)){
                    if (artistsHashMap.get(artist)==null){
                        artistsHashMap.put(artist,0);
                    }else {
                        artistsHashMap.replace(artist, artistsHashMap.get(artist)+1);
                    }
                }
            }

            ArrayList<Map.Entry<String, Integer>> toSort = new ArrayList<>(artistsHashMap.entrySet());
            toSort.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue() - o1.getValue();
                }
            });

            StringBuilder sb = new StringBuilder();
            int count=0;

            for (Map.Entry<String, Integer> entry:toSort){
                if (count>=3){
                    break;
                }
                ++count;
                sb.append(entry.getKey()).append(", ");
            }

            String artistString = sb.toString();
            if (artistString.length()>2) {
                artistString = artistString.substring(0, artistString.length() - 2);
            }else {
                artistString = "Artists";
            }
            ArtistsTextView.setText(artistString);

        }else if (UPDATE_QUEUE_PROGRESS.equals(message)){
            int totalDuration = queueObject.durationInMillis;
            int durationCompleted = (queueObject.durationCompletedTillNowPlayingSong + queueObject.playbackPosition);
            float progress;
            if (totalDuration==0){
                progress=0;
            }else {
                progress = ((float) durationCompleted)/totalDuration;
            }
            queueProgressCanvas.setProgress(progress);
        }else if (UPDATE_QUEUE.equals(message)){
            queueFragmentRecyclerViewAdapter.notifyDataSetChanged();
        }else if (NOTIFY_ITEM_INSERTED.equals(message)){
            queueFragmentRecyclerViewAdapter.notifyItemInserted(position);
        }else if (NOW_PLAYING_THUMBNAIL_FOUND.equals(message)){
            setBigImage();
        }else if (NOW_PLAYING_SONG_CHANGED.equals(message)){
            //queueObject.updateDurationCompletedTillNowPlayingSong();
            if (queueObject.nowPlaying!=null){
                int nowPlayingSongIndex = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
                if (nowPlayingSongIndex>=0 && nowPlayingSongIndex<queueObject.QueueArray.size()) {
                    queueFragmentRecyclerViewAdapter.notifyItemChanged(nowPlayingSongIndex, R.id.songNameSongs);
                    queueFragmentRecyclerViewAdapter.notifyItemChanged(nowPlayingSongIndex, R.id.artistSongs);
                    queueFragmentRecyclerViewAdapter.notifyItemChanged(nowPlayingSongIndex, R.id.durationSongs);
                }
            }
            if (songPlayingWhenActivityStops == null) {
                if (queueObject.previousSong != null) {
                    int previousSongIndex = queueObject.QueueArray.indexOf(queueObject.previousSong);
                    if (previousSongIndex >= 0 && previousSongIndex < queueObject.QueueArray.size()) {
                        queueFragmentRecyclerViewAdapter.notifyItemChanged(previousSongIndex, R.id.songNameSongs);
                        queueFragmentRecyclerViewAdapter.notifyItemChanged(previousSongIndex, R.id.artistSongs);
                        queueFragmentRecyclerViewAdapter.notifyItemChanged(previousSongIndex, R.id.durationSongs);
                    }
                }
                setBigImage();
            }else {
                int playingSongWhenActivityStopsIndex = queueObject.QueueArray.indexOf(songPlayingWhenActivityStops);
                if (playingSongWhenActivityStopsIndex >= 0 && playingSongWhenActivityStopsIndex < queueObject.QueueArray.size()) {
                    queueFragmentRecyclerViewAdapter.notifyItemChanged(playingSongWhenActivityStopsIndex, R.id.songNameSongs);
                    queueFragmentRecyclerViewAdapter.notifyItemChanged(playingSongWhenActivityStopsIndex, R.id.artistSongs);
                    queueFragmentRecyclerViewAdapter.notifyItemChanged(playingSongWhenActivityStopsIndex, R.id.durationSongs);
                }
                if (queueObject.nowPlaying != songPlayingWhenActivityStops) {
                    setBigImage();
                }
                songPlayingWhenActivityStops=null;
            }
        }else if (UPDATE_ITEM_COLORS.equals(message)){
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(baseColor));
            queueProgressCanvas.updatePaintColor();
        }else if (UPDATE_STOP_AFTER_THIS.equals(message)){
            int prev = queueObject.QueueArray.indexOf(queueObject.previousStop);
            int newStop = queueObject.QueueArray.indexOf(queueObject.stopAfterThis);
            if (prev>=0){queueFragmentRecyclerViewAdapter.notifyItemChanged(prev,R.id.songNameSongs);}
            if (newStop>=0){queueFragmentRecyclerViewAdapter.notifyItemChanged(newStop,R.id.songNameSongs);}
        }else if (SCROLL_TO_TOP.equals(message)){
            queueRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void thumbnailUpdated(SongItem songItem){
        for (int i=0;i<queueObject.QueueArray.size();++i){
            if (queueObject.QueueArray.get(i).isSimilar(songItem)){
                queueFragmentRecyclerViewAdapter.notifyItemChanged(i,R.id.thumbnailSongs);
            }
        }
    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Queue");
        mainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.VISIBLE);
        currentFragmentInLaunchFragment=name;
    }

    public void setBigImage(){
        if(bitmapInImageView==null){
            bitmapInImageView = lowResDefaultBitmap;
        }
        if (!bitmapInImageView.sameAs(queueObject.getNowPlayingThumbnail())) {
            if (queueObject.nowPlaying != null) {
                Bitmap bitmap = queueObject.nowPlaying.getThumbnail();
                if (bitmap==null)
                    bitmap=lowResDefaultBitmap;
                BigImage.setImageBitmap(bitmap);
                SmallImage.setImageBitmap(bitmap);
                if (bitmap.getHeight() == thumbnailPixels && bitmap.getWidth() == thumbnailPixels) {
                    differentThread runnable = new differentThread(queueObject.nowPlaying);
                    new Thread(runnable).start();
                }
            } else {
                BigImage.setImageBitmap(lowResDefaultBitmap);
                SmallImage.setImageBitmap(lowResDefaultBitmap);
            }
            bitmapInImageView = queueObject.getNowPlayingThumbnail();
        }
    }

    class differentThread implements Runnable{
        SongItem item;
        public differentThread(SongItem songItem) {
            this.item = songItem;
        }

        @Override
        public void run() {
            Bitmap image;
            image = getThumbnail(item.getUri());
            if (image != null) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        BigImage.setImageBitmap(image);
                        SmallImage.setImageBitmap(image);
                    }
                });
            }
        }
    }

    ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {

        boolean swipeBack = false;
        float oldDX=0;

        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(UP | DOWN, LEFT | RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromposition = viewHolder.getAdapterPosition();
            int toposition = target.getAdapterPosition();

            Collections.swap(queueObject.QueueArray , fromposition, toposition);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(fromposition, toposition);
            queueObject.updateDurationCompletedTillNowPlayingSong();
            getMessage(UPDATE_QUEUE_PROGRESS,-1,null,null);

            System.out.println("QWERTYUIOP][';LKJHGFDSA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + fromposition + " : " + toposition);

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
            int pos = viewHolder.getAdapterPosition();

            if (direction==RIGHT){
                SongItem itemToRemove = queueObject.QueueArray.get(pos);
                queueObject.remove(pos);
                queueFragmentRecyclerViewAdapter.notifyItemRemoved(pos);
                SaveQueue(getContext());

                if (pos == index){
                    Bundle bundle = new Bundle();
                    bundle.putInt("indexToPlay", pos);
                    MediaControllerCompat.getMediaController(mainActivity).sendCommand("GoToThisSong", bundle, null);
                }

                Snackbar snackbar = Snackbar
                        .make(QueueFragmentMotionLayout, "Song removed from queue.", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.nowPlayingRecyclerView)
                        .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        queueObject.add(pos,itemToRemove);
                        if (queueObject.QueueArray.size()==1){
                            queueObject.setNowPlaying(itemToRemove);
                        }
                        queueFragmentRecyclerViewAdapter.notifyItemInserted(pos);
                        SaveQueue(getContext());
                        //queueRecyclerView.scrollToPosition(pos);
                    }
                });
                snackbar.getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.setActionTextColor(secondaryBaseColor);
                snackbar.show();
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
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ACTION_STATE_SWIPE) {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            float scaledDX = dX;
            if (dX<0) {
                scaledDX = (float) (dX * Math.exp(1.39f*(dX/displayWidthInPixels)));
            }

            if (dX==0 && oldDX<0){
                int index = viewHolder.getAdapterPosition();
                if (index>=0 && index<queueObject.QueueArray.size()){
                    SongItem song = queueObject.QueueArray.get(index);
                    queueObject.setStopAfterThis(song);
                }
            }
            oldDX=dX;
            super.onChildDraw(c, recyclerView, viewHolder, scaledDX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (swipeBack) {
                swipeBack = false;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchListener(Canvas c,
                                      RecyclerView recyclerView,
                                      RecyclerView.ViewHolder viewHolder,
                                      float dX, float dY,
                                      int actionState, boolean isCurrentlyActive) {

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    swipeBack = (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) && dX<0;
                    return false;
                }
            });
        }
    };

    @Override
    public void StartDrag(int position) {
        itemTouchHelper.startDrag(Objects.requireNonNull(queueRecyclerView.findViewHolderForAdapterPosition(position)));
    }

}
