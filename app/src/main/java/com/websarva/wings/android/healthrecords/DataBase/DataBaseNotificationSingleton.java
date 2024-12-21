package com.websarva.wings.android.healthrecords.DataBase;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DataBaseNotificationSingleton {
    private static DataBaseNotification instance = null;

    static final Migration migration1To2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE entity_notification ADD COLUMN noti_switch INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static DataBaseNotification getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        synchronized (DataBaseNotification.class) {
            if (instance == null) {
                instance = Room.databaseBuilder(
                                context.getApplicationContext(),
                                DataBaseNotification.class,
                                "database_notification"
                        )
                        .addMigrations(migration1To2)
                        .build();
            }
        }
        return instance;
    }
}