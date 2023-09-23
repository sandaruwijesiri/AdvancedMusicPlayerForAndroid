package com.example.testapplication2.DataClasses;

import android.graphics.Bitmap;

import com.example.testapplication2.Fragments.QueueFragment;
import com.example.testapplication2.MainActivity;

import java.io.Serializable;

import static com.example.testapplication2.Enums.EnumMessages.NOW_PLAYING_THUMBNAIL_FOUND;
import static com.example.testapplication2.MainActivity.queueObject;
import static com.example.testapplication2.MainActivity.weakReferencesToFragments;
import static com.example.testapplication2.OtherClasses.Methods.updateMetadata;

public class SongItem implements Serializable {

    private WrapperForCommonDataSongItem wrapperForCommonDataSongItem;
    private transient boolean isSelected = false;

    public SongItem(String msongName, String martist, String muri, int mduration, Bitmap mthumbnail, float loudnessIndex, long mcreationTimeSinceEpochInMillis, String mdateCreated, long lastModifiedTimeSinceEpochInMillis){
        this.wrapperForCommonDataSongItem = new WrapperForCommonDataSongItem(msongName,martist,muri,mduration,mcreationTimeSinceEpochInMillis,mdateCreated,lastModifiedTimeSinceEpochInMillis,mthumbnail,loudnessIndex);
    }

    public SongItem(WrapperForCommonDataSongItem wrapperForSongItemThumbnail){

        this.wrapperForCommonDataSongItem = wrapperForSongItemThumbnail;
    }

    public String getSongName() {
        return wrapperForCommonDataSongItem.getSongName();
    }

    public void setSongName(String songName) {
        wrapperForCommonDataSongItem.setSongName(songName);
    }

    public String getArtist() {
        return wrapperForCommonDataSongItem.getArtist();
    }

    public void setArtist(String artist) {
        wrapperForCommonDataSongItem.setArtist(artist);
    }

    public String getUri() {
        return wrapperForCommonDataSongItem.getUri();
    }

    public void setUri(String uri) {
        wrapperForCommonDataSongItem.setUri(uri);
    }

    public int getDuration() {
        return wrapperForCommonDataSongItem.getDuration();
    }

    public void setDuration(int duration) {
        wrapperForCommonDataSongItem.setDuration(duration);
    }

    public Bitmap getThumbnail() {
        return wrapperForCommonDataSongItem.getThumbnail();
    }

    public void setThumbnail(Bitmap thumbnail) {
        wrapperForCommonDataSongItem.setThumbnail(thumbnail);
        weakReferencesToFragments.thumbnailUpdated(this);
        if (this.isSimilar(queueObject.nowPlaying)){
            updateMetadata();
            weakReferencesToFragments.getMessage(MainActivity.name, NOW_PLAYING_THUMBNAIL_FOUND, -1, null);
            weakReferencesToFragments.getMessage(QueueFragment.name, NOW_PLAYING_THUMBNAIL_FOUND,-1,null);
        }
    }

    public float getLoudnessIndex() {
        return wrapperForCommonDataSongItem.getLoudnessIndex();
    }

    public void setLoudnessIndex(float loudnessIndex) {
        wrapperForCommonDataSongItem.setLoudnessIndex(loudnessIndex);
    }

    public WrapperForCommonDataSongItem getWrapperForCommonDataSongItem() {
        return wrapperForCommonDataSongItem;
    }

    public long getCreationTimeSinceEpochInMillis() {
        return wrapperForCommonDataSongItem.getCreationTimeSinceEpochInMillis();
    }

    public String getDateCreated() {
        return wrapperForCommonDataSongItem.getDateCreated();
    }

    public long getLastModifiedTimeSinceEpochInMillis() {
        return wrapperForCommonDataSongItem.getLastModifiedTimeSinceEpochInMillis();
    }

    public boolean isSelected(){
        return this.isSelected;
    }

    public SongItem setIsSelected(boolean isSelected){
        this.isSelected = isSelected;return this;
    }

    public float getFavScore(){
        return wrapperForCommonDataSongItem.getFavScore();
    }

    public void setFavScore(float favScore){
        wrapperForCommonDataSongItem.setFavScore(favScore);
    }

    public float getSubBass() {
        return wrapperForCommonDataSongItem.getSubBass();
    }

    public void setSubBass(float subBass) {
        wrapperForCommonDataSongItem.setSubBass(subBass);
    }

    public float getBass() {
        return wrapperForCommonDataSongItem.getBass();
    }

    public void setBass(float bass) {
        wrapperForCommonDataSongItem.setBass(bass);
    }

    public float getLowerMidrange() {
        return wrapperForCommonDataSongItem.getLowerMidrange();
    }

    public void setLowerMidrange(float lowerMidrange) {
        wrapperForCommonDataSongItem.setLowerMidrange(lowerMidrange);
    }

    public float getMidrange() {
        return wrapperForCommonDataSongItem.getMidrange();
    }

    public void setMidrange(float midrange) {
        wrapperForCommonDataSongItem.setMidrange(midrange);
    }

    public float getHigherMidrange() {
        return wrapperForCommonDataSongItem.getHigherMidrange();
    }

    public void setHigherMidrange(float higherMidrange) {
        wrapperForCommonDataSongItem.setHigherMidrange(higherMidrange);
    }

    public float getPresence() {
        return wrapperForCommonDataSongItem.getPresence();
    }

    public void setPresence(float presence) {
        wrapperForCommonDataSongItem.setPresence(presence);
    }

    public float getBrilliance() {
        return wrapperForCommonDataSongItem.getBrilliance();
    }

    public void setBrilliance(float brilliance) {
        wrapperForCommonDataSongItem.setBrilliance(brilliance);
    }

    public boolean isSame(SongItem item){
        return this==item;
    }

    public boolean isSimilar(SongItem item){
        if (item==null) return false;
        return wrapperForCommonDataSongItem.getUri().equals(item.getWrapperForCommonDataSongItem().getUri());
    }

    public SongItem dup(){
        return new SongItem(wrapperForCommonDataSongItem);
    }
}
