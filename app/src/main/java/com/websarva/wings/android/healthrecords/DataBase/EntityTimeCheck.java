package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_check")
public class EntityTimeCheck {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final boolean morning;
    private final boolean evening;
    private final boolean afternoon;

    public EntityTimeCheck(boolean morning, boolean evening, boolean afternoon) {
        this.morning = morning;
        this.evening = evening;
        this.afternoon = afternoon;
    }

    public boolean isMorning() {
        return morning;
    }

    public boolean isEvening() {
        return evening;
    }

    public boolean isAfternoon() {
        return afternoon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
