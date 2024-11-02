package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_entity")
public class RecordEntity {
    public RecordEntity(int health_id, boolean health_state, boolean morning, boolean evening, boolean afternoon, String detail) {
        this.health_id = health_id;
        this.health_state = health_state;
        this.morning = morning;
        this.evening = evening;
        this.afternoon = afternoon;
        this.detail = detail;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;                    //ID
    private int health_id;             //健康ID
    private boolean health_state;      //健康状態
    private boolean morning;          //朝
    private boolean evening;          //昼
    private boolean afternoon;        //夕方
    private String detail;              //詳細;               //日付

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHealth_id() {
        return health_id;
    }

    public void setHealth_id(int health_id) {
        this.health_id = health_id;
    }

    public boolean isHealth_state() {
        return health_state;
    }

    public void setHealth_state(boolean health_state) {
        this.health_state = health_state;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
