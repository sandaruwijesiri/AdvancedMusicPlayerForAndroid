package com.example.testapplication2.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CurrentMonthAndHalfDao {
    @Query("SELECT * FROM CurrentMonthAndHalf")
    List<CurrentMonthAndHalf> getAll();

    @Insert
    void insert(CurrentMonthAndHalf... currentMonthAndHalf);

    @Delete
    void delete(CurrentMonthAndHalf currentMonthAndHalf);

    @Update
    void update(CurrentMonthAndHalf currentMonthAndHalf);
}
