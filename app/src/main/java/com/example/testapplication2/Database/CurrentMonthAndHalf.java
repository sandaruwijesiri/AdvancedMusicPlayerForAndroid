package com.example.testapplication2.Database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CurrentMonthAndHalf {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "MonthAndHalf")
    public String monthAndHalf;

    public CurrentMonthAndHalf(@NonNull String monthAndHalf) {
        this.monthAndHalf = monthAndHalf;
    }
}
