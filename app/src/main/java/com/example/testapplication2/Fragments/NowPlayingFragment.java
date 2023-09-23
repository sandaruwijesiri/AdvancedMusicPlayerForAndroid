package com.example.testapplication2.Fragments;

import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_SONG_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_LYRICS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_NOW_PLAYING_POSITION;
import static com.example.testapplication2.MainActivity.baseColor;
import static com.example.testapplication2.MainActivity.clickedNowPlayingCard;
import static com.example.testapplication2.MainActivity.contrastColor;
import static com.example.testapplication2.MainActivity.currentFragmentInLaunchFragment;
import static com.example.testapplication2.MainActivity.lowResDefaultBitmap;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.secondaryBaseColor;
import static com.example.testapplication2.MainActivity.songPlayingWhenActivityStops;
import static com.example.testapplication2.MainActivity.theFormat;
import static com.example.testapplication2.MainActivity.thumbnailPixels;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.MediaPlaybackService.mediaPlayer;
import static com.example.testapplication2.OtherClasses.ImageProcessor.getThumbnail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.DataClasses.WrapperForCommonDataSongItem;
import com.example.testapplication2.Database.AppDatabase;
import com.example.testapplication2.Database.SongDetails;
import com.example.testapplication2.Database.SongDetailsDao;
import com.example.testapplication2.EditLyricsActivity;
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.R;

public class NowPlayingFragment extends MyFragmentSuperClass {

    public static String name = "NowPlayingFragment";
    ImageView nowPlayingFragmentImageView;
    CardView nowPlayingFragmentCardView;
    TextView nowPlayingFragmentSongName;
    TextView nowPlayingFragmentArtist;
    TextView lyrics;
    ImageView editLyrics;
    CardView lyricsTitleCardView;
    TextView lyricsTitleTextView;
    SeekBar seekBar;
    TextView nowPlayingFragmentProgress;
    TextView nowPlayingFragmentDuration;
    ImageView previous;
    ImageView rewind;
    ImageView playPause;
    ImageView fastForward;
    ImageView next;
    ImageView moreControls;
    CardView moreControlsCardView;
    MainActivity mainActivity;

    Bitmap bitmapInImageView;
    StringBuilder stringBuilder;

    boolean trackingTouch = false;

    public static int animationDuration = 200; // relevant xml files should have the same duration as well.

    public NowPlayingFragment() {
        super("NowPlayingFragment");
        weakReferencesToFragments.saveFragment(8,this);
        stringBuilder = new StringBuilder();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.now_playing_fragment,container,false);

        mainActivity = ((MainActivity) requireActivity());
        nowPlayingFragmentCardView = rootView.findViewById(R.id.nowPlayingFragmentCardView);
        nowPlayingFragmentImageView = rootView.findViewById(R.id.nowPlayingFragmentImageView);
        nowPlayingFragmentSongName = rootView.findViewById(R.id.nowPlayingFragmentSongName);
        nowPlayingFragmentArtist = rootView.findViewById(R.id.nowPlayingFragmentArtist);
        lyrics = rootView.findViewById(R.id.nowPlayingFragmentLyrics);
        editLyrics = rootView.findViewById(R.id.imageViewEditLyrics);
        lyricsTitleCardView = rootView.findViewById(R.id.lyricsTitleCardView);
        lyricsTitleTextView = rootView.findViewById(R.id.lyricsTitleTextView);
        nowPlayingFragmentProgress = rootView.findViewById(R.id.nowPlayingFragmentProgress);
        nowPlayingFragmentDuration = rootView.findViewById(R.id.nowPlayingFragmentDuration);
        previous = rootView.findViewById(R.id.nowPlayingFragmentPrevious);
        rewind = rootView.findViewById(R.id.nowPlayingFragmentRewind);
        playPause = rootView.findViewById(R.id.nowPlayingFragmentPlayPause);
        fastForward = rootView.findViewById(R.id.nowPlayingFragmentFastForward);
        next = rootView.findViewById(R.id.nowPlayingFragmentNext);
        moreControls = rootView.findViewById(R.id.moreControls);
        moreControlsCardView = rootView.findViewById(R.id.moreControlsCardView);

        lyricsTitleCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queueObject!=null && queueObject.nowPlaying!=null) {
                    Intent intent = new Intent(requireContext(), EditLyricsActivity.class);
                    intent.putExtra("SongName", String.valueOf(nowPlayingFragmentSongName.getText()));
                    intent.putExtra("SongUri", String.valueOf(queueObject.nowPlaying.getUri()));
                    intent.putExtra("Lyrics", String.valueOf(lyrics.getText()));
                    startActivity(intent);
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(mainActivity).getTransportControls().skipToPrevious();
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(mainActivity).sendCommand("rewind",null,null);
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pbState = MediaControllerCompat.getMediaController(mainActivity).getPlaybackState().getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().pause();
                } else{
                    MediaControllerCompat.getMediaController(mainActivity).getTransportControls().play();
                }
            }
        });

        fastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(mainActivity).sendCommand("fast_forward",null,null);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(mainActivity).getTransportControls().skipToNext();
                queueObject.playbackPosition=0;
                getMessage(UPDATE_NOW_PLAYING_POSITION,-1,null, null);
            }
        });

        moreControls.setOnClickListener(new View.OnClickListener() {
            boolean isVisible = false;
            @Override
            public void onClick(View v) {
                if (isVisible){
                    moreControlsCardView.setVisibility(View.GONE);     // Gone doesn't work due to motionlayout. So create a constraint set and apply to motionlayout to make it work.
                }else {
                    moreControlsCardView.setVisibility(View.VISIBLE);
                }
                isVisible=!isVisible;
            }
        });

        MediaControllerCompat mediaControllerCompat = MediaControllerCompat.getMediaController(mainActivity);
        if (mediaControllerCompat!=null) {
            playbackStateChanged(mediaControllerCompat.getPlaybackState().getState());
        }

        seekBar = rootView.findViewById(R.id.nowPlayingFragmentSeekBar);
        seekBar.setMax(2000);
        updateSeekBarColor();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int progressInSeconds = progress*queueObject.nowPlaying.getDuration()/(seekBar.getMax()*1000);
                stringBuilder.setLength(0);
                nowPlayingFragmentProgress.setText(stringBuilder.append(theFormat.format(progressInSeconds / 60)).append(":").append(theFormat.format(progressInSeconds % 60)).toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                trackingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                trackingTouch = false;
                MediaControllerCompat.getMediaController(mainActivity).getTransportControls().seekTo((long) seekBar.getProgress() *queueObject.nowPlaying.getDuration()/seekBar.getMax());
            }
        });

        bitmapInImageView=lowResDefaultBitmap;
        setBigImage();
        getMessage(UPDATE_LYRICS, -1, null, null);
        //getMessage(NOW_PLAYING_SONG_CHANGED, -1, null,null);

        return rootView;
    }

    public void playbackStateChanged(int pbState){
        if (pbState == PlaybackStateCompat.STATE_PLAYING) {
            playPause.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_pause_24));
        } else{
            playPause.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_play_24));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (clickedNowPlayingCard){
            if (queueObject!=null && queueObject.nowPlaying!=null){
                nowPlayingFragmentImageView.setImageBitmap(queueObject.nowPlaying.getThumbnail());
                setupSomeMetaData();

                int durationInSeconds = queueObject.nowPlaying.getDuration()/1000;
                stringBuilder.setLength(0);
                nowPlayingFragmentDuration.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());

                durationInSeconds = queueObject.playbackPosition/1000;
                stringBuilder.setLength(0);
                nowPlayingFragmentProgress.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());
            }else {
                nowPlayingFragmentImageView.setImageBitmap(lowResDefaultBitmap);
                nowPlayingFragmentSongName.setText("");
                nowPlayingFragmentArtist.setText("");
                nowPlayingFragmentDuration.setText("");
                nowPlayingFragmentProgress.setText("");
            }
            clickedNowPlayingCard = false;
        }
        getMessage(UPDATE_NOW_PLAYING_POSITION,-1,null, null);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (clickedNowPlayingCard){
                if (queueObject!=null && queueObject.nowPlaying!=null){
                    nowPlayingFragmentImageView.setImageBitmap(queueObject.nowPlaying.getThumbnail());
                    setupSomeMetaData();

                    int durationInSeconds = queueObject.nowPlaying.getDuration()/1000;
                    stringBuilder.setLength(0);
                    nowPlayingFragmentDuration.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());

                    durationInSeconds = queueObject.playbackPosition/1000;
                    stringBuilder.setLength(0);
                    nowPlayingFragmentProgress.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());
                }else {
                    nowPlayingFragmentImageView.setImageBitmap(lowResDefaultBitmap);
                    nowPlayingFragmentSongName.setText("");
                    nowPlayingFragmentArtist.setText("");
                    nowPlayingFragmentDuration.setText("");
                    nowPlayingFragmentProgress.setText("");
                }
                clickedNowPlayingCard = false;
            }
            getMessage(UPDATE_NOW_PLAYING_POSITION,-1,null, null);
        }
    }

    public void setupSomeMetaData(){
        WrapperForCommonDataSongItem wrapper = queueObject.nowPlaying.getWrapperForCommonDataSongItem();
        if (wrapper.getSongNameApi()==null){
            nowPlayingFragmentSongName.setText(queueObject.nowPlaying.getSongName());
            nowPlayingFragmentArtist.setText(queueObject.nowPlaying.getArtist());
        }else {
            nowPlayingFragmentSongName.setText("Song: " + wrapper.getSongNameApi());//queueObject.nowPlaying.getSongName());
            nowPlayingFragmentArtist.setText("Artist: " + wrapper.getArtistApi() + "\n" +
                    "Album: " + wrapper.getAlbumApi() + "\n" +
                    "Genres: " + wrapper.getGenresApi() + "\n" +
                    "Released: " + wrapper.getReleasedApi() + "\n" +
                    "Label: " + wrapper.getLabelApi() + "\n" +
                    "IsExplicit: " + wrapper.isExplicitAPI());//queueObject.nowPlaying.getArtist());
        }
    }

    @Override
    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){
        if (NOW_PLAYING_SONG_CHANGED.equals(message) && queueObject.nowPlaying!=null){
            WrapperForCommonDataSongItem wrapper = queueObject.nowPlaying.getWrapperForCommonDataSongItem();
            if (wrapper.getSongNameApi()==null){
                nowPlayingFragmentSongName.setText(queueObject.nowPlaying.getSongName());
                nowPlayingFragmentArtist.setText(queueObject.nowPlaying.getArtist());
            }else {
                nowPlayingFragmentSongName.setText("Song: " + wrapper.getSongNameApi());//queueObject.nowPlaying.getSongName());
                nowPlayingFragmentArtist.setText("Artist: " + wrapper.getArtistApi() + "\n" +
                        "Album: " + wrapper.getAlbumApi() + "\n" +
                        "Genres: " + wrapper.getGenresApi() + "\n" +
                        "Released: " + wrapper.getReleasedApi() + "\n" +
                        "Label: " + wrapper.getLabelApi() + "\n" +
                        "IsExplicit: " + wrapper.isExplicitAPI());//queueObject.nowPlaying.getArtist());
            }

            int durationInSeconds = queueObject.nowPlaying.getDuration()/1000;
            stringBuilder.setLength(0);
            nowPlayingFragmentDuration.setText(stringBuilder.append(theFormat.format(durationInSeconds / 60)).append(":").append(theFormat.format(durationInSeconds % 60)).toString());
            nowPlayingFragmentProgress.setText("00:00");

            if (songPlayingWhenActivityStops == null) {
                setBigImage();
            }else {
                if (queueObject.nowPlaying != songPlayingWhenActivityStops) {
                    setBigImage();
                }
                songPlayingWhenActivityStops=null;
            }

            getMessage(UPDATE_LYRICS,-1,null,null);
        }else if (UPDATE_ITEM_COLORS.equals(message)){
            updateSeekBarColor();
        }else if (UPDATE_NOW_PLAYING_POSITION.equals(message)){
            if (!this.isHidden() && !trackingTouch) {
                try {
                    int pos = mediaPlayer.getCurrentPosition();
                    if (pos>=0) {
                        queueObject.playbackPosition = pos;
                    }
                }catch (Exception e) {
                    Methods.addToErrorLog(e.getMessage(),getContext());
                    e.printStackTrace();
                }
                seekBar.setProgress(2000 * queueObject.playbackPosition / queueObject.nowPlaying.getDuration());
            }
        }else if (UPDATE_LYRICS.equals(message)){
            (new Thread(new updateLyricsAndOtherMetadataExceptThumbnailThread())).start();
        }
    }

    public void thumbnailUpdated(SongItem songItem){

    }

    @Override
    public void updateData(){
        if (mainActivity==null){
            mainActivity = (MainActivity) weakReferencesToFragments.mainActivity.get();
        }
        mainActivity.toolbar.setTitle("Now Playing ...");
        mainActivity.bottomNavigationView.setVisibility(View.GONE);
        mainActivity.nowPlayingRecyclerView.setVisibility(View.GONE);
        currentFragmentInLaunchFragment=name;
    }

    public void setBigImage(){
        if (!bitmapInImageView.sameAs(queueObject.getNowPlayingThumbnail())) {
            if (queueObject.nowPlaying != null) {
                Bitmap bitmap = queueObject.nowPlaying.getThumbnail();
                nowPlayingFragmentImageView.setImageBitmap(bitmap);
                if (bitmap.getHeight() == thumbnailPixels && bitmap.getWidth() == thumbnailPixels) {
                    differentThread runnable = new differentThread(queueObject.nowPlaying);
                    new Thread(runnable).start();
                }
            } else {
                nowPlayingFragmentImageView.setImageBitmap(lowResDefaultBitmap);
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
            image=getThumbnail(item.getUri());

            /*try {

                URL urlConnection = new URL("https://is3-ssl.mzstatic.com/image/thumb/Music126/v4/6d/ad/28/6dad2828-52c4-01dc-8e33-3ad3c05b73fd/pr_source.png/800x800cc.jpg");
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                image = myBitmap;
            }catch (Exception e){
                e.printStackTrace();
            }*/

            if (image != null) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        nowPlayingFragmentImageView.setImageBitmap(image);
                    }
                });
            }
        }
    }

    class updateLyricsAndOtherMetadataExceptThumbnailThread implements Runnable{

        @Override
        public void run() {
            if (queueObject!=null && queueObject.nowPlaying!=null) {
                AppDatabase db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "AppDatabase").build();
                SongDetailsDao songDetailsDao = db.SongDetailsDao();
                SongDetails songDetails = songDetailsDao.getForUri(queueObject.nowPlaying.getUri());
                if (songDetails!=null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (songDetails.lyrics==null || "".equals(songDetails.lyrics.trim())){
                                lyricsTitleTextView.setText("Tap to add lyrics");
                            }else {
                                lyricsTitleTextView.setText("Lyrics");
                            }
                            lyrics.setText(songDetails.lyrics);
                            setupSomeMetaData();
                        }
                    });
                }
                db.close();
            }
        }
    }

    public void updateSeekBarColor(){

        float[] arr = new float[3];
        Color.colorToHSV(contrastColor,arr);
        arr[1]=0.5f;
        arr[2]=0.5f;

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked}, // state_checked
                new int[] { }  //
        };
        int[] colors = new int[] {
                secondaryBaseColor,
                baseColor
        };
        int[] colors2 = new int[] {
                Color.HSVToColor(arr),
                baseColor
        };
        seekBar.setProgressTintList(new ColorStateList(states,colors));
        seekBar.setThumbTintList(new ColorStateList(states,colors));
        seekBar.setProgressBackgroundTintList(new ColorStateList(states,colors2));
    }
}
