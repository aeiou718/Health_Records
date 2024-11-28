package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RecordEntity.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract RecordDao recordDao();

}