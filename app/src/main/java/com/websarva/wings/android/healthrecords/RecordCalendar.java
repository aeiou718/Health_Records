package com.websarva.wings.android.healthrecords;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.websarva.wings.android.healthrecords.DataBase.DaoLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.DaoTimeCheck;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealth;
import com.websarva.wings.android.healthrecords.DataBase.DataBaseHealthSingleton;
import com.websarva.wings.android.healthrecords.DataBase.EntityLevelHealth;
import com.websarva.wings.android.healthrecords.DataBase.EntityTimeCheck;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordCalendar extends AppCompatActivity {
    private DataBaseHealth dbh;
    private RecordCalendarViewModel viewModel;
    ExecutorService executorService;
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

        dbh = DataBaseHealthSingleton.getInstance(getApplicationContext());

        calendar = Calendar.getInstance();
        currentDate = calendar.getTimeInMillis();
        CalendarView healthCalendar = findViewById(R.id.health_calendar);
        healthCalendar.setDate(currentDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dayId = year * 10000 + (month + 1) * 100 + day;

        viewModel = new ViewModelProvider(this).get(RecordCalendarViewModel.class);

        radioGroup = findViewById(R.id.health_radio);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> executorService.submit(new LevelHealthData(dbh, new EntityLevelHealth(checkedId))));

        morning = findViewById(R.id.morning_checkBox);
        evening = findViewById(R.id.evening_checkBox);
        afternoon = findViewById(R.id.afternoon_checkBox);

        morning.setOnCheckedChangeListener((buttonView, isChecked) -> executorService.submit(new CheckBoxData(dbh, new EntityTimeCheck(morning.isChecked(), evening.isChecked(), afternoon.isChecked()))));
        evening.setOnCheckedChangeListener((buttonView, isChecked) -> executorService.submit(new CheckBoxData(dbh, new EntityTimeCheck(morning.isChecked(), evening.isChecked(), afternoon.isChecked()))));
        afternoon.setOnCheckedChangeListener((buttonView, isChecked) -> executorService.submit(new CheckBoxData(dbh, new EntityTimeCheck(morning.isChecked(), evening.isChecked(), afternoon.isChecked()))));

        final Observer<List<EntityLevelHealth>> lhObserver = entityLevelHealths -> {
            if (entityLevelHealths != null) {
                entityLevelHealths.stream()
                        .filter(e -> e.getId() == dayId)
                        .findFirst()
                        .ifPresent(entityLevelHealth -> radioGroup.check(entityLevelHealth.getLevel()));
            }
        };

        final Observer<List<EntityTimeCheck>> tcObserver = entityTimeCheck -> {
            if (entityTimeCheck != null) {
                entityTimeCheck.stream()
                        .filter(e -> e.getId() == dayId)
                        .findFirst()
                        .ifPresent(entityTimeCheck1 -> {
                            morning.setChecked(entityTimeCheck1.isMorning());
                            evening.setChecked(entityTimeCheck1.isEvening());
                            afternoon.setChecked(entityTimeCheck1.isAfternoon());
                        });
            }
        };
        viewModel.getElh_live().observe(RecordCalendar.this, lhObserver);
        viewModel.getEtc_live().observe(RecordCalendar.this, tcObserver);

        Button inDetail = findViewById(R.id.inDetail);
        inDetail.setOnClickListener(v -> {
            Intent intent = new Intent(RecordCalendar.this, HealthRecordsDetail.class);
            intent.putExtra("dayId", dayId);
            startActivity(intent);
        });

        //カレンダー変更
        healthCalendar.setOnDateChangeListener((view, year1, month1, dayOfMonth) -> {
            dayId = year1 * 10000 + (month1 + 1) * 100 + dayOfMonth;

            executorService.submit(new DataRead());
            calendar.set(year1, month1, dayOfMonth);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataRead();
    }

    void dataRead() {
        executorService.submit(new DataRead());
    }

    //RadioGroupのデータベース
    private class LevelHealthData implements Runnable {
        private final DataBaseHealth db;
        EntityLevelHealth entityLevelHealth;

        public LevelHealthData(DataBaseHealth db, EntityLevelHealth entityLevelHealth) {
            this.db = db;
            this.entityLevelHealth = entityLevelHealth;
        }

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = db.daoLevelHealth();
            entityLevelHealth.setId(dayId);
            daoLevelHealth.insert(entityLevelHealth);
        }
    }

    //CheckBoxのデータベース
    private class CheckBoxData implements Runnable {
        private final DataBaseHealth db;
        EntityTimeCheck entityTimeCheck;

        public CheckBoxData(DataBaseHealth db, EntityTimeCheck entityTimeCheck) {
            this.db = db;
            this.entityTimeCheck = entityTimeCheck;
        }

        @Override
        public void run() {
            DaoTimeCheck daoTimeCheck = db.daoTimeCheck();
            entityTimeCheck.setId(dayId);
            daoTimeCheck.insert(entityTimeCheck);
        }
    }

    class DataRead implements Runnable {
        private List<EntityLevelHealth> elh_list;
        private List<EntityTimeCheck> etc_list;
        private EntityLevelHealth elh_top;
        private EntityTimeCheck etc_top;

        @Override
        public void run() {
            DaoLevelHealth daoLevelHealth = dbh.daoLevelHealth();
            DaoTimeCheck daoTimeCheck = dbh.daoTimeCheck();

            elh_list = daoLevelHealth.getAll();
            etc_list = daoTimeCheck.getAll();

            // ラジオボタン
            elh_top = daoLevelHealth.getElhById(dayId);

            // チェックボックス
            etc_top = daoTimeCheck.getEtcById(dayId);

            new Handler(Looper.getMainLooper()).post(this::onPostExecute);
        }

        void onPostExecute() {
            viewModel.updateElh(elh_list);
            if (elh_top == null) {
                radioGroup.clearCheck();
            }
            viewModel.updateEtc(etc_list);
            if (etc_top == null) {
                morning.setChecked(false);
                evening.setChecked(false);
                afternoon.setChecked(false);
            }
        }
    }
}