package com.websarva.wings.android.healthrecords.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoTimeCheck {
    @Query("SELECT * FROM time_check")
    List<EntityTimeCheck> getAll();

    @Query("SELECT * FROM time_check WHERE id = :id")
    LiveData<EntityTimeCheck> getEntityTimeCheckById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntityTimeCheck entityTimeCheck);

    @Update
    void update(EntityTimeCheck entityTimeCheck);
}