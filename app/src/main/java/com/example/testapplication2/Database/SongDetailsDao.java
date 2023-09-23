package com.example.testapplication2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SongDetailsDao {
    @Query("SELECT * FROM SongDetails")
    List<SongDetails> getAll();
    @Query("SELECT * FROM SongDetails WHERE uri=:uri")
    SongDetails getForUri(String uri);

    @Insert
    void insert(SongDetails... songDetails);

    @Delete
    void delete(SongDetails songDetails);

    @Update
    void update(SongDetails songDetails);
}
