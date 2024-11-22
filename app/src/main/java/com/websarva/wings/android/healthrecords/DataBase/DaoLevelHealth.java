package com.websarva.wings.android.healthrecords.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoLevelHealth {
    @Query("SELECT * FROM health_level")
    List<EntityLevelHealth> getAll();

    @Query("SELECT * FROM health_level WHERE id = :id")
    EntityLevelHealth getElhById(int id);

    @Query("SELECT * FROM health_level WHERE id = :id")
    LiveData<List<EntityLevelHealth>> getElhLd(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(EntityLevelHealth levelHealth);

    @Update
    void update(EntityLevelHealth levelHealth);
}
