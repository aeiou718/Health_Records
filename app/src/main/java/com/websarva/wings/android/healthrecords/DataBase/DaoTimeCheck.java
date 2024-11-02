package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoTimeCheck {
    @Query("SELECT * FROM time_check")
    List<EntityTimeCheck> getAll();

    @Update
    void update(EntityTimeCheck entityTimeCheck);

    @Insert
    void insert(EntityTimeCheck entityTimeCheck);

    @Delete
    void delete(EntityTimeCheck entityTimeCheck);

}