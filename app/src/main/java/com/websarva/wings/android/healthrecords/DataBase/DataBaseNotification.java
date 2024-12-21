package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {EntityNotification.class}, version = 2)
public abstract class DataBaseNotification extends RoomDatabase {
    public abstract DaoNotification daoNotification();
}