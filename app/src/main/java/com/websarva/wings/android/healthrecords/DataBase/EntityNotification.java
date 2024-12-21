package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "entity_notification")
public class EntityNotification {
    @PrimaryKey
    private int id;
    private final int hour;
    private final int minute;
    private final boolean notification_switch;

    public EntityNotification(int hour, int minute, boolean notification_switch) {
        this.hour = hour;
        this.minute = minute;
        this.notification_switch = notification_switch;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isNotification_switch() {
        return notification_switch;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
