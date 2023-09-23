package com.example.testapplication2.Database;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SongDetails {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "URI")
    public String uri;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "thumbnailArray")
    public byte[] thumbnailArray;

    @ColumnInfo(name = "themeColor", defaultValue = "-16777216")
    public int themeColor;

    @ColumnInfo(name = "loudnessIndex")
    public float loudnessIndex;

    @ColumnInfo(name = "lastModifiedTime")
    public long lastModified;

    @ColumnInfo(name = "lyrics")
    public String lyrics;


    @ColumnInfo()
    public float monthHalf0;
    @ColumnInfo()
    public float monthHalf1;
    @ColumnInfo()
    public float monthHalf2;
    @ColumnInfo()
    public float monthHalf3;
    @ColumnInfo()
    public float monthHalf4;
    @ColumnInfo()
    public float monthHalf5;
    @ColumnInfo()
    public float monthHalf6;
    @ColumnInfo()
    public float monthHalf7;
    @ColumnInfo()
    public float monthHalf8;
    @ColumnInfo()
    public float monthHalf9;
    @ColumnInfo()
    public float monthHalf10;
    @ColumnInfo()
    public float monthHalf11;


    @ColumnInfo()
    public float subBass;
    @ColumnInfo()
    public float bass;
    @ColumnInfo()
    public float lowerMidrange;
    @ColumnInfo()
    public float midrange;
    @ColumnInfo()
    public float higherMidrange;
    @ColumnInfo()
    public float presence;
    @ColumnInfo()
    public float brilliance;



    @ColumnInfo()
    public String JSONResponseAPI;
    @ColumnInfo()
    public String songNameAPI;
    @ColumnInfo()
    public String artistAPI;
    @ColumnInfo()
    public String albumAPI;
    @ColumnInfo()
    public String genresAPI;    //use a delimiter to add multiple genres.
    @ColumnInfo(defaultValue = "false")
    public boolean isExplicitAPI;
    @ColumnInfo()
    public String labelAPI;
    @ColumnInfo()
    public String releasedAPI;

    public SongDetails(@NonNull String uri, String name, int duration, byte[] thumbnailArray, int themeColor, float loudnessIndex, long lastModified,
                       float monthHalf0, float monthHalf1, float monthHalf2, float monthHalf3, float monthHalf4, float monthHalf5,
                       float monthHalf6, float monthHalf7, float monthHalf8, float monthHalf9, float monthHalf10, float monthHalf11,
                       float subBass,float bass, float lowerMidrange, float midrange, float higherMidrange, float presence, float brilliance)
    {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.thumbnailArray = thumbnailArray;
        this.themeColor = themeColor;
        this.loudnessIndex = loudnessIndex;
        this.lastModified = lastModified;

        this.monthHalf0 = monthHalf0;
        this.monthHalf1 = monthHalf1;
        this.monthHalf2 = monthHalf2;
        this.monthHalf3 = monthHalf3;
        this.monthHalf4 = monthHalf4;
        this.monthHalf5 = monthHalf5;
        this.monthHalf6 = monthHalf6;
        this.monthHalf7 = monthHalf7;
        this.monthHalf8 = monthHalf8;
        this.monthHalf9 = monthHalf9;
        this.monthHalf10 = monthHalf10;
        this.monthHalf11 = monthHalf11;

        this.subBass = subBass;
        this.bass = bass;
        this.lowerMidrange = lowerMidrange;
        this.midrange = midrange;
        this.higherMidrange = higherMidrange;
        this.presence = presence;
        this.brilliance = brilliance;
    }
}
