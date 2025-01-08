package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface DaoDetail {
    @Query("SELECT * FROM entity_detail WHERE id = :id")
    EntityDetail getEdById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntityDetail entityDetail);
}
