package com.websarva.wings.android.healthrecords.DataBase;

import android.content.Context;

import androidx.room.Room;

public class DataBaseHealthSingleton {
    private static DataBaseHealth instance = null;

    private DataBaseHealthSingleton() {
    }

    public static DataBaseHealth getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        instance = Room.databaseBuilder(context,
                DataBaseHealth.class, "app_database").build();
        return instance;
    }
}
