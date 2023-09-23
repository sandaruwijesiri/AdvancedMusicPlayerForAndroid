package com.example.testapplication2.DataClasses;

import android.graphics.Bitmap;

import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_DATA_SET_CHANGED;
import static com.example.testapplication2.Enums.EnumMessages.QUEUE_DETAILS_UPDATED;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_QUEUE_PROGRESS;
import static com.example.testapplication2.Enums.EnumMessages.UPDATE_STOP_AFTER_THIS;
import static com.example.testapplication2.MainActivity.lowResDefaultBitmap;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.updateMetadata;

public class QueueObject implements Serializable {
    public ArrayList<SongItem> QueueArray;
    public SongItem nowPlaying;
    public SongItem previousSong;
    public SongItem stopAfterThis;
    public SongItem previousStop;
    public int playbackPosition = 0;
    public int durationInMillis = 0;
    public int durationCompletedTillNowPlayingSong = 0;

    public QueueObject() {
        QueueArray = new ArrayList<>();
    }

    public void setNowPlaying(SongItem nowPlaying) {
        this.previousSong = this.nowPlaying;
        this.nowPlaying = nowPlaying;
        weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
        updateDurationCompletedTillNowPlayingSong();
        updateMetadata();
        weakReferencesToFragments.getMessage(QueueFragment.name, UPDATE_QUEUE_PROGRESS,-1,null);
    }

    public void setStopAfterThis(SongItem stopAfterThis) {
        if (this.stopAfterThis == stopAfterThis && stopAfterThis!=null){
            setStopAfterThis(null);
        }else {
            previousStop = this.stopAfterThis;
            this.stopAfterThis = stopAfterThis;
            weakReferencesToFragments.getMessage(MainActivity.name, UPDATE_STOP_AFTER_THIS,-1,null);
            weakReferencesToFragments.getMessage(QueueFragment.name, UPDATE_STOP_AFTER_THIS,-1,null);
        }
    }

    public Bitmap getNowPlayingThumbnail(){
        if (this.nowPlaying==null){
            return lowResDefaultBitmap;
        }else {
            return this.nowPlaying.getThumbnail();
        }
    }

    public void updateDurationCompletedTillNowPlayingSong() {
        durationCompletedTillNowPlayingSong = 0;
        if (queueObject.nowPlaying==null) return;
        for (SongItem songItem:queueObject.QueueArray){
            if (songItem==queueObject.nowPlaying){
                break;
            }
            durationCompletedTillNowPlayingSong+=songItem.getDuration();
        }
    }

    public void updateQueueObjectFullyExceptPreviousSong(ArrayList<SongItem> queueArray, int nowPlayingIndex){
        this.QueueArray.clear();
        this.durationInMillis=0;
        for (SongItem songItem:queueArray){
            this.QueueArray.add(songItem.dup());
            this.durationInMillis+=songItem.getDuration();
        }
        if (nowPlayingIndex>=0 && nowPlayingIndex<this.QueueArray.size()) {
            this.setNowPlaying(this.QueueArray.get(nowPlayingIndex));
        }else {
            this.setNowPlaying(null);
        }
        this.stopAfterThis=null;
        this.playbackPosition=0;
        updateQueueDetails();

    }

    public void updateQueueObjectFully(ArrayList<SongItem> queueArray, int nowPlayingIndex){
        this.QueueArray.clear();
        this.durationInMillis=0;
        for (SongItem songItem:queueArray){
            this.QueueArray.add(songItem.dup());
            this.durationInMillis+=songItem.getDuration();
        }
        if (nowPlayingIndex>=0 && nowPlayingIndex<this.QueueArray.size()) {
            this.setNowPlaying(this.QueueArray.get(nowPlayingIndex));
        }else {
            this.setNowPlaying(null);
        }
        this.previousSong=null;
        this.stopAfterThis=null;
        this.previousStop=null;
        this.playbackPosition=0;
        updateQueueDetails();

    }

    public void add(SongItem songItem){
        boolean updateNowPlayingCard = QueueArray.indexOf(nowPlaying)==QueueArray.size()-1;
        this.QueueArray.add(songItem.dup());
        this.durationInMillis+=songItem.getDuration();
        updateQueueDetails();
        if (updateNowPlayingCard){
            weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
        }
    }

    public void add(int index, SongItem songItem){
        int now = QueueArray.indexOf(nowPlaying);
        boolean updateNowPlayingCard = (now==0 && index==0) || (now==QueueArray.size()-1 && index==QueueArray.size());
        this.QueueArray.add(index, songItem.dup());
        this.durationInMillis+=songItem.getDuration();
        updateQueueDetails();
        if (updateNowPlayingCard){
            weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
        }
    }

    public void addAll(ArrayList<SongItem> songItemArrayList){
        boolean updateNowPlayingCard = QueueArray.indexOf(nowPlaying)==QueueArray.size()-1;
        ArrayList<SongItem> dupSongs = new ArrayList<>();
        for (SongItem songItem:songItemArrayList){
            this.durationInMillis+=songItem.getDuration();
            dupSongs.add(songItem.dup());
        }
        this.QueueArray.addAll(dupSongs);
        updateQueueDetails();
        if (updateNowPlayingCard){
            weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
        }
    }

    public void set(int index, SongItem songItem){
        this.durationInMillis-=QueueArray.get(index).getDuration();
        this.durationInMillis+=songItem.getDuration();
        this.QueueArray.set(index, songItem.dup());
        updateQueueDetails();
    }

    public void remove(SongItem songItem){
        int index = QueueArray.indexOf(songItem);
        boolean updateNowPlayingCard = (index==0 || index==(QueueArray.size()-1)) && !songItem.equals(nowPlaying);
        this.QueueArray.remove(songItem);
        this.durationInMillis-=songItem.getDuration();
        updateQueueDetails();
        if (updateNowPlayingCard){
            weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
        }
    }

    public void remove(int index){
        SongItem item = QueueArray.get(index);
        boolean updateNowPlayingCard = (index==0 || index==(QueueArray.size()-1)) && !item.equals(nowPlaying);
        this.durationInMillis-=item.getDuration();
        this.QueueArray.remove(index);
        updateQueueDetails();
        if (updateNowPlayingCard){
            weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_DATA_SET_CHANGED,-1,null);
        }
    }

    public void clear(){
        this.QueueArray.clear();
        this.durationInMillis=0;
        setNowPlaying(null);
        updateQueueDetails();
    }

    public void updateQueueDetails(){
        updateDurationCompletedTillNowPlayingSong();
        weakReferencesToFragments.getMessage(QueueFragment.name, QUEUE_DETAILS_UPDATED, -1, null);
    }
}
