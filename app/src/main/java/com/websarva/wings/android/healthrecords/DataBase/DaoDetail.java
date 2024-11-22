package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoDetail {
    @Query("SELECT * FROM entity_detail")
    List<EntityDetail> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntityDetail entityDetail);

    @Update
    void update(EntityDetail entityDetail);
}
