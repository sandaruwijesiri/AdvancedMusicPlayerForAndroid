package com.example.testapplication2;

import static com.example.testapplication2.Enums.EnumMessages.CREATE_NEW_PLAYLIST;
import static com.example.testapplication2.Enums.EnumMessages.LOAD_SPECIFIC_PLAYLIST_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.NOTIFY_DATA_SET_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_DATA_SET_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_SONG_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_THUMBNAIL_FOUND;
import static com.example.testapplication2.Enums.EnumMessages.PLAYLIST_CREATED;
import static com.example.testapplication2.Enums.EnumMessages.PLAYLIST_RENAMED;
import static com.example.testapplication2.Enums.EnumMessages.SCROLL_TO_TOP;
import static com.example.testapplication2.Enums.EnumMessages.SET_TOOLBAR_ICON;
import static com.example.testapplication2.Enums.EnumMessages.SET_TOOLBAR_ICON_TO_DEFAULT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_ARTISTS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_MORE_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_NOW_PLAYING_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_PLAYLISTS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_SETTINGS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.SHOW_SONGS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_FAV_SCORE;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_ITEM_COLORS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_LYRICS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_NOW_PLAYING_POSITION;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_QUEUE;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_SELECTED_ITEMS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_STOP_AFTER_THIS;
import static com.example.testapplication2.Fragments.NowPlayingFragment.animationDuration;
import static com.example.testapplication2.Fragments.SettingsFragment.keepTrackOfFrequentSongs;
import static com.example.testapplication2.MediaPlaybackService.mediaPlayer;
import static com.example.testapplication2.MediaPlaybackService.mediaSession;
import static com.example.testapplication2.MediaPlaybackService.resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet;
import static com.example.testapplication2.MediaPlaybackService.seeking;
import static com.example.testapplication2.OtherClasses.ImageProcessor.CreatingLowResBitmap;
import static com.example.testapplication2.OtherClasses.ImageProcessor.CropBitmapTo1to1AspectRatio;
import static com.example.testapplication2.OtherClasses.ImageProcessor.getDominantColorDiscourageSkinAndGrey;
import static com.example.testapplication2.OtherClasses.ImageProcessor.getThumbnail;
import static com.example.testapplication2.OtherClasses.Methods.ReadQueue;
import static com.example.testapplication2.OtherClasses.Methods.SaveQueue;
import static com.example.testapplication2.OtherClasses.Methods.doesSongExist;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.room.Room;

import com.example.testapplication2.DataClasses.ArtistItem;
import com.example.testapplication2.DataClasses.PlaylistItem;
import com.example.testapplication2.DataClasses.QueueObject;
import com.example.testapplication2.DataClasses.SongItem;
import com.example.testapplication2.DataClasses.WeakReferencesToFragments;
import com.example.testapplication2.DataClasses.WrapperForCommonDataSongItem;
import com.example.testapplication2.Database.AppDatabase;
import com.example.testapplication2.Database.CurrentMonthAndHalf;
import com.example.testapplication2.Database.CurrentMonthAndHalfDao;
import com.example.testapplication2.Database.SongDetails;
import com.example.testapplication2.Database.SongDetailsDao;
import com.example.testapplication2.Enums.EnumFragments;
import com.example.testapplication2.Fragments.ArtistsFragment;
import com.example.testapplication2.Fragments.HomeFragment;
import com.example.testapplication2.Fragments.MoreFragment;
import com.example.testapplication2.Fragments.NowPlayingFragment;
import com.example.testapplication2.Fragments.PlaylistsFragment;
import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.Fragments.SettingsFragment;
import com.example.testapplication2.Fragments.SongsFragment;
import com.example.testapplication2.Fragments.SpecificPlaylistFragment;
import com.example.testapplication2.Interfaces.AccessToMainActivity;
import com.example.testapplication2.Miscellaneous.AllowPermissionDialog;
import com.example.testapplication2.Miscellaneous.AudioScanner;
import com.example.testapplication2.Miscellaneous.GetFrequencySpectrumDataSyncAsyncHybrid;
import com.example.testapplication2.Miscellaneous.GetMetadataForSongThroughAPI;
import com.example.testapplication2.Miscellaneous.MyCanvas;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;
import com.example.testapplication2.OtherClasses.Methods;
import com.example.testapplication2.RecyclerViewAdapters.nowPlayingRecyclerViewAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements AllowPermissionDialog.AllowPermissionDialogListener, AccessToMainActivity {

    public static String name = "MainActivity";
    public static int thumbnailPixels = 300;
    public static int targetLoudness = 6000;//2000;
    public static float floatToSetMaxGainTo10DeciBells = (float) (targetLoudness/10);
    Resources r;
    public static int displayHeightInPixels = 0;
    public static int displayWidthInPixels = 0;
    public static float dpToPixels;

    public MyCanvas myCanvas;
    Palette.Swatch swatch;
    int defaultFirstColor = Color.parseColor("#FFAA33BB");
    int defaultSecondColor = Color.parseColor("#88CC7700");

    public Toolbar toolbar;
    public MenuItem exit;
    public MenuItem saveToPlaylist;
    public MenuItem clearQueue;
    public MenuItem scrollToTop;
    Button scanButton;

    public static DecimalFormat theFormat = new  DecimalFormat("00");
    public static ArrayList<PlaylistItem> allPlaylists = new ArrayList<>();
    public static PlaylistItem currentPlaylist;
    public static PlaylistItem archivePlaylistItem;
    public static ArrayList<SongItem> AllSongs = new ArrayList<>();
    public static ArrayList<SongItem> AllSongsExceptArchived = new ArrayList<>();
    public static ArrayList<SongItem> SelectedSongs = new ArrayList<>();

    public static QueueObject queueObject = new QueueObject();
    public static SongItem songPlayingWhenActivityStops = null;

    public static boolean updateQueue = false;
    public static boolean updateSongsRecyclerViewItemConstraintLayout = false;
    public static boolean rewritePlaylistData = false;
    public boolean saveToPlaylistClicked = false;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


    //MediaPlaybackService
    Intent intent;
    //MediaPlaybackService

    private MediaBrowserCompat mediaBrowserCompat;

    public static Bitmap lowResDefaultBitmap = null;

    public static ArrayList<ArtistItem> artistArrayList = new ArrayList<>();

    public static int sortSongsByWhat = 0;
    public static int sortSongsByMode = 0;

    public static WeakReferencesToFragments weakReferencesToFragments;


    public BottomNavigationView bottomNavigationView;

    public static String currentFragmentInLaunchFragment = "";
    FragmentManager parentFragmentManager;
    ArrayList<MyFragmentSuperClass> fragmentStack = new ArrayList<>();

    Drawable uncheckedCheckBox;
    Drawable checkedCheckBox;
    Drawable defaultIcon;

    public static volatile AtomicInteger numberOfScanBitmapAndAudioThreads = new AtomicInteger(0);
    public static volatile int maxNumberOfScanBitmapAndAudioThreadsAllowed = 4;    //2

    public static volatile AtomicInteger numberOfCallShazamAPIThreads = new AtomicInteger(0);
    public static volatile int maxNumberOfCallShazamAPIThreadsAllowed = 4;    //2

    public static int baseColor = Color.parseColor("#FFF07828");
    public static int secondaryBaseColor = Color.parseColor("#FFF07828");
    public static int contrastColor = Color.parseColor("#FF0F87D7");
    public static int canvasColor = Color.parseColor("#FFF07828");

    public RecyclerView nowPlayingRecyclerView;
    LinearLayoutManager nowPlayingLayoutManager;
    public nowPlayingRecyclerViewAdapter nowPlayingRecyclerViewAdapter;
    SnapHelper nowPlayingSnapHelper;
    public static boolean clickedNowPlayingCard = false;

    MainActivity mainActivity;
    public static volatile boolean isActivityVisible = false;

    public static AppDatabase db;
    public static String thisMonthAndHalf;
    public static float gammaForFavScore = 0.9f;
    public static int totalTimeListened = 0;
    public static int lastPlaybackStartPosition = 0;

    /*public native String helloWorld();
    public native String helloWorrrrd();

    static {
        System.loadLibrary("nativemodule");
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        maxNumberOfScanBitmapAndAudioThreadsAllowed = Runtime.getRuntime().availableProcessors();
        weakReferencesToFragments = WeakReferencesToFragments.getInstance();
        weakReferencesToFragments.saveActivity(this);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        keepTrackOfFrequentSongs = sharedPreferences.getBoolean("keepTrackOfFrequentSongs",true);

        //Toast.makeText(getApplicationContext(),helloWorrrrd(),Toast.LENGTH_SHORT).show();

        r = getResources();
        DisplayMetrics displayMetrics = r.getDisplayMetrics();
        displayHeightInPixels = displayMetrics.heightPixels;
        displayWidthInPixels = displayMetrics.widthPixels;
        dpToPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                displayMetrics
        );

        lowResDefaultBitmap = BitmapFactory.decodeResource(r,R.drawable.low_res_app_icon2_300x300);

        myCanvas = findViewById(R.id.myCanvas);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // Sets the app fullscreen

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(selectedListener);

        if (SpecificPlaylistFragment.name.equals(currentFragmentInLaunchFragment) || SongsFragment.name.equals(currentFragmentInLaunchFragment)
                || SettingsFragment.name.equals(currentFragmentInLaunchFragment) || MoreFragment.name.equals(currentFragmentInLaunchFragment)){
            bottomNavigationView.setVisibility(View.GONE);
        }

        nowPlayingRecyclerView = findViewById(R.id.nowPlayingRecyclerView);
        nowPlayingLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        nowPlayingRecyclerView.setLayoutManager(nowPlayingLayoutManager);
        nowPlayingRecyclerView.setItemViewCacheSize(0);
        nowPlayingRecyclerViewAdapter = new nowPlayingRecyclerViewAdapter(this);
        nowPlayingRecyclerView.setAdapter(nowPlayingRecyclerViewAdapter);
        nowPlayingSnapHelper = new LinearSnapHelper();
        nowPlayingSnapHelper.attachToRecyclerView(nowPlayingRecyclerView);
        nowPlayingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    int pos = nowPlayingLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (pos!=RecyclerView.NO_POSITION) {
                        int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
                        if ((index==0 && pos==1) || pos==2){
                            MediaControllerCompat.getMediaController(mainActivity).getTransportControls().skipToNext();
                        }else if (index>0 && pos==0){
                            MediaControllerCompat.getMediaController(mainActivity).sendCommand("directlySkipToPrevious",null,null);
                        }
                    }
                }
            }
        });


        parentFragmentManager = getSupportFragmentManager();

        scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(scanButtonOnClickListener);

        toolbar = findViewById(R.id.toolbar);
        uncheckedCheckBox = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_unchecked_check_box_24);
        checkedCheckBox = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_checked_check_box_24);
        defaultIcon = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_baseline_image_24);

        Menu menu = toolbar.getMenu();
        exit = menu.findItem(R.id.exit);
        saveToPlaylist = menu.findItem(R.id.saveToPlaylist);
        clearQueue = menu.findItem(R.id.clearQueue);
        scrollToTop = menu.findItem(R.id.scrollToTop);

        toolbar.setOnMenuItemClickListener(menuItemClickListener);
        toolbar.setNavigationIcon(defaultIcon);

        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            /*mlp.leftMargin = insets.left;
            mlp.bottomMargin = insets.bottom;
            mlp.rightMargin = insets.right;*/
            mlp.topMargin = insets.top;
            v.setLayoutParams(mlp);

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        }); // Applies margin for the toolbar from the top due to the status bar because the app is fullscreen

        if (savedInstanceState==null) {
            weakReferencesToFragments.saveFragmentStack(fragmentStack);
            pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.HOME_FRAGMENT));
        }else {
            fragmentStack = (ArrayList<MyFragmentSuperClass>) weakReferencesToFragments.fragmentStack.get();
            for (int i=0;i<fragmentStack.size();++i){
                if (fragmentStack.get(i) instanceof HomeFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.HOME_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof MoreFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.MORE_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof NowPlayingFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.NOW_PLAYING_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof PlaylistsFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.PLAYLISTS_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof QueueFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.QUEUE_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof SettingsFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.SETTINGS_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof SongsFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.SONGS_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof SpecificPlaylistFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.SPECIFIC_PLAYLIST_FRAGMENT));
                }else if (fragmentStack.get(i) instanceof ArtistsFragment){
                    fragmentStack.set(i,(MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.ARTISTS_FRAGMENT));
                }
            }
            fragmentStack.get(fragmentStack.size()-1).updateData();
        }

        if (intent==null) {
            Context context = getApplicationContext();
            intent = new Intent(context, MediaPlaybackService.class); // Build the intent for the service
            //context.startForegroundService(intent);
        }

        // Create MediaBrowserServiceCompat
        mediaBrowserCompat = new MediaBrowserCompat(this,
                new ComponentName(this, MediaPlaybackService.class),
                connectionCallbacks,
                null); // optional Bundle


        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "AppDatabase").build();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM");
        long now = System.currentTimeMillis();
        String month = simpleDateFormat.format(new Date(now));
        Date d = null;
        try {
            d = simpleDateFormat.parse(month);
            long millis = d.getTime();
            if ((now-millis)<15*24*60*60*1000){
                thisMonthAndHalf=month+"/1";
            }else {
                thisMonthAndHalf=month+"/2";
            }
        } catch (ParseException e) {
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
            e.printStackTrace();
        }


        if (AllSongsExceptArchived.size() == 0) {
            ScanDeviceForAudio();
        } else {
            scanButton.setVisibility(View.GONE);
            getMessage(NOW_PLAYING_DATA_SET_CHANGED,-1,null, null);
        }
    }

    private final BottomNavigationView.OnItemSelectedListener selectedListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            int id = menuItem.getItemId();

            if (id==R.id.navigation_home && !currentFragmentInLaunchFragment.equals(HomeFragment.name)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.HOME_FRAGMENT));
                return true;
            }else if (id==R.id.navigation_playlists && !currentFragmentInLaunchFragment.equals(PlaylistsFragment.name)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.PLAYLISTS_FRAGMENT));
                return true;
            }else if (id==R.id.navigation_queue && !currentFragmentInLaunchFragment.equals(QueueFragment.name)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.QUEUE_FRAGMENT));
                return true;
            }
            return false;
        }
    };

    public void hideOtherFragmentsAndShowThisFragment(MyFragmentSuperClass fragment){

        FragmentTransaction transaction = parentFragmentManager.beginTransaction();
        if (fragment instanceof NowPlayingFragment){
            transaction.setCustomAnimations(R.anim.now_playing_fragment_entry, R.anim.fade_out);
        }else {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        }

        MyFragmentSuperClass fragment1;
        for (WeakReference<MyFragmentSuperClass> wrf:WeakReferencesToFragments.arrayListOfFragments){
            if (wrf==null){
                continue;
            }
            fragment1 = wrf.get();
            if (fragment1==null){
                continue;
            }
            if (fragment1==fragment){
                if (fragment.isAdded()) {
                    if (fragment.isHidden()) {
                        transaction.show(fragment);
                    }
                }else {
                    transaction.add(R.id.parentFragmentHolder, fragment, currentFragmentInLaunchFragment +"Tag");
                }
            }else {
                if (fragment1.isAdded()) {
                    if (!fragment1.isHidden()) {
                        transaction.hide(fragment1);
                    }
                }
            }
        }

        transaction.runOnCommit(new Runnable() {
            @Override
            public void run() {
                fragment.updateData();      // So that data is updated only after the fragment is visible. (Not sure about reliability)
            }
        });

        transaction.commit();
        //fragment.updateData();
    }

    public void pushToFragmentStack(MyFragmentSuperClass fragment){
        if (fragmentStack.size()>0 && ((fragment instanceof HomeFragment) || (fragment instanceof PlaylistsFragment) || (fragment instanceof QueueFragment))){
            fragmentStack.set(0,fragment);
        }else {
            fragmentStack.add(fragment);
        }
        hideOtherFragmentsAndShowThisFragment(fragment);
    }

    public boolean popFragmentStack(){
        if (fragmentStack.size()<=1){
            return true;
        }
        fragmentStack.remove(fragmentStack.size()-1);
        if (currentFragmentInLaunchFragment.equals(NowPlayingFragment.name)){
            FragmentTransaction transaction = parentFragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.now_playing_fragment_entry, R.anim.now_playing_fragment_exit);
            transaction.hide((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.NOW_PLAYING_FRAGMENT));
            transaction.commit();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideOtherFragmentsAndShowThisFragment(fragmentStack.get(fragmentStack.size()-1));
                }
            },animationDuration);
        }else {
            hideOtherFragmentsAndShowThisFragment(fragmentStack.get(fragmentStack.size() - 1));
        }
        return false;
    }


    @Override
    protected void onStart() {
        super.onStart();

        getMessage(UPDATE_NOW_PLAYING_POSITION,-1,null,null);

        isActivityVisible = true;
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                MediaPlaybackService.startPlaybackPositionThread();
            }
        }catch (Exception e){
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
        }
        weakReferencesToFragments.getMessage(QueueFragment.name, NOW_PLAYING_SONG_CHANGED,-1,null);
        weakReferencesToFragments.getMessage(NowPlayingFragment.name, NOW_PLAYING_SONG_CHANGED,-1,null);
        getMessage(NOW_PLAYING_THUMBNAIL_FOUND,-1,null, null); // canvas doesn't update if the activity is in stopped state when the
        // songs are changing. so when the activity is visible again, canvas is updated.
        mediaBrowserCompat.connect();
        if (SelectedSongs.size()>0) {
            IsSelectingSongs(true);
            toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
        }else {
            IsSelectingSongs(false);
            toolbar.setSubtitle(null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityVisible = false;
        if (MediaControllerCompat.getMediaController(MainActivity.this) != null) {
            MediaControllerCompat.getMediaController(MainActivity.this).unregisterCallback(controllerCallback);
        }
        mediaBrowserCompat.disconnect();

        songPlayingWhenActivityStops = queueObject.nowPlaying;
        System.out.println("On Stop!!!!!!!!!!!!!!!!!!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        System.out.println("Destroy!!!!!!!!!!!!!!!!!!!");
    }

    @Override
    public void onBackPressed() {
        if (popFragmentStack()){
            super.onBackPressed();
        }
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {

            // Get the token for the MediaSession
            MediaSessionCompat.Token token = mediaBrowserCompat.getSessionToken();

            // Create a MediaControllerCompat
            MediaControllerCompat mediaController =
                    null;
            try {
                mediaController = new MediaControllerCompat(MainActivity.this, // Context
                        token);
            } catch (RemoteException e) {
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }

            // Save the controller
            MediaControllerCompat.setMediaController(MainActivity.this, mediaController);

            // Finish building the UI
            buildTransportControls();
            System.out.println("Connected!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
            System.out.println("Not Connected!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    };

    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            weakReferencesToFragments.getMessage(QueueFragment.name, NOW_PLAYING_SONG_CHANGED,-1,null);
            weakReferencesToFragments.getMessage(NowPlayingFragment.name, NOW_PLAYING_SONG_CHANGED,-1,null);
            metadataChanged(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (!seeking) {
                weakReferencesToFragments.playbackStateChanged(getMediaController().getPlaybackState().getState());
            }
        }

        @Override
        public void onSessionDestroyed() {
            System.out.println("Session Destroyed!!!!!!!!!!!!!!!!!!!!!!!!!!");
            mediaBrowserCompat.disconnect();
            // maybe schedule a reconnection using a new MediaBrowser instance
        }
    };

    public void metadataChanged(MediaMetadataCompat metadata){
        Bitmap src;
        if (metadata==null){ src= lowResDefaultBitmap;}
        else{ src = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);}

        /*Palette.from(src).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {
                boolean swatchChanged = true;

                /*if (palette!=null) {
                    if (palette.getMutedSwatch() != null) {
                        swatch = palette.getMutedSwatch();
                    } else if (palette.getLightMutedSwatch() != null) {
                        swatch = palette.getLightMutedSwatch();
                    } else if (palette.getDarkMutedSwatch() != null) {
                        swatch = palette.getDarkMutedSwatch();
                    } else if (palette.getDominantSwatch() != null) {
                        swatch = palette.getDominantSwatch();
                    } else if (palette.getVibrantSwatch() != null) {
                        swatch = palette.getVibrantSwatch();
                    } else if (palette.getLightVibrantSwatch() != null) {
                        swatch = palette.getLightVibrantSwatch();
                    } else if (palette.getDarkVibrantSwatch() != null) {
                        swatch = palette.getDarkVibrantSwatch();
                    } else {
                        swatchChanged = false;
                    }
                }else {
                    swatchChanged = false;
                }*/

                /*int color;
                if (swatchChanged) {
                    color = swatch.getRgb();
                } else {
                    color = Color.parseColor("#FFF07828");
                }
                myCanvas.SetColor(color);*/

            /*}
        });*/

        //int color = getAvgColor(src);
        //int color = getDominantColor(src);
        int color;
        if (queueObject==null || queueObject.nowPlaying==null){
            color = getDominantColorDiscourageSkinAndGrey(src);
        }else {
            color = queueObject.nowPlaying.getWrapperForCommonDataSongItem().getThemeColor();
        }

        float[] hsv = new float[3];
        Color.colorToHSV(color,hsv);

        // HSV color model
        // hue is color: 0-60 red, 60-120 yellow, 120-180 green, 180-240 cyan, 240-300 blue, 300-360 magenta
        // saturation is how much of color. 0% gives a greyscale value, 100% gives pure color
        // value is brightness, 0% is full black, 100% is full brightness.

        float[] checked = new float[3];
        checked[0] = hsv[0];
        checked[1] = (hsv[1]==0) ? 0:1;;//Math.min(hsv[1],0.5f);
        checked[2] = 1;
        secondaryBaseColor = Color.HSVToColor(checked);
        canvasColor = secondaryBaseColor;

        checked[2]=0.5f;
        baseColor = Color.HSVToColor(checked);

        checked[2] = 0.8f;
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked}, // state_checked
                new int[] { }  //
        };
        int[] colors = new int[] {
                Color.HSVToColor(checked),
                baseColor
        };

        checked[0] = (180 + checked[0])%360;
        contrastColor = Color.HSVToColor(checked);

        myCanvas.SetColor(canvasColor);

        ColorStateList myColorList = new ColorStateList(states, colors);
        bottomNavigationView.setItemIconTintList(myColorList);
        bottomNavigationView.setItemTextColor(myColorList);

        DrawableCompat.setTint(defaultIcon,baseColor);
        DrawableCompat.setTint(checkedCheckBox,baseColor);
        DrawableCompat.setTint(uncheckedCheckBox,baseColor);
        DrawableCompat.setTint(exit.getIcon(),baseColor);
        DrawableCompat.setTint(scrollToTop.getIcon(),baseColor);
        DrawableCompat.setTint(Objects.requireNonNull(toolbar.getOverflowIcon()),baseColor);
        toolbar.setSubtitleTextColor(baseColor);
        nowPlayingRecyclerViewAdapter.setPaintColor();
        weakReferencesToFragments.getMessage(HomeFragment.name, UPDATE_ITEM_COLORS,-1,null);
        weakReferencesToFragments.getMessage(SongsFragment.name, UPDATE_ITEM_COLORS,-1,null);
        weakReferencesToFragments.getMessage(PlaylistsFragment.name, UPDATE_ITEM_COLORS,-1,null);
        weakReferencesToFragments.getMessage(QueueFragment.name, UPDATE_ITEM_COLORS,-1,null);
        weakReferencesToFragments.getMessage(NowPlayingFragment.name, UPDATE_ITEM_COLORS,-1,null);


    }

    void buildTransportControls() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable navigationIcon = toolbar.getNavigationIcon();
                if (navigationIcon==uncheckedCheckBox){
                    ArrayList<SongItem> songItems = ((SongsFragment) weakReferencesToFragments.songsFragment.get()).songsFragmentAllSongs;
                    for (SongItem songItem:songItems){
                        if (songItem.isSelected()){
                            SelectedSongs.remove(songItem);
                        }
                        songItem.setIsSelected(true);
                    }
                    SelectedSongs.addAll(songItems);
                    toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
                    toolbar.setNavigationIcon(checkedCheckBox);
                    updateSongsRecyclerViewItemConstraintLayout = true;
                    weakReferencesToFragments.getMessage(currentFragmentInLaunchFragment, UPDATE_SELECTED_ITEMS,-1,null);
                }else if (navigationIcon==checkedCheckBox){
                    ArrayList<SongItem> songItems = ((SongsFragment) weakReferencesToFragments.songsFragment.get()).songsFragmentAllSongs;
                    for (SongItem songItem:songItems){
                        SelectedSongs.remove(songItem);
                        songItem.setIsSelected(false);
                    }
                    if (SelectedSongs.size()>0) {
                        toolbar.setSubtitle(String.valueOf(SelectedSongs.size()));
                        toolbar.setNavigationIcon(uncheckedCheckBox);
                    }else {
                        toolbar.setSubtitle(null);
                        toolbar.setNavigationIcon(defaultIcon);
                    }
                    updateSongsRecyclerViewItemConstraintLayout = true;
                    weakReferencesToFragments.getMessage(currentFragmentInLaunchFragment, UPDATE_SELECTED_ITEMS,-1,null);
                }else {
                    //myCanvas.CycleThrough();
                }
            }
        });

        DrawableCompat.setTint(Objects.requireNonNull(toolbar.getNavigationIcon()),baseColor);

        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(MainActivity.this);

        // Display the initial state
        MediaMetadataCompat metadata = mediaController.getMetadata();
        PlaybackStateCompat pbState = mediaController.getPlaybackState();
        weakReferencesToFragments.playbackStateChanged(pbState.getState());

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);

    }

    private final Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();
            if (id==R.id.exit){
                getApplicationContext().stopService(intent); // removing service from foreground and making the notification dismissible also possible instead of destroying it all completely
                finish();
            }else if (id==R.id.saveToPlaylist){
                if (queueObject.QueueArray.size()>0) {
                    weakReferencesToFragments.getMessage(MainActivity.name, SHOW_PLAYLISTS_FRAGMENT, -1, null);
                    saveToPlaylistClicked = true;
                    Intent intent = new Intent(MainActivity.this, CreatePlaylistActivity.class);
                    intent.putExtra("Requirement", "CreatePlaylist");
                    createPlaylistActivityResultLauncher.launch(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"Queue is empty.",Toast.LENGTH_SHORT).show();
                }

            }else if (id==R.id.clearQueue){
                queueObject.updateQueueObjectFullyExceptPreviousSong(new ArrayList<SongItem>(),0);
                resetMediaPlayerAndUpdateFavScoreAfterNowPlayingSongIsSet();
                weakReferencesToFragments.getMessage(QueueFragment.name, UPDATE_QUEUE, -1, null);
                SaveQueue(getApplicationContext());
                mediaSession.getController().getTransportControls().stop();

            }else if (id==R.id.scrollToTop){
                if (currentFragmentInLaunchFragment.equals(SpecificPlaylistFragment.name)) {
                    weakReferencesToFragments.getMessage(SpecificPlaylistFragment.name, SCROLL_TO_TOP, -1, null);
                }else if (currentFragmentInLaunchFragment.equals(SongsFragment.name)){
                    weakReferencesToFragments.getMessage(SongsFragment.name, SCROLL_TO_TOP, -1, null);
                }else if (currentFragmentInLaunchFragment.equals(QueueFragment.name)){
                    weakReferencesToFragments.getMessage(QueueFragment.name, SCROLL_TO_TOP, -1, null);
                }

            }
            return false;
        }
    };

    private final View.OnClickListener scanButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ScanButton();
        }
    };

    public void ScanButton(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            scanButton.setVisibility(View.GONE);
            scanForAudioThread runnable = new scanForAudioThread(db);
            new Thread(runnable).start();
        }
        else {
            DialogFragment allowPermissionDialogFragment = new AllowPermissionDialog();
            allowPermissionDialogFragment.show(getSupportFragmentManager(),"AllowPermissionDialogFragment");
        }
    }

    public void ScanDeviceForAudio(){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            scanButton.setVisibility(View.GONE);
            scanForAudioThread runnable = new scanForAudioThread(db);
            new Thread(runnable).start();
        }
        else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onAllowPermissionDialogPositiveClick(DialogFragment dialog) {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onAllowPermissionDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(getApplicationContext(),"App cannot access audio files due to lack of permission",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void IsSelectingSongs(boolean selectingSongsDummy) {

        if (!selectingSongsDummy){
            toolbar.setSubtitle(null);
        }
        if (currentFragmentInLaunchFragment.equals(SongsFragment.name)) {
            getMessage(SET_TOOLBAR_ICON, -1, null, null);
        }
    }

    @Override
    public void RenamingPlaylist(String playlistName, int positionIndex) {
        Intent intent = new Intent(getApplicationContext(), CreatePlaylistActivity.class);
        intent.putExtra("Requirement","RenamePlaylist");
        intent.putExtra("PlaylistName",playlistName);
        intent.putExtra("PlaylistPosition",positionIndex);
        createPlaylistActivityResultLauncher.launch(intent);
    }

    public void getMessage(Enum message, int position, SongItem songItem, Bundle bundle){
        if (LOAD_SPECIFIC_PLAYLIST_FRAGMENT.equals(message)) {
            if (!SpecificPlaylistFragment.name.equals(currentFragmentInLaunchFragment)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.SPECIFIC_PLAYLIST_FRAGMENT));
            }
        }else if (NOW_PLAYING_THUMBNAIL_FOUND.equals(message)){
            nowPlayingRecyclerViewAdapter.notifyItemChanged(Math.min(1,queueObject.QueueArray.indexOf(queueObject.nowPlaying)),R.id.thumbnailNowPlayingCardView);

            MediaMetadataCompat mediaMetadataCompat;
            if (queueObject.nowPlaying==null){
                mediaMetadataCompat = null;
            }else {
                mediaMetadataCompat = new MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, queueObject.nowPlaying.getSongName())
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, queueObject.nowPlaying.getArtist())
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, queueObject.nowPlaying.getThumbnail())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, queueObject.nowPlaying.getDuration())
                        .build();
            }
            metadataChanged(mediaMetadataCompat);
        }else if (SHOW_PLAYLISTS_FRAGMENT.equals(message)){
            bottomNavigationView.setSelectedItemId(R.id.navigation_playlists);
        }else if (SHOW_SONGS_FRAGMENT.equals(message)){
            if (!SongsFragment.name.equals(currentFragmentInLaunchFragment)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.SONGS_FRAGMENT));
            }
        }else if (SHOW_SETTINGS_FRAGMENT.equals(message)){
            if (!SettingsFragment.name.equals(currentFragmentInLaunchFragment)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.SETTINGS_FRAGMENT));
            }
        }else if (SHOW_MORE_FRAGMENT.equals(message)){
            if (!MoreFragment.name.equals(currentFragmentInLaunchFragment)){
                pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.MORE_FRAGMENT));
            }
        }else if(SET_TOOLBAR_ICON.equals(message)){
            if (SelectedSongs.size()>0){
                ArrayList<SongItem> songItems = ((SongsFragment) weakReferencesToFragments.songsFragment.get()).songsFragmentAllSongs;
                for (SongItem songItem1:songItems){
                    if (!songItem1.isSelected()){
                        toolbar.setNavigationIcon(uncheckedCheckBox);
                        return;
                    }
                }
                toolbar.setNavigationIcon(checkedCheckBox);
            }else {
                toolbar.setNavigationIcon(defaultIcon);
            }
        }else if(SET_TOOLBAR_ICON_TO_DEFAULT.equals(message)){
            toolbar.setNavigationIcon(defaultIcon);
        }else if(CREATE_NEW_PLAYLIST.equals(message)){
            Intent intent = new Intent(MainActivity.this,CreatePlaylistActivity.class);
            intent.putExtra("Requirement","CreatePlaylist");
            createPlaylistActivityResultLauncher.launch(intent);
        }else if(NOW_PLAYING_DATA_SET_CHANGED.equals(message)){
            nowPlayingRecyclerViewAdapter.NotifyDataSetChanged();
            int index = queueObject.QueueArray.indexOf(queueObject.nowPlaying);
            if (index==0) {
                nowPlayingLayoutManager.scrollToPosition(0);
            }else {
                nowPlayingLayoutManager.scrollToPosition(1);
            }
        }else if(UPDATE_NOW_PLAYING_POSITION.equals(message)){
            nowPlayingRecyclerViewAdapter.updatePosition();
        }else if(SHOW_NOW_PLAYING_FRAGMENT.equals(message)){
            clickedNowPlayingCard = true;
            pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.NOW_PLAYING_FRAGMENT));
        }else if(UPDATE_STOP_AFTER_THIS.equals(message)){
            int first = nowPlayingLayoutManager.findFirstVisibleItemPosition();
            int last = nowPlayingLayoutManager.findLastVisibleItemPosition();
            for (int i=first; i<=last;++i) {
                nowPlayingRecyclerViewAdapter.notifyItemChanged(i,R.id.stopAfterThisNowPlaying);
            }
        }else if(UPDATE_FAV_SCORE.equals(message)){
            if (keepTrackOfFrequentSongs) {
                if (songItem != null && position > 0) {         //  timeListened has to be positive.
                    songItem.setFavScore(songItem.getFavScore() + (float) position / songItem.getDuration());
                    updateFavScore runnable = new updateFavScore(songItem, position);
                    new Thread(runnable).start();
                    totalTimeListened = 0;
                    lastPlaybackStartPosition = 0;
                }
            }else {
                Toast.makeText(getApplicationContext(),"Didn't keep track",Toast.LENGTH_SHORT).show();
            }
        }else if(UPDATE_LYRICS.equals(message)){
            (new Thread(new updateLyrics(bundle))).start();
        }else if(SHOW_ARTISTS_FRAGMENT.equals(message)){
            pushToFragmentStack((MyFragmentSuperClass) weakReferencesToFragments.getFragment(EnumFragments.ARTISTS_FRAGMENT));
        }
    }

    class updateFavScore implements Runnable{
        SongItem item;
        int timeListened;

        public updateFavScore(SongItem item, int timeListened) {
            this.item = item;
            this.timeListened = timeListened;
        }

        @Override
        public void run() {
            SongDetailsDao songDetailsDao = db.SongDetailsDao();
            SongDetails songDetails = songDetailsDao.getForUri(item.getUri());
            if (songDetails!=null) {
                songDetails.monthHalf0 = songDetails.monthHalf0 + (float) timeListened/item.getDuration();
                songDetailsDao.update(songDetails);
            }
        }
    }
    class updateLyrics implements Runnable{

        Bundle bundle;
        public updateLyrics(Bundle bundle) {
            this.bundle=bundle;
        }

        @Override
        public void run() {
            SongDetailsDao songDetailsDao = db.SongDetailsDao();
            SongDetails songDetails = songDetailsDao.getForUri(bundle.getString("SongUri"));
            if (songDetails!=null) {
                System.out.println("NOT NULL!!!!!!!!!!!!!!!!!!!!");
                songDetails.lyrics = bundle.getString("NewLyrics");
                songDetailsDao.update(songDetails);
                weakReferencesToFragments.getMessage(NowPlayingFragment.name,UPDATE_LYRICS,-1,null);
            }else {
                System.out.println("NULL!!!!!!!!!!!!!!!!!!!! ");
            }
        }
    }

    class scanForAudioThread implements Runnable {
        AppDatabase db;

        public scanForAudioThread(AppDatabase db) {
            this.db = db;
        }

        @Override
        public void run() {
            getSortOrder();
            getAllAudioFiles();
            Handler newhandler2 = new Handler(Looper.getMainLooper());
            newhandler2.post(new Runnable() {
                @Override
                public void run() {
                    weakReferencesToFragments.getMessage(SongsFragment.name, NOTIFY_DATA_SET_CHANGED,-1,null);
                }
            });
            getPlaylists();
            newhandler2.post(new Runnable() {
                @Override
                public void run() {
                    weakReferencesToFragments.getMessage(PlaylistsFragment.name, NOTIFY_DATA_SET_CHANGED,-1,null);
                }
            });

            ReadQueue(getApplicationContext());
            newhandler2.post(new Runnable() {
                @Override
                public void run() {
                    weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
                    weakReferencesToFragments.getMessage(MainActivity.name, UPDATE_NOW_PLAYING_POSITION,-1,null);
                }
            });

            ReadDatabaseExceptForAPICallData runnable = new ReadDatabaseExceptForAPICallData(db);
            new Thread(runnable).start();
        }
    }

    public void getSortOrder(){

        File file = new File(getApplicationContext().getDir("Preferences", MODE_PRIVATE),"SortData");

        try {
            file.createNewFile();
            String line;
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            while (((line = br.readLine()) != null)) {
                if (!line.equals("")) {
                    sortSongsByWhat = Integer.parseInt(line.trim().substring(0,line.indexOf("*")));
                    sortSongsByMode = Integer.parseInt(line.trim().substring(line.indexOf("*")+1));
                }
            }
        } catch (IOException e) {
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
            e.printStackTrace();
        }
    }

    public void getAllAudioFiles(){

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri,null,null,null,null);

        AllSongsExceptArchived.clear();

        if (songCursor != null && songCursor.moveToFirst()) {
            //int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songuri = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            //int songDateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
            long creationTime = 0;
            long modificationTime = 0;
            do {
                String uri = songCursor.getString(songuri);

                try {
                    creationTime = Files.readAttributes(Paths.get(uri), BasicFileAttributes.class).creationTime().toMillis();
                } catch (IOException e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }

                try {
                    modificationTime = Files.readAttributes(Paths.get(uri), BasicFileAttributes.class).lastModifiedTime().toMillis();
                } catch (IOException e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }

                //
               /* AllSongs.add(new SongItem((new File(uri)).getName(),songCursor.getString(songArtist),uri, Integer.parseInt(songCursor.getString(songDuration))
                        , null,songCursor.getInt(songDateAdded) * 1000L,dateFormat.format(new Date(songCursor.getInt(songDateAdded) * 1000L))));*/

                // changes when a file is moved even within the same storage (what i observed, not official doc).

                try {

                    String filename = (new File(uri)).getName().trim();
                    AllSongsExceptArchived.add(new SongItem(filename.substring(0,filename.lastIndexOf(".")),songCursor.getString(songArtist),uri, Integer.parseInt(songCursor.getString(songDuration))
                            ,lowResDefaultBitmap,targetLoudness,creationTime,dateFormat.format(new Date(creationTime)), modificationTime));

                    // doesn't change when a file is moved within the same storage (what i observed, not official doc).


                }catch (Exception e){
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                }
            }while (songCursor.moveToNext());
        }

        try {
            songCursor.close();
        } catch (Exception e) {
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
            e.printStackTrace();
        }

        File playlistsOrderFile = new File(getApplicationContext().getDir("Archive", MODE_PRIVATE), "Archive.ser");
        try {
            playlistsOrderFile.createNewFile();
            FileInputStream fileIn = new FileInputStream(playlistsOrderFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);

            archivePlaylistItem = (PlaylistItem) in.readObject();

            in.close();
            fileIn.close();
        } catch (Exception e) {
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
            e.printStackTrace();
        }

        if (archivePlaylistItem ==null){
            ArrayList<SongItem> arr = new ArrayList<>();
            archivePlaylistItem = new PlaylistItem("Archive", arr,0,"00:00:00");
            try {
                FileOutputStream fileOut = new FileOutputStream(playlistsOrderFile);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(archivePlaylistItem);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }
        }

        archivePlaylistItem.setPlaylistName("Archive");

        boolean updateArchive = false;
        SongItem item;
        SongItem songItem;
        ArrayList<SongItem> archivedSongs = archivePlaylistItem.getSongItemsPlaylist();
        for (int i=0;i<archivedSongs.size();++i){
            songItem = archivedSongs.get(i);
            item = doesSongExist(songItem.getSongName(),songItem.getUri(),String.valueOf(songItem.getDuration()));
            if (item==null){
                updateArchive = true;
                archivedSongs.remove(i);
                --i;
            }else {
                archivedSongs.set(i,item);
                AllSongsExceptArchived.remove(item);
            }
        }

        if (updateArchive){
            try {
                FileOutputStream fileOut = new FileOutputStream(playlistsOrderFile);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(archivePlaylistItem);
                out.close();
                fileOut.close();
            } catch (IOException e) {
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }
        }


        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        String alphabet2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<SongItem> temporary = new ArrayList<>();
        SongItem song;
        for (int i = 0; i< AllSongsExceptArchived.size(); ++i){
            song = AllSongsExceptArchived.get(i);
            if (!alphabet.contains(song.getSongName().substring(0,1))){
                temporary.add(song);
                AllSongsExceptArchived.remove(i);
                --i;
            }
        }

        temporary.sort(new Comparator<SongItem>() {
            @Override
            public int compare(SongItem songItem, SongItem t1) {
                return songItem.getSongName().compareToIgnoreCase(t1.getSongName());
            }
        });

        AllSongsExceptArchived.sort(new Comparator<SongItem>() {
            @Override
            public int compare(SongItem songItem, SongItem t1) {
                return songItem.getSongName().compareToIgnoreCase(t1.getSongName());
            }
        });

        AllSongsExceptArchived.addAll(0,temporary);
        temporary.clear();

        AllSongs.addAll(AllSongsExceptArchived);
        AllSongs.addAll(archivedSongs);
    }

    public void getArtists(){
        ArrayList<String> artistNames = new ArrayList<>();

        artistArrayList.clear();

        WrapperForCommonDataSongItem wrapper;
        String artistName;

        for (SongItem songItem: AllSongsExceptArchived){
            wrapper = songItem.getWrapperForCommonDataSongItem();
            if (wrapper.getArtistApi()==null){
                artistName = "unknown";
            }else {
                artistName = wrapper.getArtistApi();
            }
            if (!artistNames.contains(artistName)){
                artistNames.add(artistName);
                artistArrayList.add(new ArtistItem(artistName,lowResDefaultBitmap,new ArrayList<>(Arrays.asList(songItem))));
                //artistArrayList.get(artistArrayList.size()-1).getArtistSongs().add(songItem);
            }else {
                artistArrayList.get(artistNames.indexOf(artistName)).addSong(songItem);
            }
        }

        for (ArtistItem artistItem : artistArrayList){
            for (SongItem item: AllSongsExceptArchived){
                if (!artistItem.getArtistSongs().contains(item) &&
                        (
                                (item.getWrapperForCommonDataSongItem().getSongNameApi() != null && item.getWrapperForCommonDataSongItem().getSongNameApi().toLowerCase().contains(artistItem.getArtistName().toLowerCase()))
                                || (item.getWrapperForCommonDataSongItem().getArtistApi() != null && item.getWrapperForCommonDataSongItem().getArtistApi().toLowerCase().contains(artistItem.getArtistName().toLowerCase()))
                                || item.getSongName().toLowerCase().contains(artistItem.getArtistName().toLowerCase())
                                || (!"<unknown>".equals(item.getArtist()) && item.getArtist().toLowerCase().contains(artistItem.getArtistName().toLowerCase()))
                        )
                ) {
                    artistItem.addSong(item);
                    artistArrayList.get(artistNames.indexOf("unknown")).removeSong(item);
                }
            }
        }

        artistArrayList.sort(new Comparator<ArtistItem>() {
            @Override
            public int compare(ArtistItem artistItem, ArtistItem t1) {
                return Integer.compare(t1.getArtistSongs().size(),artistItem.getArtistSongs().size());
            }
        });

    }

    public void updateArtistThumbnails(){
        Random random = new Random();
        for (ArtistItem artistItem:artistArrayList){
            setArtistBitmap(artistItem,random, new ArtistItem(artistItem.getArtistName(),artistItem.getArtistBitmap(),new ArrayList<>(artistItem.getArtistSongs())));
        }
    }

    public void setArtistBitmap(ArtistItem artistItem, Random random, ArtistItem clone){
        if (clone.getArtistSongs().size()==0){
            artistItem.setArtistBitmap(lowResDefaultBitmap);
        }else {
            int index = random.nextInt(clone.getArtistSongs().size());
            Bitmap bitmap = clone.getArtistSongs().get(index).getThumbnail();
            if (bitmap != null && !lowResDefaultBitmap.sameAs(bitmap)) {
                artistItem.setArtistBitmap(bitmap);
            } else {
                clone.getArtistSongs().remove(index);
                setArtistBitmap(artistItem, random, clone);
            }
        }
    }

    public void getPlaylists(){

        allPlaylists.clear();

        File[] playlists = getDir("Playlists",MODE_PRIVATE).listFiles();

        ArrayList<PlaylistItem> tempAllPlaylists = new ArrayList<>();
        PlaylistItem playlist;
        ArrayList<SongItem> songs;
        int durationInSeconds;
        int duration;
        String filename;

        for (File file:playlists){
            try {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);

                playlist = (PlaylistItem) in.readObject();

                duration = 0;
                songs = playlist.getSongItemsPlaylist();
                for (int i = 0; i < songs.size(); ++i) {
                    SongItem item = songs.get(i);
                    item = doesSongExist(item.getSongName(), item.getUri(), String.valueOf(item.getDuration()));
                    if (item == null) {
                        songs.remove(i);
                        --i;
                    } else {
                        songs.set(i,item);
                        duration += item.getDuration();
                    }
                }

                playlist.setDuration(duration);
                durationInSeconds = duration / 1000;
                playlist.setDurationAsAString(theFormat.format(durationInSeconds / (60 * 60)) + ":" + theFormat.format(((durationInSeconds / 60) % 60)) + ":" + theFormat.format(durationInSeconds % (60)));
                filename = file.getName();
                playlist.setPlaylistName(filename.substring(0,filename.indexOf(".")));
                tempAllPlaylists.add(playlist);

                in.close();
                fileIn.close();
            } catch (Exception e) {
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }
        }

        File playlistsOrderFile = new File(getApplicationContext().getDir("Preferences", MODE_PRIVATE), "PlaylistsOrder");

        ArrayList<String> orderData = new ArrayList<>();
        try {
            playlistsOrderFile.createNewFile();
            String plylst;
            FileInputStream fis = new FileInputStream(playlistsOrderFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            while (((plylst = br.readLine()) != null)) {
                if (!plylst.equals("")) {
                    orderData.add(plylst);
                }
            }
            fis.close();
        } catch (IOException e) {
            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
            e.printStackTrace();
        }

        for (String name : orderData) {
            for (int i=0; i<tempAllPlaylists.size();++i) {
                if (tempAllPlaylists.get(i).getPlaylistName().equals(name)) {
                    allPlaylists.add(tempAllPlaylists.get(i));
                    tempAllPlaylists.remove(i);
                    break;
                }
            }
        }

        allPlaylists.addAll(tempAllPlaylists);
    }

    class ReadDatabaseExceptForAPICallData implements Runnable{
        AppDatabase db;
        boolean needToUpdateCurrentMonthAndHalf = false;

        public ReadDatabaseExceptForAPICallData(AppDatabase db) {
            this.db = db;
        }

        @Override
        public void run() {

            long initTime = System.currentTimeMillis();
            Handler handler = new Handler(Looper.getMainLooper());
            SongDetailsDao songDetailsDao = db.SongDetailsDao();

            ArrayList<String> UrisInDatabase = new ArrayList<>();
            ArrayList<SongDetails> songDetails = new ArrayList<>(songDetailsDao.getAll());

            CurrentMonthAndHalfDao currentMonthAndHalfDao = db.CurrentMonthAndHalfDao();
            List<CurrentMonthAndHalf> list = currentMonthAndHalfDao.getAll();
            if (list.size()==0){
                currentMonthAndHalfDao.insert(new CurrentMonthAndHalf(thisMonthAndHalf));
            }else {
                String saved = list.get(0).monthAndHalf;
                needToUpdateCurrentMonthAndHalf = thisMonthAndHalf != null && !thisMonthAndHalf.equals(saved);
                if (needToUpdateCurrentMonthAndHalf) {
                    currentMonthAndHalfDao.delete(list.get(0));
                    currentMonthAndHalfDao.insert(new CurrentMonthAndHalf(thisMonthAndHalf));
                }
            }

            for (int i=0;i<songDetails.size();++i){
                SongDetails details = songDetails.get(i);
                for (SongItem songItem: queueObject.QueueArray){
                    if (details.uri.equals(songItem.getUri()) && !songItem.getWrapperForCommonDataSongItem().thumbnailSet){
                        Bitmap bitmap = null;
                        float loudnessIndex=targetLoudness;

                        if (details.lastModified+5<songItem.getLastModifiedTimeSinceEpochInMillis()){// +5 in case of approximation errors.
                            while (numberOfScanBitmapAndAudioThreads.get()>=maxNumberOfScanBitmapAndAudioThreadsAllowed){
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                                    e.printStackTrace();
                                }
                            }
                            ScanBitmapAndAudio runnable = new ScanBitmapAndAudio(songItem, lowResDefaultBitmap, null, loudnessIndex, songDetailsDao, details, thumbnailPixels, floatToSetMaxGainTo10DeciBells);
                            new Thread(runnable).start();
                        }else {
                            loudnessIndex = details.loudnessIndex;
                            byte[] thumbnailByteArray = details.thumbnailArray;
                            if (thumbnailByteArray!=null) {
                                bitmap = BitmapFactory.decodeByteArray(thumbnailByteArray, 0, thumbnailByteArray.length);
                                if (lowResDefaultBitmap.sameAs(bitmap)) {
                                    details.thumbnailArray=null;
                                    songDetailsDao.update(details);
                                }
                            }
                            if (bitmap==null){
                                bitmap=lowResDefaultBitmap;
                            }
                            Bitmap finalBitmap = bitmap;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    songItem.setThumbnail(finalBitmap);
                                    songItem.getWrapperForCommonDataSongItem().setThemeColor(details.themeColor);
                                }
                            });
                            songItem.setLoudnessIndex((float) Math.max(loudnessIndex, floatToSetMaxGainTo10DeciBells));
                            songItem.setSubBass(details.subBass);
                            songItem.setBass(details.bass);
                            songItem.setLowerMidrange(details.lowerMidrange);
                            songItem.setMidrange(details.midrange);
                            songItem.setHigherMidrange(details.higherMidrange);
                            songItem.setPresence(details.presence);
                            songItem.setBrilliance(details.brilliance);
                        }
                        break;
                    }
                }
            }

            while (numberOfScanBitmapAndAudioThreads.get()>0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }
            }

            SongDetails details;
            for (int d=0;d<songDetails.size();++d){
                details=songDetails.get(d);
                boolean thisUriExistsInAllSongs = false;
                for (SongItem songItem: AllSongs){
                    if (details.uri.equals(songItem.getUri())){
                        thisUriExistsInAllSongs = true;

                        if (needToUpdateCurrentMonthAndHalf){
                            details.monthHalf11=details.monthHalf10;
                            details.monthHalf10=details.monthHalf9;
                            details.monthHalf9=details.monthHalf8;
                            details.monthHalf8=details.monthHalf7;
                            details.monthHalf7=details.monthHalf6;
                            details.monthHalf6=details.monthHalf5;
                            details.monthHalf5=details.monthHalf4;
                            details.monthHalf4=details.monthHalf3;
                            details.monthHalf3=details.monthHalf2;
                            details.monthHalf2=details.monthHalf1;
                            details.monthHalf1=details.monthHalf0;
                            details.monthHalf0=0;
                            songDetailsDao.update(details);
                        }
                        float[] arr = {details.monthHalf0,details.monthHalf1,details.monthHalf2,details.monthHalf3,details.monthHalf4,details.monthHalf5,
                                details.monthHalf6,details.monthHalf7,details.monthHalf8,details.monthHalf9,details.monthHalf10,details.monthHalf11};
                        float score=0;
                        for (int i=0;i<arr.length;++i){
                            score+=arr[i]*Math.pow(gammaForFavScore,i);
                        }
                        songItem.setFavScore(score);

                        if (!songItem.getWrapperForCommonDataSongItem().thumbnailSet) {
                            Bitmap bitmap = null;
                            float loudnessIndex=targetLoudness;

                            if (details.lastModified + 5 < songItem.getLastModifiedTimeSinceEpochInMillis()) {// +5 in case of approximation errors.
                                while (numberOfScanBitmapAndAudioThreads.get()>=maxNumberOfScanBitmapAndAudioThreadsAllowed){
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                                        e.printStackTrace();
                                    }
                                }
                                ScanBitmapAndAudio runnable = new ScanBitmapAndAudio(songItem, lowResDefaultBitmap, null, loudnessIndex, songDetailsDao, details, thumbnailPixels, floatToSetMaxGainTo10DeciBells);
                                new Thread(runnable).start();
                            } else {
                                loudnessIndex = details.loudnessIndex;
                                byte[] thumbnailByteArray = details.thumbnailArray;
                                if (thumbnailByteArray != null) {
                                    bitmap = BitmapFactory.decodeByteArray(thumbnailByteArray, 0, thumbnailByteArray.length);
                                    if (lowResDefaultBitmap.sameAs(bitmap)) {
                                        details.thumbnailArray=null;
                                        songDetailsDao.update(details);
                                    }
                                }
                                if (bitmap == null) {
                                    bitmap = lowResDefaultBitmap;
                                }
                                Bitmap finalBitmap = bitmap;
                                SongDetails finalDetails = details;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        songItem.setThumbnail(finalBitmap);
                                        songItem.getWrapperForCommonDataSongItem().setThemeColor(finalDetails.themeColor);
                                    }
                                });
                                songItem.setLoudnessIndex((float) Math.max(loudnessIndex, floatToSetMaxGainTo10DeciBells));
                                songItem.setSubBass(details.subBass);
                                songItem.setBass(details.bass);
                                songItem.setLowerMidrange(details.lowerMidrange);
                                songItem.setMidrange(details.midrange);
                                songItem.setHigherMidrange(details.higherMidrange);
                                songItem.setPresence(details.presence);
                                songItem.setBrilliance(details.brilliance);
                            }
                        }
                        break;
                    }
                }
                if (thisUriExistsInAllSongs) {
                    UrisInDatabase.add(details.uri);
                } else {
                    songDetailsDao.delete(details);
                    System.out.println("Deleted From database!!!!!!!!!!! " + details.uri);
                }
            }
            try {
                weakReferencesToFragments.getMessage(SongsFragment.name, NOTIFY_DATA_SET_CHANGED, -1, null);
            }catch (Exception e){
                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                e.printStackTrace();
            }

            while (numberOfScanBitmapAndAudioThreads.get()>0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }
            }

            for (SongItem item: AllSongs){
                String Uri = item.getUri();
                if (!UrisInDatabase.contains(Uri)){
                    while (numberOfScanBitmapAndAudioThreads.get()>=maxNumberOfScanBitmapAndAudioThreadsAllowed){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                            e.printStackTrace();
                        }
                    }
                    ScanBitmapAndAudioForUrisNotInDatabase runnable = new ScanBitmapAndAudioForUrisNotInDatabase(item, lowResDefaultBitmap, null
                            , targetLoudness, songDetailsDao, Uri, thumbnailPixels, floatToSetMaxGainTo10DeciBells);
                    new Thread(runnable).start();
                }
            }

            while (numberOfScanBitmapAndAudioThreads.get()>0){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                    e.printStackTrace();
                }
            }

            long totalTimeTaken = System.currentTimeMillis() - initTime;
            if (totalTimeTaken>2000){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Time taken: " + totalTimeTaken,Toast.LENGTH_SHORT).show();
                        System.out.println("Total Time Taken!!!!!!!!!!! : " + totalTimeTaken);
                        /*scanButton.setVisibility(View.VISIBLE);
                        scanButton.setText(String.valueOf(totalTimeTaken));*/
                    }
                });
            }

            ReadDatabaseForAPICallData(songDetails, songDetailsDao);
        }
    }

    public void ReadDatabaseForAPICallData(ArrayList<SongDetails> songDetails, SongDetailsDao songDetailsDao) {
        ArrayList<SongDetails> songsToCallShazamAPI = new ArrayList<>();
        WrapperForCommonDataSongItem wrapper;
        for (SongDetails details:songDetails){
            if (details.JSONResponseAPI==null){
                if (details.duration/1000 > 60) {
                    songsToCallShazamAPI.add(details);
                }else {
                    details.JSONResponseAPI="";
                    songDetailsDao.update(details);
                }
            }else {
                if (!"".equals(details.JSONResponseAPI)){
                    for (SongItem songItem:AllSongs){
                        wrapper = songItem.getWrapperForCommonDataSongItem();
                        if (wrapper.getUri().equals(details.uri)){
                            wrapper.setSongNameApi(details.songNameAPI);
                            wrapper.setArtistApi(details.artistAPI);
                            wrapper.setAlbumApi(details.albumAPI);
                            wrapper.setLabelApi(details.labelAPI);
                            wrapper.setReleasedApi(details.releasedAPI);
                            wrapper.setGenresApi(details.genresAPI);
                            wrapper.setExplicitAPI(details.isExplicitAPI);
                        }
                    }
                }
            }
        }

        for (SongDetails details:songsToCallShazamAPI){
            while (numberOfCallShazamAPIThreads.get()>=maxNumberOfCallShazamAPIThreadsAllowed) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("THREADS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + numberOfCallShazamAPIThreads.get());
            }
            CallShazamAPI callShazamAPI = new CallShazamAPI(songDetailsDao, details, getApplicationContext());
            new Thread(callShazamAPI).start();
        }

        while (numberOfCallShazamAPIThreads.get()>0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("THREADS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + numberOfCallShazamAPIThreads.get());
        }


        for (SongDetails details:songDetails){
            if (details.JSONResponseAPI==null){
                if (details.duration/1000 > 40) {
                }else {
                    details.JSONResponseAPI="";
                    songDetailsDao.update(details);
                }
            }else {
                if (!"".equals(details.JSONResponseAPI)){
                    for (SongItem songItem:AllSongs){
                        wrapper = songItem.getWrapperForCommonDataSongItem();
                        if (wrapper.getUri().equals(details.uri)){
                            wrapper.setSongNameApi(details.songNameAPI);
                            wrapper.setArtistApi(details.artistAPI);
                            wrapper.setAlbumApi(details.albumAPI);
                            wrapper.setLabelApi(details.labelAPI);
                            wrapper.setReleasedApi(details.releasedAPI);
                            wrapper.setGenresApi(details.genresAPI);
                            wrapper.setExplicitAPI(details.isExplicitAPI);
                        }
                    }
                }
            }
        }

        getArtists();
        updateArtistThumbnails();

    }


    static class CallShazamAPI implements Runnable {
        SongDetailsDao songDetailsDao;
        SongDetails details;
        Context context;
        public CallShazamAPI(SongDetailsDao songDetailsDao, SongDetails details, Context context) {
            this.songDetailsDao = songDetailsDao;
            this.details = details;
            this.context = context;
            numberOfCallShazamAPIThreads.getAndIncrement();
        }

        @Override
        public void run() {

            String text = fetchMetaData(30);
            try {
                JSONObject jsonObject = new JSONObject(text);
                try {
                    String trackString = jsonObject.getString("track");
                    processResponse(trackString);
                }catch (JSONException e){
                    e.printStackTrace();
                    text = fetchMetaData(40);String trackString = jsonObject.getString("track");
                    processResponse(trackString);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

            details.JSONResponseAPI = text;
            songDetailsDao.update(details);

            numberOfCallShazamAPIThreads.getAndDecrement();
        }

        public String fetchMetaData(int startSecond){
            GetMetadataForSongThroughAPI hopeThisWorks = new GetMetadataForSongThroughAPI(details.uri, startSecond);
            while (!hopeThisWorks.done) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            hopeThisWorks.adjustForSampleRate();
            return hopeThisWorks.getMetaData();
        }

        public void processResponse(String trackString){
            try {
                JSONObject track = new JSONObject(trackString);
                details.songNameAPI = track.getString("title");
                details.artistAPI = track.getString("subtitle");

                JSONArray sections = new JSONArray(track.getString("sections"));
                for (int i = 0; i < sections.length(); ++i) {
                    JSONObject object = new JSONObject(sections.getString(i));
                    if (object.getString("type").equals("LYRICS")) {
                        JSONArray lyrics = new JSONArray(object.getString("text"));
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int o = 0; o < lyrics.length(); ++o) {
                            if (lyrics.getString(o).equals("")) {
                                stringBuilder.append("\n");
                            } else {
                                stringBuilder.append(lyrics.getString(o)).append("\n");
                            }
                        }
                        try {
                            stringBuilder.append("\n").append("\n").append("\n").append(object.getString("footer"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        details.lyrics = stringBuilder.toString();
                    } else if (object.getString("type").equals("SONG")) {
                        JSONArray metadata = new JSONArray(object.getString("metadata"));
                        for (int o = 0; o < metadata.length(); ++o) {
                            JSONObject object1 = new JSONObject(metadata.getString(o));
                            if ("Album".equals(object1.getString("title"))) {
                                details.albumAPI = object1.getString("text");
                            }
                            if ("Label".equals(object1.getString("title"))) {
                                details.labelAPI = object1.getString("text");
                            }
                            if ("Released".equals(object1.getString("title"))) {
                                details.releasedAPI = object1.getString("text");
                            }
                        }
                    }
                }

                JSONObject genres = new JSONObject(track.getString("genres"));
                details.genresAPI = genres.getString("primary");

                JSONObject hub = new JSONObject(track.getString("hub"));
                details.isExplicitAPI = hub.getBoolean("explicit");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    static class ScanBitmapAndAudioForUrisNotInDatabase implements Runnable {
        SongItem songItem;
        Bitmap lowResDefault;
        Bitmap bitmap;
        float loudnessIndex;
        SongDetailsDao songDetailsDao;
        String Uri;
        int pixels;
        float MaxGainTo10DeciBells;

        float[] floatSounds;

        public ScanBitmapAndAudioForUrisNotInDatabase(SongItem songItem, Bitmap lowResDefault, Bitmap bitmap, float loudnessIndex, SongDetailsDao songDetailsDao, String uri, int pixels,float maxGainTo10DeciBells) {
            this.songItem = songItem;
            this.lowResDefault = lowResDefault;
            this.bitmap = bitmap;
            this.loudnessIndex = loudnessIndex;
            this.songDetailsDao = songDetailsDao;
            this.Uri = uri;
            this.pixels = pixels;
            this.MaxGainTo10DeciBells = maxGainTo10DeciBells;
            numberOfScanBitmapAndAudioThreads.getAndIncrement();
        }

        @Override
        public void run() {
            bitmap = CreatingLowResBitmap(CropBitmapTo1to1AspectRatio(getThumbnail(Uri)),pixels,pixels);
            GetFrequencySpectrumDataSyncAsyncHybrid hopeThisWorks = new GetFrequencySpectrumDataSyncAsyncHybrid(songItem.getUri());
            while (!hopeThisWorks.done){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            floatSounds = hopeThisWorks.floatSounds;
            loudnessIndex = AudioScanner.getLoudnessIndex(floatSounds);
            AudioScanner.normalizeFrequencyAmplitudes(floatSounds);

            //loudnessIndex = getLoudnessIndex(Uri);
            if (bitmap==null){
                bitmap = lowResDefault;
            }
            int themeColor = getDominantColorDiscourageSkinAndGrey(bitmap);
            songItem.setThumbnail(bitmap);
            songItem.getWrapperForCommonDataSongItem().setThemeColor(themeColor);
            songItem.setLoudnessIndex(Math.max(loudnessIndex, MaxGainTo10DeciBells));
            songItem.setSubBass(floatSounds[0]);
            songItem.setBass(floatSounds[1]);
            songItem.setLowerMidrange(floatSounds[2]);
            songItem.setMidrange(floatSounds[3]);
            songItem.setHigherMidrange(floatSounds[4]);
            songItem.setPresence(floatSounds[5]);
            songItem.setBrilliance(floatSounds[6]);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            songDetailsDao.insert(new SongDetails(Uri, songItem.getSongName(), songItem.getDuration(), byteArray, themeColor,loudnessIndex,songItem.getLastModifiedTimeSinceEpochInMillis(),
                    0,0,0,0,0,0,0,0,0,0,0,0,
                    floatSounds[0],floatSounds[1],floatSounds[2],floatSounds[3],floatSounds[4],floatSounds[5],floatSounds[6]));
            numberOfScanBitmapAndAudioThreads.getAndDecrement();
        }
    }

    static class ScanBitmapAndAudio implements Runnable {
        SongItem songItem;
        Bitmap lowResDefault;
        Bitmap bitmap;
        float loudnessIndex;
        SongDetailsDao songDetailsDao;
        SongDetails details;
        int pixels;
        float MaxGainTo10DeciBells;

        float[] floatSounds;

        public ScanBitmapAndAudio(SongItem songItem, Bitmap lowResDefault, Bitmap bitmap, float loudnessIndex, SongDetailsDao songDetailsDao, SongDetails details, int pixels,float maxGainTo10DeciBells) {
            this.songItem = songItem;
            this.lowResDefault = lowResDefault;
            this.bitmap = bitmap;
            this.loudnessIndex = loudnessIndex;
            this.songDetailsDao = songDetailsDao;
            this.details = details;
            this.pixels = pixels;
            this.MaxGainTo10DeciBells = maxGainTo10DeciBells;
            numberOfScanBitmapAndAudioThreads.getAndIncrement();
        }

        @Override
        public void run() {
            bitmap = CreatingLowResBitmap(CropBitmapTo1to1AspectRatio(getThumbnail(songItem.getUri())), pixels, pixels);
            GetFrequencySpectrumDataSyncAsyncHybrid hopeThisWorks = new GetFrequencySpectrumDataSyncAsyncHybrid(songItem.getUri());
            while (!hopeThisWorks.done){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            floatSounds = hopeThisWorks.floatSounds;
            loudnessIndex = AudioScanner.getLoudnessIndex(floatSounds);
            AudioScanner.normalizeFrequencyAmplitudes(floatSounds);

            //loudnessIndex = getLoudnessIndex(songItem.getUri());

            details.name=songItem.getSongName();
            details.duration=songItem.getDuration();
            details.loudnessIndex=loudnessIndex;
            details.lastModified=songItem.getLastModifiedTimeSinceEpochInMillis();
            details.subBass=floatSounds[0];
            details.bass=floatSounds[1];
            details.lowerMidrange=floatSounds[2];
            details.midrange=floatSounds[3];
            details.higherMidrange=floatSounds[4];
            details.presence=floatSounds[5];
            details.brilliance=floatSounds[6];
            if (bitmap == null) {
                details.thumbnailArray=null;
                bitmap = lowResDefault;
                details.themeColor = getDominantColorDiscourageSkinAndGrey(bitmap);
                songDetailsDao.update(details);
            } else {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                details.thumbnailArray= stream.toByteArray();
                details.themeColor = getDominantColorDiscourageSkinAndGrey(bitmap);
                songDetailsDao.update(details);
            }
            songItem.setThumbnail(bitmap);
            songItem.getWrapperForCommonDataSongItem().setThemeColor(details.themeColor);
            songItem.setLoudnessIndex(Math.max(loudnessIndex, MaxGainTo10DeciBells));
            songItem.setSubBass(floatSounds[0]);
            songItem.setBass(floatSounds[1]);
            songItem.setLowerMidrange(floatSounds[2]);
            songItem.setMidrange(floatSounds[3]);
            songItem.setHigherMidrange(floatSounds[4]);
            songItem.setPresence(floatSounds[5]);
            songItem.setBrilliance(floatSounds[6]);
            numberOfScanBitmapAndAudioThreads.getAndDecrement();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            scanButton.setVisibility(View.GONE);
            scanForAudioThread runnable = new scanForAudioThread(db);
            new Thread(runnable).start();
        } else {
            Toast.makeText(getApplicationContext(),"App cannot access audio files due to lack of permission",Toast.LENGTH_SHORT).show();
        }
    });

    private final ActivityResultLauncher<Intent> createPlaylistActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        Intent data = result.getData();
        if (data!=null) {
            if (result.getResultCode() == Activity.RESULT_CANCELED) {
                System.out.println("RESULT!!!!!!!!!!!!!!!!!!!:  " + data.getExtras().getString("EditTextText"));
            } else if (result.getResultCode() == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String playlistName = bundle.getString("EditTextText");

                if ("".equals(playlistName)) {
                    Toast.makeText(getApplicationContext(), "Invalid Name", Toast.LENGTH_SHORT).show();
                } else {
                    if ("RenamePlaylist".equals(bundle.getString("Requirement"))){

                        String renamingFromName = bundle.getString("PlaylistName");
                        String renamingToName = bundle.getString("EditTextText");

                        if (!renamingFromName.equals(renamingToName)) {
                            File renamingFrom = new File(getDir("Playlists", MODE_PRIVATE), renamingFromName + ".ser");
                            File renamingTo = new File(getDir("Playlists", MODE_PRIVATE), renamingToName + ".ser");
                            if (renamingTo.exists()) {
                                Toast.makeText(getApplicationContext(), "A playlist of the same name already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                if (renamingFrom.exists()) {
                                    if (renamingFrom.renameTo(renamingTo)) {
                                        allPlaylists.get(bundle.getInt("PlaylistPosition")).setPlaylistName(renamingToName);
                                        weakReferencesToFragments.getMessage(PlaylistsFragment.name, PLAYLIST_RENAMED, bundle.getInt("PlaylistPosition"),null);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error renaming playlist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Playlist does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }else if ("CreatePlaylist".equals(bundle.getString("Requirement"))) {
                        File playlistFile = new File(getDir("Playlists", MODE_PRIVATE), playlistName + ".ser");
                        if (playlistFile.exists()) {
                            Toast.makeText(getApplicationContext(), "Playlist already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                if (playlistFile.createNewFile()) {
                                    if (saveToPlaylistClicked){
                                        int durationInSeconds = queueObject.durationInMillis / 1000;
                                        String duration = theFormat.format(durationInSeconds / (60 * 60)) + ":" + theFormat.format(((durationInSeconds / 60) % 60)) + ":" + theFormat.format(durationInSeconds % (60));
                                        PlaylistItem playlistItem = new PlaylistItem(playlistName, new ArrayList<>(queueObject.QueueArray), queueObject.durationInMillis, duration);
                                        allPlaylists.add(playlistItem);

                                        try {
                                            FileOutputStream fileOut = new FileOutputStream(playlistFile);
                                            ObjectOutputStream out = new ObjectOutputStream(fileOut);
                                            out.writeObject(playlistItem);
                                            out.close();
                                            fileOut.close();
                                        } catch (IOException e) {
                                            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                                            e.printStackTrace();
                                        }

                                    }else {
                                        PlaylistItem playlistItem = new PlaylistItem(playlistName, new ArrayList<SongItem>(), 0, "00:00:00");
                                        allPlaylists.add(playlistItem);

                                        try {
                                            FileOutputStream fileOut = new FileOutputStream(playlistFile);
                                            ObjectOutputStream out = new ObjectOutputStream(fileOut);
                                            out.writeObject(playlistItem);
                                            out.close();
                                            fileOut.close();
                                        } catch (IOException e) {
                                            Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                                            e.printStackTrace();
                                        }
                                    }
                                    weakReferencesToFragments.getMessage(PlaylistsFragment.name, PLAYLIST_CREATED, -1,null);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error creating playlist", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                                e.printStackTrace();
                            }
                        }
                    }

                    File file = new File(getApplicationContext().getDir("Preferences", MODE_PRIVATE),"PlaylistsOrder");
                    StringBuilder writeThis = new StringBuilder();

                    for (PlaylistItem playlistItem:allPlaylists){
                        writeThis.append(playlistItem.getPlaylistName()).append("\n");
                    }

                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file,false);
                        fileOutputStream.write(writeThis.toString().getBytes());
                        fileOutputStream.close();
                    } catch (IOException e) {
                        Methods.addToErrorLog(e.getMessage(),getApplicationContext());
                        e.printStackTrace();
                    }

                }
            }
        }
        else {
            System.out.println("NULL RESULT DATA!!!!!!!!!!!!!!!!!!!!!! "); // happens when activity is destroyed without setResult() being called, ex:- when user taps outside the activity layout area.
        }

        saveToPlaylistClicked = false;
    });

}