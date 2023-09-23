package com.example.testapplication2.DataClasses;

import static com.example.testapplication2.MainActivity.theFormat;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ArtistItem {
    String artistName;
    Bitmap artistBitmap;
    ArrayList<SongItem> artistSongs;
    String durationString;
    int durationInMillis=0;

    public ArtistItem(String artistName,Bitmap artistBitmap, ArrayList<SongItem> artistSongs) {
        this.artistName = artistName;
        this.artistBitmap = artistBitmap;
        this.artistSongs = artistSongs;

        setDurationData();
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Bitmap getArtistBitmap() {
        return artistBitmap;
    }

    public void setArtistBitmap(Bitmap artistBitmap) {
        this.artistBitmap = artistBitmap;
    }

    public ArrayList<SongItem> getArtistSongs() {
        return artistSongs;
    }

    public void setArtistSongs(ArrayList<SongItem> artistSongs) {
        this.artistSongs = artistSongs;
        setDurationData();
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public int getDurationInMillis() {
        return durationInMillis;
    }

    public void setDurationInMillis(int durationInMillis) {
        this.durationInMillis = durationInMillis;
    }

    public void setDurationData(){
        durationInMillis=0;
        for (SongItem item:artistSongs){
            durationInMillis+=item.getDuration();
        }
        int durationInSeconds = durationInMillis / 1000;
        durationString = theFormat.format(durationInSeconds / (60 * 60)) + ":" + theFormat.format(((durationInSeconds / 60) % 60)) + ":" + theFormat.format(durationInSeconds % (60));
    }

    public void addSong(SongItem song){
        artistSongs.add(song);
        setDurationData();
    }

    public void removeSong(SongItem song){
        artistSongs.remove(song);
        setDurationData();
    }
}
