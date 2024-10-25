package com.websarva.wings.android.healthrecords;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_entity")
public class RecordEntity {
    public RecordEntity(RadioGroup health, RadioButton morning, RadioButton evening, RadioButton afternoon) {
        this.health = health;
        this.morning = morning;
        this.evening = evening;
        this.afternoon = afternoon;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;             //ID
    private RadioGroup health;         //健康状態
    private RadioButton morning;    //朝
    private RadioButton evening;    //昼
    private RadioButton afternoon;  //夕方

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

    public RadioButton isMorning() {
        return morning;
    }

    public void setMorning(RadioButton morning) {
        this.morning = morning;
    }

    public RadioButton isEvening() {
        return evening;
    }

    public void setEvening(RadioButton evening) {
        this.evening = evening;
    }

    public RadioButton isAfternoon() {
        return afternoon;
    }

    public void setAfternoon(RadioButton afternoon) {
        this.afternoon = afternoon;
    }

}
