package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoLevelHealth {
    @Query("SELECT * FROM health_level")
    List<EntityLevelHealth> getAll();

    @Update
    void update(EntityLevelHealth levelHealth);

    @Insert
    long insert(EntityLevelHealth levelHealth);

    @Delete
    void delete(EntityLevelHealth levelHealth);
}
