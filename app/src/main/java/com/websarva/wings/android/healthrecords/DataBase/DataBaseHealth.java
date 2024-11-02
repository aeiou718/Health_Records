package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {EntityLevelHealth.class, EntityTimeCheck.class, EntityDetail.class}, version = 1, exportSchema = false)
public abstract class DataBaseHealth extends RoomDatabase {
    public abstract DaoLevelHealth daoLevelHealth();

    public abstract DaoTimeCheck daoTimeCheck();

    public abstract DaoDetail daoDetail();
}
