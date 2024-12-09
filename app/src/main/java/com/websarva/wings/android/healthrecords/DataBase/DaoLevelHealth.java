package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoLevelHealth {
    @Query("SELECT * FROM health_level")
    List<EntityLevelHealth> getAll();

    @Query("SELECT * FROM health_level WHERE id BETWEEN :startDate AND :entDate")
    List<EntityLevelHealth> getElhForWeek(long startDate, long entDate);

    @Query("SELECT * FROM health_level WHERE id = :id")
    EntityLevelHealth getElhById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(EntityLevelHealth levelHealth);
}
