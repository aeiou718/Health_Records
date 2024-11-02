package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DaoDetail {
    @Query("SELECT * FROM entity_detail")
    List<EntityDetail> getAll();

    @Update
    void update(EntityDetail entityDetail);

    @Insert
    void insert(EntityDetail entityDetail);

    @Delete
    void delete(EntityDetail entityDetail);
}
