package com.example.testapplication2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import java.util.ArrayList;
import java.util.List;

import static com.example.testapplication2.App.MUSIC_CHANNEL_ID;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_QUEUE_PROGRESS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_FAV_SCORE;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_NOW_PLAYING_POSITION;
import static com.example.testapplication2.MainActivity.isActivityVisible;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.targetLoudness;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.MainActivity.totalTimeListened;
import static com.example.testapplication2.MainActivity.lastPlaybackStartPosition;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;

import com.example.testapplication2.Fragments.NowPlayingFragment;
import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.OtherClasses.Methods;

public class MediaPlaybackService extends MediaBrowserServiceCompat {

    final int ONGOING_NOTIFICATION_ID = 1;
    public static MediaSessionCompat mediaSession; // private non-static
    private PlaybackStateCompat.Builder stateBuilder;

    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private AudioManager audioManager;
    private AudioAttributes audioAttributes;
    private AudioFocusRequest audioFocusRequest;
    public static MediaPlayer mediaPlayer;
    private LoudnessEnhancer loudnessEnhancer;
    private NotificationManager notificationManager;

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyBroadcastReceiver myNoisyAudioStreamReceiver = new BecomingNoisyBroadcastReceiver();

    private PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();

    private boolean isServiceDestroyedFirst = false;

    private boolean shouldCallPlayOnAudioFocusGain = false;

    private static playbackPositionUpdater playbackPositionUpdater;
    static volatile boolean exitPlaybackPositionUpdater = false;
    static volatile boolean seeking = false;

    VolumeShaper.Configuration linearRamp;
    VolumeShaper volumeShaper;
    int volumeShaperDuration = 1000;
    boolean wantToPause = false;

    //Visualizer visualizer;

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        //if (allowBrowsing(clientPackageName, clientUid)) {
        // Returns a root ID that clients can use with onLoadChildren() to retrieve
        // the content hierarchy.
        //if (rootHints.containsKey(BrowserRoot.EXTRA_RECENT)){System.out.println("ExtraRecents!!!!!!!!!!!!!!!!");}
        if (rootHints != null) {
            for (String key:rootHints.keySet()){
                System.out.println(key + ": " + rootHints.get(key) + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
            System.out.println(rootHints.keySet().size() + "1111111111111111111111111111111111111111111!!!!!!!!!!!!!");
            if (rootHints.getBoolean(BrowserRoot.EXTRA_RECENT)){
                System.out.println("ON GET ROOT Extra!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  "+clientPackageName);
            }else if (rootHints.getBoolean(BrowserRoot.EXTRA_OFFLINE)){
                System.out.println("ON GET ROOT Extra Offline!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  "+clientPackageName);
            }else if (rootHints.getBoolean(BrowserRoot.EXTRA_SUGGESTED)){
                System.out.println("ON GET ROOT Extra Suggested!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  "+clientPackageName);
            }else{
                System.out.println("ON GET ROOT!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  "+clientPackageName);
            }
        }else {
            System.out.println("ON GET ROOT null RootHints!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  "+clientPackageName);
        }
        return new BrowserRoot(MY_MEDIA_ROOT_ID, rootHints);
        //} else {
        // Clients can connect, but this BrowserRoot is an empty hierarchy
        // so onLoadChildren returns nothing. This disables the ability to browse for content.
        // return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        //}
        //return null to not allow clients to connect.
    }

    /*@Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result, @NonNull Bundle options) {
        //super.onLoadChildren(parentId, result, options);
        System.out.println("ON LOAD CHILDREN2!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  " + parentId);
    }*/

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        System.out.println("ON LOAD CHILDREN!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  " + parentId);


        /*MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Bitmap daBitmap = null;
        try {
            mediaMetadataRetriever.setDataSource("/storage/3263-1A2B/Songs/ZAYN, Taylor Swift - I Don’t Wanna Live Forever (Fifty Shades Darker).mp3");
            byte[] byte1 = mediaMetadataRetriever.getEmbeddedPicture();
            mediaMetadataRetriever.release();
            if(byte1 != null) {
                daBitmap = BitmapFactory.decodeByteArray(byte1, 0, byte1.length);
            }
            else{
                daBitmap = null;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("ILLEGALARGUMENTEXCEPTION!!!");
            daBitmap = null;
        }*/

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        mediaItems.add(new MediaBrowserCompat.MediaItem(new MediaDescriptionCompat.Builder().setMediaId(MY_MEDIA_ROOT_ID)//"ID")
                .setTitle("ZAYN, Taylor Swift - I Don’t Wanna Live Forever (Fifty Shades Darker)")
                .setSubtitle("ZAYN, Taylor Swift - I Don’t Wanna Live Forever (Fifty Shades Darker)")
                .setMediaUri(Uri.parse("/storage/3263-1A2B/Songs/ZAYN, Taylor Swift - I Don’t Wanna Live Forever (Fifty Shades Darker).mp3"))
                .setIconUri(Uri.parse("/storage/3263-1A2B/DCIM/Camera/20191011_094252.jpg"))
                //.setIconBitmap(BitmapFactory.decodeFile("/storage/3263-1A2B/Songs/ZAYN, Taylor Swift - I Don’t Wanna Live Forever (Fifty Shades Darker).mp3"))
                .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));

        result.sendResult(mediaItems);
    }

    @Override
    public void onLoadItem(String itemId, @NonNull Result<MediaBrowserCompat.MediaItem> result) {
        System.out.println("ON LOAD ITEM!!!!!!!!!!!!!!!!!!!!!!!!!!!!! :  " + itemId);
        super.onLoadItem(itemId, result);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .setAcceptsDelayedFocusGain(true)
                .build();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);                         // QWERTYUIOPASDFGHJKLZXCVBNM

        mediaSession = new MediaSessionCompat(this,"mediaSessionTagForDebuggingPurposes",null,pendingIntent);

        // Enable callbacks from MediaButtons and TransportControls
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        stateBuilder = builder
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE|PlaybackStateCompat.ACTION_PAUSE|PlaybackStateCompat.ACTION_STOP|PlaybackStateCompat.ACTION_SKIP_TO_NEXT|PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS|
                                PlaybackStateCompat.ACTION_SEEK_TO);

        mediaSession.setPlaybackState(stateBuilder.build());
        mediaSession.setSessionActivity(pendingIntent);

        // MySessionCallback() has methods that handle callbacks from a media controller
        mediaSession.setCallback(myMediaSessionCallback);

        if (queueObject.nowPlaying == null) {
            mediaSession.setMetadata(null);
        } else {
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, queueObject.nowPlaying.getSongName())
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, queueObject.nowPlaying.getArtist())
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, queueObject.nowPlaying.getThumbnail())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, queueObject.nowPlaying.getDuration())
                    .build()
            );
        }

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mediaSession.getSessionToken());

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mediaPlayerOnPreparedListener);
        mediaPlayer.setOnCompletionListener(mediaPlayerOnCompletionListener);
        mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);

        loudnessEnhancer = new LoudnessEnhancer(mediaPlayer.getAudioSessionId());

        linearRamp =
                new VolumeShaper.Configuration.Builder()
                        .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_CUBIC_MONOTONIC)
                        .setCurve(new float[] { 0.f, 1.f }, // times
                                new float[] { 1.f, 0.f }) // volumes
                        .setDuration(volumeShaperDuration)
                        .build();

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        /*visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {       // always gives mono samples.
                System.out.println(waveform[0]);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                System.out.println(fft[0]);

            }
        },1000,true,true);
        visualizer.setEnabled(true);*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        System.out.println("onStartCommand Called!!!!!!!!!!!!");
        return super.onStartCommand(intent, flags, startId);
    }

    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            MediaControllerCompat mediaControllerCompat = mediaSession.getController();
            int pbState = mediaControllerCompat.getPlaybackState().getState();
            if (i==AudioManager.AUDIOFOCUS_GAIN){
                if (shouldCallPlayOnAudioFocusGain && pbState==PlaybackStateCompat.STATE_PAUSED){
                    mediaControllerCompat.getTransportControls().play();
                }
            }else if (i==AudioManager.AUDIOFOCUS_LOSS){
                if (pbState==PlaybackStateCompat.STATE_PLAYING){
                    mediaControllerCompat.getTransportControls().pause();
                    shouldCallPlayOnAudioFocusGain = true;
                }else {
                    shouldCallPlayOnAudioFocusGain = false;
                }
            }else if (i==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                if (pbState==PlaybackStateCompat.STATE_PLAYING){
                    mediaControllerCompat.getTransportControls().pause();
                    shouldCallPlayOnAudioFocusGain = true;
                }else{
                    shouldCallPlayOnAudioFocusGain = false;
                }
            }else {
                shouldCallPlayOnAudioFocusGain = false;
            }
        }
    };

    private final MediaSessionCompat.Callback myMediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();

            System.out.println("onplaycalled!!!!!!!");

            if (audioManager.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                System.out.println("audioFocusGranted!!!!!");

                MediaControllerCompat mediaControllerCompat = mediaSession.getController();
                int pbState = mediaControllerCompat.getPlaybackState().getState();

                if (pbState==PlaybackStateCompat.STATE_NONE) {
                    if (queueObject.nowPlaying!=null) {

                        startForegroundService(new Intent(getApplicationContext(), MediaPlaybackService.class)); // startService(new Intent(getApplicationContext(), MediaPlaybackService.class));
                        //UpdateMediaSessionMetadata();
                        mediaSession.setActive(true);

                        // Notification ID cannot be 0.
                        //startForeground(ONGOING_NOTIFICATION_ID, CreateNotification(false));

                        if (queueObject.playbackPosition==-1){queueObject.playbackPosition=0;}
                        try {
                            mediaPlayer.reset();
                            //resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                            mediaPlayer.setDataSource(queueObject.nowPlaying.getUri());
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                            e.printStackTrace();
                        }
                        startPlaybackPositionThread();
                    }else {
                        Toast.makeText(getApplicationContext(),"Queue is empty.",Toast.LENGTH_SHORT).show();
                        stopForeground(true);
                        stopSelf();
                    }

                }else if (pbState==PlaybackStateCompat.STATE_PAUSED){
                    mediaPlayerStart();
                    try {
                        volumeShaper.apply(VolumeShaper.Operation.REVERSE);
                    }catch (Exception e){
                        Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                        e.printStackTrace();
                        mediaPlayer.pause();
                        volumeShaper = mediaPlayer.createVolumeShaper(linearRamp);
                        volumeShaper.apply(VolumeShaper.Operation.PLAY);
                        mediaPlayer.start();
                        volumeShaper.apply(VolumeShaper.Operation.REVERSE);
                    }
                    startPlaybackPositionThread();
                    mediaSession.setActive(true);
                    //System.out.println("RXTCYVUBHINJLMK!!!!!!!!!! " + (new SimpleDateFormat("hh:mm:ss")).format((new Date(System.currentTimeMillis()))));
                    //notificationManager.notify(ONGOING_NOTIFICATION_ID,CreateNotification(false));
                    /*startService(new Intent(getApplicationContext(), MediaPlaybackService.class));*/
                    mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_PLAYING,mediaPlayer.getCurrentPosition(),1).build());
                    startForeground(ONGOING_NOTIFICATION_ID, CreateNotification());

                    try {
                        intentFilter.addAction(String.valueOf(PlaybackStateCompat.ACTION_PAUSE));
                        registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                    }catch (Exception e){
                        Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                        e.printStackTrace();
                    }

                }
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            try {
                volumeShaper.apply(VolumeShaper.Operation.PLAY);
            }catch (Exception e){
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
                volumeShaper = mediaPlayer.createVolumeShaper(linearRamp);
                volumeShaper.apply(VolumeShaper.Operation.PLAY);
            }
            wantToPause = true;

            (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayerPause();
                }
            },volumeShaperDuration);

            exitPlaybackPositionUpdater=true;
            try {
                unregisterReceiver(myNoisyAudioStreamReceiver);
            }catch (Exception e){
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
            }

            //stopForeground(false);
            //notificationManager.notify(ONGOING_NOTIFICATION_ID,CreateNotification(true));
            mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_PAUSED,mediaPlayer.getCurrentPosition(),1).build());
            notificationManager.notify(ONGOING_NOTIFICATION_ID,CreateNotification());
            System.out.println("Pause Called!!!!!!!!!!!!!!");
        }

        @Override
        public void onStop() {
            super.onStop();
            releaseAudioResources();
            //QueueArray.clear();
            System.out.println("Stop!!!!!!!!!!!!!!!");
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            if ("StartPlayBackOver".equals(command)){
                resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                //mediaPlayer.reset();
                //UpdateMediaSessionMetadata();
                mediaSession.setActive(true);

                try {
                    mediaPlayer.setDataSource(queueObject.nowPlaying.getUri());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }
                startPlaybackPositionThread();
                //startForeground(ONGOING_NOTIFICATION_ID, CreateNotification(false));
            }else if ("GoToThisSong".equals(command)){
                goToNextSong(extras.getInt("indexToPlay"));
            }else if ("rewind".equals(command)){
                long seekPos = mediaPlayer.getCurrentPosition() - 3000;
                if (seekPos<0){seekPos=0;}
                mediaSession.getController().getTransportControls().seekTo(seekPos);
            }else if ("fast_forward".equals(command)){
                long seekPos = mediaPlayer.getCurrentPosition() + 3000;
                int dur = mediaPlayer.getDuration();
                if (seekPos>dur){seekPos=dur;}
                mediaSession.getController().getTransportControls().seekTo(seekPos);
            }else if("directlySkipToPrevious".equals(command)){
                queueObject.playbackPosition=-1;

                int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);

                if (index>0) {
                    //mediaPlayer.reset();
                    queueObject.setNowPlaying(queueObject.QueueArray.get(index - 1));
                    resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                    mediaSession.setActive(true);

                    try {
                        mediaPlayer.setDataSource(queueObject.nowPlaying.getUri());
                        mediaPlayer.prepare();
                    } catch (Exception e) {
                        Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                        e.printStackTrace();
                    }
                    //startForeground(ONGOING_NOTIFICATION_ID, CreateNotification(mediaSession.getController().getPlaybackState().getState()==PlaybackStateCompat.STATE_PAUSED));

                }else {
                    mediaSession.getController().getTransportControls().seekTo(0);
                }
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            updateTotalTimeListened();
            lastPlaybackStartPosition= (int) pos;
            mediaPlayer.seekTo(pos,MediaPlayer.SEEK_PREVIOUS_SYNC); // same as mediaPlayer.seekTo(int pos)
            queueObject.playbackPosition= (int) pos;
            int playbackState = mediaSession.getController().getPlaybackState().getState();
            if(playbackState!=PlaybackStateCompat.STATE_PLAYING){        //playbackState==PlaybackStateCompat.STATE_PAUSED
                MediaPlaybackService.updateNotificationSeekbarHack runnable = new MediaPlaybackService.updateNotificationSeekbarHack(playbackState,pos);
                new Thread(runnable).start();
            }else {
                mediaSession.setPlaybackState(builder.setState(playbackState, pos, 1).build());
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            goToNextSong();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            goToPreviousSong();
        }
    };

    public void goToPreviousSong(){
        queueObject.playbackPosition=-1;
        if (mediaPlayer.getCurrentPosition()>3000){
            mediaSession.getController().getTransportControls().seekTo(0);
        }else {
            int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);

            if (index>0) {

                //mediaPlayer.reset();
                queueObject.setNowPlaying(queueObject.QueueArray.get(index - 1));
                resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                mediaSession.setActive(true);

                try {
                    mediaPlayer.setDataSource(queueObject.nowPlaying.getUri());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }
                //startForeground(ONGOING_NOTIFICATION_ID, CreateNotification(mediaSession.getController().getPlaybackState().getState()==PlaybackStateCompat.STATE_PAUSED));

            }else {
                mediaSession.getController().getTransportControls().seekTo(0);
            }
        }
    }

    public void goToNextSong(){
        goToNextSong(-1);
    }
    public void goToNextSong(int indexToPlay){

        if (queueObject.nowPlaying!=null) {

            queueObject.playbackPosition=-1;
            if (queueObject.nowPlaying == queueObject.stopAfterThis) {
                if (queueObject.QueueArray.size()==0){
                    queueObject.setNowPlaying(null);
                }
                else {
                    if (indexToPlay==-1){
                        indexToPlay = queueObject.QueueArray.indexOf(queueObject.nowPlaying)+1;
                    }
                    indexToPlay=indexToPlay%queueObject.QueueArray.size();
                    queueObject.setNowPlaying(queueObject.QueueArray.get(indexToPlay));
                }

                resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                queueObject.setStopAfterThis(null);
                mediaSession.getController().getTransportControls().stop();
            } else {
                if (indexToPlay==-1) {
                    indexToPlay = queueObject.QueueArray.indexOf(queueObject.nowPlaying) + 1;
                }
                //mediaPlayer.reset();
                if (queueObject.QueueArray.size()==0){
                    queueObject.setNowPlaying(null);
                    queueObject.setStopAfterThis(null);
                    resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                    mediaSession.getController().getTransportControls().stop();
                } else {
                    if (indexToPlay>=queueObject.QueueArray.size()){
                        queueObject.setNowPlaying(queueObject.QueueArray.get(0));
                        queueObject.setStopAfterThis(null);
                        resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                        mediaSession.getController().getTransportControls().stop();
                    }else {
                        queueObject.setNowPlaying(queueObject.QueueArray.get(indexToPlay));
                        resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                        mediaSession.setActive(true);
                        try {
                            mediaPlayer.setDataSource(queueObject.nowPlaying.getUri());
                            mediaPlayer.prepare();
                        } catch (Exception e) {
                            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                            e.printStackTrace();
                        }
                        //startForeground(ONGOING_NOTIFICATION_ID, CreateNotification(mediaSession.getController().getPlaybackState().getState()==PlaybackStateCompat.STATE_PAUSED));
                    }
                }
            }
        }
    }

    private final MediaPlayer.OnPreparedListener mediaPlayerOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            totalTimeListened=0;
            lastPlaybackStartPosition=0;
            volumeShaper = mediaPlayer.createVolumeShaper(linearRamp);
            if (queueObject.playbackPosition==-1){
                if (mediaSession.getController().getPlaybackState().getState()==PlaybackStateCompat.STATE_PLAYING){
                    mediaPlayerStart();
                    mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1).build());
                }else {
                    mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_PAUSED, 0, 1).build());
                }
            }else {
                if (queueObject.playbackPosition!=0) {
                    mediaPlayer.seekTo(queueObject.playbackPosition);
                }
                mediaPlayerStart();
                mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_PLAYING,queueObject.playbackPosition, 1).build());
            }
            startForeground(ONGOING_NOTIFICATION_ID, CreateNotification());
            shouldCallPlayOnAudioFocusGain = false;

            try {
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
            }catch (Exception e){
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }
        }
    };

    private final MediaPlayer.OnCompletionListener mediaPlayerOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            goToNextSong();
        }
    };

    private final MediaPlayer.OnErrorListener mediaPlayerOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

            mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_NONE,PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,1).build());
            if (i1==MediaPlayer.MEDIA_ERROR_IO) {
                System.out.println("IOOOOOOOOOOOOOOOOOOOOOOOO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                goToNextSong();
            }
            return true;
        }
    };

    public Notification CreateNotification(){

        System.out.println("Notification Called!!!!!!!!!!!!!!!!!!!!!!!");

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent callStopIntent = new Intent(this, MainActivity.class);
        callStopIntent.putExtra("Extra", "Stop");
        PendingIntent callStopPendingIntent = PendingIntent.getActivity(this, 0, callStopIntent, PendingIntent.FLAG_IMMUTABLE);


        Intent pauseIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        pauseIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PAUSE));
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, pauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent playIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        playIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_PLAY));
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, playIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        stopIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_STOP));
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent previousIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        previousIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, previousIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent nextIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        nextIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_SKIP_TO_NEXT));
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, nextIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent rewindIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        rewindIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_REWIND));
        PendingIntent rewindPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, rewindIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent fastForwardIntent = new Intent(getApplicationContext(),PlaybackControlsBroadcastReceiver.class);
        fastForwardIntent.setAction(String.valueOf(PlaybackStateCompat.ACTION_FAST_FORWARD));
        PendingIntent fastForwardPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, fastForwardIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, MUSIC_CHANNEL_ID)
                        .setContentTitle(description.getTitle())
                        .setContentText(description.getSubtitle())
                        .setSubText(description.getDescription())
                        .setSmallIcon(R.drawable.app_icon)
                        .setLargeIcon(description.getIconBitmap())
                        .setColor(Color.BLUE)
                        .setContentIntent(controller.getSessionActivity())
                        // Stop the service when the notification is swiped away
                        .setDeleteIntent(stopPendingIntent)
                        .addAction(R.drawable.ic_previous_24,"Skip to previous", previousPendingIntent)//MediaButtonReceiver.buildMediaButtonPendingIntent(this,null, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
                        .addAction(R.drawable.ic_rewind_24,"Rewind",rewindPendingIntent);
        if (controller.getPlaybackState().getState()!=PlaybackStateCompat.STATE_PLAYING){
            builder.addAction(R.drawable.ic_play_24, "Play", playPendingIntent);//MediaButtonReceiver.buildMediaButtonPendingIntent(this,null, PlaybackStateCompat.ACTION_PLAY));
        }else {
            builder.addAction(R.drawable.ic_pause_24, "Pause", pausePendingIntent);//MediaButtonReceiver.buildMediaButtonPendingIntent(this,null, PlaybackStateCompat.ACTION_PAUSE));
        }
        builder.addAction(R.drawable.ic_fast_forward_24,"Fast forward",fastForwardPendingIntent)//MediaButtonReceiver.buildMediaButtonPendingIntent(this,null, PlaybackStateCompat.ACTION_STOP))
                .addAction(R.drawable.ic_next_24,"Skip to next",nextPendingIntent)// MediaButtonReceiver.buildMediaButtonPendingIntent(this,null, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,2,4)
                        .setMediaSession(mediaSession.getSessionToken())

                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopPendingIntent)
                )

                //.setOngoing(true)  //check for swipe behaviour // stop not called when swiped away

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveQueue(getApplicationContext());
        if (mediaSession.getController().getPlaybackState().getState()!=PlaybackStateCompat.STATE_NONE) {
            isServiceDestroyedFirst = true;
            releaseAudioResources();//mediaSession.getController().getTransportControls().stop();
        }
        try {
            unregisterReceiver(myNoisyAudioStreamReceiver);
        }catch (Exception e){
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
        }

        mediaPlayer.release();
        mediaSession.release();//*/

        System.out.println("MediaPlaybackService Destroyed!!!!!!!!!!!!!");
        //System.exit(0); // service is destroyed after activity because service is bound to the activity.
    }

    public void releaseAudioResources(){
        try {
            mediaPlayer.reset(); // valid in every state as per the documentation... maybe it's released before this method is called in the onDestroy callback of the service
        }catch (Exception exception){
            Methods.addToErrorLog(exception.getMessage(),getApplicationContext());
            exception.printStackTrace();
        }

        exitPlaybackPositionUpdater = true;

        try {
            unregisterReceiver(myNoisyAudioStreamReceiver);
        }catch (Exception e){
            e.printStackTrace();
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
        }

        audioManager.abandonAudioFocusRequest(audioFocusRequest);
        //UpdateMediaSessionMetadata();
        mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_NONE,PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,1).build());
        mediaSession.setActive(false);
        if (!isServiceDestroyedFirst) {
            stopForeground(true);//false*/
            //stopSelf();
            stopSelf();
            //stopForeground(false);
        }
    }

    public void UpdateMediaSessionMetadata(){
        if (queueObject.nowPlaying==null){
            mediaSession.setMetadata(null);
        }else {
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, queueObject.nowPlaying.getSongName())
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, queueObject.nowPlaying.getArtist())
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, queueObject.nowPlaying.getThumbnail())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, queueObject.nowPlaying.getDuration())
                    .build()
            );
        }
    }

    public static void updateTotalTimeListened(){
        totalTimeListened=totalTimeListened+mediaPlayer.getCurrentPosition()-lastPlaybackStartPosition;
        lastPlaybackStartPosition=mediaPlayer.getCurrentPosition();
    }

    static class playbackPositionUpdater extends Thread{
        Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void run() {
            updatePlaybackPosition();
        }

        public void updatePlaybackPosition(){
            if (isActivityVisible && !exitPlaybackPositionUpdater) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            queueObject.playbackPosition = mediaPlayer.getCurrentPosition();
                            weakReferencesToFragments.getMessage(MainActivity.name, UPDATE_NOW_PLAYING_POSITION,-1,null);
                            weakReferencesToFragments.getMessage(NowPlayingFragment.name, UPDATE_NOW_PLAYING_POSITION,-1,null);
                            weakReferencesToFragments.getMessage(QueueFragment.name, UPDATE_QUEUE_PROGRESS,-1,null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updatePlaybackPosition();
            }
        }
    }

    class updateNotificationSeekbarHack implements Runnable {
        long seekPosition;
        int playbackState;
        updateNotificationSeekbarHack(int playbackState,long seekPosition){
            this.seekPosition = seekPosition;
            this.playbackState = playbackState;
        }
        @Override
        public void run() {
            seeking = true;
            mediaSession.setPlaybackState(builder.setState(PlaybackStateCompat.STATE_PLAYING,seekPosition,1).build());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mediaSession.setPlaybackState(builder.setState(playbackState,seekPosition,1).build());
            seeking = false;
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    weakReferencesToFragments.getMessage(MainActivity.name, UPDATE_NOW_PLAYING_POSITION,-1,null);
                    weakReferencesToFragments.getMessage(NowPlayingFragment.name, UPDATE_NOW_PLAYING_POSITION,-1,null);
                }
            });
        }
    }

    public static void startPlaybackPositionThread(){
        if (playbackPositionUpdater==null|| !playbackPositionUpdater.isAlive()) {
            exitPlaybackPositionUpdater=false;
            playbackPositionUpdater = new playbackPositionUpdater();
            playbackPositionUpdater.start();
        }
    }

    public void mediaPlayerStart(){
        wantToPause=false;

        int millibells = (int) (1000 * (Math.log10(targetLoudness / queueObject.nowPlaying.getLoudnessIndex())));
        loudnessEnhancer.setTargetGain(millibells);
        loudnessEnhancer.setEnabled(true);

        /*System.out.println("LOudNessINdex!!!!!!!!!!!!!!!!!!!: " + queueObject.nowPlaying.getLoudnessIndex() + "\n"
                + "Decibells: " + (float)millibells / 100);*/

        mediaPlayer.start();
    }

    public void mediaPlayerPause(){
        if (wantToPause) {
            mediaPlayer.pause();
        }
    }

    public static void resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet(){
        updateTotalTimeListened();
        weakReferencesToFragments.getMessage(MainActivity.name,UPDATE_FAV_SCORE,totalTimeListened,queueObject.previousSong);
        mediaPlayer.reset();
    }
}