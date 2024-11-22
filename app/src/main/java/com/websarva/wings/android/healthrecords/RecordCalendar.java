package com.websarva.wings.android.healthrecords;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.websarva.wings.android.healthrecords.DataBase.DaoDetail;
import com.websarva.wings.android.healthrecords.DataBase.DaoLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.DaoTimeCheck;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealth;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealthSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityDetail;
import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordCalendar extends AppCompatActivity {
    private DataBaseHealth dbh;
    private RecordCalendarViewModel viewModel;
    ExecutorService executorService;
    DataBaseHealth dataBaseHealth;
    List<EntityLevelHealth> elh_list;
    List<EntityTimeCheck> etc_list;
    List<EntityDetail> ed_list;
    EntityLevelHealth elh_top;
    EntityTimeCheck etc_top;
    EntityDetail ed_top;
    DaoLevelHealth dlh_top;
    DaoTimeCheck dtc_top;
    DaoDetail dd_top;
    Calendar calendar;
    RadioGroup radioGroup;
    CheckBox morning;
    CheckBox evening;
    CheckBox afternoon;
    long currentDate;   //カレンダー
    int dayId;          //日付

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.record_calendar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.record_calendar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        executorService = Executors.newSingleThreadExecutor();
        dbh = Room.databaseBuilder(getApplicationContext(), DataBaseHealth.class, "health_records_db").allowMainThreadQueries().build();

        dataBaseHealth = DataBaseHealthSingleton.getInstance(getApplicationContext());
        dlh_top = dataBaseHealth.daoLevelHealth();
        dtc_top = dataBaseHealth.daoTimeCheck();
        dd_top = dataBaseHealth.daoDetail();

        calendar = Calendar.getInstance();
        currentDate = calendar.getTimeInMillis();
        CalendarView healthCalendar = findViewById(R.id.health_calendar);
        healthCalendar.setDate(currentDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dayId = year * 10000 + (month + 1) * 100 + day;
        Log.d("dayId", String.valueOf(dayId));

        viewModel = new ViewModelProvider(this).get(RecordCalendarViewModel.class);
        new DataRead(dbh, RecordCalendar.this).execute();

        radioGroup = findViewById(R.id.health_radio);
        //　　チェック状態
//        radioGroup.check(R.id.normal);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("health_level", String.valueOf(checkedId));
            new LevelHealthData(dbh, RecordCalendar.this).execute();
        });

        morning = findViewById(R.id.morning_checkBox);
        evening = findViewById(R.id.evening_checkBox);
        afternoon = findViewById(R.id.afternoon_checkBox);

        morning.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("morning", String.valueOf(morning.isChecked()));
            new CheckBoxData(dbh, RecordCalendar.this).execute();
        });
        evening.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("evening", String.valueOf(evening.isChecked()));
            new CheckBoxData(dbh, RecordCalendar.this).execute();
        });
        afternoon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("afternoon", String.valueOf(afternoon.isChecked()));
            new CheckBoxData(dbh, RecordCalendar.this).execute();
        });

        final Observer<EntityLevelHealth> lhObserver = entityLevelHealth -> {
            if (entityLevelHealth != null) {
                entityLevelHealth.setId(dayId);
                radioGroup.check(entityLevelHealth.getLevel());
            }
        };
        final Observer<EntityTimeCheck> tcObserver = entityTimeCheck -> {
            if (entityTimeCheck != null) {
                Log.d("EntityTimeCheck", String.valueOf(entityTimeCheck.getId()));
                entityTimeCheck.setId(dayId);
                Log.d("EntityTimeCheck_morning", String.valueOf(entityTimeCheck.isMorning()));
                morning.setChecked(entityTimeCheck.isMorning());
                evening.setChecked(entityTimeCheck.isEvening());
                afternoon.setChecked(entityTimeCheck.isAfternoon());
            }
        };

        //カレンダー変更
        healthCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dayId = year * 10000 + (month + 1) * 100 + dayOfMonth;

                EntityLevelHealth elh = viewModel.getEntityLevelHealth(dayId);
                EntityTimeCheck etc = viewModel.getEntityTimeCheck(dayId, etc_top);

                if (elh != null) {
                    Log.d("elh", "elh.start");
                    radioGroup.check(elh.getLevel());
                    new LevelHealthData(dbh, RecordCalendar.this).execute();
                }

                if (etc != null) {
                    Log.d("etc", "etc.start");
                    morning.setChecked(etc.isMorning());
                    evening.setChecked(etc.isEvening());
                    afternoon.setChecked(etc.isAfternoon());
                    new CheckBoxData(dbh, RecordCalendar.this).execute();
                }

                Log.d("Selected Date", year + "/" + (month + 1) + "/" + dayOfMonth);
//                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                dayId = year * 10000 + (month + 1) * 100 + dayOfMonth;
                Log.d("Selected Date dayId", String.valueOf(dayId));

                viewModel.getElh_live(dayId).observe(RecordCalendar.this, lhObserver);
                viewModel.getEtc_live(dayId).observe(RecordCalendar.this, tcObserver);
            }
        });


        viewModel.getElh_live(dayId).observe(RecordCalendar.this, lhObserver);
        viewModel.getEtc_live(dayId).observe(RecordCalendar.this, tcObserver);
    }

    //RadioGroupのデータベース
    private class LevelHealthData implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final DataBaseHealth db;
        RadioButton healthLevel;

        public LevelHealthData(DataBaseHealth db, Activity activity) {
            this.db = db;
            this.weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
//            RadioGroup radioGroup = findViewById(R.id.health_radio);
//            healthLevel = findViewById(radioGroup.getCheckedRadioButtonId());

            DaoLevelHealth daoLevelHealth = db.daoLevelHealth();
            EntityLevelHealth entityLevelHealth = dlh_top.getElhById(dayId);
//            daoLevelHealth.getAll();
            entityLevelHealth.setLevel(radioGroup.getCheckedRadioButtonId());
            daoLevelHealth.insert(entityLevelHealth);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(entityLevelHealth));
        }

        void execute() {
            executorService.submit(new LevelHealthData(db, weakActivity.get()));
        }

        void onPostExecute(EntityLevelHealth elh) {
            viewModel.getElh_live(dayId).setValue(elh);
        }
    }

    //CheckBoxのデータベース
    private class CheckBoxData implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final DataBaseHealth db;
        boolean morning;
        boolean evening;
        boolean afternoon;

        public CheckBoxData(DataBaseHealth db, Activity activity) {
            this.weakActivity = new WeakReference<>(activity);
            this.db = db;
        }

        @Override
        public void run() {
            CheckBox boxMorning = findViewById(R.id.morning_checkBox);
            morning = boxMorning.isChecked();
            CheckBox boxEvening = findViewById(R.id.evening_checkBox);
            evening = boxEvening.isChecked();
            CheckBox boxAfternoon = findViewById(R.id.afternoon_checkBox);
            afternoon = boxAfternoon.isChecked();

            DaoTimeCheck daoTimeCheck = db.daoTimeCheck();
            EntityTimeCheck entityTimeCheck = new EntityTimeCheck(morning, evening, afternoon);
            daoTimeCheck.getAll();
            entityTimeCheck.setId(dayId);
            entityTimeCheck.setMorning(morning);
            entityTimeCheck.setEvening(evening);
            entityTimeCheck.setAfternoon(afternoon);
            daoTimeCheck.insert(entityTimeCheck);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(entityTimeCheck));
        }

        void execute() {
            executorService.submit(new CheckBoxData(db, weakActivity.get()));
        }

        void onPostExecute(EntityTimeCheck etc) {
            viewModel.getEtc_live(dayId).setValue(etc);
        }
    }

    class DataRead implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final DataBaseHealth db;

        public DataRead(DataBaseHealth db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            if (dlh_top != null || dtc_top != null || dd_top != null) {
                elh_list = dlh_top.getAll();
                etc_list = dtc_top.getAll();
                ed_list = dd_top.getAll();
            }
            elh_top = dlh_top.getElhById(dayId);
            etc_top = new EntityTimeCheck(morning.isChecked(), evening.isChecked(), afternoon.isChecked());
            ed_top = new EntityDetail("");

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute());
        }

        void execute() {
            executorService.submit(new DataRead(db, weakActivity.get()));
        }

        void onPostExecute() {

        }
    }
}