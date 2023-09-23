package com.example.testapplication2.Database;


import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {SongDetails.class, CurrentMonthAndHalf.class},
        version = 5,
        exportSchema = true,
        autoMigrations = {
                @AutoMigration (from = 4, to = 5)
        }
        )
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDetailsDao SongDetailsDao();
    public abstract CurrentMonthAndHalfDao CurrentMonthAndHalfDao();
}
