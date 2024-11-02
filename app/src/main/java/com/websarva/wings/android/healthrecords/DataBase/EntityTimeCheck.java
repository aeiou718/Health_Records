package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_check")
public class EntityTimeCheck {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private boolean morning;
    private boolean evening;
    private boolean afternoon;

    public EntityTimeCheck(boolean morning, boolean evening, boolean afternoon) {
        this.morning = morning;
        this.evening = evening;
        this.afternoon = afternoon;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isEvening() {
        return evening;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }

    public boolean isAfternoon() {
        return afternoon;
    }

    public void setAfternoon(boolean afternoon) {
        this.afternoon = afternoon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
