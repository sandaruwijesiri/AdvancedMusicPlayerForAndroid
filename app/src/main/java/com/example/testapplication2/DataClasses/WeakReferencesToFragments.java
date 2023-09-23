package com.example.testapplication2.DataClasses;

import static com.example.testapplication2.Enums.EnumFragments.ARTISTS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.HOME_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.MORE_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.NOW_PLAYING_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.PLAYLISTS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.QUEUE_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.SETTINGS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.SONGS_FRAGMENT;
import static com.example.testapplication2.Enums.EnumFragments.SPECIFIC_PLAYLIST_FRAGMENT;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.Fragment;

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
import com.example.testapplication2.MainActivity;
import com.example.testapplication2.Miscellaneous.MyFragmentSuperClass;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WeakReferencesToFragments {

    // To refer to fragments without preventing gc from collecting them.

    static WeakReferencesToFragments me = null;
    public WeakReference<Activity> mainActivity = new WeakReference<>(null);
    public WeakReference<MyFragmentSuperClass> homeFragment = new WeakReference<>(null);//1
    public WeakReference<MyFragmentSuperClass> songsFragment = new WeakReference<>(null);//2
    public WeakReference<MyFragmentSuperClass> playlistsFragment = new WeakReference<>(null);//3
    public WeakReference<MyFragmentSuperClass> queueFragment = new WeakReference<>(null);//4
    public WeakReference<MyFragmentSuperClass> moreFragment = new WeakReference<>(null);//5
    public WeakReference<MyFragmentSuperClass> specificPlaylistFragment = new WeakReference<>(null);//6
    public WeakReference<MyFragmentSuperClass> settingsFragment = new WeakReference<>(null);//7
    public WeakReference<MyFragmentSuperClass> nowPlayingFragment = new WeakReference<>(null);//8
    public WeakReference<MyFragmentSuperClass> artistsFragment = new WeakReference<>(null);//9

    public WeakReference<ArrayList<MyFragmentSuperClass>> fragmentStack = new WeakReference<>(null);

    public static ArrayList<WeakReference<MyFragmentSuperClass>> arrayListOfFragments = new ArrayList<>();

    private WeakReferencesToFragments(){}

    public static WeakReferencesToFragments getInstance(){
        if (me==null){
            me = new WeakReferencesToFragments();
        }
        return me;
    }

    public void saveActivity(Activity activity){
        this.mainActivity = new WeakReference<>(activity);
    }

    public void saveFragment(int fragmentType, MyFragmentSuperClass fragment){
        WeakReference<MyFragmentSuperClass> weakReference = new WeakReference<>(fragment);

        if (fragmentType==1){
            homeFragment=weakReference;
        }else if (fragmentType==2){
            songsFragment=weakReference;
        }else if (fragmentType==3){
            playlistsFragment=weakReference;
        }else if (fragmentType==4){
            queueFragment=weakReference;
        }else if (fragmentType==5){
            moreFragment=weakReference;
        }else if (fragmentType==6){
            specificPlaylistFragment=weakReference;
        }else if (fragmentType==7){
            settingsFragment=weakReference;
        }else if (fragmentType==8){
            nowPlayingFragment=weakReference;
        }else if (fragmentType==9){
            artistsFragment=weakReference;
        }

        arrayListOfFragments.add(weakReference);
    }

    public void saveFragmentStack(ArrayList<MyFragmentSuperClass> fragmentStack){
        this.fragmentStack = new WeakReference<>(fragmentStack);
    }

    public void playbackStateChanged(int pbState){
        QueueFragment queue = (QueueFragment) queueFragment.get();
        if (queue!=null && queue.isAdded()){queue.playbackStateChanged(pbState);}

        NowPlayingFragment nowPlaying = (NowPlayingFragment) nowPlayingFragment.get();
        if (nowPlaying!=null && nowPlaying.isAdded()){nowPlaying.playbackStateChanged(pbState);}
    }



    public void thumbnailUpdated(SongItem songItem){
        waitTillAllThumbnailsAreUpdated waitTillAllThumbnailsAreUpdatedThread = new waitTillAllThumbnailsAreUpdated(songItem);
        new Thread(waitTillAllThumbnailsAreUpdatedThread).start();
    }

    class waitTillAllThumbnailsAreUpdated implements Runnable{
        HomeFragment home = (HomeFragment) homeFragment.get();
        SongsFragment songs = (SongsFragment) songsFragment.get();
        PlaylistsFragment playlists = (PlaylistsFragment) playlistsFragment.get();
        QueueFragment queue = (QueueFragment) queueFragment.get();
        MoreFragment more = (MoreFragment) moreFragment.get();
        SpecificPlaylistFragment specific = (SpecificPlaylistFragment) specificPlaylistFragment.get();
        SettingsFragment settings = (SettingsFragment) settingsFragment.get();
        NowPlayingFragment nowPlaying = (NowPlayingFragment) nowPlayingFragment.get();
        ArtistsFragment artists = (ArtistsFragment) artistsFragment.get();

        SongItem songItem;

        public waitTillAllThumbnailsAreUpdated(SongItem songItem) {
            this.songItem = songItem;
        }

        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (home!=null && home.isAdded()){home.thumbnailUpdated(songItem);}
                    if (songs!=null && songs.isAdded()){songs.thumbnailUpdated(songItem);}
                    if (playlists!=null && playlists.isAdded()){playlists.thumbnailUpdated(songItem);}
                    if (queue!=null && queue.isAdded()){queue.thumbnailUpdated(songItem);}
                    if (more!=null && more.isAdded()){more.thumbnailUpdated(songItem);}
                    if (specific!=null && specific.isAdded()){specific.thumbnailUpdated(songItem);}
                    if (settings!=null && settings.isAdded()){settings.thumbnailUpdated(songItem);}
                    if (nowPlaying!=null && nowPlaying.isAdded()){nowPlaying.thumbnailUpdated(songItem);}
                    if (artists!=null && artists.isAdded()){artists.thumbnailUpdated(songItem);}
                }
            });
        }
    }

    public void getMessage(String fragmentOrActivity, Enum message, int position, SongItem songItem){
        getMessage(fragmentOrActivity, message, position, songItem, null);
    }
    public void getMessage(String fragmentOrActivity, Enum message, int position, SongItem songItem, Bundle bundle){
        MyFragmentSuperClass mfsc;
        for (WeakReference<MyFragmentSuperClass> wrf: arrayListOfFragments){
            mfsc = wrf.get();
            if (mfsc!=null && mfsc.name.equals(fragmentOrActivity) && mfsc.isAdded()){
                mfsc.getMessage(message, position, songItem, bundle);
            }
        }

        if (MainActivity.name.equals(fragmentOrActivity)){
            MainActivity main = (MainActivity) mainActivity.get();
            if (main!=null){main.getMessage(message, position, songItem, bundle);}
        }/*else if (HomeFragment.name.equals(fragmentOrActivity)){
            HomeFragment home = (HomeFragment) homeFragment.get();
            if (home!=null && home.isAdded()){home.getMessage(message, position, songItem, bundle);}
        }else if (SongsFragment.name.equals(fragmentOrActivity)){
            SongsFragment songs = (SongsFragment) songsFragment.get();
            if (songs!=null && songs.isAdded()){songs.getMessage(message, position, songItem, bundle);}
        }else if (PlaylistsFragment.name.equals(fragmentOrActivity)){
            PlaylistsFragment playlists = (PlaylistsFragment) playlistsFragment.get();
            if (playlists!=null && playlists.isAdded()){playlists.getMessage(message, position, songItem, bundle);}
        }else if (QueueFragment.name.equals(fragmentOrActivity)){
            QueueFragment queue = (QueueFragment) queueFragment.get();
            if (queue!=null && queue.isAdded()){queue.getMessage(message, position, songItem, bundle);}
        }else if (MoreFragment.name.equals(fragmentOrActivity)){
            MoreFragment more = (MoreFragment) moreFragment.get();
            if (more!=null && more.isAdded()){more.getMessage(message, position, songItem, bundle);}
        }else if (SpecificPlaylistFragment.name.equals(fragmentOrActivity)){
            SpecificPlaylistFragment specific = (SpecificPlaylistFragment) specificPlaylistFragment.get();
            if (specific!=null && specific.isAdded()){specific.getMessage(message, position, songItem, bundle);}
        }else if (SettingsFragment.name.equals(fragmentOrActivity)){
            SettingsFragment settings = (SettingsFragment) settingsFragment.get();
            if (settings!=null && settings.isAdded()){settings.getMessage(message, position, songItem, bundle);}
        }else if (NowPlayingFragment.name.equals(fragmentOrActivity)){
            NowPlayingFragment nowPlaying = (NowPlayingFragment) nowPlayingFragment.get();
            if (nowPlaying!=null && nowPlaying.isAdded()){nowPlaying.getMessage(message, position, songItem, bundle);}
        }else if (ArtistsFragment.name.equals(fragmentOrActivity)){
            ArtistsFragment artists = (ArtistsFragment) artistsFragment.get();
            if (artists!=null && artists.isAdded()){artists.getMessage(message, position, songItem, bundle);}
        }*/
    }

    public Fragment getFragment(EnumFragments fragment){
        if (HOME_FRAGMENT.equals(fragment)){
            Fragment f = homeFragment.get();
            if (f==null){
                f = new HomeFragment();
            }
            return f;
        }else if (PLAYLISTS_FRAGMENT.equals(fragment)){
            Fragment f = playlistsFragment.get();
            if (f==null){
                f = new PlaylistsFragment();
            }
            return f;
        }else if (QUEUE_FRAGMENT.equals(fragment)){
            Fragment f = queueFragment.get();
            if (f==null){
                f = new QueueFragment();
            }
            return f;
        }else if (SONGS_FRAGMENT.equals(fragment)){
            Fragment f = songsFragment.get();
            if (f==null){
                f = new SongsFragment();
            }
            return f;
        }else if (ARTISTS_FRAGMENT.equals(fragment)){
            Fragment f = artistsFragment.get();
            if (f==null){
                f = new ArtistsFragment();
            }
            return f;
        }else if (SETTINGS_FRAGMENT.equals(fragment)){
            Fragment f = settingsFragment.get();
            if (f==null){
                f = new SettingsFragment();
            }
            return f;
        }else if (MORE_FRAGMENT.equals(fragment)){
            Fragment f = moreFragment.get();
            if (f==null){
                f = new MoreFragment();
            }
            return f;
        }else if (SPECIFIC_PLAYLIST_FRAGMENT.equals(fragment)){
            Fragment f = specificPlaylistFragment.get();
            if (f==null){
                f = new SpecificPlaylistFragment();
            }
            return f;
        }else if (NOW_PLAYING_FRAGMENT.equals(fragment)){
            Fragment f = nowPlayingFragment.get();
            if (f==null){
                f = new NowPlayingFragment();
            }
            return f;
        }

        return null;
    }
}
