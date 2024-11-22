package com.websarva.wings.android.healthrecords;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.websarva.wings.android.healthrecords.DataBase.DaoLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.DaoTimeCheck;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealth;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealthSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordCalendarViewModel extends AndroidViewModel {
    private MediatorLiveData<EntityLevelHealth> elh_live;
    private MediatorLiveData<EntityTimeCheck> etc_live;
    ExecutorService executorService;
    DataBaseHealth dbh;

    private final DaoTimeCheck dtc;
    private final DaoLevelHealth dlh;

    public RecordCalendarViewModel(Application application) {
        super(application);
        executorService = Executors.newSingleThreadExecutor();
        dlh = DataBaseHealthSingleton.getInstance(application).daoLevelHealth();
        dtc = DataBaseHealthSingleton.getInstance(application).daoTimeCheck();

        dbh = DataBaseHealthSingleton.getInstance(application);
    }

    public MutableLiveData<EntityLevelHealth> getElh_live(int id) {
        if (elh_live == null) {
            elh_live = new MediatorLiveData<>();
        }
        elh_live.addSource(dlh.getElhLd(id), new Observer<List<EntityLevelHealth>>() {
            @Override
            public void onChanged(List<EntityLevelHealth> entityLevelHealth) {
                if (entityLevelHealth != null && !entityLevelHealth.isEmpty()) {
                    elh_live.setValue(entityLevelHealth.get(0));
                } else {
                    elh_live.setValue(null);
                }
//                elh_live.setValue(entityLevelHealth);
            }
        });
//        elh_live = (MutableLiveData<EntityLevelHealth>) dlh.getEntityLevelHealthById(id);
        return elh_live;
    }

    public MutableLiveData<EntityTimeCheck> getEtc_live(int id) {
        etc_live = new MediatorLiveData<>();

        etc_live.addSource(dtc.getEntityTimeCheckById(id), new Observer<EntityTimeCheck>() {
            @Override
            public void onChanged(EntityTimeCheck entityTimeCheck) {
                etc_live.setValue(entityTimeCheck);
            }
        });
//        etc_live = (MutableLiveData<EntityTimeCheck>) dtc.getEntityTimeCheckById(id);
        return etc_live;
    }

    // EntityLevelHealth
    public EntityLevelHealth getEntityLevelHealth(int date) {
//        if (elh.getLevel() != -1) {
//            new getEntityLevelHealth(dbh, new RecordCalendar(), elh.getLevel()).execute(elh.getLevel());
//            this.elh_live.setValue(elh);
//        }
        new getEntityLevelHealth(dbh, new RecordCalendar(), date).execute(date);
        return elh_live.getValue();
    }

    private class getEntityLevelHealth implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final DataBaseHealth db;
        private final int id;

        public getEntityLevelHealth(DataBaseHealth db, Activity activity, int id) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
            this.id = id;
        }

        @Override
        public void run() {
            dlh.getAll();
            EntityLevelHealth elh = dlh.getElhById(id);
            dlh.insert(elh);
            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(elh));
        }

        public void execute(int id) {
            executorService.submit(new getEntityLevelHealth(db, weakActivity.get(), id));
        }

        public void onPostExecute(EntityLevelHealth elh) {
            elh_live.setValue(elh);
        }
    }

    //EntityTimeCheck
    public EntityTimeCheck getEntityTimeCheck(int date, EntityTimeCheck etc) {
//        etc.setId(date);
//        if (!etc.isMorning() && !etc.isEvening() && !etc.isAfternoon()) {
//            etc.setMorning(false);
//            etc.setEvening(false);
//            etc.setAfternoon(false);
//            new getEntityTimeCheck(dbh, new RecordCalendar(), etc.isMorning(), etc.isEvening(), etc.isAfternoon()).execute(etc.isMorning(), etc.isEvening(), etc.isAfternoon());
//            this.etc_live.setValue(etc);
//        }
        new getEntityTimeCheck(dbh, new RecordCalendar(), etc.isMorning(), etc.isEvening(), etc.isAfternoon()).execute(etc.isMorning(), etc.isEvening(), etc.isAfternoon());
        this.etc_live.setValue(etc);
        return etc_live.getValue();
    }

    private class getEntityTimeCheck implements Runnable {
        private final DataBaseHealth db;
        private final WeakReference<Activity> weakActivity;
        private final boolean morning;
        private final boolean evening;
        private final boolean afternoon;

        public getEntityTimeCheck(DataBaseHealth db, Activity activity, boolean morning, boolean evening, boolean afternoon) {
            this.weakActivity = new WeakReference<>(activity);
            this.db = db;
            this.morning = morning;
            this.evening = evening;
            this.afternoon = afternoon;
        }

        @Override
        public void run() {
            EntityTimeCheck etc = new EntityTimeCheck(morning, evening, afternoon);
            dtc.insert(etc);
            new Handler(Looper.getMainLooper()).post(() -> onPostExecute());
        }

        public void execute(boolean morning, boolean evening, boolean afternoon) {
            executorService.submit(new getEntityTimeCheck(db, weakActivity.get(), morning, evening, afternoon));
        }

        public void onPostExecute() {
        }
    }

    public void updateEntityTimeCheck(EntityTimeCheck etc) {
        dtc.update(etc);
        this.etc_live.setValue(etc);
    }
}