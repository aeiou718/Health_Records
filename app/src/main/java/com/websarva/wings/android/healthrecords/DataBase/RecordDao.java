package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecordDao {
    @Query("SELECT * FROM record_entity")
    List<RecordEntity> getAll();

    @Update
    void update(RecordEntity recordEntity);

    @Insert
    long insert(RecordEntity recordEntity);

    @Delete
    void delete(RecordEntity recordEntity);
}
