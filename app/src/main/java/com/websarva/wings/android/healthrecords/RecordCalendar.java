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
    List<EntityLevelHealth> entityLevelHealths;
    List<EntityTimeCheck> entityTimeChecks;
    List<EntityDetail> entityDetails;
    Calendar calendar;
    long currentDate;

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

        calendar = Calendar.getInstance();
        currentDate = calendar.getTimeInMillis();
        CalendarView healthCalendar = findViewById(R.id.health_calendar);
        healthCalendar.setDate(currentDate);

        RadioGroup radioGroup = findViewById(R.id.health_radio);
        //　　チェック状態
//        radioGroup.check(R.id.normal);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("health_level", String.valueOf(checkedId));
            new LevelHealthData(dbh, RecordCalendar.this).execute();
        });

        CheckBox morning = findViewById(R.id.morning_checkBox);
        morning.setChecked(true);
        CheckBox evening = findViewById(R.id.evening_checkBox);
        CheckBox afternoon = findViewById(R.id.afternoon_checkBox);
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

        healthCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("Selected Date", year + "/" + (month + 1) + "/" + dayOfMonth);
//                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                long selectedDate = year * 10000L + (month + 1) * 100L + dayOfMonth;
                Log.d("Selected Date", String.valueOf(selectedDate));
                currentDate = selectedDate;

            }
        });

        viewModel = new ViewModelProvider(this).get(RecordCalendarViewModel.class);
        final Observer<EntityLevelHealth> lhObserver = entityLevelHealth -> {
            if (entityLevelHealth != null) {
                entityLevelHealth.setId((int) currentDate);
                radioGroup.check(entityLevelHealth.getLevel());
            }
        };
        final Observer<EntityTimeCheck> tcObserver = entityTimeCheck -> {
            if (entityTimeCheck != null) {
                entityTimeCheck.setId((int) currentDate);
                morning.setChecked(entityTimeCheck.isMorning());
                evening.setChecked(entityTimeCheck.isEvening());
                afternoon.setChecked(entityTimeCheck.isAfternoon());
            }
        };
        viewModel.getElh().observe(this, lhObserver);
        viewModel.getEtc().observe(this, tcObserver);
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
            DaoLevelHealth daoLevelHealth = db.daoLevelHealth();
            EntityLevelHealth entityLevelHealth = new EntityLevelHealth(healthLevel.getId());
            entityLevelHealth.setId((int) currentDate);
            daoLevelHealth.update(entityLevelHealth);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(entityLevelHealth));
        }

        void onPreExecute() {
            RadioGroup radioGroup = findViewById(R.id.health_radio);
            healthLevel = findViewById(radioGroup.getCheckedRadioButtonId());
        }

        void execute() {
            onPreExecute();
            executorService.submit(new LevelHealthData(db, weakActivity.get()));

        }

        void onPostExecute(EntityLevelHealth elh) {
            viewModel.getElh().setValue(elh);
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
            DaoTimeCheck daoTimeCheck = db.daoTimeCheck();
            EntityTimeCheck entityTimeCheck = new EntityTimeCheck(morning, evening, afternoon);
            entityTimeCheck.setId((int) currentDate);
            daoTimeCheck.update(entityTimeCheck);

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(entityTimeCheck));

        }

        void onPreExecute() {
            CheckBox boxMorning = findViewById(R.id.morning_checkBox);
            morning = boxMorning.isChecked();
            CheckBox boxEvening = findViewById(R.id.evening_checkBox);
            evening = boxEvening.isChecked();
            CheckBox boxAfternoon = findViewById(R.id.afternoon_checkBox);
            afternoon = boxAfternoon.isChecked();
        }

        void execute() {
            onPreExecute();
            executorService.submit(new CheckBoxData(db, weakActivity.get()));

        }

        void onPostExecute(EntityTimeCheck etc) {
            viewModel.getEtc().setValue(etc);
        }
    }

    class DataSave implements Runnable {
        private final WeakReference<Activity> weakActivity;
        private final DataBaseHealth db;

        public DataSave(DataBaseHealth db, Activity activity) {
            this.db = db;
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = db.daoLevelHealth();
            DaoTimeCheck daoTimeCheck = db.daoTimeCheck();
            DaoDetail daoDetail = db.daoDetail();
//            RecordEntity recordEntity = new RecordEntity();
//            recordDao.update(recordEntity);
            entityLevelHealths = daoLevelHealth.getAll();
            entityTimeChecks = daoTimeCheck.getAll();
            entityDetails = daoDetail.getAll();

            new Handler(Looper.getMainLooper()).post(() -> onPostExecute(entityLevelHealths, entityTimeChecks, entityDetails));
        }

        void onPreExecute() {

        }

        void execute() {
            onPreExecute();
            executorService.submit(new DataSave(db, weakActivity.get()));
        }

        void onPostExecute(List<EntityLevelHealth> elh, List<EntityTimeCheck> etc, List<EntityDetail> ed) {

        }

    }
}