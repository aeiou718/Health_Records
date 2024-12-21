package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoNotification {
    @Query("SELECT * FROM entity_notification")
    List<EntityNotification> getAll();

    @Query("SELECT * FROM entity_notification WHERE id = :id")
    EntityNotification getEnById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EntityNotification entityNotification);
}
