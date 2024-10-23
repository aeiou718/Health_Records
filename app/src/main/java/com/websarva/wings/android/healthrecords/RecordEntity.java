package com.websarva.wings.android.healthrecords;

import android.widget.RadioGroup;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_entity")
public class RecordEntity {
    public RecordEntity(RadioGroup health, boolean morning, boolean evening, boolean afternoon) {
        this.health = health;
        this.morning = morning;
        this.evening = evening;
        this.afternoon = afternoon;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;             //ID
    private RadioGroup health;         //健康状態
    private boolean morning;    //朝
    private boolean evening;    //昼
    private boolean afternoon;  //夕方

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RadioGroup getHealth() {
        return health;
    }

    public void setHealth(RadioGroup health) {
        this.health = health;
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

}
