package com.websarva.wings.android.healthrecords.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "health_level")
public class EntityLevelHealth {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int level;  //RadioGroupのId値を保存

    public EntityLevelHealth(int level) {
        this.level = level;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

}
