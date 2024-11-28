package com.websarva.wings.android.healthrecords;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.util.List;

public class RecordCalendarViewModel extends AndroidViewModel {
    private MutableLiveData<List<EntityLevelHealth>> elh_live;
    private MediatorLiveData<List<EntityTimeCheck>> etc_live;

    public RecordCalendarViewModel(Application application) {
        super(application);
    }

    public MutableLiveData<List<EntityLevelHealth>> getElh_live() {
        if (elh_live == null) {
            elh_live = new MediatorLiveData<>();
        }
        return elh_live;
    }

    public MutableLiveData<List<EntityTimeCheck>> getEtc_live() {
        if (etc_live == null) {
            etc_live = new MediatorLiveData<>();
        }
        return etc_live;
    }

    // EntityLevelHealth
    public void updateElh(List<EntityLevelHealth> elh_list) {
        elh_live.setValue(elh_list);
    }

    //EntityTimeCheck
    public void updateEtc(List<EntityTimeCheck> etc) {
        etc_live.setValue(etc);
    }
}