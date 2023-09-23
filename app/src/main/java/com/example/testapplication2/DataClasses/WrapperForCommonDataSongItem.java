package com.example.testapplication2.DataClasses;

import static com.example.testapplication2.MainActivity.targetLoudness;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class WrapperForCommonDataSongItem implements Serializable {

    private String songName;
    private String artist;
    private String uri;
    private int duration;

    private long creationTimeSinceEpochInMillis;
    private String dateCreated;
    private long lastModifiedTimeSinceEpochInMillis;

    private transient Bitmap thumbnail;
    private transient int themeColor;
    public transient boolean thumbnailSet = false;
    private transient float loudnessIndex = targetLoudness;

    private transient float favScore = 0;

    private transient float subBass = 0;
    private transient float bass = 0;
    private transient float lowerMidrange = 0;
    private transient float midrange = 0;
    private transient float higherMidrange = 0;
    private transient float presence = 0;
    private transient float brilliance = 0;

    private String songNameApi;
    private String artistApi;
    private String albumApi;
    private String genresApi;
    private String labelApi;
    private String releasedApi;
    private boolean isExplicitAPI;

    /*public WrapperForCommonDataSongItem(@NonNull Bitmap thumbnail,float loudnessIndex){
        this.thumbnail = thumbnail;
        this.loudnessIndex = loudnessIndex;
    }*/

    public WrapperForCommonDataSongItem(String songName, String artist, String uri, int duration, long creationTimeSinceEpochInMillis, String dateCreated, long lastModifiedTimeSinceEpochInMillis, Bitmap thumbnail, float loudnessIndex) {
        this.songName = songName;
        this.artist = artist;
        this.uri = uri;
        this.duration = duration;
        this.creationTimeSinceEpochInMillis = creationTimeSinceEpochInMillis;
        this.dateCreated = dateCreated;
        this.lastModifiedTimeSinceEpochInMillis = lastModifiedTimeSinceEpochInMillis;
        this.thumbnail = thumbnail;
        this.loudnessIndex = loudnessIndex;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreationTimeSinceEpochInMillis() {
        return creationTimeSinceEpochInMillis;
    }

    public void setCreationTimeSinceEpochInMillis(long creationTimeSinceEpochInMillis) {
        this.creationTimeSinceEpochInMillis = creationTimeSinceEpochInMillis;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getLastModifiedTimeSinceEpochInMillis() {
        return lastModifiedTimeSinceEpochInMillis;
    }

    public void setLastModifiedTimeSinceEpochInMillis(long lastModifiedTimeSinceEpochInMillis) {
        this.lastModifiedTimeSinceEpochInMillis = lastModifiedTimeSinceEpochInMillis;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
        this.thumbnailSet = true;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }

    public float getLoudnessIndex() {
        return loudnessIndex;
    }

    public void setLoudnessIndex(float loudnessIndex) {
        this.loudnessIndex = loudnessIndex;
    }

    public float getFavScore() {
        return favScore;
    }

    public void setFavScore(float favScore) {
        this.favScore = favScore;
    }

    public float getSubBass() {
        return subBass;
    }

    public void setSubBass(float subBass) {
        this.subBass = subBass;
    }

    public float getBass() {
        return bass;
    }

    public void setBass(float bass) {
        this.bass = bass;
    }

    public float getLowerMidrange() {
        return lowerMidrange;
    }

    public void setLowerMidrange(float lowerMidrange) {
        this.lowerMidrange = lowerMidrange;
    }

    public float getMidrange() {
        return midrange;
    }

    public void setMidrange(float midrange) {
        this.midrange = midrange;
    }

    public float getHigherMidrange() {
        return higherMidrange;
    }

    public void setHigherMidrange(float higherMidrange) {
        this.higherMidrange = higherMidrange;
    }

    public float getPresence() {
        return presence;
    }

    public void setPresence(float presence) {
        this.presence = presence;
    }

    public float getBrilliance() {
        return brilliance;
    }

    public void setBrilliance(float brilliance) {
        this.brilliance = brilliance;
    }

    public String getSongNameApi() {
        return songNameApi;
    }

    public void setSongNameApi(String songNameApi) {
        this.songNameApi = songNameApi;
    }

    public String getArtistApi() {
        return artistApi;
    }

    public void setArtistApi(String artistApi) {
        this.artistApi = artistApi;
    }

    public String getAlbumApi() {
        return albumApi;
    }

    public void setAlbumApi(String albumApi) {
        this.albumApi = albumApi;
    }

    public String getGenresApi() {
        return genresApi;
    }

    public void setGenresApi(String genresApi) {
        this.genresApi = genresApi;
    }

    public String getLabelApi() {
        return labelApi;
    }

    public void setLabelApi(String labelApi) {
        this.labelApi = labelApi;
    }

    public String getReleasedApi() {
        return releasedApi;
    }

    public void setReleasedApi(String releasedApi) {
        this.releasedApi = releasedApi;
    }

    public boolean isExplicitAPI() {
        return isExplicitAPI;
    }

    public void setExplicitAPI(boolean explicitAPI) {
        isExplicitAPI = explicitAPI;
    }
}
