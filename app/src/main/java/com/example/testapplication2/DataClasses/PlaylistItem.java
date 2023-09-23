package com.example.testapplication2.DataClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class PlaylistItem implements Serializable {

    private transient String playlistName;
    private ArrayList<SongItem> songItemsPlaylist;
    private int duration;
    private String durationAsAString;

    public PlaylistItem(String mplaylistName, ArrayList<SongItem> msongItemsPlaylist, int mduration, String mdurationAsAString) {
        this.playlistName = mplaylistName;
        this.songItemsPlaylist = msongItemsPlaylist;
        this.duration = mduration;
        this.durationAsAString = mdurationAsAString;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public ArrayList<SongItem> getSongItemsPlaylist() {
        return songItemsPlaylist;
    }

    public void setSongItemsPlaylist(ArrayList<SongItem> songItemsPlaylist) {
        this.songItemsPlaylist = songItemsPlaylist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationAsAString() {
        return durationAsAString;
    }

    public void setDurationAsAString(String durationAsAString) {
        this.durationAsAString = durationAsAString;
    }

}
