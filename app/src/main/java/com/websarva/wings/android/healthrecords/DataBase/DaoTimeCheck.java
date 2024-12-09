package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoTimeCheck {
    @Query("SELECT * FROM time_check")
    List<EntityTimeCheck> getAll();

    @Query("SELECT * FROM time_check WHERE id BETWEEN :startDate AND :endDate")
    List<EntityTimeCheck> getTimeCheckForWeek(long startDate, long endDate);


    @Query("SELECT * FROM time_check WHERE id = :id")
    EntityTimeCheck getEtcById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntityTimeCheck entityTimeCheck);
}