package com.websarva.wings.android.healthrecords;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

public class RecordCalendarViewModel extends ViewModel {
    private MutableLiveData<EntityLevelHealth> elh;
    private MutableLiveData<EntityTimeCheck> etc;

    public MutableLiveData<EntityLevelHealth> getElh() {
        if (elh == null) {
            elh = new MutableLiveData<>();
        }
        return elh;
    }

    public MutableLiveData<EntityTimeCheck> getEtc() {
        if (etc == null) {
            etc = new MutableLiveData<>();
        }
        return etc;
    }
}