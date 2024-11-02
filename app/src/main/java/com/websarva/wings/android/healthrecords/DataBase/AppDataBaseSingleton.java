package com.websarva.wings.android.healthrecords.DataBase;

import android.content.Context;

import androidx.room.Room;

public class AppDataBaseSingleton {
    private static AppDataBase instance = null;

    private AppDataBaseSingleton() {
    }

    public static AppDataBase getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        instance = Room.databaseBuilder(context,
                AppDataBase.class, "app_database").build();
        return instance;
    }
}
